package br.com.jiratorio.domain.request

import br.com.jiratorio.domain.duedate.DueDateType
import br.com.jiratorio.domain.impediment.ImpedimentType
import javax.validation.constraints.NotBlank

data class UpdateBoardRequest(

    @field:NotBlank
    var name: String,

    var startColumn: String? = null,

    var endColumn: String? = null,

    var fluxColumn: MutableList<String>? = null,

    var ignoreIssueType: MutableList<String>? = null,

    var epicCF: String? = null,

    var estimateCF: String? = null,

    var systemCF: String? = null,

    var projectCF: String? = null,

    var ignoreWeekend: Boolean? = null,

    var impedimentType: ImpedimentType? = null,

    var impedimentColumns: MutableList<String>? = null,

    var touchingColumns: MutableList<String>? = null,

    var waitingColumns: MutableList<String>? = null,

    var dueDateCF: String? = null,

    var dueDateType: DueDateType? = null,

    var useLastOccurrenceWhenCalculateLeadTime: Boolean = false

)
