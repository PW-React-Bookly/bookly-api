package pw.bookly.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pw.bookly.backend.config.ParkControllerConfig;
import pw.bookly.backend.models.park.Park;
import pw.bookly.backend.models.park.ParkResponse;
import pw.bookly.backend.web.park.ParkDTO;
import pw.bookly.backend.web.park.ParkResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping(path = ParkController.PARKS_PATH)
public class ParkController {
    private final ParkControllerConfig parkControllerConfig;
    private final RestTemplate restTemplate;
    public static final String PARKS_PATH = "/parks";
    public static final String PARKLY_ENDPOINT_PATH = "/parkings";
    private static final Logger logger = LoggerFactory.getLogger(ParkController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ParkController(ParkControllerConfig parkControllerConfig, RestTemplate restTemplate) {
        this.parkControllerConfig = parkControllerConfig;
        this.restTemplate = restTemplate;
    }

    @GetMapping(path = "")
    public ResponseEntity<ParkResponseDTO> getAllParks(Pageable p, @RequestHeader HttpHeaders headers) {

        logHeaders(headers);
        String token;
        try {
            token = authenticate();
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<String>(requestHeaders);

        int firstId = p.getPageSize() * p.getPageNumber();
        int lastId = p.getPageSize() * (p.getPageNumber() + 1) - 1;

        int firstPage = firstId / 50;
        int lastPage = lastId / 50;

        if (firstPage == lastPage) {
            String url = parkControllerConfig.getParklyBackend() + PARKLY_ENDPOINT_PATH + "/getPage/" + (firstPage + 1) + "/sortDescending/true";

            ResponseEntity<ParkResponse> parks = restTemplate.exchange(url, HttpMethod.GET, request, ParkResponse.class);
            List<Park> result = new ArrayList<>();
            try {
                result = Objects.requireNonNull(parks.getBody()).getParkingLotsDto()
                        .subList(firstId % 50, Math.min(lastId % 50 + 1, parks.getBody().getParkingLotsDto().size()));
            } catch (Exception e) {
                result = Objects.requireNonNull(parks.getBody()).getParkingLotsDto();
            }

            return ResponseEntity.ok(ParkResponseDTO.valueFrom(new ParkResponse(1, result)));

        } else {

            ArrayList<Park> parkingList = new ArrayList<>();

            for(int i = firstPage; i <= lastPage; ++i) {
                String url = parkControllerConfig.getParklyBackend() + PARKLY_ENDPOINT_PATH + "/getPage/" + i + 1 + "/sortDescending/false";

                ResponseEntity<ParkResponse> parks = restTemplate.exchange(url, HttpMethod.GET, request, ParkResponse.class);

                try {
                    if (i == firstPage) {
                        parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto()
                                .subList(firstId % 50, 50));
                    } else if (i == lastPage) {
                        parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto()
                                .subList(0, lastPage % 50));
                    } else {
                        parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto());
                    }
                } catch (Exception e)
                {
                    parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto());
                }
            }
            return ResponseEntity.ok(ParkResponseDTO.valueFrom(new ParkResponse(1, parkingList)));
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ParkDTO> getParking(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        String token;
        try {
            token = authenticate();
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<String>(requestHeaders);

        String url = parkControllerConfig.getParklyBackend() + PARKLY_ENDPOINT_PATH + "/get/" + id;
        ResponseEntity<Park> park = restTemplate.exchange(url, HttpMethod.GET, request, Park.class);

        return ResponseEntity.ok(ParkDTO.valueFrom(Objects.requireNonNull(park.getBody())));
    }

    private String authenticate() throws JSONException, JsonProcessingException {
        String url = parkControllerConfig.getParklyBackend() + "/authenticate";
        var credentials = new JSONObject();
        credentials.put("username", "simpleuser");
        credentials.put("password", "Simple123*");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(credentials.toString(), headers);
        var result = restTemplate.postForObject(url, request, String.class);
        JsonNode root = objectMapper.readTree(result);
        return root.get("jwttoken").asText();
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
