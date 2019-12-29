package br.com.jiratorio.usecase.chart.issue.type

import br.com.jiratorio.config.internationalization.MessageResolver
import br.com.jiratorio.config.stereotype.UseCase
import br.com.jiratorio.domain.entity.Issue
import br.com.jiratorio.domain.entity.embedded.Chart
import br.com.jiratorio.mapper.toChart
import org.slf4j.LoggerFactory

@UseCase
class CreateIssueTypeThroughputChart(
    private val messageResolver: MessageResolver
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute(issues: List<Issue>): Chart<String, Int> {
        log.info("Method=execute, issues={}", issues)

        return issues
            .groupingBy { it.issueType ?: messageResolver("uninformed") }
            .eachCount()
            .toChart()
    }

}
