package com.rotido.backend.controller.company;


import com.rotido.backend.model.Machine;
import com.rotido.backend.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/machines")
public class MachineController {

    @Autowired
    private MachineRepository machineRepository;

    @PostMapping
    public Machine addMachine(@RequestBody Machine machine) {
        return machineRepository.save(machine);
    }

    @GetMapping
    public List<Machine> getMachines() {
        return machineRepository.findAll();
    }
}