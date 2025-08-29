package com.file.parser.repository;

import com.file.parser.entity.FileFormatDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileFormatDefinitionRepository extends JpaRepository<FileFormatDefinition, Long> {
    Optional<FileFormatDefinition> findByCode(String code);
}
