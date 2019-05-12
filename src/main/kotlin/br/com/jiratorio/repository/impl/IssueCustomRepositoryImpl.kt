package br.com.jiratorio.repository.impl

import br.com.jiratorio.aspect.annotation.ExecutionTime
import br.com.jiratorio.domain.dynamicfield.DynamicFieldsValues
import br.com.jiratorio.domain.entity.Board
import br.com.jiratorio.domain.entity.Issue
import br.com.jiratorio.domain.request.SearchIssueRequest
import br.com.jiratorio.extension.log
import br.com.jiratorio.extension.time.atEndOfDay
import br.com.jiratorio.repository.IssueCustomRepository
import br.com.jiratorio.repository.impl.rowmapper.DynamicFieldsValuesRowMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.HashMap
import javax.persistence.EntityManager
import javax.persistence.Query

@Repository
class IssueCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) : IssueCustomRepository {

    @ExecutionTime
    @Transactional(readOnly = true)
    override fun findByExample(
        board: Board,
        dynamicFilters: Map<String, Array<String>>,
        searchIssueRequest: SearchIssueRequest
    ): List<Issue> {
        log.info(
            "Method=findByExample, board={}, dynamicFilters={}, searchIssueRequest={}",
            board, searchIssueRequest, dynamicFilters
        )

        val params = HashMap<String, Any>()

        val query = StringBuilder()
        query.append(" SELECT DISTINCT issue.* FROM issue ")
        query.append(" LEFT JOIN lead_time ON issue.id = lead_time.issue_id ")
        query.append(" LEFT JOIN lead_time_config ON lead_time.lead_time_config_id = lead_time_config.id ")
        query.append(" WHERE issue.board_id = :boardId ")
        query.append(" AND issue.end_date BETWEEN :startDate AND :endDate ")

        if (searchIssueRequest.keys.isNotEmpty()) {
            query.append(" AND issue.key NOT IN (:keys) ")
            params["keys"] = searchIssueRequest.keys
        }

        if (searchIssueRequest.estimates.isNotEmpty()) {
            query.append(" AND issue.estimate IN (:estimates) ")
            params["estimates"] = searchIssueRequest.estimates
        }

        if (searchIssueRequest.systems.isNotEmpty()) {
            query.append(" AND issue.system IN (:systems) ")
            params["systems"] = searchIssueRequest.systems
        }

        if (searchIssueRequest.epics.isNotEmpty()) {
            query.append(" AND issue.epic IN (:epics) ")
            params["epics"] = searchIssueRequest.epics
        }

        if (searchIssueRequest.issueTypes.isNotEmpty()) {
            query.append(" AND issue.issue_type IN (:issueTypes) ")
            params["issueTypes"] = searchIssueRequest.issueTypes
        }

        if (searchIssueRequest.projects.isNotEmpty()) {
            query.append(" AND issue.project IN (:projects) ")
            params["projects"] = searchIssueRequest.projects
        }

        if (searchIssueRequest.priorities.isNotEmpty()) {
            query.append(" AND issue.priority IN (:priorities) ")
            params["priorities"] = searchIssueRequest.priorities
        }

        board.dynamicFields?.forEach {
            val values = dynamicFilters[it.name]
            if (!values.isNullOrEmpty()) {
                query.append(" AND issue.dynamic_fields->>'${it.name}' in (:${it.field}) ")
                params[it.field] = values.toList()
            }
        }

        params["boardId"] = board.id
        params["startDate"] = searchIssueRequest.startDate.atStartOfDay()
        params["endDate"] = searchIssueRequest.endDate.atEndOfDay()

        query.append(" ORDER BY issue.key ")

        val nativeQuery: Query = entityManager.createNativeQuery(query.toString(), Issue::class.java)
        params.forEach { (k, v) -> nativeQuery.setParameter(k, v) }

        @Suppress("UNCHECKED_CAST")
        return nativeQuery.resultList as List<Issue>
    }

    @Transactional(readOnly = true)
    override fun findAllDynamicFieldValues(boardId: Long): List<DynamicFieldsValues> {
        log.info("Method=findAllDynamicFieldValues, boardId={}", boardId)

        val dynamicFields = findAllDynamicFieldsByBoardId(boardId)
        if (dynamicFields.isEmpty()) {
            return emptyList()
        }

        val query = """
            SELECT
            ${dynamicFields.joinToString { """ ARRAY_TO_JSON(ARRAY_REMOVE(ARRAY_AGG(DISTINCT fields."$it"), null)) as "$it" """ }}
            FROM issue, JSONB_TO_RECORD(dynamic_fields) AS
            ${dynamicFields.joinToString(prefix = "fields(", postfix = ")") { """ "$it" TEXT """ }}
            WHERE board_id = ?
        """.trimIndent()

        return jdbcTemplate.queryForObject(query, DynamicFieldsValuesRowMapper(objectMapper), boardId)
            ?: emptyList()
    }

    private fun findAllDynamicFieldsByBoardId(boardId: Long): List<String> {
        log.info("Method=findAllDynamicFieldsByBoardId, boardId={}", boardId)

        val query = "SELECT DISTINCT JSONB_OBJECT_KEYS(dynamic_fields) FROM issue WHERE board_id = ?"
        return jdbcTemplate.queryForList(query, String::class.java, boardId)
    }

}
