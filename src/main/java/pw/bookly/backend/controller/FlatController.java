package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.dao.FlatRepository;
import pw.bookly.backend.exceptions.UnauthorizedException;
import pw.bookly.backend.models.Flat;
import pw.bookly.backend.services.SecurityService;
import pw.bookly.backend.web.FlatDTO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = FlatController.FLAT_PATH)
public class FlatController {

    public static final String FLAT_PATH = "/flats";
    private static final Logger logger = LoggerFactory.getLogger(FlatController.class);

    private final FlatRepository repository;
    private final SecurityService securityService;

    public FlatController(FlatRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<FlatDTO>> getAllFlats(@RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        if (securityService.isAuthorized(headers)) {
            return ResponseEntity.ok(repository.findAll().stream().map(FlatDTO::valueFrom).collect(toList()));
        }
        throw new UnauthorizedException("Request is unauthorized", FLAT_PATH);
    }

    @PostMapping(path = "")
    public ResponseEntity<Collection<FlatDTO>> createFlats(@RequestHeader HttpHeaders headers,
                                                                  @Valid @RequestBody List<FlatDTO> companies) {
        logHeaders(headers);
        if (securityService.isAuthorized(headers)) {
            List<Flat> createdCompanies = companies.stream().map(FlatDTO::convertToFlat).collect(toList());
            List<FlatDTO> result = repository.saveAll(createdCompanies)
                    .stream()
                    .map(FlatDTO::valueFrom)
                    .collect(toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
        throw new UnauthorizedException("Unauthorized access to resources.", FLAT_PATH);
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
