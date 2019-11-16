package br.com.jiratorio.service.impl

import br.com.jiratorio.domain.ImportHolidayInfo
import br.com.jiratorio.domain.entity.UserConfig
import br.com.jiratorio.domain.request.UpdateUserConfigRequest
import br.com.jiratorio.domain.response.UserConfigResponse
import br.com.jiratorio.extension.log
import br.com.jiratorio.mapper.toImportHolidayInfo
import br.com.jiratorio.mapper.toUserConfigResponse
import br.com.jiratorio.mapper.updateFromUpdateUserConfigRequest
import br.com.jiratorio.repository.UserConfigRepository
import br.com.jiratorio.service.UserConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserConfigServiceImpl(
    private val userConfigRepository: UserConfigRepository,
    @param:Value("\${holiday.token}") private val holidayToken: String
) : UserConfigService {

    @Transactional
    override fun update(username: String, updateUserConfigRequest: UpdateUserConfigRequest) {
        log.info("Method=update, username={}, updateUserConfigRequest={}", username, updateUserConfigRequest)

        val userConfig = userConfigRepository.findByUsername(username) ?: UserConfig(username)

        userConfig.updateFromUpdateUserConfigRequest(updateUserConfigRequest)

        userConfigRepository.save(userConfig)
    }

    @Transactional(readOnly = true)
    override fun retrieveHolidayInfo(username: String): ImportHolidayInfo {
        log.info("Method=retrieveHolidayInfo, username={}", username)

        val userConfig = userConfigRepository.findByUsername(username)

        return if (userConfig == null || userConfig.holidayToken.isNullOrEmpty())
            ImportHolidayInfo("SP", "ARARAQUARA", holidayToken)
        else
            userConfig.toImportHolidayInfo()
    }

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): UserConfigResponse {
        log.info("Method=findByUsername, username={}", username)

        return userConfigRepository.findByUsername(username)
            ?.toUserConfigResponse()
            ?: UserConfigResponse(username)
    }

}
