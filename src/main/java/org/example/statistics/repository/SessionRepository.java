package org.example.statistics.repository;

import org.example.statistics.domain.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, String> {
	Optional<Session> findByUserName(String userName);
}
