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
import pw.bookly.backend.config.FlatControllerConfig;
import pw.bookly.backend.models.Flat;
import pw.bookly.backend.services.UserService;
import pw.bookly.backend.web.FlatDTO;
import pw.bookly.backend.web.FlatResponseDTO;

import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = FlatController.FLATS_PATH)
public class FlatController {
    private String token;
    private String username = "user12345@gmail.com";
    private String password = "user12345";

    private final RestTemplate restTemplate;
    private final FlatControllerConfig config;

    public static final String FLATS_PATH = "/flats";
    public static final String LOGIN_PATH = "/auth/login";
    private static final Logger logger = LoggerFactory.getLogger(FlatController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    public FlatController(RestTemplate restTemplate, FlatControllerConfig config, UserService userService) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.userService = userService;
    }

    @GetMapping(path = "")
    public ResponseEntity<FlatResponseDTO> getAllFlats(Pageable p,
                                                           @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var user = userService.authorizeUser(headers);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String url = config.getFlatlyBackend() +
                FLATS_PATH +
                "?page=" + (p.getPageNumber()) +
                "&size=" + p.getPageSize();
        try {
            generateToken();
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", getTokenHeader());
        HttpEntity<String> request = new HttpEntity<String>(requestHeaders);
        var response = restTemplate.exchange(url, HttpMethod.GET, request, Flat[].class);
        var responseDTO = FlatResponseDTO.of(Arrays.stream(response.getBody()).map(FlatDTO::valueFrom).collect(toList()),
                p.getPageNumber(), p.getPageSize());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<FlatDTO> getFlat(@PathVariable String id,
                                           @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        String url = config.getFlatlyBackend() + FLATS_PATH + "/" + id;
        try {
            generateToken();
        } catch (JSONException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", getTokenHeader());
        HttpEntity<String> request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Flat> flat =
                restTemplate.exchange(url, HttpMethod.GET, request, Flat.class);

        return ResponseEntity.ok(FlatDTO.valueFrom(Objects.requireNonNull(flat.getBody())));
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
                restTemplate.postForObject(config.getFlatlyBackend() + LOGIN_PATH, request, String.class);
        JsonNode root = objectMapper.readTree(result);
        token = root.get("jwttoken").asText();
    }
}
