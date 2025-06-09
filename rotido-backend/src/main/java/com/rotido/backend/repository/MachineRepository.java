package com.rotido.backend.repository;
import com.rotido.backend.model.Machine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface MachineRepository extends MongoRepository<Machine, String> {
}