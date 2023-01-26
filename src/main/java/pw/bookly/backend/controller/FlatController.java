package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.models.Flat;
import pw.bookly.backend.web.FlatDTO;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = FlatController.FLATS_PATH)
public class FlatController {

    public static final String FLATS_PATH = "/flats";
    private static final Logger logger = LoggerFactory.getLogger(FlatController.class);

    public FlatController() {
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<FlatDTO>> getAllFlats(Pageable p,
                                                           @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        Random rand = new Random();
        var mockFlats = Stream.generate(Flat::new)
                .limit(25)
                .peek(flat -> {
                    flat.setDescription(String.format("I'm a flat and my favourite number is %d", rand.nextInt()));
                    flat.setItemExternalId(String.format("%d",rand.nextLong()));
                }).toList();

        return ResponseEntity.ok(mockFlats.stream().map(FlatDTO::valueFrom).collect(toList()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<FlatDTO> getFlat(@PathVariable String id,
                                           @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        Random rand = new Random();
        var mockFlat = new Flat();
        mockFlat.setDescription(String.format("I'm a flat No %s.\nMy favourite number is %d", id, rand.nextInt()));

        return ResponseEntity.ok(FlatDTO.valueFrom(mockFlat));
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
