package pw.bookly.backend.web.carly;

import pw.bookly.backend.models.carly.Model;

public record ModelDTO(
        BrandDTO brand,
        String name,
        String productionYear,
        String carType,
        String fuelType,
        boolean isGearBoxAutomatic,
        int numberOfDoors,
        int numberOfSeats,
        int trunkCapacity,
        int horsePower,
        float avgFuelConsumption
) {
        public static ModelDTO valueFrom(Model model) {
                return new ModelDTO(
                        BrandDTO.valueFrom(model.getBrand()),
                        model.getName(),
                        model.getProductionYear(),
                        model.getCarType().getName(),
                        model.getFuelType().getName(),
                        model.isGearBoxAutomatic(),
                        model.getNumberOfDoors(),
                        model.getNumberOfSeats(),
                        model.getTrunkCapacity(),
                        model.getHorsePower(),
                        model.getAvgFuelConsumption()
                );
        }
}