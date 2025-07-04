package com.rotido.backend.repository;

import com.rotido.backend.model.ServiceProvider;
import org.springframework.data.mongodb.repository.MongoRepository;

// public interface ServiceProviderRepository extends MongoRepository<ServiceProvider, String> {
// }

// package com.rotido.backend.repository;

// import com.rotido.backend.model.ServiceProvider;
// import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ServiceProviderRepository extends MongoRepository<ServiceProvider, String> {
    Optional<ServiceProvider> findByUsername(String username);
    void deleteByUsername(String username);
}
