package br.com.leonardoferreira.jirareport.client;

import br.com.leonardoferreira.jirareport.domain.Project;
import br.com.leonardoferreira.jirareport.domain.vo.ProjectStatusList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Created by lferreira on 3/26/18
 */
@FeignClient(name = "project-client", url = "${jira.url}")
public interface ProjectClient {

    @GetMapping("/rest/api/2/project")
    List<Project> findAll(@RequestHeader("Authorization") String token);

    @GetMapping("/rest/api/2/project/{projectId}/statuses")
    List<ProjectStatusList> findStatusFromProject(@RequestHeader("Authorization") String token,
                                                  @PathVariable("projectId") Long projectId);

}
