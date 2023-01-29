package pw.bookly.backend.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import pw.bookly.backend.web.car.CarDTO;

import java.util.List;

public record FlatResponseDTO(
        int pageNumber,
        int pageSize,
        int totalRecords,
        List<FlatDTO> bookables
) {
    public static FlatResponseDTO of(List<FlatDTO> flats, int pageNumber, int pageSize) {
        return new FlatResponseDTO(pageNumber, pageSize, flats.size(), flats);
    }
}

