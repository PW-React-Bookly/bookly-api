package pw.bookly.backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import pw.bookly.backend.models.User;

public interface UserService {
    User validateAndSave(User user);
    User updatePassword(User user, String password);
    void setPasswordEncoder(PasswordEncoder passwordEncoder);
}
