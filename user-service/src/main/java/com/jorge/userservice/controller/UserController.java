package com.jorge.userservice.controller;

import com.jorge.userservice.configuration.security.jwt.JwtUtil;
import com.jorge.userservice.exceptions.AuthException;
import com.jorge.userservice.model.Role;
import com.jorge.userservice.model.User;
import com.jorge.userservice.model.dto.*;
import com.jorge.userservice.repository.UserRepository;
import com.jorge.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "User-Service Controller doc", description = "All endpoints from User-service")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Returns all users from the database"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All users were found",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "List", implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "An internal error happened in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    )
            }
    )
    @GetMapping("")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @Operation(
            summary = "Save User",
            description = "Save user to the database and return it"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User was saved successfully",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "An internal error happened in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    )
            }
    )
    @PostMapping("")
    public ResponseEntity<UserDTO> save(@RequestBody UserSignUpDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        userService.save(user)
                );
    }

    @Operation(
            summary = "Log in",
            description = "User logs in with credentials and returns an Access Token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Login failed due to wrong credentials",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "An internal error happened in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @Operation(
            summary = "Validate token",
            description = "JWT is validated and returns user info as UserDTO"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWT is valid",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "JWT is not valid",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "An internal error happened in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    )
            }
    )

    @PostMapping("/validateToken")
    public ResponseEntity<UserDTO> validateToken(@RequestParam String token) {
        return ResponseEntity.ok().body(userService.validateToken(token));
    }
}
    