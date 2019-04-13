package br.com.jiratorio.service.impl

import br.com.jiratorio.domain.jira.changelog.JiraChangelogItem
import br.com.jiratorio.domain.entity.embedded.Changelog
import br.com.jiratorio.extension.log
import br.com.jiratorio.extension.time.daysDiff
import br.com.jiratorio.service.ChangelogService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ChangelogServiceImpl : ChangelogService {

    override fun parseChangelog(
        changelogItems: List<JiraChangelogItem>,
        holidays: List<LocalDate>,
        ignoreWeekend: Boolean?
    ): List<Changelog> {
        log.info(
            "Method=parseChangelog, changelogItems={}, holidays={}, ignoreWeekend={}",
            changelogItems, holidays, ignoreWeekend
        )

        val changelog = changelogItems
            .filter { it.field == "status" && it.created != null }
            .map { Changelog(from = it.fromString, to = it.toString, created = it.created!!) }
            .sortedBy { it.created }

        changelog.forEachIndexed { i, current ->
            if (i + 1 != changelog.size) {
                val next = changelog[i + 1]
                current.leadTime = current.created.daysDiff(next.created, holidays, ignoreWeekend)
                current.endDate = next.created
            }
        }

        return changelog
    }

}
