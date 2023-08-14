package com.glackfag.shorty.services;

import com.glackfag.shorty.models.Association;
import com.glackfag.shorty.repositories.AssociationRepository;
import com.glackfag.shorty.repositories.RedisAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
    }

    @Transactional
    public void delete(String alias) {
        jpaRepository.deleteByAlias(alias);
        redisRepository.delete(alias);
    }

    @Transactional
    public void updateStats(String alias){
        Association association = findOne(alias).orElseThrow();

        association.setUsages(association.getUsages() + 1);
        association.setLastUsage(LocalDate.now());
    }

    public Optional<Association> findOne(String alias) {
        Optional<Association> association = redisRepository.find(alias);

        return association.isPresent() ? association : jpaRepository.findByAlias(alias);
    }

    public boolean exists(Association association) {
        return jpaRepository.existsByAliasAndDestination(
                association.getAlias(), association.getDestination());
    }

    public boolean isAliasAssociatedWithDifferentDestination(String alias, String destination) {
        return jpaRepository.existsByAliasAndDestination(alias, destination);
    }
}
