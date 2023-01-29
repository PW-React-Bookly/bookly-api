package pw.bookly.backend.web;

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

