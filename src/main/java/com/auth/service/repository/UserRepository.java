package com.auth.service.repository;

import com.auth.service.dto.UserClientDTO;
import com.auth.service.dto.UserDTO;
import com.auth.service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  UserClientDTO findUserClientDTOByUsername(String username);
}
