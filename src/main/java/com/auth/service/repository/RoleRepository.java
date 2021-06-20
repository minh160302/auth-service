package com.auth.service.repository;

import com.auth.service.entity.Role;
import com.auth.service.entity.enumeration.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
