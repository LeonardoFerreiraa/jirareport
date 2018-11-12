package br.com.leonardoferreira.jirareport.mapper;

import br.com.leonardoferreira.jirareport.aspect.annotation.ExecutionTime;
import br.com.leonardoferreira.jirareport.domain.Board;
import br.com.leonardoferreira.jirareport.domain.Issue;
import br.com.leonardoferreira.jirareport.domain.embedded.Changelog;
import br.com.leonardoferreira.jirareport.domain.embedded.DueDateHistory;
import br.com.leonardoferreira.jirareport.domain.vo.DynamicFieldConfig;
import br.com.leonardoferreira.jirareport.domain.vo.changelog.JiraChangelog;
import br.com.leonardoferreira.jirareport.domain.vo.changelog.JiraChangelogHistory;
import br.com.leonardoferreira.jirareport.domain.vo.changelog.JiraChangelogItem;
import br.com.leonardoferreira.jirareport.service.HolidayService;
import br.com.leonardoferreira.jirareport.util.CalcUtil;
import br.com.leonardoferreira.jirareport.util.DateUtil;
import br.com.leonardoferreira.jirareport.util.ParseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author lferreira
 * @since 7/28/17 12:52 PM
 */
@Slf4j
@Component
public class IssueMapper {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ObjectMapper objectMapper;

    private final JsonParser jsonParser = new JsonParser();

