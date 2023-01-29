package pw.bookly.backend.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import pw.bookly.backend.models.Flat;

import java.math.BigDecimal;

public record FlatDTO(String id,
                      String country,
                      String town,
                      String address,
                      int capacity,
                      int rooms,
                      float footage,
                      BigDecimal price,
                      String contactInfo,
                      String description,
                      String thumbnail) {

    public static FlatDTO valueFrom(Flat flat) {
        return new FlatDTO(flat.getId(), flat.getCountry(), flat.getTown(), flat.getAddress(),
                flat.getCapacity(), flat.getRooms(), flat.getFootage(), flat.getPrice(), flat.getContactInfo(),
                flat.getDescription(), flat.getThumbnail());
    }

//    public static Flat convertToFlat(FlatDTO dto) {
//        Flat flat = new Flat();
//        flat.setItemExternalId(dto.itemExternalId());
//        flat.setAddress(dto.address());
//        flat.setNumberOfPeople(dto.numberOfPeople());
//        flat.setPricePerNight(dto.pricePerNight());
//        flat.setDescription(dto.description());
//        return flat;
//    }
}
