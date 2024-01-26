package com.jorge.bears.controller;

import com.jorge.bears.model.Bear;
import com.jorge.bears.repository.BearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bear")
public class BearController {
    private final BearRepository bearRepository;
    @GetMapping
    public List<Bear> findAll(){
        return bearRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Bear saveBear(Bear bear){
        return bearRepository.save(bear);
    }
}
