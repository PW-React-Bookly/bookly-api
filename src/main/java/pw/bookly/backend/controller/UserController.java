package pw.bookly.backend.controller;

import com.querydsl.core.BooleanBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.dao.UserRepository;
import pw.bookly.backend.models.QUser;
import pw.bookly.backend.models.User;
import pw.bookly.backend.services.UserService;
import pw.bookly.backend.web.TokenResponseDTO;
import pw.bookly.backend.web.UserDTO;

import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class UserController {
    public static final String USERS_PATH = "/users";
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final QUser Q_USER = QUser.user;
    private final UserRepository repository;
    private final UserService userService;

    public UserController(UserRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<UserDTO>> getAllUsers(Pageable p, @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        BooleanBuilder builder = new BooleanBuilder(Q_USER.isActive.isTrue());
        var predicate = builder.getValue();

        return ResponseEntity.ok(repository.findAll(Objects.requireNonNull(predicate), p)
                .stream().map(UserDTO::valueFrom).collect(toList()));
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

    @PostMapping(path = "/login")
    public ResponseEntity<TokenResponseDTO> signInUser(@RequestBody UserDTO user) {
        var dbUser = repository.findByEmail(user.email());
        if(dbUser.isPresent() && Objects.equals(dbUser.get().getPassword(), user.password())) {
            dbUser.get().setJwtToken(userService.generateToken());
            repository.save(dbUser.get());
            var token = new TokenResponseDTO(dbUser.get().getJwtToken());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



    @PostMapping(path = "/ban/{id}")
    public void banUser(@PathVariable Long id,
                        @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var user = repository.findById(id);
        if(user.isPresent())
        {
            var value = user.get();
            value.setActive(false);
            repository.save(value);
        }
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
