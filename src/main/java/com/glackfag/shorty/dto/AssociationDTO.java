package com.glackfag.shorty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationDTO implements Serializable {
    @URL
    private String destination;
    private String alias;
    private LocalDate lastUsage;
    private Integer usages;
    private Long creatorId;
}

