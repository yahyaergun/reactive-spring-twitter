package com.yergun.twitter.repository;

import com.yergun.twitter.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, Long> {
}
