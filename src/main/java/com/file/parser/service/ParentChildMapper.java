package com.file.parser.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class ParentChildMapper {

    public List<Map<String, Object>> map(List<String[]> rows, JsonNode mappingJson) {
        List<Map<String, Object>> parsedParents = new ArrayList<>();

        List<String> parentFields = extractFields(mappingJson.path("fields"));

        // children mapping
        Map<String, List<String>> childrenFieldsByKey = new LinkedHashMap<>();
        for (JsonNode childNode : mappingJson.path("children")) {
            String childKey = childNode.path("key").asText();
            List<String> childFields = extractFields(childNode.path("fields"));
            childrenFieldsByKey.put(childKey, childFields);
        }

        Map<String, Object> currentParent = null;

        for (String[] line : rows) {
            if (line.length == 0) continue;

            String recordType = line[0];
            if (recordType == null || recordType.trim().isEmpty()) continue;

            if ("P".equalsIgnoreCase(recordType)) {
                // create parent
                Map<String, Object> parentRecord = new LinkedHashMap<>();
                for (int i = 0; i < parentFields.size() && (i + 1) < line.length; i++) {
                    parentRecord.put(parentFields.get(i), line[i + 1]);
                }
                // initialize child lists
                for (String childKey : childrenFieldsByKey.keySet()) {
                    parentRecord.put(childKey, new ArrayList<Map<String, Object>>());
                }

                parsedParents.add(parentRecord);
                currentParent = parentRecord;

            } else if ("C".equalsIgnoreCase(recordType)) {
                if (currentParent == null) {
                    throw new RuntimeException("Child record found before any parent record.");
                }

                // only one child type for now (can extend to multiple later)
                String childKey = childrenFieldsByKey.keySet().iterator().next();
                List<String> childFields = childrenFieldsByKey.get(childKey);

                Map<String, Object> childRecord = new LinkedHashMap<>();
                for (int i = 0; i < childFields.size() && (i + 1) < line.length; i++) {
                    childRecord.put(childFields.get(i), line[i + 1]);
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> childList = (List<Map<String, Object>>) currentParent.get(childKey);
                childList.add(childRecord);

            } else {
                throw new RuntimeException("Unknown record type: " + recordType);
            }
        }

        return parsedParents;
    }

    private List<String> extractFields(JsonNode fieldsNode) {
        List<String> fields = new ArrayList<>();
        for (JsonNode fieldNode : fieldsNode) {
            fields.add(fieldNode.path("sourceField").asText());
        }
        return fields;
    }
}
