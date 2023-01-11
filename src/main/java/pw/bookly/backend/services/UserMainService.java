package pw.bookly.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.bookly.backend.dao.UserRepository;
import pw.bookly.backend.exceptions.UserValidationException;
import pw.bookly.backend.models.User;

import java.util.Optional;

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

    @Override
    public User updatePassword(User user, String password) {

        if(!isValidUser(user))
            return user;

        logger.info("Setting new password.");
        user.setPasswordHash(passwordEncoder.encode(password));
        user = userRepository.save(user);
        return user;
    }

    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isValidUser(User user) {
        if (user != null) {
            if (!isValid(user.getUsername())) {
                logger.error("Empty username.");
                throw new UserValidationException("Empty username.");
            }
            if (!isValid(user.getPassword())) {
                logger.error("Empty user password.");
                throw new UserValidationException("Empty user password.");
            }
            if (!isValid(user.getEmail())) {
                logger.error("Empty email.");
                throw new UserValidationException("Empty email.");
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