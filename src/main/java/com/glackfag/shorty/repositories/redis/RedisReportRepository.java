package com.glackfag.shorty.repositories.redis;

import com.glackfag.shorty.models.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisReportRepository extends CrudRepository<Report, Integer> {
    boolean existsByAliasAndReporterId(String alias, long reporterId);
}
