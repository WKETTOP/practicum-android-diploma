package ru.practicum.android.diploma.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Debounce {
    private var isClickAllowed = true
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun click(delayMillis: Long = 1000L): Boolean {
        if (isClickAllowed) {
            isClickAllowed = false
            job?.cancel()
            job = coroutineScope.launch {
                delay(delayMillis)
                isClickAllowed = true
            }
            return true
        }
        return false
    }

    fun reset() {
        job?.cancel()
        isClickAllowed = true
    }
}
