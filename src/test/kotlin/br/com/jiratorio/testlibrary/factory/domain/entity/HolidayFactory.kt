package br.com.jiratorio.testlibrary.factory.domain.entity

import br.com.jiratorio.domain.entity.HolidayEntity
import br.com.jiratorio.testlibrary.factory.KBacon
import br.com.jiratorio.repository.HolidayRepository
import com.github.javafaker.Faker
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class HolidayFactory(
    private val faker: Faker,
    private val boardFactory: BoardFactory,
    holidayRepository: HolidayRepository?
) : KBacon<HolidayEntity>(holidayRepository) {

    override fun builder(): HolidayEntity {
        return HolidayEntity(
            date = LocalDate.now().plusDays(faker.number().randomNumber(5, true)),
            description = faker.lorem().word(),
            board = boardFactory.create()
        )
    }

}
