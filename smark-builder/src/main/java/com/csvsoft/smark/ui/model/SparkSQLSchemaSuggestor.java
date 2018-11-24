package com.csvsoft.smark.ui.model;

import com.csvsoft.smark.entity.FieldDataType;
import com.csvsoft.smark.service.sqlsuggestor.BaseSQLSuggestor;
import com.csvsoft.smark.service.sqlsuggestor.Token;
import org.apache.commons.lang.StringUtils;
import org.vaadin.aceeditor.Suggester;
import org.vaadin.aceeditor.Suggestion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SparkSQLSchemaSuggestor implements Suggester {

    static List<String> schemaDataTypeList = new LinkedList<>();
    static List<Suggestion> nullSuggestionList = new LinkedList<>();
    final static List<Suggestion> EMPTY_SUGGESTION = new ArrayList<Suggestion>(0);

    static {
        init();
    }

    private static void init() {
        schemaDataTypeList = FieldDataType.TIMESTAMP.getAllDataType().stream().map(fd -> fd.getValue()).collect(Collectors.toList());
        nullSuggestionList.add(new Suggestion("NULL", "NULL", "NULL"));
        nullSuggestionList.add(new Suggestion("NOT NULL", "NOT NULL", "NOT NULL"));

    }

    @Override
    public List<Suggestion> getSuggestions(String s, int cursor) {

        if (StringUtils.isBlank(s)) {
            return EMPTY_SUGGESTION;
        }
        /*
         *
         * columnName,dataType,nullable,dateformat
         */
        // Find the previous cloest line feed  char
        int commaCount = 0;
        boolean firstComma = false;
        int firstCommaPos = 0;
        int prevLineFeedPos = 0;
        int start = Math.min(s.length() - 1, cursor - 1);
        for (int i = start; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == ',') {
                commaCount++;
                if (!firstComma) {
                    firstComma = true;
                    firstCommaPos = i;
                }
            }
            if (c == '\n') {
                prevLineFeedPos = i;
                break;
            }
        }
        String curLine = s.substring(prevLineFeedPos + 1, cursor);
        if (curLine != null && curLine.trim().startsWith("#")) {
            return EMPTY_SUGGESTION;
        }
        if (commaCount == 0) {
            return new ArrayList<Suggestion>(0);
        }
        String token = s.substring(firstCommaPos + 1, cursor).trim();

        if (commaCount == 1) {
            return getSchemaTypeSuggestion(token);
        } else if (commaCount == 2) {
            return getNULLSuggestion(token);
        } else if (commaCount == 3) {

        }


        return EMPTY_SUGGESTION;
    }

    private List<Suggestion> getNULLSuggestion(String startWith) {
        return nullSuggestionList.stream().filter(s -> {
            if (StringUtils.isBlank(startWith)) {
                return true;
            } else if (s.getSuggestionText().startsWith(startWith.toUpperCase())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());


    }

    private List<Suggestion> getSchemaTypeSuggestion(String startWith) {
        return schemaDataTypeList.stream().filter(s -> {
            if (StringUtils.isBlank(startWith)) {
                return true;
            } else if (s.toUpperCase().startsWith(startWith.toUpperCase())) {
                return true;
            }
            return false;
        }).map(s -> new Suggestion(s, s, s))
                .collect(Collectors.toList());


    }

    @Override
    public String applySuggestion(Suggestion sugg, String text, int cursor) {
        // sugg is one of the objects returned by getSuggestions -> it's a
        // MySuggestion.
        String ins = sugg.getSuggestionText();

        int insertIndex = cursor;
        char prevChar = text.charAt(cursor - 1);

        int start = Math.min(text.length() - 1, cursor);
        for (int i = start; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == ',') {
                insertIndex = i + 1;
                break;
            }
        }
        while (insertIndex < cursor && text.charAt(insertIndex) == ' ') {
            insertIndex++;
        }

        String s1 = text.substring(0, insertIndex) + ins + text.substring(cursor);
        return s1;
    }
}
