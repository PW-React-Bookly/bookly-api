package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pw.bookly.backend.config.ParkControllerConfig;
import pw.bookly.backend.models.park.Park;
import pw.bookly.backend.models.park.ParkResponse;
import pw.bookly.backend.web.park.ParkResponseDTO;

import java.util.ArrayList;
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

    public ParkController(ParkControllerConfig parkControllerConfig, RestTemplate restTemplate) {
        this.parkControllerConfig = parkControllerConfig;
        this.restTemplate = restTemplate;
    }

    @GetMapping(path = "")
    public ResponseEntity<ParkResponseDTO>
        getAllParks(Pageable p, @RequestHeader HttpHeaders headers) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        logHeaders(headers);
        String token = "";
        try {
            token = authenticate(headers);
        } catch (JSONException e) {}

        headers.add("Authorization", "Bearer " + token);

        int firstId = p.getPageSize() * p.getPageNumber();
        int lastId = p.getPageSize() * (p.getPageNumber() + 1) - 1;

        int firstPage = firstId / 50;
        int lastPage = lastId / 50;

        if (firstPage == lastPage) {
            String url = parkControllerConfig.getParklyBackend() + PARKLY_ENDPOINT_PATH + "/getPage/" + (firstPage + 1) + "/sortDescending/true";

            ResponseEntity<ParkResponse> parks = restTemplate.getForEntity(url, ParkResponse.class);
            var ret = Objects.requireNonNull(parks.getBody()).getParkingLotsDto()
                    .subList(firstId % 50, lastId % 50 + 1);

            return ResponseEntity.ok(ParkResponseDTO.valueFrom(new ParkResponse(parks.getBody().getNoOfPages(), ret)));
        } else {

            ArrayList<Park> parkingList = new ArrayList<>();

            for(int i = firstPage; i <= lastPage; ++i) {
                String url = parkControllerConfig.getParklyBackend() + PARKLY_ENDPOINT_PATH + "/getPage/" + i + 1 + "/sortDescending/false";
                ResponseEntity<ParkResponse> parks = restTemplate.getForEntity(url, ParkResponse.class);

                if (i == firstPage) {
                    parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto()
                            .subList(firstId % 50, 50));
                } else if (i == lastPage) {
                    parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto()
                            .subList(0, lastPage % 50 + 1));
                } else {
                    parkingList.addAll(Objects.requireNonNull(parks.getBody()).getParkingLotsDto());
                }
            }

            return ResponseEntity.ok(ParkResponseDTO.valueFrom(new ParkResponse(0, parkingList)));
        }
    }

    private String authenticate(HttpHeaders headers) throws JSONException {
        String url = parkControllerConfig.getParklyBackend() + "/authenticate";
        var credentials = new JSONObject();
        credentials.put("username", "simpleuser");
        credentials.put("password", "Simple123*");
        ResponseEntity<String> response
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(credentials.toString(), headers), String.class);
        return response.getBody();
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
