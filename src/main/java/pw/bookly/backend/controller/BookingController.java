package pw.bookly.backend.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.dao.BookingRepository;
import pw.bookly.backend.models.Bookable;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.models.QBooking;
import pw.bookly.backend.models.carly.CarlyBookingRequest;
import pw.bookly.backend.models.carly.CarlyBookingRequestCustomer;
import pw.bookly.backend.models.carly.FrontendBookingCarlyRequest;
import pw.bookly.backend.models.flatly.FrontendBookingFlatlyRequest;
import pw.bookly.backend.models.park.FrontendBookingParklyRequest;
import pw.bookly.backend.services.UserService;
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
    private final UserService userService;


    public BookingController(BookingRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
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
        var user = userService.authorizeUser(headers);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(Q_BOOKING.isCancelled.isFalse());
        builder.and(Q_BOOKING.user.id.eq(user.get().getId()));
        builder.and(Q_BOOKING.bookableType.eq(Bookable.valueOf(bookableType.toUpperCase())));
        predicate = builder.getValue();

        return ResponseEntity.ok(repository.findAll(Objects.requireNonNull(predicate), p)
                .stream().map(BookingDTO::valueFrom).collect(toList()));
    }

    @PostMapping(path="/book/carly")
    public ResponseEntity<Void> makeCarlyBooking(@RequestBody FrontendBookingCarlyRequest bookingRequest,
                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var user = userService.authorizeUser(headers);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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
                .customer(new CarlyBookingRequestCustomer(user.get().getId(), "testName", "testSurname"))
                .build();

        /*ResponseEntity<CarlyBooking> carlyBookingResponse = restTemplate.postForEntity(
                carControllerConfig.getCarlyBackend() + "/reservations" ,
                request,
                CarlyBooking.class);
        */
        //var carlyBooking = Objects.requireNonNull(carlyBookingResponse.getBody());
        var booking = new Booking(); // TODO Change to real booking data
        booking.setBookedFrom(bookingRequest.getBeginDate());
        booking.setBookedUntil(bookingRequest.getEndDate());
        booking.setBookableType(Bookable.CAR);
        booking.setItemExternalId(bookingRequest.getCarId());
        booking.setBookingExternalId(String.valueOf(1));//carlyBooking.getId()));
        booking.setTotalPrice(new BigDecimal(12));
        booking.setCancelled(false);
        booking.setUser(user.get());
        repository.save(booking);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path="/book/flatly")
    public ResponseEntity<Void> makeFlatlyBooking(@RequestBody FrontendBookingFlatlyRequest bookingRequest,
                                                 @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var user = userService.authorizeUser(headers);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        /*var request = CarlyBookingRequest.builder()// TODO Change to real frontend fields
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
                .customer(new CarlyBookingRequestCustomer(user.get().getId(), "testName", "testSurname"))
                .build();

        /*ResponseEntity<CarlyBooking> carlyBookingResponse = restTemplate.postForEntity(
                carControllerConfig.getCarlyBackend() + "/reservations" ,
                request,
                CarlyBooking.class);
        */
        //var carlyBooking = Objects.requireNonNull(carlyBookingResponse.getBody());
        var booking = new Booking(); // TODO Change to real booking data
        booking.setBookedFrom(bookingRequest.getBeginDate());
        booking.setBookedUntil(bookingRequest.getEndDate());
        booking.setBookableType(Bookable.FLAT);
        booking.setItemExternalId(bookingRequest.getFlatId());
        booking.setBookingExternalId(String.valueOf(1));//carlyBooking.getId()));
        booking.setTotalPrice(new BigDecimal(12));
        booking.setCancelled(false);
        booking.setUser(user.get());
        repository.save(booking);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path="/book/parkly")
    public ResponseEntity<Void> makeParklyBooking(@RequestBody FrontendBookingParklyRequest bookingRequest,
                                                  @RequestHeader HttpHeaders headers) {
        logHeaders(headers);
        var user = userService.authorizeUser(headers);
        if(user.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        /*var request = CarlyBookingRequest.builder()// TODO Change to real frontend fields
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
                .customer(new CarlyBookingRequestCustomer(user.get().getId(), "testName", "testSurname"))
                .build();

        /*ResponseEntity<CarlyBooking> carlyBookingResponse = restTemplate.postForEntity(
                carControllerConfig.getCarlyBackend() + "/reservations" ,
                request,
                CarlyBooking.class);
        */
        //var carlyBooking = Objects.requireNonNull(carlyBookingResponse.getBody());
        var booking = new Booking(); // TODO Change to real booking data
        booking.setBookedFrom(bookingRequest.getBeginDate());
        booking.setBookedUntil(bookingRequest.getEndDate());
        booking.setBookableType(Bookable.PARK);
        booking.setItemExternalId(bookingRequest.getParkId());
        booking.setBookingExternalId(String.valueOf(1));//carlyBooking.getId()));
        booking.setTotalPrice(new BigDecimal(12));
        booking.setCancelled(false);
        booking.setUser(user.get());
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

        //restTemplate.delete(carControllerConfig.getCarlyBackend() + "/reservations/" + booking.get().getBookingExternalId());

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
