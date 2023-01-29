package pw.bookly.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.bookly.backend.dao.UserRepository;
import pw.bookly.backend.exceptions.UserValidationException;
import pw.bookly.backend.models.User;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class UserMainService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserMainService.class);

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserMainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User validateAndSave(User user) {
        if (isValidUser(user)) {
            logger.info("User is valid");
            Optional<User> dbUser = userRepository.findByEmail(user.getUsername());
            if (dbUser.isPresent()) {
                logger.info("User already exists. Updating it.");
                user.setId(dbUser.get().getId());
            }
            user = userRepository.save(user);
            logger.info("User was saved.");
        }
        return user;
    }

    public String generateToken() {
        Random r = new Random();
        return r.ints(48, 123)
                .filter(num -> (num < 58 || num > 64) && (num < 91 || num > 96))
                .limit(100)
                .mapToObj(c -> (char) c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();
    }

    @Override
    public Optional<User> authorizeUser(HttpHeaders headers) {
        if(!headers.containsKey("Authorization"))
            return Optional.empty();
        String header = Objects.requireNonNull(headers.get("Authorization")).get(0);
        String token = header.split(" ")[1];
        return userRepository.findByJwtToken(token);
    }

    @Override
    public User updatePassword(User user, String password) {

        if(!isValidUser(user))
            return user;

        logger.info("Setting new password.");
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);
        return user;
    }

    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isValidUser(User user) {
        if (user != null) {
            if (!isValid(user.getEmail())) {
                logger.error("Empty email.");
                throw new UserValidationException("Empty email.");
            }
            if (!isValid(user.getPassword())) {
                logger.error("Empty user password.");
                throw new UserValidationException("Empty user password.");
            }
            return true;
        }
        logger.error("User is null.");
        throw new UserValidationException("User is null.");
    }

    private boolean isValid(String value) {
        return value != null && !value.isBlank();
    }
}
