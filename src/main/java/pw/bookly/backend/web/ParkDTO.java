package pw.bookly.backend.web;

import pw.bookly.backend.models.Park;

public record ParkDTO(String itemExternalId, String description) {

    public static ParkDTO valueFrom(Park park) {
        return new ParkDTO(park.getItemExternalId(), park.getDescription());
    }

    public static Park convertToPark(ParkDTO dto) {
        Park park = new Park();
        park.setItemExternalId(dto.itemExternalId());
        park.setDescription(dto.description());
        return park;
    }
}
