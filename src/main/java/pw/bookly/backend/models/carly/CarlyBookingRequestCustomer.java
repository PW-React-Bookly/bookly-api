package pw.bookly.backend.models.carly;

import lombok.Value;

@Value
public class CarlyBookingRequestCustomer {

    int booklyId;
    String name;
    String surname;
}
