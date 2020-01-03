package br.com.jiratorio.domain.parsed

import br.com.jiratorio.domain.FieldChangelog
import br.com.jiratorio.domain.entity.ColumnChangelog

data class ParsedChangelog(

    val fieldChangelog: Set<FieldChangelog> = emptySet(),

    val columnChangelog: Set<ColumnChangelog> = emptySet()

)
