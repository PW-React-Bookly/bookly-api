package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.models.Car;
import pw.bookly.backend.web.CarDTO;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = CarController.CARS_PATH)
public class CarController {

    public static final String CARS_PATH = "/cars";
    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    public CarController() {
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<CarDTO>> getAllCars(Pageable p,
                                                         @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        Random rand = new Random();
        var mockCars = Stream.generate(Car::new)
                .limit(25)
                .peek(car -> {
                    car.setDescription(String.format("I'm a car and my favourite number is %d", rand.nextInt()));
                    car.setItemExternalId(String.format("%d",rand.nextLong()));
                })
                .toList();

        return ResponseEntity.ok(mockCars.stream().map(CarDTO::valueFrom).collect(toList()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable String id,
                                         @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        Random rand = new Random();
        var mockCar = new Car();
        mockCar.setDescription(String.format("I'm a car No %s.\n My favourite number is %d", id, rand.nextInt()));

        return ResponseEntity.ok(CarDTO.valueFrom(mockCar));
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
