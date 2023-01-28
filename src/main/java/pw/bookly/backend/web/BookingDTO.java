package pw.bookly.backend.web;

import pw.bookly.backend.models.Bookable;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.models.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookingDTO(long id, Boolean isCancelled, BigDecimal totalPrice, LocalDate bookedFrom, LocalDate bookedUntil,
                         String itemExternalId, Bookable bookableType, User user) {
    public static BookingDTO valueFrom(Booking booking) {
        return new BookingDTO(booking.getId(), booking.getCancelled(), booking.getTotalPrice(), booking.getBookedFrom(), booking.getBookedUntil(),
                booking.getItemExternalId(), booking.getBookableType(), booking.getUser());
    }

    public static Booking convertToBooking(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setId(dto.id());
        booking.setCancelled(dto.isCancelled());
        booking.setTotalPrice(dto.totalPrice());
        booking.setBookedFrom(dto.bookedFrom());
        booking.setBookedUntil(dto.bookedUntil());
        booking.setItemExternalId(dto.itemExternalId());
        booking.setBookableType(dto.bookableType());
        booking.setUser(dto.user());
        return booking;
    }
}
