package br.com.jiratorio.controller

import br.com.jiratorio.domain.request.SearchIssueRequest
import br.com.jiratorio.domain.response.ListIssueResponse
import br.com.jiratorio.domain.response.issue.IssueDetailResponse
import br.com.jiratorio.domain.response.issue.IssueFilterKeyResponse
import br.com.jiratorio.domain.response.issue.IssueFilterResponse
import br.com.jiratorio.service.IssueService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import java.time.LocalDate
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/boards/{boardId}/issues")
class IssueController(private val issueService: IssueService) {

    @GetMapping
    fun index(
        @PathVariable boardId: Long,
        @Valid searchIssueRequest: SearchIssueRequest,
        webRequest: WebRequest
    ): ListIssueResponse {
        return issueService.findByExample(boardId, webRequest.parameterMap, searchIssueRequest)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable boardId: Long, @PathVariable id: Long): IssueDetailResponse {
        return issueService.findByBoardAndId(boardId, id)
    }

    @GetMapping("/filters")
    fun filters(@PathVariable boardId: Long): IssueFilterResponse {
        return issueService.findFilters(boardId)
    }

    @GetMapping("/filters/keys")
    fun filterKeys(
        @PathVariable boardId: Long,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate
    ): IssueFilterKeyResponse {
        return issueService.findFilterKeys(boardId, startDate, endDate)
    }

}
