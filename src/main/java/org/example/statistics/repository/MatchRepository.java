package org.example.statistics.repository;

import org.example.statistics.domain.Match;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends CrudRepository<Match,String> {
}
