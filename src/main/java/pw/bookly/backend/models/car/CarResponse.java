package pw.bookly.backend.models.car;

import lombok.Value;

import java.util.List;

@Value
public class CarResponse {
    int pageNumber;
    int pageSize;
    int totalRecords;
    List<Car> cars;
}
