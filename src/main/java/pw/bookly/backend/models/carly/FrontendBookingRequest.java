package pw.bookly.backend.models.carly;

import lombok.Value;

import java.time.LocalDate;

@Value
public class FrontendBookingRequest {

    String carId;
    LocalDate beginDate;
    LocalDate endDate;
}
