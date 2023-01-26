package pw.bookly.backend.web;

import pw.bookly.backend.models.Car;

public record CarDTO(String itemExternalId, String description) {

    public static CarDTO valueFrom(Car car) {
        return new CarDTO(car.getItemExternalId(), car.getDescription());
    }

    public static Car convertToCar(CarDTO dto) {
        Car car = new Car();
        car.setItemExternalId(dto.itemExternalId());
        car.setDescription(dto.description());
        return car;
    }
}
