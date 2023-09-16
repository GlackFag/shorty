package com.glackfag.shorty.repositories.jpa;

import com.glackfag.shorty.models.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociationRepository extends JpaRepository<Association, String> {
    List<Association> findByCreatorId(long creatorId);
    boolean existsByAliasAndDestinationNot(String alias, String destination);
    boolean existsByAliasAndCreatorIdNot(String alias, Long creatorId);
}
