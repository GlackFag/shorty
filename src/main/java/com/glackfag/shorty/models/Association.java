package com.glackfag.shorty.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Association implements Serializable {
    @Id
    @Column(name = "alias")
    private String alias;

    @URL
    @Column(name = "destination")
    private String destination;

    @Column(name = "last_usage")
    private LocalDate lastUsage;

    @Column(name = "usages")
    private int usages;
}
