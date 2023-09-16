package com.glackfag.shorty.services;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.models.Report;
import com.glackfag.shorty.report.AssociationBanner;
import com.glackfag.shorty.repositories.jpa.ReportRepository;
import com.glackfag.shorty.repositories.redis.RedisReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ReportService {
    private final ReportRepository jpaRepository;
    private final RedisReportRepository redisRepository;
    private final AssociationService associationService;
    private final AssociationBanner associationBanner;

    @Autowired
    public ReportService(ReportRepository jpaRepository, RedisReportRepository redisRepository, AssociationService associationService, AssociationBanner associationBanner) {
        this.jpaRepository = jpaRepository;
        this.redisRepository = redisRepository;
        this.associationService = associationService;
        this.associationBanner = associationBanner;
    }

    public boolean alreadyReported(Report report) {
        return alreadyReported(report.getAlias(), report.getId());
    }

    public boolean alreadyReported(String alias, int reporterId) {
        return redisRepository.existsByAliasAndReporterId(alias, reporterId) ||
                jpaRepository.existsByAliasAndReporterId(alias, reporterId);
    }

    /**
     * @return Was report final or not(was shortening banned or not)
     */
    @Transactional
    public boolean save(Report report) {
        Association association = associationService.findOne(report.getAlias()).orElseThrow();
        association.setReports(association.getReports() + 1);

        redisRepository.save(report);
        jpaRepository.save(report);
        associationService.save(association);

        log.info("Saved report:" + report.toString());

        return associationBanner.banIfNeeded(association);
    }
}
