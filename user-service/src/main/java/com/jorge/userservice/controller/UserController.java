package com.jorge.userservice.controller;

import com.jorge.userservice.configuration.security.jwt.JwtUtil;
import com.jorge.userservice.exceptions.AuthException;
import com.jorge.userservice.model.Role;
import com.jorge.userservice.model.User;
import com.jorge.userservice.model.dto.LoginRequest;
import com.jorge.userservice.model.dto.LoginResponse;
import com.jorge.userservice.model.dto.UserDTO;
import com.jorge.userservice.model.dto.UserSignUpDTO;
import com.jorge.userservice.repository.UserRepository;
import com.jorge.userservice.service.UserService;
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
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public List<UserDTO> findAll(){
        return userService.findAll();
    }
    @PostMapping("/register")
    public UserDTO save(@RequestBody UserSignUpDTO user){
        return userService.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<UserDTO> validateToken(@RequestParam String token){
        return ResponseEntity.ok().body(userService.validateToken(token));
    }
}
