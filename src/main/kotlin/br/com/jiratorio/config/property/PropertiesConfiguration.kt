package br.com.jiratorio.config.property

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    JiraProperties::class,
    HolidayProperties::class,
    SecurityProperties::class
)
class PropertiesConfiguration
