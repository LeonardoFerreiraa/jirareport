package br.com.jiratorio.base

import br.com.jiratorio.factory.domain.entity.AccountFactory
import br.com.jiratorio.service.auth.TokenService
import io.restassured.http.Header
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.context.TestSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class Authenticator(
    private val accountFactory: AccountFactory,
    private val tokenService: TokenService
) {

    fun <T> withDefaultUser(supplier: () -> T): T {
        return this.withUser(accountFactory.defaultUserName(), supplier)
    }

    fun <T> withUser(username: String, supplier: () -> T): T {
        val oldContext = TestSecurityContextHolder.getContext()

        TestSecurityContextHolder.clearContext()
        val principal = accountFactory.buildUser(username)
        TestSecurityContextHolder.setAuthentication(
            UsernamePasswordAuthenticationToken(
                principal,
                principal.password,
                principal.authorities
            )
        )

        val result = supplier()

        TestSecurityContextHolder.clearContext()
        TestSecurityContextHolder.setContext(oldContext)

        return result
    }

    fun defaultUserHeader(): Header {
        return Header("X-Auth-Token", tokenService.encode(accountFactory.defaultUser()))
    }

    fun defaultUserName(): String {
        return accountFactory.defaultUserName()
    }
}
