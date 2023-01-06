package pw.bookly.backend.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import pw.bookly.backend.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
