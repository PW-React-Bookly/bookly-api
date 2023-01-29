package pw.bookly.backend.web.carly;

import com.fasterxml.jackson.annotation.JsonProperty;
import pw.bookly.backend.models.carly.CarResponse;

import java.util.List;

public record CarResponseDTO(
            int pageNumber,
            int pageSize,
            int totalRecords,
            @JsonProperty("bookables") List<CarDTO> cars
    ) {
        public static CarResponseDTO valueFrom(CarResponse carResponse) {
            return new CarResponseDTO(
                    carResponse.getPageNumber(),
                    carResponse.getPageSize(),
                    carResponse.getTotalRecords(),
                    carResponse.getCars().stream().map(CarDTO::valueFrom).toList()
            );
        }
    }