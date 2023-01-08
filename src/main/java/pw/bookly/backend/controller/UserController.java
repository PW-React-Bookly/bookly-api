package pw.bookly.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.dao.UserRepository;
import pw.bookly.backend.models.User;
import pw.bookly.backend.services.UserService;
import pw.bookly.backend.web.UserDTO;

import java.util.Collection;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class UserController {
    public static final String USERS_PATH = "/users";
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository repository;
    private final UserService userService;

    public UserController(UserRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<UserDTO>> getAllUsers(@RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        return ResponseEntity.ok(repository.findAll().stream().map(UserDTO::valueFrom).collect(toList()));
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)) }
            )
    })
    @PostMapping(path = "")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        User newUser = userService.validateAndSave(UserDTO.convertToUser(user));
        logger.info("Password is not going to be encoded");
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.valueFrom(newUser));
    }

    private void logHeaders(@RequestHeader HttpHeaders headers) {
        logger.info("Controller request headers {}",
                headers.entrySet()
                        .stream()
                        .map(entry -> String.format("%s->[%s]", entry.getKey(), String.join(",", entry.getValue())))
                        .collect(joining(","))
        );
    }
}
