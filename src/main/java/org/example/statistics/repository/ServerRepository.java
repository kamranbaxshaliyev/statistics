package org.example.statistics.repository;

import org.example.statistics.domain.Server;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends CrudRepository<Server,String> {
}
