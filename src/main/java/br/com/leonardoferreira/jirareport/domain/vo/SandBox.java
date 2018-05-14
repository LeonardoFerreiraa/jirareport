package br.com.leonardoferreira.jirareport.domain.vo;

import br.com.leonardoferreira.jirareport.domain.Issue;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author lferreira
 * @since 5/11/18 4:25 PM
 */
@Data
public class SandBox {

    private List<Issue> issues = new ArrayList<>();

    private ChartAggregator chartAggregator;

    private Double avgLeadTime;

    public String getLeadTime() {
        return String.format("%.2f", avgLeadTime);
    }

}
