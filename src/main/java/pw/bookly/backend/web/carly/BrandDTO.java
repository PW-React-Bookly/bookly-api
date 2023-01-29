package pw.bookly.backend.web.carly;

import pw.bookly.backend.models.carly.Brand;

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
