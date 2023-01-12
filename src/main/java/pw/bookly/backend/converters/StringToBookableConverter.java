package pw.bookly.backend.converters;

import org.springframework.core.convert.converter.Converter;
import pw.bookly.backend.models.Bookable;
import pw.bookly.backend.models.Booking;

public class StringToBookableConverter implements Converter<String, Bookable> {
    @Override
    public Bookable convert(String source) {
        return Bookable.valueOf(source.toUpperCase());
    }
}
