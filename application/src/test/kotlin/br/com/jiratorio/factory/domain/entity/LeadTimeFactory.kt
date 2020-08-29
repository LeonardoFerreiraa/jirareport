package br.com.jiratorio.factory.domain.entity

import br.com.jiratorio.domain.entity.LeadTime
import br.com.jiratorio.extension.faker.jira
import br.com.jiratorio.extension.toLocalDateTime
import br.com.jiratorio.factory.KBacon
import br.com.jiratorio.repository.LeadTimeRepository
import com.github.javafaker.Faker
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class LeadTimeFactory(
    private val faker: Faker,
    private val leadTimeConfigFactory: LeadTimeConfigFactory,
    private val issueFactory: IssueFactory,
    leadTimeRepository: LeadTimeRepository?
) : KBacon<LeadTime>(leadTimeRepository) {

    override fun builder(): LeadTime {
        return LeadTime(
            leadTimeConfig = leadTimeConfigFactory.create(),
            issue = issueFactory.create(),
            leadTime = faker.jira().issueLeadTime(),
            startDate = faker.date().past(30, TimeUnit.DAYS).toLocalDateTime(),
            endDate = faker.date().past(15, TimeUnit.DAYS).toLocalDateTime()
        )
    }

}
