package pw.bookly.backend.controller;

import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.bookly.backend.dao.BookingRepository;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.web.BookingDTO;

import java.util.Collection;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = BookingController.BOOKING_PATH)
public class BookingController {

    public static final String BOOKING_PATH = "/bookings";
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingRepository repository;

    public BookingController(BookingRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<BookingDTO>> getAllBookings(Pageable p,
                                                                 @QuerydslPredicate(root = Booking.class) Predicate predicate,
                                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        return ResponseEntity.ok(repository.findAll(predicate, p)
                .stream().map(BookingDTO::valueFrom).collect(toList()));
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
