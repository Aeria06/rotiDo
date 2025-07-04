// package com.rotido.backend.repository;

// import com.rotido.backend.model.Allotment;
// import org.springframework.data.mongodb.repository.MongoRepository;

// import java.util.List;

// public interface AllotmentRepository extends MongoRepository<Allotment, String> {
//     List<Allotment> findByCustomerNumber(String customerNumber);
// }

package com.rotido.backend.repository;

import com.rotido.backend.model.Allotment;
import com.rotido.backend.model.SoldMachine;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AllotmentRepository extends MongoRepository<Allotment, String> {
    List<Allotment> findByCustomerNumber(String customerNumber);
    
    Optional<Allotment> findByMachineId(String machineId);
    
    List<Allotment> findAllByMachineId(String machineId);
}
