package br.com.jiratorio.config.internationalization

import org.springframework.stereotype.Component
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.LocaleResolver
import java.util.Locale
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
class HeaderLocaleResolver : LocaleResolver {
    private val defaultLocale = Locale("pt", "BR")

    private val supportedLocales = mapOf(
        "en" to Locale("en"),
        "pt-BR" to defaultLocale
    )

    override fun resolveLocale(request: HttpServletRequest): Locale {
        val lang: String? = request.getHeader("Accept-Language")
        if (lang == null || lang.isEmpty()) {
            return defaultLocale
        }

        return supportedLocales[lang] ?: defaultLocale
    }

    override fun setLocale(request: HttpServletRequest, response: HttpServletResponse?, locale: Locale?) {
        throw UnsupportedOperationException("Cannot change HTTP accept header - use a different locale resolution strategy")
    }

}
