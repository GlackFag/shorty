package com.glackfag.shorty.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportDTO {
    private String alias;
    private long reporterId;
}
