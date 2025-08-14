package ru.practicum.android.diploma.presentation.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.Contacts
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.presentation.viewmodel.VacancyViewModel
import ru.practicum.android.diploma.util.CurrencyFormatter
import ru.practicum.android.diploma.util.NumberFormatter.formatSalary
import ru.practicum.android.diploma.util.TextFormatter.formatToHtml

class VacancyFragment : Fragment() {
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VacancyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeViewModel()
        arguments?.getString("VACANCY_ID")?.let { viewModel.loadVacancy(it) }
    }

    private fun setupToolbar() {
        binding.vacancyToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.vacancyToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share_button -> {
                    shareVacancy()
                    true
                }

                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.vacancyState.observe(viewLifecycleOwner) { resource: Resource<VacancyDetail> ->
            when (resource) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    bindVacancyData(resource.data)
                }
                is Resource.Error -> {
                    showLoading(false)
                    showServerError()
                }
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun bindVacancyData(vacancy: VacancyDetail?) {
        if (vacancy == null) {
            showVacancyError()
            return
        }

        try {
            binding.vacancyTitle.text = vacancy.name

            binding.vacancySalary.text = formatSalary(vacancy.salary)

            binding.companyName.text = vacancy.employer.name

            binding.city.text = vacancy.address?.fullAddress ?: vacancy.area.name

            loadCompanyLogo(vacancy.employer.logo)

            binding.requiredExperience.text = vacancy.experience.name

            binding.typeEmployment.text = "${vacancy.employment.name}, ${vacancy.schedule.name}"

            bindContacts(vacancy.contacts)

            binding.descriptions.text = formatToHtml(vacancy.description)

            bindSkills(vacancy.skills)
        } catch (e: IllegalStateException) {
            Log.e("VacancyFragment", "UI binding error", e)
            showVacancyError()
        }
    }

    private fun bindContacts(contacts: Contacts?) {
        binding.contactsGroup.isVisible = contacts != null

        contacts?.let {
            binding.contactsName.apply {
                text = it.name
                isVisible = !it.name.isNullOrEmpty()
            }

            binding.contactsEmail.apply {
                text = it.email
                isVisible = !it.email.isNullOrEmpty()
                setOnClickListener { contacts.email?.let { email -> sendEmail(email) } }
            }

            if (!it.phones.isNullOrEmpty()) {
                val phonesText = it.phones.joinToString("\n\n") { phone ->
                    phone.formatted + (phone.comment?.let { " ($it)" } ?: "")
                }
                binding.contactsPhone.text = phonesText
                binding.contactsPhone.isVisible = true
                binding.contactsPhone.setOnClickListener {
                    contacts.phones?.firstOrNull()?.formatted?.let { phone ->
                        makePhoneCall(phone)
                    }
                }
            } else {
                binding.contactsPhone.isVisible = false
            }
        }
    }

    private fun bindSkills(skills: List<String>) {
        binding.keySkills.isVisible = skills.isNotEmpty()
        if (skills.isNotEmpty()) {
            binding.keySkillsDescriptions.text = skills.joinToString("\n• ", "• ")
        }
    }

    private fun formatSalary(salary: Salary?): String {
        return when {
            salary == null -> getString(R.string.text_salary_not_specified)

            salary.from != null && salary.to != null -> {
                val formattedFrom = formatSalary(salary.from)
                val formattedTo = formatSalary(salary.to)
                val currency = CurrencyFormatter.getCurrencySymbol(requireContext(), salary.currency)
                getString(R.string.text_salary_from_to, formattedFrom, formattedTo, currency)
            }

            salary.from != null -> {
                val formattedFrom = formatSalary(salary.from)
                val currency = CurrencyFormatter.getCurrencySymbol(requireContext(), salary.currency)
                getString(R.string.text_salary_from, formattedFrom, currency)
            }

            salary.to != null -> {
                val formattedTo = formatSalary(salary.to)
                val currency = CurrencyFormatter.getCurrencySymbol(requireContext(), salary.currency)
                getString(R.string.text_salary_to, formattedTo, currency)
            }

            else -> getString(R.string.text_salary_not_specified)
        }
    }

    private fun loadCompanyLogo(logoUrl: String?) {
        if (!logoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(logoUrl)
                .placeholder(R.drawable.ic_placeholder_32px)
                .into(binding.companyLogo)
        } else {
            binding.companyLogo.setImageResource(R.drawable.ic_placeholder_32px)
        }
    }

    private fun shareVacancy() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, viewModel.vacancyState.value?.data?.url ?: "")
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_vacancy)))
    }

    private fun sendEmail(email: String?) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.send_email)))
    }

    private fun makePhoneCall(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.searchProgressBar.isVisible = isLoading
        binding.scrollView.isVisible = !isLoading
    }

    private fun showVacancyError() {
        binding.searchProgressBar.isVisible = false
        binding.scrollView.isVisible = false
        binding.holderErrorServer.isVisible = false
        binding.holderErrorVacancy.isVisible = true
        binding.vacancyToolbar.menu.findItem(R.id.share_button).isVisible = false
        binding.vacancyToolbar.menu.findItem(R.id.filter_button).isVisible = false
    }
    private fun showServerError() {
        binding.searchProgressBar.isVisible = false
        binding.scrollView.isVisible = false
        binding.holderErrorServer.isVisible = true
        binding.holderErrorVacancy.isVisible = false
        binding.vacancyToolbar.menu.findItem(R.id.share_button).isVisible = false
        binding.vacancyToolbar.menu.findItem(R.id.filter_button).isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
