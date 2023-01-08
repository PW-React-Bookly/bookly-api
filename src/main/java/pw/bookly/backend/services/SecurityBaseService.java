package pw.bookly.backend.services;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
class SecurityBaseService implements SecurityService {

    private static final String SECURITY_HEADER = "security-header";
    private final String SECURITY_HEADER_VALUE = "secureMe";

    @Override
    public boolean isAuthenticated(HttpHeaders headers) {
        if (headers == null) {
            return false;
        }
        return headers.containsKey(SECURITY_HEADER) && SECURITY_HEADER_VALUE.equals(headers.getFirst(SECURITY_HEADER));
    }

    @Override
    public boolean isAuthorized(HttpHeaders headers) {
        // return isAuthenticated(headers);
        return true;
    }
}
