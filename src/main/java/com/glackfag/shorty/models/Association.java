package com.glackfag.shorty.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Association")
@Entity
@Table(name = "shortening")
@SecondaryTables({
        @SecondaryTable(name = "alias_creator", pkJoinColumns = @PrimaryKeyJoinColumn(name = "alias")),
        @SecondaryTable(name = "usage_stats", pkJoinColumns = @PrimaryKeyJoinColumn(name = "alias"))
})
public class Association implements Serializable {
    @Id
    @org.springframework.data.annotation.Id
    @Column(name = "alias", table = "shortening")
    private String alias;
    
    @URL
    @Column(name = "destination", table = "shortening")

    private String destination;
    @Column(name = "creator_id", table = "alias_creator")
    private Long creatorId;

    @Column(name = "last_usage", table = "usage_stats")
    private LocalDate lastUsage;

    @Column(name = "usage_count", table = "usage_stats")
    private int usages;

    @Column(name = "report_count", table = "usage_stats")
    private int reports;

    @Column(name = "banned", table = "shortening")
    @JsonIgnore
    private boolean isBanned;
}
