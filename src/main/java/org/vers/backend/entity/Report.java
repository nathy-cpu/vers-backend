package org.vers.backend.entity;

import jakarta.json.Json;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "Report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    public Long id;

    @Column(name = "generated_at", nullable = false)
    public LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "generated_by", nullable = false)
    public User createdBy;

    @Column(name = "data", nullable = false)
    public Json data;

    public Report(User createdBy) {
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public Report() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Json getData() {
        return data;
    }

    public void setData(Json data) {
        this.data = data;
    }
}
