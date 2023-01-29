package pw.bookly.backend.models.carly;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class CarlyBookingRequest {
    int carId;
    int[] beginDate;
    String beginPlace;
    String beginPosition;
    int[] endDate;
    String endPlace;
    String endPosition;
    boolean isMaintenance;
    CarlyBookingRequestCustomer customer;
}
