package pw.bookly.backend.web.park;

import pw.bookly.backend.models.park.Park;

public record ParkDTO(
        String id,
        String description,
        String name,
        String photo,
        double pricePerDay,
        double latitude,
        double longitude,
        boolean security,
        String parkingLotType,
        Integer capacity
) {

    public static ParkDTO valueFrom(Park park) {
        return new ParkDTO(
                park.getId(),
                park.getDescription(),
                park.getName(),
                park.getPhoto(),
                park.getPricePerDay(),
                park.getLatitude(),
                park.getLongitude(),
                park.isSecurity(),
                park.getParkingLotType(),
                park.getCapacity()
        );
    }
}
