package com.glackfag.shorty.repositories.redis;

import com.glackfag.shorty.models.Association;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisAssociationRepository extends CrudRepository<Association, String> {
    List<Association> findByCreatorId(long creatorId);

    boolean existsByAliasAndCreatorId(String alias, Long creatorId);

    boolean existsByAliasAndDestination(String alias, String destination);

    default boolean existsByAliasAndCreatorIdNot(String alias, Long creatorId) {
        return existsById(alias) && !existsByAliasAndCreatorId(alias, creatorId);
    }
    default boolean existsByAliasAndDestinationNot(String alias, String destination){
        return existsById(alias) && !existsByAliasAndDestination(alias, destination);
    }
}