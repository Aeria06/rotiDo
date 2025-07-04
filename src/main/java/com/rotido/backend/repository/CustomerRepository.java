// package com.rotido.backend.repository;

// import com.rotido.backend.model.Customer;
// import org.springframework.data.mongodb.repository.MongoRepository;

// import java.util.Optional;

// public interface CustomerRepository extends MongoRepository<Customer, String> {
//     Optional<Customer> findByUsername(String username);
// }

package com.rotido.backend.repository;

import com.rotido.backend.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Customer findByCustomerNumber(String customerNumber);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    Optional<Customer> findByUsername(String username);
    void deleteByUsername(String username);
}
