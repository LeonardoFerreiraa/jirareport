package br.com.jiratorio.jira.client.config

import br.com.jiratorio.domain.CurrentUser
import br.com.jiratorio.exception.UnauthorizedException
import br.com.jiratorio.internationalization.MessageResolver
import br.com.jiratorio.jira.domain.JiraError
import br.com.jiratorio.jira.domain.exception.JiraException
import com.fasterxml.jackson.databind.ObjectMapper
import feign.RequestInterceptor
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean

class JiraClientConfiguration(
    private val objectMapper: ObjectMapper,
    private val currentUser: CurrentUser,
    private val messageResolver: MessageResolver
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun requestInterceptor() = RequestInterceptor { template ->
        template.header("Authorization", currentUser.jiraToken)
        template.header("Accept-Language", "en")
        template.header("X-Force-Accept-Language", "true")
    }

    @Bean
    fun errorDecoder() = ErrorDecoder { methodKey, response ->
        log.info("Method=errorDecoder, methodKey={}, response={}", methodKey, response)

        if (response.status() == 401) {
            UnauthorizedException()
        } else {
            JiraException(
                try {
                    objectMapper.readValue(response.body().asInputStream(), JiraError::class.java)
                } catch (e: Exception) {
                    log.error("Method=errorDecoder, methodKey={}, response={}", methodKey, response, e)
                    JiraError(messageResolver.resolve("errors.session-timeout"), response.status().toLong())
                }
            )
        }
    }

}
