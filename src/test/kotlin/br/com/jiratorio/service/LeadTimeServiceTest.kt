package br.com.jiratorio.service

import br.com.jiratorio.testlibrary.assertion.LeadTimeAssert
import br.com.jiratorio.domain.entity.BoardEntity
import br.com.jiratorio.domain.entity.ColumnChangelogEntity
import br.com.jiratorio.domain.entity.IssueEntity
import br.com.jiratorio.domain.entity.LeadTimeConfigEntity
import br.com.jiratorio.exception.ResourceNotFound
import br.com.jiratorio.extension.toLocalDate
import br.com.jiratorio.testlibrary.extension.toLocalDateTime
import br.com.jiratorio.testlibrary.junit.testtype.UnitTest
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
import org.junit.jupiter.api.Test
import java.time.LocalDate

@UnitTest
class LeadTimeServiceTest {

    private val leadTimeConfigService = mockk<LeadTimeConfigService>()

    private val leadTimeRepository = mockk<LeadTimeRepository>()

    private val leadTimeService = LeadTimeService(
        leadTimeConfigService,
        leadTimeRepository
    )

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test create lead times without lead times config`() {
        every {
            leadTimeConfigService.findByBoard(1L)
        } returns emptyList()

        val board = defaultBoard()
        val issue = defaultIssue(board)
        val holidays = commonHolidays()

        leadTimeService.create(issue, board, holidays)

        verifyAll {
            leadTimeConfigService.findByBoard(1L)
            leadTimeRepository wasNot called
        }
    }

    @Test
    fun `test create lead times`() {
        val board = defaultBoard()

        every {
            leadTimeConfigService.findByBoard(1L)
        } returns defaultLeadTimes(board)

        every {
            leadTimeRepository.deleteByIssueId(any())
        } just runs

        every {
            leadTimeRepository.save(any())
        } answers { firstArg() }

        val issue = defaultIssue(board)

        val holidays = commonHolidays()

        leadTimeService.create(issue, board, holidays)

        verifyAll {
            leadTimeConfigService.findByBoard(1L)

            leadTimeRepository.deleteByIssueId(1L)
            leadTimeRepository.save(any())
        }

        verify(exactly = 3) {
            leadTimeRepository.save(any())
        }

        val leadTimes = issue.leadTimes ?: throw ResourceNotFound()

        leadTimes.find { it.leadTimeConfig.name == "Development Lead Time" }
            .let(LeadTimeAssert::assertThat)
            .isNotNull
            .hasLeadTime(10)
            .hasStartDate("10/01/2019 12:00".toLocalDateTime())
            .hasEndDate("23/01/2019 12:00".toLocalDateTime())

        leadTimes.find { it.leadTimeConfig.name == "Test Lead Time" }
            .let(LeadTimeAssert::assertThat)
            .isNotNull
            .hasLeadTime(8)
            .hasStartDate("23/01/2019 12:00".toLocalDateTime())
            .hasEndDate("02/02/2019 12:00".toLocalDateTime())

        leadTimes.find { it.leadTimeConfig.name == "Delivery Lead Time" }
            .let(LeadTimeAssert::assertThat)
            .isNotNull
            .hasLeadTime(7)
            .hasStartDate("02/02/2019 12:00".toLocalDateTime())
            .hasEndDate("12/02/2019 12:00".toLocalDateTime())
    }

    private fun defaultIssue(board: BoardEntity): IssueEntity {
        return IssueEntity(
            id = 1L,
            key = "JIRAT-1",
            board = board,
            columnChangelog = setOf(
                ColumnChangelogEntity(
                    from = null,
                    to = "BACKLOG",
                    startDate = "01/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "BACKLOG",
                    to = "ANALYSIS",
                    startDate = "10/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "ANALYSIS",
                    to = "DEV WIP",
                    startDate = "12/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "DEV WIP",
                    to = "DEV DONE",
                    startDate = "21/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "DEV DONE",
                    to = "TEST WIP",
                    startDate = "23/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "TEST WIP",
                    to = "TEST DONE",
                    startDate = "31/01/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "TEST DONE",
                    to = "REVIEW",
                    startDate = "01/02/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "REVIEW",
                    to = "DELIVERY LINE",
                    startDate = "02/02/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "DELIVERY LINE",
                    to = "ACCOMPANIMENT",
                    startDate = "06/02/2019 12:00".toLocalDateTime()
                ),
                ColumnChangelogEntity(
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

    private fun defaultLeadTimes(board: BoardEntity): List<LeadTimeConfigEntity> {
        return listOf(
            LeadTimeConfigEntity(
                board = board,
                name = "Development Lead Time",
                startColumn = "ANALYSIS",
                endColumn = "TEST WIP"
            ),
            LeadTimeConfigEntity(
                board = board,
                name = "Test Lead Time",
                startColumn = "TEST WIP",
                endColumn = "DELIVERY LINE"
            ),
            LeadTimeConfigEntity(
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

    private fun defaultBoard(): BoardEntity {
        return BoardEntity(
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
