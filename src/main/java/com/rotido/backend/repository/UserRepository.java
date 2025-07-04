// package com.rotido.backend.repository;

// import com.rotido.backend.model.User;
// import org.springframework.data.mongodb.repository.MongoRepository;

// import java.util.Optional;

// public interface UserRepository extends MongoRepository<User, String> {
//     Optional<User> findByUsername(String username);
// }

package com.rotido.backend.repository;

import com.rotido.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
}
