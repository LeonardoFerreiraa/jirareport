package br.com.leonardoferreira.jirareport.domain.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class IssueForm {

    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> keys = new ArrayList<>();

    private List<String> systems = new ArrayList<>();

    private List<String> taskSize = new ArrayList<>();

    private List<String> epics = new ArrayList<>();

    private List<String> issueTypes = new ArrayList<>();

    private List<String> projects = new ArrayList<>();

    private List<String> priorities = new ArrayList<>();

}
