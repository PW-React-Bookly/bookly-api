package pw.bookly.backend.web;

import pw.bookly.backend.models.Flat;

public record FlatDTO(long id, String address, int numberOfPeople, float pricePerNight) {

    public static FlatDTO valueFrom(Flat flat) {
        return new FlatDTO(flat.getId(), flat.getAddress(), flat.getNumberOfPeople(), flat.getPricePerNight());
    }

    public static Flat convertToFlat(FlatDTO dto) {
        Flat flat = new Flat();
        flat.setId(dto.id());
        flat.setAddress(dto.address());
        flat.setNumberOfPeople(dto.numberOfPeople());
        flat.setPricePerNight(dto.pricePerNight());
        return flat;
    }
}
