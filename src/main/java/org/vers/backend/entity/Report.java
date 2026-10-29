package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "report")
public class Report extends PanacheEntity {

    @Column(nullable = false)
    public LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "generated_by")
    public User generatedBy;

    // Raw JSON text, stored in a jsonb column
    @JdbcTypeCode(SqlTypes.JSON)
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
}
