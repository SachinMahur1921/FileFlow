package com.file.parser.api;

import com.file.parser.entity.FileFormatDefinition;
import com.file.parser.service.FileFormatDefinitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-formats")
public class FileFormatDefinitionController {

    private final FileFormatDefinitionService service;

    public FileFormatDefinitionController(FileFormatDefinitionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FileFormatDefinition> create(@RequestBody FileFormatDefinition definition) {
        FileFormatDefinition saved = service.create(definition);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<FileFormatDefinition>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileFormatDefinition> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<FileFormatDefinition> getByCode(@PathVariable String code) {
        return service.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
