package pw.bookly.backend.models.car;

import lombok.Value;

import java.util.List;

@Value
public class Car{

    String id;
    Model model;
    Long mileage;
    List<Equipment> equipment;
    int dayPrice;
    String color;
    List<Photo> photos;
}
