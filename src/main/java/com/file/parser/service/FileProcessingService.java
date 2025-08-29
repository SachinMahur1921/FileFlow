package com.file.parser.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.file.parser.entity.FileFormatDefinition;
import com.file.parser.repository.FileFormatDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class FileProcessingService {

    private final FileFormatDefinitionRepository repository;
    private final FileParserFactory parserFactory;
    private final ObjectMapper objectMapper;

    public FileProcessingService(FileFormatDefinitionRepository repository, FileParserFactory parserFactory, ObjectMapper objectMapper) {
        this.repository = repository;
        this.parserFactory = parserFactory;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> processFile(MultipartFile file, String fileIdentifierCode) {

        FileFormatDefinition fileFormatDefinition = repository.findByCode(fileIdentifierCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "File format not found for code: " + fileIdentifierCode));
        try (InputStream inputStream = file.getInputStream()) {
            JsonNode config = fileFormatDefinition.getMappingJson();
            FileParser parser = parserFactory.getParser("csv");
            return parser.parse(inputStream, config);
        } catch (Exception e) {
            throw new RuntimeException("File processing failed", e);
        }
    }
}
