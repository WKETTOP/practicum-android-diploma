package ru.practicum.android.diploma.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentMainBinding
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.presentation.model.VacancySeatchUiState

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()

    private lateinit var vacancyAdapter: VacancyAdapter

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
        vacancyAdapter = VacancyAdapter { vacancy ->
            onVacancyClick(vacancy)
        }

        binding.searchResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vacancyAdapter
        }
    }

    private fun setupSearchField() {
        binding.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
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
    }

    private fun handleUiState(state: VacancySeatchUiState) {
        when (state) {
            is VacancySeatchUiState.Idle -> {
                showIdle()
            }
            is VacancySeatchUiState.Loading -> {
                showLoading()
            }
            is VacancySeatchUiState.Content -> {
                showContent(state)
            }
            is VacancySeatchUiState.Emty -> {
                showEmpty()
            }
            is VacancySeatchUiState.Error -> {
                showError(state.message)
            }
        }
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

    private fun showContent(state: VacancySeatchUiState.Content) {
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

    private fun showEmpty() {}

    private fun showError(message: String?) {}

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

    private fun onVacancyClick(vacancy: VacancyDetail) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
