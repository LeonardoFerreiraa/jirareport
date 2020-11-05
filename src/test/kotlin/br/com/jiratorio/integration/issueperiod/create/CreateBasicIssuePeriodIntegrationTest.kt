package br.com.jiratorio.integration.issueperiod.create

import br.com.jiratorio.testlibrary.Authenticator
import br.com.jiratorio.testlibrary.annotation.LoadStubs
import br.com.jiratorio.testlibrary.assertion.HistogramAssert.Companion.assertThat
import br.com.jiratorio.testlibrary.assertion.IssueAssert.Companion.assertThat
import br.com.jiratorio.testlibrary.assertion.IssuePeriodAssert.Companion.assertThat
import br.com.jiratorio.domain.entity.ColumnChangelogEntity
import br.com.jiratorio.domain.entity.ColumnTimeAverageEntity
import br.com.jiratorio.testlibrary.dsl.restAssured
import br.com.jiratorio.exception.ResourceNotFound
import br.com.jiratorio.extension.toLocalDate
import br.com.jiratorio.testlibrary.extension.toLocalDate
import br.com.jiratorio.testlibrary.extension.toLocalDateTime
import br.com.jiratorio.testlibrary.factory.domain.entity.BoardFactory
import br.com.jiratorio.testlibrary.junit.testtype.IntegrationTest
import br.com.jiratorio.repository.IssuePeriodRepository
import br.com.jiratorio.repository.IssueRepository
import io.restassured.http.ContentType
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import javax.servlet.http.HttpServletResponse

@IntegrationTest
class CreateBasicIssuePeriodIntegrationTest(
    private val boardFactory: BoardFactory,
    private val authenticator: Authenticator,
    private val issuePeriodRepository: IssuePeriodRepository,
    private val issueRepository: IssueRepository,
) {

    @Test
    @LoadStubs(["issues/basic-issues"])
    fun `create basic issue period`() {
        val board = authenticator.withDefaultUser {
            boardFactory.create(boardFactory::withBasicConfigurationBuilder)
        }

        val request = object {
            val startDate = "01/01/2019"
            val endDate = "31/01/2019"
        }

        restAssured {
            given {
                header(authenticator.defaultUserHeader())
                contentType(ContentType.JSON)
                body(request)
            }
            on {
                post("/boards/{id}/issue-periods", board.id)
            }
            then {
                statusCode(HttpServletResponse.SC_CREATED)
                header("location", containsString("/boards/1/issue-periods/1"))
            }
        }

        val issuePeriod = issuePeriodRepository.findByIdOrNull(1L)
            ?: throw ResourceNotFound()

        assertThat(issuePeriod)
            .hasStartDate(request.startDate.toLocalDate())
            .hasEndDate(request.endDate.toLocalDate())
            .hasLeadTime(14.5)
            .hasLeadTimeByEstimate("Uninformed" to 14.5)
            .hasThroughputByEstimate("Uninformed" to 2)
            .hasLeadTimeBySystem("Uninformed" to 14.5)
            .hasThroughputBySystem("Uninformed" to 2)
            .hasLeadTimeByType("Task" to 14.5)
            .hasThroughputByType("Task" to 2)
            .hasLeadTimeByProject("Uninformed" to 14.5)
            .hasThroughputByProject("Uninformed" to 2)
            .hasLeadTimeByPriority("Major" to 14.5)
            .hasThroughputByPriority("Major" to 2)
            .hasThroughput(2)
            .hasWipAvg(1.26)
            .hasAvgPctEfficiency(0.0)
            .hasEmptyDynamicCharts()
            .containsColumnTimeAvg(
                ColumnTimeAverageEntity(columnName = "BACKLOG", averageTime = 4.0),
                ColumnTimeAverageEntity(columnName = "TODO", averageTime = 2.0),
                ColumnTimeAverageEntity(columnName = "WIP", averageTime = 10.0),
                ColumnTimeAverageEntity(columnName = "ACCOMPANIMENT", averageTime = 4.0),
                ColumnTimeAverageEntity(columnName = "DONE", averageTime = 0.0)
            )
            .hasEmptyLeadTimeCompareChart()

        assertThat(issuePeriod.histogram)
            .isNotNull
            .hasMedian(11)
            .hasPercentile75(18)
            .hasPercentile90(18)
            .hasChart(
                1L to 0, 2L to 0, 3L to 0, 4L to 0, 5L to 0, 6L to 0, 7L to 0, 8L to 0, 9L to 0, 10L to 0,
                11L to 1, 12L to 0, 13L to 0, 14L to 0, 15L to 0, 16L to 0, 17L to 0, 18L to 1
            )

        val issue = issueRepository.findByIdOrNull(1L)
            ?: throw ResourceNotFound()

        assertThat(issue)
            .hasKey("JIRAT-1")
            .hasIssueType("Task")
            .hasCreator("Leonardo Ferreira")
            .hasSystem(null)
            .hasEpic(null)
            .hasSummary("Calcular diferença de data de entrega com o primeiro due date")
            .hasEstimate(null)
            .hasProject(null)
            .hasStartDate("01/01/2019 10:15".toLocalDateTime())
            .hasEndDate("15/01/2019 11:20".toLocalDateTime())
            .hasLeadTime(11)
            .hasCreated("25/12/2018 11:49:35".toLocalDateTime())
            .hasPriority("Major")
            .hasColumnChangelog(
                ColumnChangelogEntity(
                    from = null,
                    to = "BACKLOG",
                    startDate = "25/12/2018 11:49:35".toLocalDateTime(),
                    leadTime = 6,
                    endDate = "01/01/2019 10:15".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "BACKLOG",
                    to = "TODO",
                    startDate = "01/01/2019 10:15".toLocalDateTime(),
                    leadTime = 1,
                    endDate = "01/01/2019 16:30".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "TODO",
                    to = "WIP",
                    startDate = "01/01/2019 16:30".toLocalDateTime(),
                    leadTime = 8,
                    endDate = "10/01/2019 13:45".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "WIP",
                    to = "ACCOMPANIMENT",
                    startDate = "10/01/2019 13:45".toLocalDateTime(),
                    leadTime = 4,
                    endDate = "15/01/2019 11:20".toLocalDateTime()
                ),
                ColumnChangelogEntity(
                    from = "ACCOMPANIMENT",
                    to = "DONE",
                    startDate = "15/01/2019 11:20".toLocalDateTime(),
                    leadTime = 0,
                    endDate = "15/01/2019 11:20".toLocalDateTime()
                )
            )
            .hasDeviationOfEstimate(0)
            .hasDueDateHistory(emptyList())
            .hasImpedimentTime(0)
            .hasEmptyImpedimentHistory()
            .hasEmptyDynamicFields()
            .hasWaitTime(0)
            .hasTouchTime(0)
            .hasPctEfficiency(0.0)
    }
}
