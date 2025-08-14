package ru.practicum.android.diploma.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.presentation.viewholde.VacancyViewHolder

class VacancyAdapter(
    private val onClick: (VacancyDetail) -> Unit,
    private val onDataUpdated: () -> Unit
) : RecyclerView.Adapter<VacancyViewHolder>() {

    private val vacancies = mutableListOf<VacancyDetail>()

    fun updateData(newItems: List<VacancyDetail>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = vacancies.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return vacancies[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return vacancies[oldItemPosition] == newItems[newItemPosition]
            }
        })
        vacancies.clear()
        vacancies.addAll(newItems)
        diff.dispatchUpdatesTo(this)

        onDataUpdated()
    }

    fun clearData() {
        vacancies.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        return VacancyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.vacancy_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val vacancy = vacancies[position]
        holder.bind(vacancy) { clickedVacancy ->
            onClick(clickedVacancy)
        }
    }

    override fun getItemCount(): Int = vacancies.size
}
