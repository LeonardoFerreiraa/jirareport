package br.com.jiratorio.extension.time

import java.time.LocalDate

class LocalDateProgression(
    override val start: LocalDate,
    override val endInclusive: LocalDate
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> =
        LocalDateProgressionIterator(start, endInclusive)

}

private class LocalDateProgressionIterator(
    private val startDate: LocalDate,
    private val endDate: LocalDate,
    private var current: LocalDate = startDate
) : Iterator<LocalDate> {

    override fun hasNext(): Boolean =
        current <= endDate

    override fun next(): LocalDate {
        val next = current
        current = current.plusDays(1)
        return next
    }
}

operator fun LocalDate.rangeTo(endInclusive: LocalDate): LocalDateProgression =
    LocalDateProgression(this, endInclusive)
