package br.com.jiratorio.controller

import br.com.jiratorio.domain.request.DynamicFieldConfigRequest
import br.com.jiratorio.domain.response.DynamicFieldConfigResponse
import br.com.jiratorio.service.DynamicFieldConfigService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/boards/{boardId}/dynamic-field-configs")
class DynamicFieldConfigController(
    private val dynamicFieldConfigService: DynamicFieldConfigService
) {

    @GetMapping
    fun findAllByBoard(@PathVariable boardId: Long): List<DynamicFieldConfigResponse> =
        dynamicFieldConfigService.findByBoard(boardId)

    @PostMapping
    fun create(
        @PathVariable boardId: Long,
        @Valid @RequestBody dynamicFieldConfigRequest: DynamicFieldConfigRequest
    ): HttpEntity<*> {
        val id = dynamicFieldConfigService.create(boardId, dynamicFieldConfigRequest)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .build(id)

        return ResponseEntity.created(location).build<Any>()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun findByBoardAndId(
        @PathVariable boardId: Long,
        @PathVariable id: Long
    ): Unit =
        dynamicFieldConfigService.deleteByBoardAndId(boardId, id)

}
