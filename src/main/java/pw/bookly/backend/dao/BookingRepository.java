package pw.bookly.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.bookly.backend.models.Booking;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
