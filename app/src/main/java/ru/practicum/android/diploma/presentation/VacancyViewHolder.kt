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

        jobText.text = "${vacancyDetail.name}, ${vacancyDetail.area.name}"
        companyText.text = vacancyDetail.employer.name
        paydayText.text = formatSalary(vacancyDetail.salary)
    }

    private fun formatSalary(salary: Salary?): String {
        if (salary == null || (salary.from == null && salary.to == null)) {
            return itemView.context.getString(R.string.text_salary_not_specified)
        }

        val currency = getCurrencySymbol(salary.currency)
        return when {
            salary.from != null && salary.to != null -> {
                itemView.context.getString(R.string.text_salary_from_to, salary.from, salary.to, currency)
            }

            salary.from != null -> {
                itemView.context.getString(R.string.text_salary_from, salary.from, currency)
            }

            else -> {
                itemView.context.getString(R.string.text_salary_to, salary.to, currency)
            }
        }
    }

    private fun getCurrencySymbol(currency: String): String {
        return when (currency) {
            "RUB" -> itemView.context.getString(R.string.currency_rub)
            "USD" -> itemView.context.getString(R.string.currcurrency_usd)
            "EUR" -> itemView.context.getString(R.string.currency_eur)
            "HKD" -> itemView.context.getString(R.string.currency_hkd)
            "AUD" -> itemView.context.getString(R.string.currency_aud)
            "GBP" -> itemView.context.getString(R.string.currency_gbp)
            "SGD" -> itemView.context.getString(R.string.currency_sgd)
            else -> currency
        }
    }

}
