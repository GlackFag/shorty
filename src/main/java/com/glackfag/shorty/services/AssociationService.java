package com.glackfag.shorty.services;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.repositories.AssociationRepository;
import com.glackfag.shorty.repositories.RedisAssociationRepository;
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
        jpaRepository.deleteByAlias(alias);
        redisRepository.delete(alias);
        log.info("Deleted association with alias: " + alias);
    }

    @Transactional
    public void updateStats(String alias) {
        Association association = findOne(alias).orElseThrow();

        association.setUsages(association.getUsages() + 1);
        association.setLastUsage(LocalDate.now());

        log.info("Updated stat info for association with alias:" + alias);
        save(association);
    }

    public Optional<Association> findOne(String alias) {
        Optional<Association> association = redisRepository.find(alias);

        log.info("Association with alias:'" + alias + "' " +
                (association.isEmpty() ? "not" : "") + " found in redis");
        return association.isPresent() ? association : jpaRepository.findByAlias(alias);
    }

    public List<Association> findAllByCreatorId(long creatorId){
        return jpaRepository.findByCreatorId(creatorId);
    }

    public boolean exists(Association association) {
        return jpaRepository.existsByAliasAndDestination(
                association.getAlias(), association.getDestination());
    }

    public boolean isAliasAssociatedWithDifferentDestination(String alias, String destination) {
        return jpaRepository.existsByAliasAndDestination(alias, destination);
    }

    public boolean existsByAliasAndCreatorNot(String alias, Long creatorId){
        return jpaRepository.existsByAliasAndCreatorIdNot(alias, creatorId);
    }

}
