package pw.bookly.backend.models.carly;

import lombok.Value;

@Value
public class CarlyBookingRequestCustomer {

    long booklyId;
    String name;
    String surname;
}
