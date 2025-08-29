package com.file.parser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Component
public class DelimitedReader implements FileParser {

    @Override
    public String getFormat() {
        return "csv";
    }

    public List<Map<String, Object>> parse(InputStream inputStream, JsonNode root) {
        List<Map<String, Object>> parsedParents = new ArrayList<>();

        try {
            char delimiter = extractDelimiter(root);
            List<String> parentFields = extractFields(root.path("fields"));
            JsonNode childrenNodes = root.path("children");

            // Map child key to list of fields for that child
            Map<String, List<String>> childrenFieldsByKey = new LinkedHashMap<>();
            for (JsonNode childNode : childrenNodes) {
                String childKey = childNode.path("key").asText();
                List<String> childFields = extractFields(childNode.path("fields"));
                childrenFieldsByKey.put(childKey, childFields);
            }

            Map<String, Object> currentParent = null;

            try (Reader reader = new InputStreamReader(inputStream);
                 CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(delimiter).build())
                     .build()) {

                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (line.length == 0) {
                        // empty line, skip
                        continue;
                    }

                    String recordType = line[0];
                    if (recordType == null || recordType.trim().isEmpty()) {
                        // blank record type line, skip
                        continue;
                    }

                    if ("P".equalsIgnoreCase(recordType)) {
                        // Parse parent fields starting from index 1
                        Map<String, Object> parentRecord = new LinkedHashMap<>();
                        for (int i = 0; i < parentFields.size() && (i + 1) < line.length; i++) {
                            parentRecord.put(parentFields.get(i), line[i + 1]);
                        }

                        // Initialize empty child lists inside parent map
                        for (String childKey : childrenFieldsByKey.keySet()) {
                            parentRecord.put(childKey, new ArrayList<Map<String, Object>>());
                        }

                        parsedParents.add(parentRecord);
                        currentParent = parentRecord;

                    } else if ("C".equalsIgnoreCase(recordType)) {
                        if (currentParent == null) {
                            throw new RuntimeException("Child record found before any parent record.");
                        }

                        // Since child key is removed, assume single child type
                        String childKey = childrenFieldsByKey.keySet().iterator().next(); // first (and only) child key
                        List<String> childFields = childrenFieldsByKey.get(childKey);

                        Map<String, Object> childRecord = new LinkedHashMap<>();
                        for (int i = 0; i < childFields.size() && (i + 1) < line.length; i++) {
                            childRecord.put(childFields.get(i), line[i + 1]);
                        }

                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> childList = (List<Map<String, Object>>) currentParent.get(childKey);
                        childList.add(childRecord);
                    }else {
                        throw new RuntimeException("Unknown record type: " + recordType);
                    }
                }
            } catch (CsvValidationException e) {
                throw new RuntimeException("CSV validation error: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV", e);
        }

        return parsedParents;
    }

    private char extractDelimiter(JsonNode root) {
        String delimiterStr = Optional.ofNullable(root.path("delimiter").asText(",")).orElse(",");
        return switch (delimiterStr) {
            case "|" -> '|';
            case "~" -> '~';
            case "I" -> 'I';
            case "\t", "TAB" -> '\t';
            default -> ',';
        };
    }

    private List<String> extractFields(JsonNode fieldsNode) {
        List<String> fields = new ArrayList<>();
        for (JsonNode fieldNode : fieldsNode) {
            fields.add(fieldNode.path("sourceField").asText());
        }
        return fields;
    }
}