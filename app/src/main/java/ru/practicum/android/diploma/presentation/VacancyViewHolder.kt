package ru.practicum.android.diploma.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.util.CurrencyFormatter
import ru.practicum.android.diploma.util.NumberFormatter

class VacancyViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val companyLogo: ImageView = itemView.findViewById(R.id.company_logo)
    private val jobText: TextView = itemView.findViewById(R.id.job_vacancies_text)
    private val companyText: TextView = itemView.findViewById(R.id.company_vacancies_text)
    private val paydayText: TextView = itemView.findViewById(R.id.payday_vacancies_text)

    fun bind(vacancyDetail: VacancyDetail, onClick: (VacancyDetail) -> Unit) {
        Glide.with(itemView)
            .load(vacancyDetail.employer.logo)
            .placeholder(R.drawable.ic_placeholder_32px)
            .error(R.drawable.ic_placeholder_32px)
            .centerCrop()
            .transform(
                RoundedCorners(
                    itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius_12)
                )
            )
            .into(companyLogo)

        jobText.text = itemView.context.getString(
            R.string.text_job_location_format,
            vacancyDetail.name,
            vacancyDetail.area.name
        )
        companyText.text = vacancyDetail.employer.name
        paydayText.text = formatSalary(vacancyDetail.salary)
    }

    private fun formatSalary(salary: Salary?): String {
        if (salary == null || salary.from == null && salary.to == null) {
            return itemView.context.getString(R.string.text_salary_not_specified)
        }

        val currency = CurrencyFormatter.getCurrencySymbol(itemView.context, salary.currency)
        return when {
            salary.from != null && salary.to != null -> {
                val from = NumberFormatter.formatSalary(salary.from)
                val to = NumberFormatter.formatSalary(salary.to)
                itemView.context.getString(R.string.text_salary_from_to, from, to, currency)
            }

            salary.from != null -> {
                val from = NumberFormatter.formatSalary(salary.from)
                itemView.context.getString(R.string.text_salary_from, from, currency)
            }

            else -> {
                val to = NumberFormatter.formatSalary(salary.to)
                itemView.context.getString(R.string.text_salary_to, to, currency)
            }
        }
    }
}
