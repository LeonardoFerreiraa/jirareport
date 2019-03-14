package br.com.jiratorio.integration.holiday

import br.com.jiratorio.base.Authenticator
import br.com.jiratorio.base.specification.notFound
import br.com.jiratorio.domain.request.HolidayRequest
import br.com.jiratorio.dsl.restAssured
import br.com.jiratorio.exception.ResourceNotFound
import br.com.jiratorio.factory.domain.request.HolidayRequestFactory
import br.com.jiratorio.factory.entity.BoardFactory
import br.com.jiratorio.factory.entity.HolidayFactory
import br.com.jiratorio.repository.HolidayRepository
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.format.DateTimeFormatter

@Tag("integration")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class UpdateHolidayIntegrationTest @Autowired constructor(
        private val holidayFactory: HolidayFactory,
        private val holidayRequestFactory: HolidayRequestFactory,
        private val boardFactory: BoardFactory,
        private val holidayRepository: HolidayRepository,
        private val authenticator: Authenticator
) {

    @Test
    fun `update holiday`() {
        authenticator.withDefaultUser { holidayFactory.create() }
        val request = holidayRequestFactory.build()

        restAssured {
            given {
                header(authenticator.defaultUserHeader())
                contentType(ContentType.JSON)
                body(request)
            }

            on {
                put("/boards/1/holidays/1")
            }

            then {
                statusCode(HttpStatus.SC_NO_CONTENT)
            }
        }

        val holiday = holidayRepository.findById(1L)
                .orElseThrow(::ResourceNotFound)

        holiday.apply {
            assertThat(description)
                    .isEqualTo(request.description)
            assertThat(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .isEqualTo(request.date)
        }
    }

    @Test
    fun `fail in validations`() {
        authenticator.withDefaultUser { holidayFactory.create() }

        restAssured {
            given {
                header(authenticator.defaultUserHeader())
                contentType(ContentType.JSON)
                body(HolidayRequest())
            }

            on {
                put("/boards/1/holidays/1")
            }

            then {
                statusCode(HttpStatus.SC_BAD_REQUEST)
                body("errors.find { it.field == 'date' }.defaultMessage", Matchers.`is`("must not be blank"))
                body("errors.find { it.field == 'description' }.defaultMessage", Matchers.`is`("must not be blank"))
            }
        }
    }

    @Test
    fun `update holiday not found`() {
        authenticator.withDefaultUser { boardFactory.create() }
        val request = holidayRequestFactory.build()

        restAssured {
            given {
                header(authenticator.defaultUserHeader())
                contentType(ContentType.JSON)
                body(request)
            }

            on {
                put("/boards/1/holidays/999")
            }

            then {
                spec(notFound())
            }
        }
    }
}
