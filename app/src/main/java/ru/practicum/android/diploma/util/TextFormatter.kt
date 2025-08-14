package ru.practicum.android.diploma.util

import android.text.Spanned
import androidx.core.text.HtmlCompat

object TextFormatter {
    fun formatToHtml(input: String): Spanned {
        val html = StringBuilder()
        val lines = input.lines().map { it.trim() }.filter { it.isNotEmpty() }
        var previousWasHeader = false
        val headerKeywords = listOf("О нас", "Задачи", "Для нас крайне важно")

        for (line in lines) {
            when {
                line.endsWith(":") || headerKeywords.any { line.equals(it, ignoreCase = true) } -> {
                    if (html.isNotEmpty()) {
                        html.append("<br>\n")
                    }
                    html.append("<b>$line</b><br>\n")
                    previousWasHeader = true
                }
                previousWasHeader -> {
                    html.append("<p>  • ${line.removePrefix("-").trim()}</p>\n")
                }
                else -> {
                    html.append("$line<br>\n")
                    previousWasHeader = false
                }
            }
        }
        return HtmlCompat.fromHtml(html.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}
