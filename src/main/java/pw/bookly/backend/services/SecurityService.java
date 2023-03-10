package pw.bookly.backend.services;

import org.springframework.http.HttpHeaders;

public interface SecurityService {
    boolean isAuthenticated(HttpHeaders headers);
    boolean isAuthorized(HttpHeaders headers);
}
