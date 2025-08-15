package ru.practicum.android.diploma.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSettingsFilterBinding
import ru.practicum.android.diploma.presentation.model.FilterUiState
import ru.practicum.android.diploma.presentation.viewmodel.FilterSettingsViewModel

class FilterSettingsFragment : Fragment() {

    private var _binding: FragmentSettingsFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterSettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.settingsFilterToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.placeWorkButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFilterFragment2_to_choosingPlaceWorkFragment2)
        }

        binding.industryButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFilterFragment2_to_choosingIndustryFragment)
        }

        binding.checkOnlyWithSalary.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onOnlyWithSalaryChanged(isChecked)
        }

        binding.applyButton.setOnClickListener {
            viewModel.onApplyClicked()
        }

        binding.resetButton.setOnClickListener {
            viewModel.onResetClicked()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                event?.let {
                    handleNavigationEvent(it)
                    viewModel.onNavigationEventHandled()
                }
            }
        }
    }

    private fun updateUI(state: FilterUiState) {
        with(binding) {
            if (state.selectedIndustry != null) {
                selectedIndustryText.text = state.selectedIndustry.name
                selectedIndustryText.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
            } else {
                selectedIndustryText.text = getString(R.string.hint_industry)
                selectedIndustryText.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.gray)
                )
            }

            checkOnlyWithSalary.isChecked = state.onlyWithSalary

            val hasActiveFilters = state.onlyWithSalary || state.selectedIndustry != null

            applyButton.isVisible = hasActiveFilters
            resetButton.isVisible = hasActiveFilters
        }
    }

    private fun handleNavigationEvent(event: FilterUiState.NavigationEvent) {
        when (event) {
            is FilterUiState.NavigationEvent.NavigationBack -> {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
