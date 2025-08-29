package com.file.parser.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileParserFactory {

    private final List<FileParser> parsers;

    public FileParserFactory(List<FileParser> parsers) {
        this.parsers = parsers;
    }

    public FileParser getParser(String format) {
        return parsers.stream()
                .filter(p -> p.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parser found for format: " + format));
    }
}