    @ExecutionTime
    @Transactional
    public List<Issue> parse(final String rawText, final Board board) {
        JsonElement response = jsonParser.parse(rawText);
        JsonArray issues = response.getAsJsonObject().getAsJsonArray("issues");

        List<LocalDate> holidays = holidayService.findDaysByBoard(board.getId());

        Set<String> startColumns = CalcUtil.calcStartColumns(board);
        Set<String> endColumns = CalcUtil.calcEndColumns(board);

        return StreamSupport.stream(issues.spliterator(), true)
                .map(issueRaw -> {
                    JsonObject issue = issueRaw.getAsJsonObject();

                    JsonObject fields = issue.get("fields").getAsJsonObject();

                    List<JiraChangelogItem> changelogItems = extractChangelogItems(issue);
                    List<Changelog> changelog = ParseUtil.parseChangelog(changelogItems, holidays, board.getIgnoreWeekend());

                    LocalDateTime created = DateUtil.parseFromJira(fields.get("created").getAsString());

                    LocalDateTime startDate = null;
                    LocalDateTime endDate = null;

                    for (Changelog cl : changelog) {
                        if (startDate == null && startColumns.contains(cl.getTo())) {
                            startDate = cl.getCreated();
                        }

                        if (endDate == null && endColumns.contains(cl.getTo())) {
                            endDate = cl.getCreated();
                        }
                    }

                    if ("BACKLOG".equals(board.getStartColumn())) {
                        startDate = created;
                    }

                    if (startDate == null || endDate == null) {
                        return null;
                    }

                    Long leadTime = DateUtil.daysDiff(startDate, endDate, holidays, board.getIgnoreWeekend());

                    String author = null;
                    JsonObject creator = fields.getAsJsonObject("creator");
                    if (creator != null) {
                        author = getAsStringSafe(creator.get("displayName"));
                    }

                    Long differenceFirstAndLastDueDate = null;
                    List<DueDateHistory> dueDateHistory = null;

                    if (Boolean.TRUE.equals(board.getCalcDueDate())) {
                        dueDateHistory = parseDueDateHistory(changelogItems);
                        if (!dueDateHistory.isEmpty()) {
                            LocalDate firstDueDate = dueDateHistory.get(0).getDueDate();
                            LocalDate finalDueDate = dueDateHistory.get(dueDateHistory.size() - 1).getDueDate();

                            differenceFirstAndLastDueDate = ChronoUnit.DAYS.between(firstDueDate, finalDueDate);
                        }
                    }

                    Long timeInImpediment = ParseUtil.countTimeInImpediment(board, changelogItems, changelog, endDate, holidays);

                    String priority = null;
                    if (fields.has("priority") && !fields.get("priority").isJsonNull() && fields.get("priority").isJsonObject()) {
                        JsonObject priorityObj = fields.getAsJsonObject("priority");
                        priority = getAsStringSafe(priorityObj.get("name"));
                    }

                    Map<String, String> dynamicFields = parseDynamicFields(board, fields);

                    return Issue.builder()
                            .creator(author)
                            .key(getAsStringSafe(issue.get("key")))
                            .issueType(getAsStringSafe(fields.getAsJsonObject("issuetype").get("name")))
                            .created(created)
                            .startDate(startDate)
                            .endDate(endDate)
                            .leadTime(leadTime)
                            .system(parseElement(fields, board.getSystemCF()))
                            .epic(parseElement(fields, board.getEpicCF()))
                            .estimated(parseElement(fields, board.getEstimateCF()))
                            .project(parseElement(fields, board.getProjectCF()))
                            .summary(fields.get("summary").getAsString())
                            .changelog(changelog)
                            .board(board)
                            .differenceFirstAndLastDueDate(differenceFirstAndLastDueDate)
                            .dueDateHistory(dueDateHistory)
                            .impedimentTime(timeInImpediment)
                            .priority(priority)
                            .dynamicFields(dynamicFields)
                            .build();

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private List<JiraChangelogItem> extractChangelogItems(final JsonObject issue) {
        JiraChangelog changelog = objectMapper.readValue(issue.getAsJsonObject("changelog").toString(), JiraChangelog.class);
        changelog.getHistories().forEach(cl -> cl.getItems().forEach(i -> i.setCreated(cl.getCreated())));
        return changelog.getHistories().parallelStream()
                .map(JiraChangelogHistory::getItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String parseElement(final JsonObject fields, final String cf) {
        if (StringUtils.isEmpty(cf)) {
            return null;
        }
        final JsonElement jsonElement = fields.get(cf);
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }

        if (jsonElement.isJsonArray()) {
            JsonArray components = jsonElement.getAsJsonArray();
            if (components == null) {
                return null;
            }

            return StreamSupport.stream(components.spliterator(), true)
                    .map(component -> component.isJsonObject()
                            ? component.getAsJsonObject().get("name").getAsString()
                            : component.getAsString())
                    .findFirst().orElse(null);
        }

        if (jsonElement.isJsonObject()) {
            return getAsStringSafe(jsonElement.getAsJsonObject().get("value"));
        }

        return jsonElement.getAsString();
    }

    private String getAsStringSafe(final JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }

        if (jsonElement.isJsonObject()) {
            JsonElement value = jsonElement.getAsJsonObject().get("value");
            return getAsStringSafe(value);
        }

        return jsonElement.getAsString();
    }

    private List<DueDateHistory> parseDueDateHistory(final List<JiraChangelogItem> changelogItems) {
        return changelogItems.stream()
                .filter(i -> "duedate".equals(i.getField()) && !StringUtils.isEmpty(i.getTo()))
                .map(i -> DueDateHistory.builder()
                        .created(i.getCreated())
                        .dueDate(LocalDate.parse(i.getTo(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build())
                .sorted(Comparator.comparing(DueDateHistory::getCreated))
                .collect(Collectors.toList());
    }

    private Map<String, String> parseDynamicFields(final Board board, final JsonObject fields) {
        if (board.getDynamicFields() == null || board.getDynamicFields().isEmpty()) {
            return null;
        }

        Map<String, String> dynamicFields = new HashMap<>();
        for (DynamicFieldConfig dynamicField : board.getDynamicFields()) {
            dynamicFields.put(dynamicField.getName(), getAsStringSafe(fields.get(dynamicField.getField())));
        }

        return dynamicFields;
    }

}
