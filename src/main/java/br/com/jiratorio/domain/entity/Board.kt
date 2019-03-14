package br.com.jiratorio.domain.entity

import br.com.jiratorio.domain.DueDateType
import br.com.jiratorio.domain.DynamicFieldConfig
import br.com.jiratorio.domain.impediment.ImpedimentType
import lombok.Data
import org.hibernate.annotations.Type
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Data
@Entity
data class Board(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null
) : BaseEntity() {

    @Column(nullable = false)
    var externalId: Long? = null

    var name: String? = null

    var startColumn: String? = null

    var endColumn: String? = null

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var fluxColumn: MutableList<String>? = null

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var ignoreIssueType: MutableList<String>? = null

    var epicCF: String? = null

    var estimateCF: String? = null

    var systemCF: String? = null

    var projectCF: String? = null

    @Column(name = "DUE_DATE_CF")
    var dueDateCF: String? = null

    var ignoreWeekend: Boolean? = null

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL])
    var leadTimeConfigs: MutableList<LeadTimeConfig>? = null

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL])
    var holidays: MutableList<Holiday>? = null

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL])
    var issues: MutableList<Issue>? = null

    @Enumerated(EnumType.STRING)
    var impedimentType: ImpedimentType? = null

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var impedimentColumns: MutableList<String>? = null

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var dynamicFields: MutableList<DynamicFieldConfig>? = null

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var touchingColumns: MutableList<String>? = null

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    var waitingColumns: MutableList<String>? = null

    @Enumerated(EnumType.STRING)
    var dueDateType: DueDateType? = null

}