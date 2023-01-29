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
import org.springframework.web.client.RestTemplate;
import pw.bookly.backend.config.CarControllerConfig;
import pw.bookly.backend.dao.BookingRepository;
import pw.bookly.backend.models.Bookable;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.models.QBooking;
import pw.bookly.backend.models.User;
import pw.bookly.backend.models.carly.CarlyBooking;
import pw.bookly.backend.models.carly.CarlyBookingRequest;
import pw.bookly.backend.models.carly.CarlyBookingRequestCustomer;
import pw.bookly.backend.models.carly.FrontendBookingRequest;
import pw.bookly.backend.web.BookingDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = BookingController.BOOKING_PATH)
public class BookingController {

    public static final String BOOKING_PATH = "/bookings";
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private static final QBooking Q_BOOKING = QBooking.booking;
    private final BookingRepository repository;
    private final RestTemplate restTemplate;
    private final CarControllerConfig carControllerConfig;

    public BookingController(BookingRepository repository, RestTemplate restTemplate, CarControllerConfig carControllerConfig) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.carControllerConfig = carControllerConfig;
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

    @GetMapping(path = "/user")
    public ResponseEntity<Collection<BookingDTO>> getAllBookings(Pageable p,
                                                                 String bookableType,
                                                                 @QuerydslPredicate(root = Booking.class) Predicate predicate,
                                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(Q_BOOKING.isCancelled.isFalse());
        builder.and(Q_BOOKING.user.id.eq(Long.valueOf(1))); // TODO Change to real user from token
        builder.and(Q_BOOKING.bookableType.eq(Bookable.valueOf(bookableType.toUpperCase())));
        predicate = builder.getValue();

        return ResponseEntity.ok(repository.findAll(Objects.requireNonNull(predicate), p)
                .stream().map(BookingDTO::valueFrom).collect(toList()));
    }

    @PostMapping(path="/book/carly")
    public ResponseEntity<Void> makeCarlyBooking(@RequestBody FrontendBookingRequest bookingRequest,
                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);

        var request = CarlyBookingRequest.builder()// TODO Change to real frontend fields
                .carId(parseInt(bookingRequest.getCarId()))
                .beginDate(new int[] {
                        bookingRequest.getBeginDate().getYear(),
                        bookingRequest.getBeginDate().getMonth().getValue(),
                        bookingRequest.getBeginDate().getDayOfMonth()
                })
                .endDate(new int[] {
                        bookingRequest.getEndDate().getYear(),
                        bookingRequest.getEndDate().getMonth().getValue(),
                        bookingRequest.getEndDate().getDayOfMonth()
                })
                .beginPlace("Mokotow")
                .beginPosition("Mok")
                .endPlace("Mokotow")
                .endPosition("Mok")
                .isMaintenance(false)
                .customer(new CarlyBookingRequestCustomer(1, "testName", "testSurname"))
                .build();

        /*ResponseEntity<CarlyBooking> carlyBookingResponse = restTemplate.postForEntity(
                carControllerConfig.getCarlyBackend() + "/reservations" ,
                request,
                CarlyBooking.class);
        */
        //var carlyBooking = Objects.requireNonNull(carlyBookingResponse.getBody());
        var booking = new Booking(); // TODO Change to real booking data
        booking.setBookedFrom(LocalDate.now());//carlyBooking.getBeginDate());
        booking.setBookedUntil(LocalDate.now().plusDays(2));//carlyBooking.getEndDate());
        booking.setBookableType(Bookable.CAR);
        booking.setItemExternalId(bookingRequest.getCarId());//carlyBooking.getId()));
        booking.setBookingExternalId(String.valueOf(1));//carlyBooking.getId()));
        booking.setTotalPrice(new BigDecimal(12));
        booking.setCancelled(false);
        var testUser = new User();
        testUser.setId(1);
        booking.setUser(testUser);
        repository.save(booking);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/cancel/{id}")
    public void cancelBooking(@PathVariable Long id,
                              @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var booking = repository.findById(id);
        if(booking.isEmpty())
            return;

        restTemplate.delete(carControllerConfig.getCarlyBackend() + "/reservations/" + booking.get().getBookingExternalId());

        var value = booking.get();
        value.setCancelled(true);
        repository.save(value);

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
