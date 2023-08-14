package com.glackfag.shorty.repositories;

import com.glackfag.shorty.models.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssociationRepository extends JpaRepository<Association, String> {
    Optional<Association> findByAlias(String alias);
    void deleteByAlias(String alias);
    boolean existsByAliasAndDestination(String alias, String destination);
    boolean existsByAliasAndDestinationIsNotLike(String alias, String destination);
}
