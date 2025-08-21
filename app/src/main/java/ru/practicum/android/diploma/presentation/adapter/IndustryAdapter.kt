package ru.practicum.android.diploma.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.presentation.viewholde.IndustryViewHolder

class IndustryAdapter(
    private val onClick: (FilterIndustry) -> Unit
) : RecyclerView.Adapter<IndustryViewHolder>() {

    private val industries = mutableListOf<FilterIndustry>()
    private var selectedIndustryId: Int? = null

    fun submitList(list: List<FilterIndustry>, selectedId: Int?) {
        industries.clear()
        industries.addAll(list)
        selectedIndustryId = selectedId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        return IndustryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_country_choosing, parent, false)
        )
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = industries[position]
        val isSelected = selectedIndustryId == industry.id
        holder.bind(industry, isSelected) { clickedIndustry ->
            selectedIndustryId = clickedIndustry.id
            onClick(clickedIndustry)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = industries.size
}
