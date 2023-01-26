package pw.bookly.backend.web;

import pw.bookly.backend.models.Flat;

public record FlatDTO(String itemExternalId, String address, int numberOfPeople, float pricePerNight, String description) {

    public static FlatDTO valueFrom(Flat flat) {
        return new FlatDTO(flat.getItemExternalId(), flat.getAddress(), flat.getNumberOfPeople(), flat.getPricePerNight(), flat.getDescription());
    }

    public static Flat convertToFlat(FlatDTO dto) {
        Flat flat = new Flat();
        flat.setItemExternalId(dto.itemExternalId());
        flat.setAddress(dto.address());
        flat.setNumberOfPeople(dto.numberOfPeople());
        flat.setPricePerNight(dto.pricePerNight());
        flat.setDescription(dto.description());
        return flat;
    }
}
