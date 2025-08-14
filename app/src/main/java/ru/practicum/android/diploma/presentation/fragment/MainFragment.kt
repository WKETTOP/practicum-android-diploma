package ru.practicum.android.diploma.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentMainBinding
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.presentation.adapter.VacancyAdapter
import ru.practicum.android.diploma.presentation.model.VacancySearchUiState
import ru.practicum.android.diploma.presentation.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()

    private val vacancyAdapter = VacancyAdapter(
        onClick = { vacancy ->
            navigateToVacancyDetail(vacancy)
        },
        onDataUpdated = {
            binding.paginationProgressBar.isVisible = false
        }
    )

    private var isProgrammaticChange = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupSearchField()
        observeViewModel()

        viewModel.loadInitialDataIfNeeded()
    }

    private fun setupUI() {
        binding.searchToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_button -> {
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFilterFragment2)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.searchResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vacancyAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lm = recyclerView.layoutManager as LinearLayoutManager
                    if (lm.findLastVisibleItemPosition() == vacancyAdapter.itemCount - 1) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupSearchField() {
        binding.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Не используется
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (isProgrammaticChange) return

                val query = s?.toString() ?: ""
                viewModel.onSearchQueryChanged(query)
                updateClearButtonVisibility(query)
            }
        })

        binding.searchClearButton.setOnClickListener {
            binding.searchInputText.setText("")
            viewModel.clearSearch()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                handleUiState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.searchQuery.collect { query ->
                if (binding.searchInputText.text.toString() != query) {
                    binding.searchInputText.setText(query)
                    binding.searchInputText.setSelection(query.length)
                }
                updateClearButtonVisibility(query)
            }
        }

        lifecycleScope.launch {
            viewModel.toastMessage.collect { msg ->
                msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun handleUiState(state: VacancySearchUiState) {
        when (state) {
            is VacancySearchUiState.Idle -> showIdle()
            is VacancySearchUiState.Loading -> showLoading()
            is VacancySearchUiState.Content -> showContent(state)
            is VacancySearchUiState.Empty -> showEmpty()
            is VacancySearchUiState.Error -> showError(state.message)
            is VacancySearchUiState.PaginationLoading -> showPaginationLoading(state)
        }
    }

    private fun showPaginationLoading(state: VacancySearchUiState.PaginationLoading) {
        showContent(VacancySearchUiState.Content(state.currentData))
        binding.paginationProgressBar.isVisible = true
    }

    private fun showIdle() {
        with(binding) {
            searchProgressBar.isVisible = false
            searchResult.isVisible = false
            searchCountResult.isVisible = false
            searchImage.isVisible = true
            searchErrorText.isVisible = false

            searchImage.setImageResource(R.drawable.search_placeholder)
        }

        vacancyAdapter.clearData()
    }

    private fun showLoading() {
        with(binding) {
            searchProgressBar.isVisible = true
            searchResult.isVisible = false
            searchCountResult.isVisible = false
            searchImage.isVisible = false
            searchErrorText.isVisible = false
        }
    }

    private fun showContent(state: VacancySearchUiState.Content) {
        with(binding) {
            searchProgressBar.isVisible = false
            searchResult.isVisible = true
            searchCountResult.isVisible = true
            searchImage.isVisible = false
            searchErrorText.isVisible = false

            val foundCount = state.data.found
            searchCountResult.text = resources.getQuantityString(
                R.plurals.text_found_vacancies,
                foundCount,
                foundCount
            )
        }

        vacancyAdapter.updateData(state.data.vacancies)
    }

    private fun showEmpty() {
        with(binding) {
            searchProgressBar.isVisible = false
            searchResult.isVisible = false
            searchCountResult.isVisible = true
            searchImage.isVisible = true
            searchErrorText.isVisible = true

            searchCountResult.text = getString(R.string.text_no_such_vacancy)
            searchImage.setImageResource(R.drawable.not_find_vacancy_placeholder)
            searchErrorText.text = getString(R.string.placeholder_unable_to_retrieve_job_listing)
        }
    }

    private fun showError(message: String?) {
        with(binding) {
            searchProgressBar.isVisible = false
            searchResult.isVisible = false
            searchCountResult.isVisible = false
            searchImage.isVisible = true
            searchErrorText.isVisible = true

            when (message) {
                "Нет подключения к интернету" -> {
                    searchImage.setImageResource(R.drawable.no_internet_placeholder)
                    searchErrorText.text = getString(R.string.placeholder_no_internet)
                }
                "Ошибка сервера" -> {
                    searchImage.setImageResource(R.drawable.server_error_placeholder)
                    searchErrorText.text = getString(R.string.placeholder_server_error)
                }
                else -> {
                    searchImage.setImageResource(R.drawable.not_find_vacancy_placeholder)
                    searchErrorText.text = getString(R.string.placeholder_unable_to_retrieve_job_listing)
                }
            }
        }
    }

    private fun updateClearButtonVisibility(query: String) {
        binding.searchClearButton.isVisible = true

        if (query.isEmpty()) {
            binding.searchClearButton.setImageResource(R.drawable.ic_search_24)
            binding.searchClearButton.isEnabled = false
        } else {
            binding.searchClearButton.setImageResource(R.drawable.ic_close_24)
            binding.searchClearButton.isEnabled = true
        }
    }
    private fun navigateToVacancyDetail(vacancy: VacancyDetail) {
        val args = bundleOf("VACANCY_ID" to vacancy.id)

        findNavController().navigate(
            R.id.action_mainFragment_to_vacancyFragment,
            args
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
