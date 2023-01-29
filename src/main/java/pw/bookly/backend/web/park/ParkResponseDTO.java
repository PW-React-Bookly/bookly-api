package pw.bookly.backend.web.park;

import com.fasterxml.jackson.annotation.JsonProperty;
import pw.bookly.backend.models.park.ParkResponse;

import java.util.List;

public record ParkResponseDTO(
    int noOfPages,
    @JsonProperty("bookables") List<ParkDTO> parkingLotsDto
    ) {
    public static ParkResponseDTO valueFrom(ParkResponse parkResponse) {
        return new ParkResponseDTO(
                parkResponse.getNoOfPages(),
                parkResponse.getParkingLotsDto().stream().map(ParkDTO::valueFrom).toList()
        );
    }
}
