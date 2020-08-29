package br.com.jiratorio.usecase.issue.period

import br.com.jiratorio.property.JiraProperties
import br.com.jiratorio.stereotype.UseCase
import br.com.jiratorio.domain.chart.IssuePeriodChartResponse
import br.com.jiratorio.domain.entity.Board
import br.com.jiratorio.domain.entity.IssuePeriod
import br.com.jiratorio.domain.entity.embedded.Chart
import br.com.jiratorio.domain.response.issueperiod.IssuePeriodListResponse
import br.com.jiratorio.exception.ResourceNotFound
import br.com.jiratorio.extension.decimal.format
import br.com.jiratorio.mapper.toIssuePeriodResponse
import br.com.jiratorio.repository.BoardRepository
import br.com.jiratorio.repository.IssuePeriodRepository
import br.com.jiratorio.usecase.chart.issue.period.CreateIssueTypePerformanceCompareChart
import br.com.jiratorio.usecase.chart.issue.period.CreateLeadTimeCompareChartByPeriod
import br.com.jiratorio.usecase.chart.issue.period.CreateThroughputByEstimateChart
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@UseCase
class FindAllIssuePeriods(
    private val boardRepository: BoardRepository,
    private val issuePeriodRepository: IssuePeriodRepository,
    private val jiraProperties: JiraProperties,
    private val createLeadTimeCompareChartByPeriod: CreateLeadTimeCompareChartByPeriod,
    private val createThroughputByEstimateChart: CreateThroughputByEstimateChart,
    private val createIssueTypePerformanceCompareChart: CreateIssueTypePerformanceCompareChart
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun execute(boardId: Long, startDate: LocalDate, endDate: LocalDate): IssuePeriodListResponse {
        log.info("Action=findAllIssuePeriods, boardId={}, startDate={}, endDate={}", boardId, startDate, endDate)

        val board = boardRepository.findByIdOrNull(boardId) ?: throw ResourceNotFound()

        val issuePeriods = issuePeriodRepository.findAll(boardId, startDate, endDate)

        return IssuePeriodListResponse(
            periods = issuePeriods.toIssuePeriodResponse(jiraProperties.url),
            charts = buildCharts(issuePeriods, board)
        )
    }

    private fun buildCharts(issuePeriods: List<IssuePeriod>, board: Board): IssuePeriodChartResponse {
        log.info("Method=buildCharts, issuePeriods={}, board={}", issuePeriods, board)

        val leadTime: Chart<String, String> = Chart()
        val throughput: Chart<String, Int> = Chart()

        for (issuePeriod in issuePeriods) {
            leadTime[issuePeriod.name] = issuePeriod.leadTime.format()
            throughput[issuePeriod.name] = issuePeriod.throughput
        }

        return IssuePeriodChartResponse(
            leadTime = leadTime,
            throughput = throughput,
            leadTimeCompareChart = createLeadTimeCompareChartByPeriod.execute(issuePeriods, board),
            throughputByEstimate = createThroughputByEstimateChart.execute(issuePeriods),
            issueTypePerformanceCompareChart = createIssueTypePerformanceCompareChart.execute(issuePeriods)
        )
    }

}