package pw.bookly.backend.services;

import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.bookly.backend.models.User;

import java.util.Optional;

public interface UserService {
    User validateAndSave(User user);
    User updatePassword(User user, String password);
    void setPasswordEncoder(PasswordEncoder passwordEncoder);

    String generateToken();

    Optional<User> authorizeUser(HttpHeaders headers);
}
