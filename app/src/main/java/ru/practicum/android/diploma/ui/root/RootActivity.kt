package ru.practicum.android.diploma.ui.root

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private var _binding: ActivityRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.rootFragmentContainerView.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.root_fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.vacancyFragment, R.id.settingsFilterFragment2, R.id.choosingIndustryFragment -> {
                    binding.bottomNavView.isGone = true
                    binding.elevation.isGone = true
                }
                else -> {
                    if (binding.bottomNavView.isGone) {
                        binding.root.postDelayed({
                            binding.bottomNavView.isGone = false
                            binding.elevation.isGone = false
                        }, BOTTOM_NAV_VIEW_GONE_DELAY)
                    } else {
                        binding.bottomNavView.isGone = false
                        binding.elevation.isGone = false
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, null)
        _binding = null
    }

    companion object {
        private const val BOTTOM_NAV_VIEW_GONE_DELAY = 200L
    }

}
