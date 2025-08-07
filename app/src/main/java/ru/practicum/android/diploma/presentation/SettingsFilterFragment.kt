package ru.practicum.android.diploma.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSettingsFilterBinding

class SettingsFilterFragment : Fragment() {

    private var _binding: FragmentSettingsFilterBinding? = null
    private val binding get() = _binding!!

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

        binding.placeWorkButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFilterFragment2_to_choosingPlaceWorkFragment2)
        }

        binding.industryButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFilterFragment2_to_choosingIndustryFragment)
        }

        binding.applyButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
