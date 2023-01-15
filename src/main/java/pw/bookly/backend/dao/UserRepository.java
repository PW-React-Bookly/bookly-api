package pw.bookly.backend.dao;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import pw.bookly.backend.models.User;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long>,
        QuerydslPredicateExecutor<User> {
    Optional<User> findByEmail(String username);
}
