package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pw.bookly.backend.config.CarControllerConfig;
import pw.bookly.backend.models.carly.*;
import pw.bookly.backend.web.carly.CarDTO;
import org.springframework.data.domain.Pageable;
import pw.bookly.backend.web.carly.CarResponseDTO;

import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping(path = CarController.CARS_PATH)
public class CarController {

    private final CarControllerConfig carControllerConfig;
    private final RestTemplate restTemplate;
    public static final String CARS_PATH = "/cars";
    public static final String CARLY_CARS_ENDPOINT = "/cars";
    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    public CarController(RestTemplate restTemplate, CarControllerConfig carControllerConfig) {
        this.restTemplate = restTemplate;
        this.carControllerConfig = carControllerConfig;
    }

    @GetMapping(path = "")
    public ResponseEntity<CarResponseDTO> getAllCars(Pageable p, String carType,
                                                     @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        String url = carControllerConfig.getCarlyBackend() +
                CARLY_CARS_ENDPOINT +
                "?page=" + (p.getPageNumber() + 1) +
                "&size=" + p.getPageSize();
        if(carType != null)
            url += "&carType=" + carType;


        ResponseEntity<CarResponse> cars = restTemplate.getForEntity(url, CarResponse.class);

        return ResponseEntity.ok(CarResponseDTO.valueFrom(Objects.requireNonNull(cars.getBody())));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable String id,
                                         @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        ResponseEntity<Car> cars = restTemplate.getForEntity(carControllerConfig.getCarlyBackend() + CARLY_CARS_ENDPOINT + "/" + id, Car.class);

        return ResponseEntity.ok(CarDTO.valueFrom(Objects.requireNonNull(cars.getBody())));
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
