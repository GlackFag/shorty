package com.glackfag.shorty.repositories;

import com.glackfag.shorty.models.Association;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RedisAssociationRepository {
    private final HashOperations<String, String, Association> hashOperations;
    private static final String HASH_NAME = "associations";

    @Autowired
    public RedisAssociationRepository(RedisTemplate<String, Association> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(Association association) {
        hashOperations.put(HASH_NAME, association.getAlias(), association);
    }

    public Optional<Association> find(String alias) {
        return Optional.ofNullable(hashOperations.get(HASH_NAME, alias));
    }

    public void delete(String alias){
        hashOperations.delete(HASH_NAME, alias);
    }
}