package br.com.jiratorio.domain.changelog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraChangelogItem {

    private String field;

    private String fieldtype;

    private String from;

    private String fromString;

    private String to;

    private String toString;

    @JsonIgnore
    private LocalDateTime created;

}
