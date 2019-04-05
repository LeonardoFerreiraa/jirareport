package br.com.jiratorio.base.executionlistener

import br.com.jiratorio.base.CleanDatabase
import br.com.jiratorio.extension.log
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener

class CleanDatabaseTestExecutionListener : AbstractTestExecutionListener() {

    override fun beforeTestMethod(testContext: TestContext) {
        try {
            testContext.applicationContext
                .getBean(CleanDatabase::class.java)
                .clean()
        } catch (e: NoSuchBeanDefinitionException) {
            log.info("Method=beforeTestMethod, Info=Not a integration test")
        }
    }

}
