package br.com.jiratorio.usecase.project

import br.com.jiratorio.client.ProjectClient
import br.com.jiratorio.config.stereotype.UseCase
import br.com.jiratorio.domain.jira.JiraProjectDetails
import br.com.jiratorio.exception.ResourceNotFound
import org.slf4j.LoggerFactory

@UseCase
class FindJiraProjectDetails(
    private val projectClient: ProjectClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun execute(projectId: Long): JiraProjectDetails {
        log.info("Action=findJiraProjectDetails, projectId={}", projectId)

        return projectClient.findById(projectId)
            .orElseThrow { ResourceNotFound() }
    }

}
