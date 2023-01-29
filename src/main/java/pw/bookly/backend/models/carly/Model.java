package pw.bookly.backend.models.carly;

import lombok.Value;

@Value
public class Model {

    String id;
    Brand brand;
    String name;
    Variant variant;
    String productionYear;
    CarType carType;
    FuelType fuelType;
    boolean isGearBoxAutomatic;
    int numberOfDoors;
    int numberOfSeats;
    int trunkCapacity;
    int horsePower;
    float avgFuelConsumption;

}
