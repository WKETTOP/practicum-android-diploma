package ru.practicum.android.diploma.presentation.viewholde

import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.FilterIndustry

class IndustryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val nameIndustry: TextView = itemView.findViewById(R.id.textView)
    private val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)

    fun bind(
        industry: FilterIndustry,
        isSelected: Boolean,
        onClick: (FilterIndustry) -> Unit
    ) {
        nameIndustry.text = industry.name
        radioButton.isChecked = isSelected

        itemView.setOnClickListener { onClick(industry) }
        radioButton.setOnClickListener { onClick(industry) }
    }
}
