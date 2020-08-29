package br.com.jiratorio.domain

import br.com.jiratorio.extension.toStringBuilder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class Account(

    private val username: String,

    val name: String,

    val email: String,

    val token: String

) : UserDetails {
    companion object {
        private val serialVersionUID = -8167414094792278950L
    }

    override fun toString() =
        toStringBuilder(Account::name, Account::username)

    override fun getUsername() = username

    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun getPassword(): String? = null

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
