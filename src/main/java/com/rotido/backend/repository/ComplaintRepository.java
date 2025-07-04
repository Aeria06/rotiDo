package com.rotido.backend.repository;

import com.rotido.backend.model.Subscription;
import com.rotido.backend.model.Complaint;
import com.rotido.backend.model.Machine;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface ComplaintRepository extends MongoRepository<Complaint, String> {
    List<Complaint> findAllByCustomerNumber(String customerNumber);
    List<Complaint> findByCustomerNumber(String customerNumber); // Added for compatibility
    List<Complaint> findAllByAssignedTo(String assignedTo);
}