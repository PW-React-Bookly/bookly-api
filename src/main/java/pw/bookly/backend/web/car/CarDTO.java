package pw.bookly.backend.web.car;

import pw.bookly.backend.models.car.Car;
import pw.bookly.backend.models.car.Equipment;
import pw.bookly.backend.models.car.Photo;

import java.util.List;

public record CarDTO(
        String id,
        ModelDTO model,
        Long mileage,
        List<String> equipment,
        int dayPrice,
        String color,
        List<Integer> photos
        ) {

    public static CarDTO valueFrom(Car car) {
        return new CarDTO(
                car.getId(),
                ModelDTO.valueFrom(car.getModel()),
                car.getMileage(),
                car.getEquipment().stream().map(Equipment::getName).toList(),
                car.getDayPrice(),
                car.getColor(),
                car.getPhotos().stream().map(Photo::getId).toList()
        );
    }
}
