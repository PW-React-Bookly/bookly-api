package pw.bookly.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pw.bookly.backend.config.FlatControllerConfig;
import pw.bookly.backend.exceptions.UnauthorizedException;
import pw.bookly.backend.models.Flat;
import pw.bookly.backend.services.UserService;
import pw.bookly.backend.web.FlatDTO;
import pw.bookly.backend.web.FlatResponseDTO;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<FlatResponseDTO> getAllFlats(Pageable p, @RequestParam Map<String, String> params,
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
            throw new UnauthorizedException("Flatly token can't be fetched", "");
        }
        MultiValueMap<String, String> externalParams = new LinkedMultiValueMap<>();
        if(params.containsKey("priceFrom"))
            externalParams.put("minPrice", List.of(params.get("priceFrom")));
        if(params.containsKey("priceTo"))
            externalParams.put("maxPrice", List.of(params.get("priceTo")));
        if(params.containsKey("dateFrom"))
            externalParams.put("dateFrom", List.of(params.get("dateFrom") + "T00:00:00Z"));
        if(params.containsKey("dateTo"))
            externalParams.put("dateTo", List.of(params.get("dateTo") + "T00:00:00Z"));
        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParams(externalParams)
                .buildAndExpand(externalParams)
                .toUri();
        var requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", getTokenHeader());
        HttpEntity<String> request = new HttpEntity<String>(requestHeaders);
        var response = restTemplate.exchange(uri, HttpMethod.GET, request, Flat[].class);
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
