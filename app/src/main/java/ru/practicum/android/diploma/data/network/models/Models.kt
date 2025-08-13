package ru.practicum.android.diploma.data.network.models

import com.google.gson.annotations.SerializedName

data class ApiVacancySearchResponse(
    @SerializedName("found") val found: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("items") val items: List<ApiVacancyDetail>
)

data class ApiVacancyDetail(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("salary") val salary: ApiSalary?,
    @SerializedName("address") val address: ApiAddress?,
    @SerializedName("experience") val experience: ApiExperience,
    @SerializedName("schedule") val schedule: ApiSchedule,
    @SerializedName("employment") val employment: ApiEmployment,
    @SerializedName("contacts") val contacts: ApiContacts?,
    @SerializedName("employer") val employer: ApiEmployer,
    @SerializedName("area") val area: ApiFilterArea,
    @SerializedName("skills") val skills: List<String>,
    @SerializedName("url") val url: String,
    @SerializedName("industry") val industry: ApiFilterIndustry
)

data class ApiSalary(
    @SerializedName("from") val from: Int?,
    @SerializedName("to") val to: Int?,
    @SerializedName("currency") val currency: String
)

data class ApiAddress(
    @SerializedName("city") val city: String,
    @SerializedName("street") val street: String?,
    @SerializedName("building") val building: String?,
    @SerializedName("fullAddress") val fullAddress: String?
)

data class ApiExperience(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class ApiSchedule(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class ApiEmployment(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class ApiContacts(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phones") val phones: List<ApiPhone>?
) {
    data class ApiPhone(
        @SerializedName("comment") val comment: String?,
        @SerializedName("formatted") val formatted: String
    )
}

data class ApiEmployer(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("logo") val logo: String?
)

data class ApiFilterArea(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("parentId") val parentId: Int?,
    @SerializedName("areas") val areas: List<ApiFilterArea>
)

data class ApiFilterIndustry(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
