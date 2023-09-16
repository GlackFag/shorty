package com.glackfag.shorty.repositories.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CreatorRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CreatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findAliasesByCreatorId(long creatorId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("creatorId", creatorId);

        return jdbcTemplate.queryForList("SELECT alias FROM alias_creator ac WHERE ac.creator_id = :creatorId",
                String.class, parameterSource);
    }

    public List<String> findDestinationsByCreatorId(long creatorId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("creatorId", creatorId);

        return jdbcTemplate.queryForList("SELECT destination FROM alias_destination ad " +
                        "WHERE ad.alias IN (SELECT alias FROM alias_creator ac WHERE ac.creator = : creatorId)",
                String.class, parameterSource);
    }


}
