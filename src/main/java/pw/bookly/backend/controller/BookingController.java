package pw.bookly.backend.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.dao.BookingRepository;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.models.QBooking;
import pw.bookly.backend.web.BookingDTO;

import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = BookingController.BOOKING_PATH)
public class BookingController {

    public static final String BOOKING_PATH = "/bookings";
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private static final QBooking Q_BOOKING = QBooking.booking;
    private final BookingRepository repository;

    public BookingController(BookingRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<BookingDTO>> getAllBookings(Pageable p,
                                                                 @QuerydslPredicate(root = Booking.class) Predicate predicate,
                                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(Q_BOOKING.isCancelled.isFalse());
        predicate = builder.getValue();

        return ResponseEntity.ok(repository.findAll(Objects.requireNonNull(predicate), p)
                .stream().map(BookingDTO::valueFrom).collect(toList()));
    }

    @PostMapping(path = "/cancel/{id}")
    public void cancelBooking(@PathVariable Long id,
                              @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var booking = repository.findById(id);
        if(booking.isPresent())
        {
            var value = booking.get();
            value.setCancelled(true);
            repository.save(value);
        }
    }

    private void logHeaders(@RequestHeader HttpHeaders headers) {
        logger.info("Controller request headers {}",
                headers.entrySet()
                        .stream()
                        .map(entry -> String.format("%s->[%s]", entry.getKey(), String.join(",", entry.getValue())))
                        .collect(joining(","))
        );
    }
}
