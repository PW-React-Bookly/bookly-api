package pw.bookly.backend.models.park;

import lombok.Value;

@Value
public class Park {
    String id;
    String description;
    String name;
    String photo;
    double pricePerDay;
    double latitude;
    double longitude;
    boolean security;
    String parkingLotType;
    Integer capacity;

}
