package br.com.jiratorio.domain.entity

import br.com.jiratorio.domain.changelog.ColumnChangelog
import br.com.jiratorio.extension.equalsComparing
import br.com.jiratorio.extension.time.LocalDateProgression
import br.com.jiratorio.extension.time.rangeTo
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "column_changelog")
data class ColumnChangelogEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "issue_id", nullable = false)
    var issueId: Long = 0,

    @Column(name = "column_from")
    override var from: String? = null,

    @Column(name = "column_to", nullable = false)
    override var to: String,

    @Column(nullable = false)
    override var startDate: LocalDateTime,

    @Column(nullable = false)
    override var endDate: LocalDateTime = startDate,

    @Column(nullable = false)
    override var leadTime: Long = 0

) : BaseEntity(), ColumnChangelog {

    val dateRange: LocalDateProgression
        get() = startDate.toLocalDate()..endDate.toLocalDate()

    override fun equals(other: Any?): Boolean =
        equalsComparing(
            other,
            ColumnChangelogEntity::from,
            ColumnChangelogEntity::to,
            ColumnChangelogEntity::startDate,
            ColumnChangelogEntity::endDate,
            ColumnChangelogEntity::leadTime
        )

    override fun hashCode(): Int =
        Objects.hash(from, to, startDate, endDate, leadTime)

}
