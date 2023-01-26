package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.models.Park;
import pw.bookly.backend.web.ParkDTO;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = ParkController.PARKS_PATH)
public class ParkController {

    public static final String PARKS_PATH = "/parks";
    private static final Logger logger = LoggerFactory.getLogger(ParkController.class);

    public ParkController() {
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<ParkDTO>> getAllFlats(Pageable p,
                                                           @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        Random rand = new Random();
        var mockParks = Stream.generate(Park::new)
                .limit(25)
                .peek(park -> {
                    park.setDescription(String.format("I'm a park and my favourite number is %d", rand.nextInt()));
                    park.setItemExternalId(String.format("%d",rand.nextLong()));
                })
                .toList();

        return ResponseEntity.ok(mockParks.stream().map(ParkDTO::valueFrom).collect(toList()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ParkDTO> getFlat(@PathVariable String id,
                                           @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        Random rand = new Random();
        var mockPark = new Park();
        mockPark.setDescription(String.format("I'm a park No %s.\n My favourite number is %d", id, rand.nextInt()));

        return ResponseEntity.ok(ParkDTO.valueFrom(mockPark));
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
