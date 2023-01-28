package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pw.bookly.backend.config.CarControllerConfig;
import pw.bookly.backend.models.car.Car;
import pw.bookly.backend.models.car.CarResponse;
import pw.bookly.backend.web.car.CarDTO;
import org.springframework.data.domain.Pageable;
import pw.bookly.backend.web.car.CarResponseDTO;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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
    public ResponseEntity<CarResponseDTO> getAllCars(Pageable p, @RequestParam Map<String, String> params,
                                                     @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        MultiValueMap<String, String> externalParams = new LinkedMultiValueMap<>();
        String url = carControllerConfig.getCarlyBackend() +
                CARLY_CARS_ENDPOINT +
                "?page=" + (p.getPageNumber() + 1) +
                "&size=" + p.getPageSize();
        if(params.containsKey("carType"))
            externalParams.put("carType", List.of(params.get("carType")));
        if(params.containsKey("brand"))
            externalParams.put("brand", List.of(params.get("brand")));
        if(params.containsKey("priceFrom"))
            externalParams.put("dayPriceMin", List.of(params.get("priceFrom")));
        if(params.containsKey("priceTo"))
            externalParams.put("dayPriceMax", List.of(params.get("priceTo")));
        if(params.containsKey("dateFrom"))
            externalParams.put("startDate", List.of(params.get("dateFrom")));
        if(params.containsKey("dateTo"))
            externalParams.put("endDate", List.of(params.get("dateTo")));

        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParams(externalParams)
                .buildAndExpand(externalParams)
                .toUri();
        ResponseEntity<CarResponse> cars = restTemplate.getForEntity(uri, CarResponse.class);

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
