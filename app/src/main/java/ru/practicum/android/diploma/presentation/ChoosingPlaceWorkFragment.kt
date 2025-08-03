package ru.practicum.android.diploma.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoosingPlaceWorkBinding

class ChoosingPlaceWorkFragment : Fragment() {

    private var _binding: FragmentChoosingPlaceWorkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosingPlaceWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countryButton.setOnClickListener {
            findNavController().navigate(R.id.action_choosingPlaceWorkFragment2_to_choosingCountryFragment)
        }

        binding.regionButton.setOnClickListener {
            findNavController().navigate(R.id.action_choosingPlaceWorkFragment2_to_choosingRegionFragment)
        }

        binding.choiceButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
