package pw.bookly.backend.models.carly;

import lombok.Value;

@Value
public class CarlyBookingResponseCustomer {

    int id;
    int booklyId;
    String name;
    String surname;
    boolean isBlocked;
}
