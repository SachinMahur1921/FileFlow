package com.file.parser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.file.parser.service.FileParser;
import com.file.parser.service.ParentChildMapper;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DelimitedReader implements FileParser {

    private final ParentChildMapper mapper = new ParentChildMapper();

    @Override
    public String getFormat() {
        return "csv";
    }

    @Override
    public List<Map<String, Object>> parse(InputStream inputStream, JsonNode root) {
        List<String[]> rows = new ArrayList<>();

        try (Reader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator(extractDelimiter(root))
                             .build())
                     .build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                rows.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV", e);
        }

        return mapper.map(rows, root);
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
}
