package br.com.leonardoferreira.jirareport.domain.vo.changelog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraChangelogAuthor {

    private String self;

    @JsonProperty("name")
    private String username;

    @JsonProperty("key")
    private String key;

    @JsonProperty("emailAddress")
    private String email;

    @JsonProperty("displayName")
    private String name;

    private Boolean active;

    private String timeZone;
}
