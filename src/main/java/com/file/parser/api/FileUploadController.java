package com.file.parser.api;

import com.file.parser.service.FileProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final FileProcessingService fileProcessingService;

    public FileUploadController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Map<String,Object>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileIdentifierCode") String fileIdentifierCode
    ) {
        List<Map<String, Object>> records = fileProcessingService.processFile(file, fileIdentifierCode);
        return ResponseEntity.ok(records);
    }
}
