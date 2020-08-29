package br.com.jiratorio.domain.entity

import br.com.jiratorio.domain.chart.ChartType
import br.com.jiratorio.extension.equalsComparing
import br.com.jiratorio.extension.toStringBuilder
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class UserConfig(

    @Column(unique = true, nullable = false)
    val username: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var state: String? = null,

    var city: String? = null,

    var holidayToken: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var leadTimeChartType: ChartType = ChartType.BAR,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var throughputChartType: ChartType = ChartType.DOUGHNUT

) : BaseEntity() {
    companion object {
        private val serialVersionUID = -9168105728096346993L
    }

    override fun toString() =
        toStringBuilder(
            UserConfig::id,
            UserConfig::username,
            UserConfig::state,
            UserConfig::city,
            UserConfig::holidayToken,
            UserConfig::leadTimeChartType,
            UserConfig::throughputChartType
        )

    override fun equals(other: Any?) =
        equalsComparing(other, UserConfig::username)

    override fun hashCode() =
        Objects.hash(username)

}
