package com.jorge.bears.controller;

import com.jorge.bears.dto.BearDto;
import com.jorge.bears.dto.ErrorResponseDto;
import com.jorge.bears.model.Bear;
import com.jorge.bears.repository.BearRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bear")
@Tag(name = "Bear Controller")
public class BearController {
    private final BearRepository bearRepository;

    @Operation(
            summary = "Returns All Bears",
            description = "Returns all bears available in the database"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All bears were found",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "List", implementation = Bear.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User has no authorization to execute this request",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "An internal error happened in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @Parameters(
            value = {
                    @Parameter(in = ParameterIn.HEADER, name = "auth-user-username", required = true, description = "Username from User (TESTING ONLY)"),
                    @Parameter(in = ParameterIn.HEADER, name = "auth-user-roles", required = true, description = "Roles from User (TESTING ONLY")
            }
    )
    @GetMapping
    public List<Bear> findAll() {
        return bearRepository.findAll();
    }

    @Operation(
            summary = "Saves a bear and returns the object",
            description = "Saves a bear to the database and returns the saved Bear"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Bear was saved successfully",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Bear.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User has no authorization to execute this request",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User has no permission to execute this request",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "An internal error happened in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @Parameters(
            value = {
                    @Parameter(in = ParameterIn.HEADER, name = "auth-user-username", required = true, description = "Username from User (TESTING ONLY)"),
                    @Parameter(in = ParameterIn.HEADER, name = "auth-user-roles", required = true, description = "Roles from User (TESTING ONLY")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Bear> saveBear(@RequestBody BearDto bear) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        bearRepository.save(
                                Bear.builder()
                                        .name(bear.getName())
                                        .species(bear.getSpecies())
                                        .build()
                        )
                );
    }
}
