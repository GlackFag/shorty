package com.glackfag.shorty.services;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.repositories.jpa.AssociationRepository;
import com.glackfag.shorty.repositories.redis.RedisAssociationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class AssociationService {
    private final AssociationRepository jpaRepository;
    private final RedisAssociationRepository redisRepository;

    @Autowired
    public AssociationService(AssociationRepository jpaRepository, RedisAssociationRepository redisRepository) {
        this.jpaRepository = jpaRepository;
        this.redisRepository = redisRepository;
    }

    @Transactional
    public void save(Association association) {
        jpaRepository.save(association);
        redisRepository.save(association);
        log.info("Saved/updated association: " + association);
    }

    @Transactional
    public void delete(String alias) {
        jpaRepository.deleteById(alias);
        redisRepository.deleteById(alias);
        log.info("Deleted association with alias: " + alias);
    }

    @Transactional
    public void updateUsageStats(String alias) {
        Association association = findOne(alias).orElseThrow();

        association.setUsages(association.getUsages() + 1);
        association.setLastUsage(LocalDate.now());

        log.info("Updated usage stats for association with alias:" + alias);
        save(association);
    }

    public Optional<Association> findOne(String alias) {
        Optional<Association> association = redisRepository.findById(alias);

        log.info("Association with alias:'" + alias + "' " +
                (association.isEmpty() ? "not" : "") + " found in redis");
        return association.isPresent() ? association : jpaRepository.findById(alias);
    }

    public List<Association> findAllByCreatorId(long creatorId) {
        List<Association> redisResult = redisRepository.findByCreatorId(creatorId);

        return !redisResult.isEmpty() ? redisResult : jpaRepository.findByCreatorId(creatorId);
    }

    public boolean exists(Association association) {
        return redisRepository.existsByAliasAndDestinationNot(association.getAlias(), association.getDestination()) ||
                jpaRepository.existsByAliasAndDestinationNot(association.getAlias(), association.getDestination());
    }

    public boolean existsByAlias(String alias) {
        return redisRepository.existsById(alias) || jpaRepository.existsById(alias);
    }

    public boolean isValidForCreation(Association association) {
        return existsByAliasAndDestinationNot(association.getAlias(), association.getDestination()) ||
                existsByAliasAndCreatorNot(association.getAlias(), association.getCreatorId());
    }

    public boolean existsByAliasAndDestinationNot(String alias, String destination) {
        return redisRepository.existsByAliasAndDestinationNot(alias, destination) ||
                jpaRepository.existsByAliasAndDestinationNot(alias, destination);
    }

    public boolean existsByAliasAndCreatorNot(String alias, Long creatorId) {
        return redisRepository.existsByAliasAndCreatorIdNot(alias, creatorId) ||
                jpaRepository.existsByAliasAndCreatorIdNot(alias, creatorId);
    }
}
