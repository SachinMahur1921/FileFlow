package com.file.parser.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileParser {
    String getFormat();
    List<Map<String, Object>> parse(InputStream inputStream, JsonNode config);
}
