package pw.bookly.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.dao.BookingRepository;
import pw.bookly.backend.models.Bookable;
import pw.bookly.backend.specifications.BookingFilters;
import pw.bookly.backend.specifications.BookingSpecification;
import pw.bookly.backend.web.BookingDTO;
import pw.bookly.backend.web.UserDTO;

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
    public ResponseEntity<Collection<BookingDTO>> getAllBookings(@RequestParam String page,
                                                                 @RequestParam String pageSize,
                                                                 @RequestParam(required = false) String firstName,
                                                                 @RequestParam(required = false) String lastName,
                                                                 @RequestParam(required = false) Bookable bookableType,
                                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        BookingFilters bookingFilters = new BookingFilters(firstName, lastName, bookableType);
        var specification = new BookingSpecification(bookingFilters);
        var pageRequest = PageRequest.of(Integer.parseInt(page), Integer.parseInt(pageSize));
        return ResponseEntity.ok(repository.findAll(specification, pageRequest)
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
