package pw.bookly.backend.models.carly;

import lombok.Value;

import java.time.LocalDate;

@Value
public class CarlyBooking {

    int id;
    Car car;
    LocalDate beginDate;
    String beginPlace;
    String beginPosition;
    LocalDate endDate;
    String endPlace;
    String endPosition;
    boolean isMaintenance;
    String details;
    CarlyBookingResponseCustomer customer;
}
