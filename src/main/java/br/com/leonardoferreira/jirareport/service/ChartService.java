package br.com.leonardoferreira.jirareport.service;

import br.com.leonardoferreira.jirareport.domain.Issue;
import br.com.leonardoferreira.jirareport.domain.embedded.Chart;
import br.com.leonardoferreira.jirareport.domain.embedded.ColumnTimeAvg;
import br.com.leonardoferreira.jirareport.domain.vo.ChartAggregator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lferreira on 3/26/18
 */
public interface ChartService {

    CompletableFuture<Chart<Long, Long>> issueHistogram(List<Issue> issues);

    CompletableFuture<Chart<String, Long>> estimatedChart(List<Issue> issues);

    CompletableFuture<Chart<String, Double>> leadTimeBySystem(List<Issue> issues);

    CompletableFuture<Chart<String, Long>> tasksBySystem(List<Issue> issues);

    CompletableFuture<Chart<String, Double>> leadTimeBySize(List<Issue> issues);

    CompletableFuture<List<ColumnTimeAvg>> columnTimeAvg(List<Issue> issues);

    CompletableFuture<Chart<String, Double>> leadTimeByType(List<Issue> issues);

    CompletableFuture<Chart<String, Long>> tasksByType(List<Issue> issues);

    CompletableFuture<Chart<String, Double>> leadTimeByProject(List<Issue> issues);

    CompletableFuture<Chart<String, Long>> tasksByProject(List<Issue> issues);

    ChartAggregator buildAllCharts(List<Issue> issues);
}
