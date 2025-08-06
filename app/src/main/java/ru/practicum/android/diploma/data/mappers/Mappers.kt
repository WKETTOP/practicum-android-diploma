package ru.practicum.android.diploma.data.network.models

import ru.practicum.android.diploma.domain.models.*

fun ApiVacancySearchResponse.toDomain(): VacancyResponse {
    return VacancyResponse (
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
    return Contacts(id, name, email, phone)
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
