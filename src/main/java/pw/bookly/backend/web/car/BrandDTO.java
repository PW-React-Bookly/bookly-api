package pw.bookly.backend.web.car;

import pw.bookly.backend.models.car.Brand;

public record BrandDTO(
        String name,
        String country,
        String logo
) {
    public static BrandDTO valueFrom(Brand brand) {
        return new BrandDTO(
                brand.getName(),
                brand.getCountry(),
                brand.getLogo()
        );
    }
}
