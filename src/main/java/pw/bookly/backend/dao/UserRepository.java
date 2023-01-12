package pw.bookly.backend.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import pw.bookly.backend.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByEmail(String username);
}
