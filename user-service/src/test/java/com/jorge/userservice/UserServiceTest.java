package com.jorge.userservice;

import com.jorge.userservice.configuration.security.jwt.JwtUtil;
import com.jorge.userservice.model.Role;
import com.jorge.userservice.model.User;
import com.jorge.userservice.model.dto.LoginRequest;
import com.jorge.userservice.model.dto.LoginResponse;
import com.jorge.userservice.model.dto.UserDTO;
import com.jorge.userservice.model.dto.UserSignUpDTO;
import com.jorge.userservice.repository.RoleRepository;
import com.jorge.userservice.repository.UserRepository;
import com.jorge.userservice.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private  RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void success_findAllUsers(){
        Role role1 = Role.builder().name("role1").build();

        User user1 = User.builder().username("user1").roles(Set.of(role1)).build();
        User user2 = User.builder().username("user2").roles(Set.of(role1)).build();


        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDTO> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    public void success_SaveUsers(){
        Role role = Role.builder().name("USER").build();

        UserSignUpDTO newUserRequest = UserSignUpDTO.builder()
                .username("user1")
                .password("password1")
                .build();

        User newUser = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password1")).thenReturn("encodedPassword1");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserDTO newUserDTO = userService.save(newUserRequest);

        assertNotNull(newUserDTO);
        assertEquals(newUserDTO.getUsername(), newUserRequest.getUsername());
        assertTrue(newUserDTO.getRoles().contains("USER"));
    }

    @Test
    public void success_Login(){
        LoginRequest loginRequest = LoginRequest.builder().username("user1").password("password1").build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("user1", "password1");

        User autenticatedUser = User.builder().id(1L).username("user1").password("encodedPassword1").build();

        when(authenticationManager.authenticate(auth)).thenReturn(new TestingAuthenticationToken(autenticatedUser, null));
        when(jwtUtil.generateToken(autenticatedUser)).thenReturn("jwt");

        LoginResponse loginResponse = userService.login(loginRequest);

        assertNotNull(loginResponse);
        assertEquals(loginResponse.getAccess_token(), "jwt");
    }

    @Test
    public void success_ValidateToken(){
        String jwtToken = "token";
        String usernameFromToken = "user1";

        User userFromToken = User.builder().id(1L).username("user1").password("encodedPassword1").roles(Set.of()).build();

        //when(jwtUtil.extractUsername(jwtToken)).thenReturn(usernameFromToken);
        //when(userRepository.findByUsername(usernameFromToken)).thenReturn(Optional.of(userFromToken));
        when(jwtUtil.validateToken(jwtToken)).thenReturn(userFromToken);

        UserDTO userDTO = userService.validateToken(jwtToken);

        assertNotNull(userDTO);
        assertEquals(userDTO.getUsername(), usernameFromToken);
    }
}
