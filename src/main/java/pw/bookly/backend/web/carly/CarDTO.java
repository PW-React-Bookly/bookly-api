package pw.bookly.backend.web.carly;

import pw.bookly.backend.models.carly.Car;
import pw.bookly.backend.models.carly.Equipment;
import pw.bookly.backend.models.carly.Photo;

import java.util.List;

public record CarDTO(
        String id,
        ModelDTO model,
        List<String> equipment,
        int dayPrice,
        String color,
        List<String> photos
        ) {

    public static CarDTO valueFrom(Car car) {
        return new CarDTO(
                car.getId(),
                ModelDTO.valueFrom(car.getModel()),
                car.getEquipment().stream().map(Equipment::getName).toList(),
                car.getDayPrice(),
                car.getColor(),
                car.getPhotos().stream().map(Photo::getName).toList()
        );
    }
}
