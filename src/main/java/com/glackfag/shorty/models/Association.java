package com.glackfag.shorty.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alias_destination")
@SecondaryTables({
        @SecondaryTable(name = "alias_creator", pkJoinColumns = @PrimaryKeyJoinColumn(name = "alias")),
        @SecondaryTable(name = "usage_stats", pkJoinColumns = @PrimaryKeyJoinColumn(name = "alias"))
})
public class Association implements Serializable {
    @Id
    @Column(name = "alias")
    private String alias;

    @URL
    @Column(name = "destination", table = "alias_destination")
    private String destination;

    @Column(name = "last_usage", table = "usage_stats")
    private LocalDate lastUsage;

    @Column(name = "usage_count", table = "usage_stats")
    private Integer usages;

    @Column(name = "creator_id", table = "alias_creator")
    private Long creatorId;
}
