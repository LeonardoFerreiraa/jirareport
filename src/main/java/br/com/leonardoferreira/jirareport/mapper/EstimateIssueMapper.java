package br.com.leonardoferreira.jirareport.mapper;

import br.com.leonardoferreira.jirareport.domain.Board;
import br.com.leonardoferreira.jirareport.domain.embedded.Changelog;
import br.com.leonardoferreira.jirareport.domain.vo.EstimateIssue;
import br.com.leonardoferreira.jirareport.domain.vo.changelog.JiraChangelog;
import br.com.leonardoferreira.jirareport.domain.vo.changelog.JiraChangelogHistory;
import br.com.leonardoferreira.jirareport.domain.vo.changelog.JiraChangelogItem;
import br.com.leonardoferreira.jirareport.service.HolidayService;
import br.com.leonardoferreira.jirareport.util.CalcUtil;
import br.com.leonardoferreira.jirareport.util.DateUtil;
import br.com.leonardoferreira.jirareport.util.ParseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class EstimateIssueMapper {
    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public List<EstimateIssue> parseEstimate(final String rawText, final Board board) {
        JsonNode jsonNode = objectMapper.readTree(rawText);
        Iterator<JsonNode> issues = jsonNode.path("issues").elements();

        List<LocalDate> holidays = holidayService.findDaysByBoard(board.getId());

        Set<String> startColumns = CalcUtil.calcStartColumns(board);

        Iterable<JsonNode> iterable = () -> issues;
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(issue -> {
                    JsonNode fields = issue.path("fields");

                    List<JiraChangelogItem> changelogItems = extractChangelogItems(issue);
                    List<Changelog> changelog = ParseUtil.parseChangelog(changelogItems, holidays, board.getIgnoreWeekend());
                    if (!changelog.isEmpty()) {
                        Changelog changelogItem = changelog.get(changelog.size() - 1);
                        changelogItem.setLeadTime(DateUtil.daysDiff(changelogItem.getCreated(), LocalDateTime.now(),
                                holidays, board.getIgnoreWeekend()));
                        changelogItem.setEndDate(LocalDateTime.now());
                    }
                    LocalDateTime created = DateUtil.parseFromJira(fields.get("created").asText());

                    LocalDateTime startDate = null;

                    for (Changelog cl : changelog) {
                        if (startDate == null && startColumns.contains(cl.getTo())) {
                            startDate = cl.getCreated();
                        }
                    }
                    if ("BACKLOG".equals(board.getStartColumn())) {
                        startDate = created;
                    }
                    if (startDate == null) {
                        return null;
                    }
                    String priority = null;
                    if (fields.has("priority") && !fields.get("priority").isNull() && !fields.path("priority").isMissingNode()) {
                        priority = fields.path("priority").get("name").asText(null);
                    }

                    Long leadTime = DateUtil.daysDiff(startDate, LocalDateTime.now(), holidays, board.getIgnoreWeekend());

                    Long timeInImpediment = ParseUtil.countTimeInImpediment(board, changelogItems, changelog, LocalDateTime.now(), holidays);

                    String author = null;
                    JsonNode creator = fields.path("creator");
                    if (creator != null && !creator.isNull()) {
                        author = creator.get("displayName").asText(null);
                    }
                    return EstimateIssue.builder()
                            .creator(author)
                            .key(issue.get("key").asText(null))
                            .issueType(fields.path("issuetype").get("name").asText(null))
                            .created(created)
                            .startDate(startDate)
                            .leadTime(leadTime)
                            .system(parseElement(fields, board.getSystemCF()))
                            .epic(parseElement(fields, board.getEpicCF()))
                            .estimated(parseElement(fields, board.getEstimateCF()))
                            .project(parseElement(fields, board.getProjectCF()))
                            .summary(fields.get("summary").asText())
                            .changelog(changelog)
                            .impedimentTime(timeInImpediment)
                            .priority(priority)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private List<JiraChangelogItem> extractChangelogItems(final JsonNode issue) {
        JiraChangelog changelog = objectMapper.readValue(issue.path("changelog").toString(), JiraChangelog.class);
        changelog.getHistories().forEach(cl -> cl.getItems().forEach(i -> i.setCreated(cl.getCreated())));
        return changelog.getHistories().parallelStream()
                .map(JiraChangelogHistory::getItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String parseElement(final JsonNode fields, final String cf) {
        if (StringUtils.isEmpty(cf)) {
            return null;
        }
        final JsonNode jsonElement = fields.path(cf);
        if (jsonElement == null || jsonElement.isMissingNode()) {
            return null;
        }

        if (jsonElement.isArray()) {
            Iterator<JsonNode> elements = jsonElement.elements();
            if (elements == null || !elements.hasNext()) {
                return null;
            }
            Iterable<JsonNode> iterable = () -> elements;
            return StreamSupport.stream(iterable.spliterator(), true)
                    .map(component -> component.isContainerNode()
                            ? component.get("name").asText(null)
                            : component.asText(null))
                    .findFirst().orElse(null);
        }

        if (jsonElement.isContainerNode()) {
            return jsonElement.get("value").asText(null);
        }

        return jsonElement.asText(null);
    }

}
