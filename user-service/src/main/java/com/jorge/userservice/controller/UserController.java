package com.jorge.userservice.controller;

import com.jorge.userservice.configuration.security.jwt.JwtUtil;
import com.jorge.userservice.exceptions.AuthException;
import com.jorge.userservice.model.Role;
import com.jorge.userservice.model.dto.LoginRequest;
import com.jorge.userservice.model.dto.LoginResponse;
import com.jorge.userservice.model.dto.UserDTO;
import com.jorge.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        try {
            UsernamePasswordAuthenticationToken userLogin =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            UserDetails authenticatedUser = (UserDetails) authenticationManager.authenticate(userLogin).getPrincipal();
            return ResponseEntity.ok().body(
                    LoginResponse.builder()
                            .access_token(jwtUtil.generateToken(authenticatedUser))
                            .build()
            );
        } catch (AuthenticationException ex) {
            throw new AuthException("Login failed. wrong credentials");
        }
    }
}
