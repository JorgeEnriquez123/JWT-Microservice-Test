package com.jorge.userservice.controller;

import com.jorge.userservice.model.Role;
import com.jorge.userservice.model.dto.UserDTO;
import com.jorge.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    // Quick usage | no service class
    private final UserRepository userRepository;

    @GetMapping("")
    public List<UserDTO> findAll(){
        return userRepository.findAll().stream()
                .map(user ->
                        UserDTO.builder()
                                .username(user.getUsername())
                                .roles(user.getRoles().stream()
                                        .map(Role::getName).collect(Collectors.toSet()))
                                .build())
                .toList();
    }
}
