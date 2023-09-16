package com.glackfag.shorty.repositories.jpa;

import com.glackfag.shorty.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    boolean existsByAliasAndReporterId(String alias, int reporterId);
}
