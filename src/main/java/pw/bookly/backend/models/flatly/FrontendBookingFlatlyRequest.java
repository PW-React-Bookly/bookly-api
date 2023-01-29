package pw.bookly.backend.models.flatly;

import lombok.Value;

import java.time.LocalDate;

@Value
public class FrontendBookingFlatlyRequest {

    String flatId;
    LocalDate beginDate;
    LocalDate endDate;
}
