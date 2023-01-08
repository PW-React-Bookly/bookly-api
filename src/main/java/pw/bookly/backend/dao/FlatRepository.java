package pw.bookly.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.bookly.backend.models.Flat;

public interface FlatRepository extends JpaRepository<Flat, Long> {
}
