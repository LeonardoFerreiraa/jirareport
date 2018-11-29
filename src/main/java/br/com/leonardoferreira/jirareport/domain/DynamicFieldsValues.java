package br.com.leonardoferreira.jirareport.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFieldsValues {

    private String field;

    private List<String> values;

}
