package org.example.statistics.repository;

import org.example.statistics.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,String> {
	Optional<User> findByUserName(String userName);
}
