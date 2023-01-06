package pw.bookly.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.bookly.backend.models.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValue(String token);
}
