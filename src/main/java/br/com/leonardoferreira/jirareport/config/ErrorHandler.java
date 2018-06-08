package br.com.leonardoferreira.jirareport.config;

import br.com.leonardoferreira.jirareport.domain.vo.JiraError;
import br.com.leonardoferreira.jirareport.exception.JiraException;
import br.com.leonardoferreira.jirareport.exception.ResourceNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ModelAndView resourceNotFoundHander(final ResourceNotFound resourceNotFound) {
        resourceNotFound.printStackTrace();
        return new ModelAndView("errors/404");
    }

    @ExceptionHandler(JiraException.class)
    public ModelAndView jiraHandler(final JiraException e) {
        JiraError jiraError = e.getJiraError();
        log.info("Method=jiraHandler, msg={}", jiraError.getMessage());

        return new ModelAndView("errors/feignException")
                .addObject("jiraError", jiraError);
    }

}
