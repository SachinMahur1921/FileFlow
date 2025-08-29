package com.file.parser.service;

import com.file.parser.entity.FileFormatDefinition;
import com.file.parser.repository.FileFormatDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileFormatDefinitionService {

    private final FileFormatDefinitionRepository repository;

    public FileFormatDefinitionService(FileFormatDefinitionRepository repository) {
        this.repository = repository;
    }

    public FileFormatDefinition create(FileFormatDefinition definition) {
        return repository.save(definition);
    }

    public List<FileFormatDefinition> findAll() {
        return repository.findAll();
    }

    public Optional<FileFormatDefinition> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<FileFormatDefinition> findByCode(String code) {
        return repository.findByCode(code);
    }
}
