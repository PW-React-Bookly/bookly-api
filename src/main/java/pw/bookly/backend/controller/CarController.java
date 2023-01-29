package pw.bookly.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pw.bookly.backend.config.CarControllerConfig;
import pw.bookly.backend.models.User;
import pw.bookly.backend.models.Flat;
import pw.bookly.backend.models.carly.*;
import pw.bookly.backend.services.UserService;
import pw.bookly.backend.web.FlatDTO;
import pw.bookly.backend.web.FlatResponseDTO;
import pw.bookly.backend.web.carly.CarDTO;
import org.springframework.data.domain.Pageable;
import pw.bookly.backend.web.carly.CarResponseDTO;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = CarController.CARS_PATH)
public class CarController {
    private String token;
    private String username = "Aaa";
    private String password = "Aaa";

    private final CarControllerConfig carControllerConfig;
    private final UserService userService;
    private final RestTemplate restTemplate;

    public static final String CARS_PATH = "/cars";
    public static final String LOGIN_PATH = "/auth/login";
    private static final Logger logger = LoggerFactory.getLogger(CarController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CarController(RestTemplate restTemplate, CarControllerConfig carControllerConfig, UserService userService) {
        this.restTemplate = restTemplate;
        this.carControllerConfig = carControllerConfig;
        this.userService = userService;
    }

    @GetMapping(path = "")
    public ResponseEntity<CarResponseDTO> getAllCars(Pageable p, @RequestParam Map<String, String> params,
                                                     @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var user = userService.authorizeUser(headers);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        MultiValueMap<String, String> externalParams = new LinkedMultiValueMap<>();
        String url = carControllerConfig.getCarlyBackend() +
                CARS_PATH +
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

//        try {
//            generateToken();
//        } catch (JSONException | JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParams(externalParams)
                .buildAndExpand(externalParams)
                .toUri();
        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBYWEiLCJzdXJuYW1lIjoiYWRtaW40IiwiaXAiOiIyMTMuMTM0LjE5MS40OjEzNDE1IiwibmFtZSI6ImFkbWluNCIsInNjb3BlcyI6IlJPTEVfQ0FSTFlBRE1JTiIsImV4cCI6MTY3NTA5ODEwNiwiaWF0IjoxNjc1MDExNzA2LCJlbWFpbCI6InRlc3Q0QHRlc3QucGwiLCJ1c2VyLWFnZW50IjoiSmF2YS8xNy4wLjYifQ.Hx-IHAz31VkpISjpv3YopDmG4NUSw1lPvcNhPG4VgePMbemvKlpTGtQDiSdDix1pmi_TQzUcjvUgxjC74Dzsgg");
        HttpEntity<String> request = new HttpEntity<String>(requestHeaders);
        var response = restTemplate.exchange(uri, HttpMethod.GET, request, CarResponse.class);

        return ResponseEntity.ok(CarResponseDTO.valueFrom(Objects.requireNonNull(response.getBody())));

        /*var responseDTO = CarResponseDTO.of(Arrays.stream(response.getBody()).map(CarResponseDTO::valueFrom).collect(toList()),
                p.getPageNumber(), p.getPageSize());
        return ResponseEntity.ok(responseDTO);*/
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable String id,
                                         @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

//        try {
//            generateToken();
//        } catch (JSONException | JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        var requestHeaders = new HttpHeaders();
//        requestHeaders.set("Authorization", getTokenHeader());
//        logHeaders(headers);
        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBYWEiLCJzdXJuYW1lIjoiYWRtaW40IiwiaXAiOiIyMTMuMTM0LjE5MS40OjEzNDE1IiwibmFtZSI6ImFkbWluNCIsInNjb3BlcyI6IlJPTEVfQ0FSTFlBRE1JTiIsImV4cCI6MTY3NTA5ODEwNiwiaWF0IjoxNjc1MDExNzA2LCJlbWFpbCI6InRlc3Q0QHRlc3QucGwiLCJ1c2VyLWFnZW50IjoiSmF2YS8xNy4wLjYifQ.Hx-IHAz31VkpISjpv3YopDmG4NUSw1lPvcNhPG4VgePMbemvKlpTGtQDiSdDix1pmi_TQzUcjvUgxjC74Dzsgg");

        ResponseEntity<Car> cars = restTemplate.getForEntity(carControllerConfig.getCarlyBackend() + CARS_PATH + "/" + id, Car.class);

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

    private String getTokenHeader() {
        return "Bearer " + token;
    }

    private void generateToken() throws JSONException, JsonProcessingException {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var data = new JSONObject();
        data.put("username", username);
        data.put("password", password);
        HttpEntity<String> request = new HttpEntity<String>(data.toString(), headers);
        String result =
                restTemplate.postForObject(carControllerConfig.getCarlyBackend() + LOGIN_PATH, request, String.class);
        JsonNode root = objectMapper.readTree(result);
        token = root.get("jwttoken").asText();
    }
}
