package ru.practicum.android.diploma.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentMainBinding
import ru.practicum.android.diploma.domain.models.ErrorType
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

    companion object {
        const val KEY = "VACANCY_ID"
    }

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

        updateFilterButtonState()
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
        binding.searchInputText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString() ?: ""
            viewModel.onSearchQueryChanged(query)
            updateClearButtonVisibility(query)
        }

        binding.searchClearButton.setOnClickListener {
            binding.searchInputText.setText("")
            viewModel.clearSearch()
        }
    }

    private fun updateFilterButtonState(hasFilters: Boolean = viewModel.hasActiveFilters()) {
        val menu = binding.searchToolbar.menu
        val filterItem = menu.findItem(R.id.filter_button)

        if (hasFilters) {
            filterItem.setIcon(R.drawable.ic_filter_on_24)
        } else {
            filterItem.setIcon(R.drawable.ic_filter_off_24)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
                launch {
                    viewModel.searchQuery.collect { query ->
                        if (binding.searchInputText.text.toString() != query) {
                            binding.searchInputText.setText(query)
                            binding.searchInputText.setSelection(query.length)
                        }
                        updateClearButtonVisibility(query)
                    }
                }
                launch {
                    viewModel.toastMessage.collect { errorType ->
                        errorType?.let {
                            val message = when (it) {
                                ErrorType.NO_INTERNET -> getString(R.string.error_no_internet)
                                ErrorType.SERVER_ERROR -> getString(R.string.error_server)
                                ErrorType.DATA_FORMAT_ERROR -> getString(R.string.error_data_format)
                                ErrorType.NOT_FOUND -> getString(R.string.error_vacancy_not_found)
                                ErrorType.EMPTY_RESPONSE -> getString(R.string.error_response_empty)
                                ErrorType.UNKNOWN -> getString(R.string.error_unnown)
                            }
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

                            viewModel.clearToastMessage()
                        }
                    }
                }
                launch {
                    viewModel.hasActiveFilters.collect { hasFilters ->
                        updateFilterButtonState(hasFilters)
                    }
                }
            }
        }
    }

    private fun handleUiState(state: VacancySearchUiState) {
        when (state) {
            is VacancySearchUiState.Idle -> showIdle()
            is VacancySearchUiState.Loading -> showLoading()
            is VacancySearchUiState.Content -> showContent(state)
            is VacancySearchUiState.Empty -> showEmpty()
            is VacancySearchUiState.Error -> showError(state.errorType)
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

    private fun showError(errorType: ErrorType) {
        with(binding) {
            searchProgressBar.isVisible = false
            searchResult.isVisible = false
            searchCountResult.isVisible = false
            searchImage.isVisible = true
            searchErrorText.isVisible = true

            val (imageRes, textRes) = when (errorType) {
                ErrorType.NO_INTERNET -> Pair(
                    R.drawable.no_internet_placeholder,
                    R.string.placeholder_no_internet
                )

                ErrorType.SERVER_ERROR -> Pair(
                    R.drawable.server_error_placeholder,
                    R.string.placeholder_server_error
                )

                else -> Pair(
                    R.drawable.not_find_vacancy_placeholder,
                    R.string.placeholder_unable_to_retrieve_job_listing
                )
            }

            searchImage.setImageResource(imageRes)
            searchErrorText.setText(textRes)
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
        val args = bundleOf(KEY to vacancy.id)

        findNavController().navigate(
            R.id.action_mainFragment_to_vacancyFragment,
            args
        )
    }

    override fun onResume() {
        super.onResume()
        updateFilterButtonState()

        val currentQuery = binding.searchInputText.text.toString()
        if (currentQuery.isNotEmpty() && viewModel.hasActiveFilters()) {
            viewModel.onSearchQueryChanged(currentQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
