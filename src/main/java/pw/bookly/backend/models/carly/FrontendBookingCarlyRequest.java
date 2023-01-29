package pw.bookly.backend.models.carly;

import lombok.Value;

import java.time.LocalDate;

@Value
public class FrontendBookingCarlyRequest {

    String carId;
    LocalDate beginDate;
    LocalDate endDate;
}
