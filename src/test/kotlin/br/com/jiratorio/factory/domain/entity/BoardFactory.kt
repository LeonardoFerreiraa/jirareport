package br.com.jiratorio.factory.domain.entity

import br.com.jiratorio.domain.duedate.DueDateType
import br.com.jiratorio.domain.entity.Board
import br.com.jiratorio.domain.impediment.ImpedimentType
import br.com.jiratorio.extension.faker.jira
import br.com.jiratorio.factory.KBacon
import br.com.jiratorio.repository.BoardRepository
import com.github.javafaker.Faker
import org.springframework.stereotype.Component

@Component
class BoardFactory(
    private val faker: Faker,
    boardRepository: BoardRepository?
) : KBacon<Board>(boardRepository) {

    override fun builder(): Board {
        return Board(
            externalId = faker.number().randomNumber(5, true),
            name = faker.lorem().word()
        )
    }

    fun fullBoardBuilder(): Board {
        return builder().apply {
            startColumn = "TODO"
            endColumn = "DONE"
            fluxColumn = mutableListOf("TODO", "WIP", "DONE")
            ignoreIssueType = mutableListOf("IT_1")
            epicCF = faker.jira().customField()
            estimateCF = faker.jira().customField()
            systemCF = faker.jira().customField()
            projectCF = faker.jira().customField()
            ignoreWeekend = false
            impedimentType = ImpedimentType.COLUMN
            impedimentColumns = mutableListOf("IMP_COLUMN1", "IMP_COLUMN2", "IMP_COLUMN3")
            touchingColumns = faker.lorem().words()
            waitingColumns = faker.lorem().words()
            dueDateCF = faker.jira().customField()
            dueDateType = DueDateType.FIRST_DUE_DATE_AND_END_DATE
        }
    }

    fun withBasicConfigurationBuilder(): Board {
        return builder().apply {
            startColumn = "TODO"
            endColumn = "DONE"
            fluxColumn = mutableListOf("BACKLOG", "TODO", "WIP", "ACCOMPANIMENT", "DONE")
        }
    }

    fun withCompleteConfigurationBuilder(): Board {
        return builder().apply {
            startColumn = "ANALYSIS"
            endColumn = "DONE"
            fluxColumn = mutableListOf(
                "BACKLOG", "ANALYSIS", "DEV WIP", "DEV DONE", "TEST WIP", "TEST DONE", "REVIEW", "ACCOMPANIMENT", "DONE"
            )
            ignoreIssueType = mutableListOf("SubTask")
            epicCF = "customfield_1000"
            estimateCF = "customfield_2000"
            systemCF = "customfield_3000"
            projectCF = "customfield_4000"
            ignoreWeekend = false
            impedimentType = ImpedimentType.FLAG
            impedimentColumns = mutableListOf()
            touchingColumns = mutableListOf(
                "ANALYSIS", "DEV WIP", "TEST WIP", "REVIEW", "ACCOMPANIMENT"
            )
            waitingColumns = mutableListOf(
                "BACKLOG", "DEV DONE", "TEST DONE"
            )
            dueDateCF = "duedate"
            dueDateType = DueDateType.FIRST_DUE_DATE_AND_END_DATE
        }
    }
}
