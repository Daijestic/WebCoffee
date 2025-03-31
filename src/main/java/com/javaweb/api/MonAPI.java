package com.javaweb.api;

import com.javaweb.model.MonDTO;
import com.javaweb.service.MonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MonAPI {

    @Autowired
    private MonService monService;

    @GetMapping("/api/mon")
    public List<MonDTO> testAPI(@RequestParam(name = "name") String name) {
        return monService.findAll(name);
    }

    @DeleteMapping("/api/mon/{id}")
    public void delete(@PathVariable Long id) {
        MonDTO monDTO = monService.findById(id);
        if (monDTO != null) {
            monService.deleteById(id);
        }
    }
}
