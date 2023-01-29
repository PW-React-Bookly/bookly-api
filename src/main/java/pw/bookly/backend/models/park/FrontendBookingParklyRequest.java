package pw.bookly.backend.models.park;

import lombok.Value;

import java.time.LocalDate;

@Value
public class FrontendBookingParklyRequest {

    String parkId;
    LocalDate beginDate;
    LocalDate endDate;
}
