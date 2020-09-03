package br.com.jiratorio.usecase.leadtime

import br.com.jiratorio.assertion.assertThat
import br.com.jiratorio.junit.testtype.UnitTest
import br.com.jiratorio.domain.entity.Board
import br.com.jiratorio.domain.entity.ColumnChangelog
import br.com.jiratorio.domain.entity.Issue
import br.com.jiratorio.domain.entity.LeadTime
import br.com.jiratorio.domain.entity.LeadTimeConfig
import br.com.jiratorio.exception.ResourceNotFound
import br.com.jiratorio.extension.toLocalDate
import br.com.jiratorio.extension.toLocalDateTime
import br.com.jiratorio.repository.LeadTimeConfigRepository
import br.com.jiratorio.repository.LeadTimeRepository
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

@UnitTest
internal class CreateLeadTimeTest {

    private val leadTimeConfigRepository = mockk<LeadTimeConfigRepository>()

    private val leadTimeRepository = mockk<LeadTimeRepository>()

    private val createLeadTime = CreateLeadTime(
        leadTimeConfigRepository,
        leadTimeRepository
    )

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test create lead times without lead times config`() {
        every {
            leadTimeConfigRepository.findByBoardId(1L)
        } returns emptyList()

        val board = defaultBoard()
        val issue = defaultIssue(board)
        val holidays = commonHolidays()

        createLeadTime.execute(issue, board, holidays)

        verifyAll {
            leadTimeConfigRepository.findByBoardId(1L)
            leadTimeRepository wasNot called
        }
    }

    @Test
    fun `test create lead times`() {
        val board = defaultBoard()

        every {
            leadTimeConfigRepository.findByBoardId(1L)
        } returns defaultLeadTimes(board)

        every {
            leadTimeRepository.deleteByIssueId(any())
        } just runs

        every {
            leadTimeRepository.save(any<LeadTime>())
        } answers { firstArg() }

        val issue = defaultIssue(board)

        val holidays = commonHolidays()

        createLeadTime.execute(issue, board, holidays)

        verifyAll {
            leadTimeConfigRepository.findByBoardId(1L)

            leadTimeRepository.deleteByIssueId(1L)
            leadTimeRepository.save(any<LeadTime>())
        }

        verify(exactly = 3) {
            leadTimeRepository.save(any<LeadTime>())
        }

        val leadTimes = issue.leadTimes ?: throw ResourceNotFound()

        leadTimes.find {
            it.leadTimeConfig.name == "Development Lead Time"
        }?.assertThat {
            hasLeadTime(10)
            hasStartDate("10/01/2019 12:00".toLocalDateTime())
            hasEndDate("23/01/2019 12:00".toLocalDateTime())
        } ?: Assertions.fail("Development Lead Time not found")

        leadTimes.find {
            it.leadTimeConfig.name == "Test Lead Time"
        }?.assertThat {
            hasLeadTime(8)
            hasStartDate("23/01/2019 12:00".toLocalDateTime())
            hasEndDate("02/02/2019 12:00".toLocalDateTime())
        } ?: Assertions.fail("Test Lead Time not found")

        leadTimes.find {
            it.leadTimeConfig.name == "Delivery Lead Time"
        }?.assertThat {
            hasLeadTime(7)
            hasStartDate("02/02/2019 12:00".toLocalDateTime())
            hasEndDate("12/02/2019 12:00".toLocalDateTime())
        } ?: Assertions.fail("Delivery Lead Time not found")
    }

    private fun defaultIssue(board: Board): Issue {
        return Issue(
            id = 1L,
            key = "JIRAT-1",
            board = board,
            columnChangelog = setOf(
                ColumnChangelog(
                    from = null,
                    to = "BACKLOG",
                    startDate = "01/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "BACKLOG",
                    to = "ANALYSIS",
                    startDate = "10/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "ANALYSIS",
                    to = "DEV WIP",
                    startDate = "12/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "DEV WIP",
                    to = "DEV DONE",
                    startDate = "21/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "DEV DONE",
                    to = "TEST WIP",
                    startDate = "23/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "TEST WIP",
                    to = "TEST DONE",
                    startDate = "31/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "TEST DONE",
                    to = "REVIEW",
                    startDate = "01/02/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "REVIEW",
                    to = "DELIVERY LINE",
                    startDate = "02/02/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "DELIVERY LINE",
                    to = "ACCOMPANIMENT",
                    startDate = "06/02/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelog(
                    from = "ACCOMPANIMENT",
                    to = "DONE",
                    startDate = "12/02/2019 12:00".toLocalDateTime()
                )
            ),
            created = "01/01/2019 12:00".toLocalDateTime(),
            startDate = "12/01/2019 12:00".toLocalDateTime(),
            endDate = "12/02/2019 12:00".toLocalDateTime(),
            leadTime = 7L,
            summary = "JIRAT-1 summary"
        )
    }

    private fun defaultLeadTimes(board: Board): List<LeadTimeConfig> {
        return listOf(
            LeadTimeConfig(
                board = board,
                name = "Development Lead Time",
                startColumn = "ANALYSIS",
                endColumn = "TEST WIP"
            ),
            LeadTimeConfig(
                board = board,
                name = "Test Lead Time",
                startColumn = "TEST WIP",
                endColumn = "DELIVERY LINE"
            ),
            LeadTimeConfig(
                board = board,
                name = "Delivery Lead Time",
                startColumn = "DELIVERY LINE",
                endColumn = "DONE"
            )
        )
    }

    private fun commonHolidays(): List<LocalDate> {
        return listOf(
            "01/01/2019".toLocalDate(),
            "01/05/2019".toLocalDate(),
            "25/12/2019".toLocalDate()
        )
    }

    private fun defaultBoard(): Board {
        return Board(
            id = 1L,
            externalId = 123L,
            name = "My Board",
            startColumn = "ANALYSIS",
            endColumn = "ACCOMPANIMENT",
            fluxColumn = mutableListOf(
                "BACKLOG",
                "ANALYSIS",
                "DEV WIP",
                "DEV DONE",
                "TEST WIP",
                "TEST DONE",
                "REVIEW",
                "DELIVERY LINE",
                "ACCOMPANIMENT",
                "DONE"
            )
        )
    }

}