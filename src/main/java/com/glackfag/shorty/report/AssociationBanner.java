package com.glackfag.shorty.report;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.services.AssociationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@Slf4j
public class AssociationBanner {
    private final AssociationService associationService;

    private final double reportsForBan;

    @Value("${reports.forBan.min}")
    private int reportsMin;

    @Autowired
    public AssociationBanner(AssociationService associationService,
                             @Value("${reports.forBan.min}") int reportsForBan) {
        this.associationService = associationService;
        this.reportsForBan = 0.01D * (double) reportsForBan;
    }

    /**
     * @return Was report final or not(was association banned or not)
     */
    public boolean banIfNeeded(Association association) {
        int reportNeeded = (int) (association.getReports() * reportsForBan);
        int reports = association.getReports();

        log.info(String.format("Alias '%s' reported %d times. %d left to get it banned",
                association.getAlias(), reports,
                reportNeeded > reportsMin ? reportNeeded - reports : reportsMin - reports));

        if (reports >= reportNeeded && reports >= reportsMin) {
            this.banShortening(association);
            return true;
        }
        return false;
    }

    private void banShortening(Association association) {
        if (!associationService.existsByAlias(association.getAlias())) {
            String message = String.format("Attempt to ban not existing association with alias '%s'", association.getAlias());
            log.debug(message);
            throw new NoSuchElementException(message);
        }

        association.setBanned(true);
        associationService.save(association);
        log.info(String.format("Banned shortening with alias: '%s'", association.getAlias()));
    }
}
