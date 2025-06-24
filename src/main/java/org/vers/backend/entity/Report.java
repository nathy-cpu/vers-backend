package org.vers.backend.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import jakarta.persistence.Table;

@Entity
@Table(name = "report")
public class Report extends PanacheEntity {

    @Column(nullable = false)
    public LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "generated_by")
    public User generatedBy;

    @Column(nullable = false, columnDefinition = "jsonb")
    public String data;

    public Report() {
        this.generatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return (
            "Report{" +
            "id=" +
            id +
            ", generatedBy=" +
            generatedBy +
            ", generatedAt=" +
            generatedAt +
            ", data='" +
            data +
            '\'' +
            '}'
        );
    }

    public JsonNode getData()
        throws JsonMappingException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(this.data);
    }

    public void setData(JsonNode jsonData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.data = objectMapper.writeValueAsString(jsonData);
    }
}
