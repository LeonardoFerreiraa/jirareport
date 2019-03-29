package br.com.jiratorio.domain.sandbox;

import br.com.jiratorio.domain.dynamicfield.DynamicFieldsValues;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SandBoxFilter {

    private List<String> keys;

    private List<String> estimatives;

    private List<String> systems;

    private List<String> epics;

    private List<String> issueTypes;

    private List<String> projects;

    private List<String> priorities;

    private List<DynamicFieldsValues> dynamicFieldsValues;

}