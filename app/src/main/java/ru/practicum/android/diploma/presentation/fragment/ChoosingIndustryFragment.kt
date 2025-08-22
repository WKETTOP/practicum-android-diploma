package ru.practicum.android.diploma.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoosingIndustryBinding
import ru.practicum.android.diploma.domain.models.ErrorType
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.presentation.adapter.IndustryAdapter
import ru.practicum.android.diploma.presentation.viewmodel.ChoosingIndustryViewModel

class ChoosingIndustryFragment : Fragment() {

    private var _binding: FragmentChoosingIndustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChoosingIndustryViewModel by viewModel()

    private val adapterIndustry = IndustryAdapter { industry ->
        viewModel.selectIndustry(industry)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosingIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupUi()
    }

    private fun setupUi() {
        with(binding) {
            applyButton.setOnClickListener {
                viewModel.selectedIndustry.value?.let { industry ->
                    navigateToFilter(industry)
                }
            }

            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterIndustry
            }

            settingsFilterToolbar.setOnClickListener {
                findNavController().navigate(
                    R.id.action_choosingIndustryFragment_to_settingsFilterFragment2
                )
            }

            searchInputText.addTextChangedListener { editable ->
                if (editable.isNullOrEmpty()) {
                    searchClearButton.setImageResource(R.drawable.ic_search_24)
                    searchClearButton.isEnabled = false
                } else {
                    searchClearButton.setImageResource(R.drawable.ic_close_24)
                    searchClearButton.isEnabled = true
                }
                viewModel.setSearchQuery(editable.toString())
            }

            searchClearButton.setOnClickListener {
                searchInputText.text?.clear()
                viewModel.setSearchQuery("")
            }
        }
    }

    private fun setupObservers() {
        viewModel.industryState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> showContent(resource.data)
                is Resource.Error -> handleError(resource)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filteredIndustries.collect { list ->
                        val selectedId = viewModel.selectedIndustry.value?.id
                        adapterIndustry.submitList(list, selectedId)
                    }
                }
                launch {
                    viewModel.isSelectButtonVisible.collect { visible ->
                        binding.applyButton.isVisible = visible
                    }
                }
                launch {
                    viewModel.searchQuery.collect { query ->
                        if (binding.searchInputText.text.toString() != query) {
                            binding.searchInputText.setText(query)
                            binding.searchInputText.setSelection(query.length)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToFilter(industry: FilterIndustry) {
        val args = bundleOf(KEY_ID to industry.id, KEY_NAME to industry.name)

        findNavController().navigate(
            R.id.action_choosingIndustryFragment_to_settingsFilterFragment2,
            args
        )
    }

    private fun handleError(resource: Resource.Error<List<FilterIndustry>>) {
        with(binding) {
            progressBar.isVisible = false

            when (resource.errorType) {
                ErrorType.NO_INTERNET -> {
                    if (!resource.data.isNullOrEmpty()) {
                        showContent(resource.data)
                    } else {
                        showNoInternetError()
                    }
                }

                ErrorType.SERVER_ERROR -> showServerError()
                else -> showServerError()
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            recyclerView.isVisible = false
            placeholderNoInternet.isVisible = false
            placeholderSeverError.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showServerError() {
        with(binding) {
            recyclerView.isVisible = false
            placeholderNoInternet.isVisible = false
            placeholderSeverError.isVisible = true
            progressBar.isVisible = false
        }
    }

    private fun showNoInternetError() {
        with(binding) {
            recyclerView.isVisible = false
            placeholderNoInternet.isVisible = true
            placeholderSeverError.isVisible = false
            progressBar.isVisible = false
        }
    }

    private fun showContent(data: List<FilterIndustry>?) {
        with(binding) {
            recyclerView.isVisible = true
            placeholderNoInternet.isVisible = false
            placeholderSeverError.isVisible = false
            progressBar.isVisible = false

            if (data.isNullOrEmpty()) {
                showServerError()
            }
        }
    }

    companion object {
        const val KEY_ID = "INDUSTRY_ID"
        const val KEY_NAME = "INDUSTRY_NAME"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
