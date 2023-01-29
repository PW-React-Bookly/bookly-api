package pw.bookly.backend.models.park;

import lombok.Value;

import java.util.List;

@Value
public class ParkResponse {
    int noOfPages;
    List<Park> parkingLotsDto;
}
