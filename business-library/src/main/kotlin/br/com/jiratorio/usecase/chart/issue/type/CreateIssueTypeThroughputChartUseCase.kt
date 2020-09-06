package br.com.jiratorio.usecase.chart.issue.type

import br.com.jiratorio.domain.entity.embedded.Chart
import br.com.jiratorio.domain.issue.Issue
import br.com.jiratorio.internationalization.MessageResolver
import br.com.jiratorio.mapper.toChart
import br.com.jiratorio.stereotype.UseCase
import org.slf4j.LoggerFactory

@UseCase
class CreateIssueTypeThroughputChartUseCase(
    private val messageResolver: MessageResolver
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute(issues: List<Issue>): Chart<String, Int> {
        log.info("Action=createIssueTypeThroughputChart, issues={}", issues)

        val uninformedValue = messageResolver.resolve("uninformed")

        return issues
            .groupingBy { it.issueType ?: uninformedValue }
            .eachCount()
            .toChart()
    }

}
