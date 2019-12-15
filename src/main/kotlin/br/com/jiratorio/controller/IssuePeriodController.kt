package br.com.jiratorio.controller

import br.com.jiratorio.domain.request.CreateIssuePeriodRequest
import br.com.jiratorio.domain.response.issueperiod.IssuePeriodByBoardResponse
import br.com.jiratorio.domain.response.issueperiod.IssuePeriodByIdResponse
import br.com.jiratorio.service.IssuePeriodService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/boards/{boardId}/issue-periods")
class IssuePeriodController(
    private val issuePeriodService: IssuePeriodService
) {

    @GetMapping
    fun index(@PathVariable boardId: Long): IssuePeriodByBoardResponse =
        issuePeriodService.findIssuePeriodByBoard(boardId)

    @GetMapping("/{issuePeriodId}")
    fun findById(
        @PathVariable boardId: Long,
        @PathVariable issuePeriodId: Long
    ): IssuePeriodByIdResponse =
        issuePeriodService.findById(boardId, issuePeriodId)

    @PostMapping
    fun create(
        @PathVariable boardId: Long,
        @Valid @RequestBody createIssuePeriodRequest: CreateIssuePeriodRequest
    ): HttpEntity<*> {
        val id = issuePeriodService.create(createIssuePeriodRequest, boardId)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .build(id)

        return ResponseEntity.created(location).build<Any>()
    }

    @PutMapping("/{issuePeriodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable boardId: Long,
        @PathVariable issuePeriodId: Long
    ): Unit =
        issuePeriodService.update(boardId, issuePeriodId)

    @DeleteMapping("/{issuePeriodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable boardId: Long,
        @PathVariable issuePeriodId: Long
    ): Unit =
        issuePeriodService.removeByBoardAndId(boardId, issuePeriodId)
}
