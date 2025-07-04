
package com.rotido.backend.repository;
import com.rotido.backend.model.Machine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

// public interface MachineRepository extends MongoRepository<Machine, String> {

//     long countByStatus(String status);

//     @Query(value = "{}", fields = "{totalChapatisMade: 1}")
//     default long sumTotalChapatis() {
//         return findAll().stream()
//             .mapToLong(Machine::getTotalChapatisMade)
//             .sum();
//     }
// }
import java.util.Optional;
import java.util.List;

public interface MachineRepository extends MongoRepository<Machine, String> {

    long countByStatus(String status);

    Optional<Machine> findByMachineId(String machineId);

    @Query(value = "{}", fields = "{totalChapatisMade: 1}")
    default long sumTotalChapatis() {
        return findAll().stream()
            .mapToLong(Machine::getTotalChapatisMade)
            .sum();
    }
}
