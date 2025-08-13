package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.network.models.ApiAddress
import ru.practicum.android.diploma.data.network.models.ApiContacts
import ru.practicum.android.diploma.data.network.models.ApiEmployer
import ru.practicum.android.diploma.data.network.models.ApiEmployment
import ru.practicum.android.diploma.data.network.models.ApiExperience
import ru.practicum.android.diploma.data.network.models.ApiFilterArea
import ru.practicum.android.diploma.data.network.models.ApiFilterIndustry
import ru.practicum.android.diploma.data.network.models.ApiSalary
import ru.practicum.android.diploma.data.network.models.ApiSchedule
import ru.practicum.android.diploma.data.network.models.ApiVacancyDetail
import ru.practicum.android.diploma.data.network.models.ApiVacancySearchResponse
import ru.practicum.android.diploma.domain.models.Address
import ru.practicum.android.diploma.domain.models.Contacts
import ru.practicum.android.diploma.domain.models.Employer
import ru.practicum.android.diploma.domain.models.Employment
import ru.practicum.android.diploma.domain.models.Experience
import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.Schedule
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse

fun ApiVacancySearchResponse.toDomain(): VacancyResponse {
    return VacancyResponse(
        found = found,
        pages = pages,
        page = page,
        vacancies = items.map { it.toDomain() }
    )
}

fun ApiVacancyDetail.toDomain(): VacancyDetail {
    return VacancyDetail(
        id = id,
        name = name,
        description = description,
        salary = salary?.toDomain(),
        address = address?.toDomain(),
        experience = experience.toDomain(),
        schedule = schedule.toDomain(),
        employment = employment.toDomain(),
        contacts = contacts?.toDomain(),
        employer = employer.toDomain(),
        area = area.toDomain(),
        skills = skills,
        url = url,
        industry = industry.toDomain()
    )
}

fun ApiSalary.toDomain(): Salary {
    return Salary(
        from = from,
        to = to,
        currency = currency
    )
}

fun ApiAddress.toDomain(): Address {
    return Address(
        city = city,
        street = street,
        building = building,
        fullAddress = fullAddress
    )
}

fun ApiExperience.toDomain(): Experience {
    return Experience(id, name)
}

fun ApiSchedule.toDomain(): Schedule {
    return Schedule(id, name)
}

fun ApiEmployment.toDomain(): Employment {
    return Employment(id, name)
}

fun ApiContacts.toDomain(): Contacts {
    return Contacts(
        id = id,
        name = name,
        email = email,
        phones = phones?.map { phone ->
            Contacts.Phone(
                comment = phone.comment,
                formatted = phone.formatted
            )
        }
    )
}

fun ApiEmployer.toDomain(): Employer {
    return Employer(id, name, logo)
}

fun ApiFilterArea.toDomain(): FilterArea {
    return FilterArea(
        id,
        name,
        parentId,
        areas.map { it.toDomain() }
    )
}

fun ApiFilterIndustry.toDomain(): FilterIndustry {
    return FilterIndustry(id, name)
}
