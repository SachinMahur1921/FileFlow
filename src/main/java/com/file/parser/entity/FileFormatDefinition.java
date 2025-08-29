package com.file.parser.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mesh_fileflow_format_contract", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"code"})
})

public class FileFormatDefinition  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @Column(nullable = false)
    private String formatType;

    @Column(nullable = false)
    private String processName;

    private String delimiter;
    private String delimiterName;
    private String fileExtension;
    private String recordStructure;
    private String rejectionType;

    @Column(name = "stop_on_first_error")
    private Boolean stopOnFirstError;

    @Column(name = "error_threshold")
    private Integer errorThreshold;

    private String targetApi;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mapping_json", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private JsonNode mappingJson;
}
