package pw.bookly.backend.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private Boolean isCancelled;
    @Column
    private BigDecimal totalPrice;
    @Column
    private LocalDate bookedFrom;
    @Column
    private LocalDate bookedUntil;
    @Column
    private String itemExternalId;
    @Column
    private String bookingExternalId;
    @Column
    @Enumerated(EnumType.STRING)
    private Bookable bookableType;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName ="id", nullable = false)
    private User user;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getBookedFrom() {
        return bookedFrom;
    }

    public void setBookedFrom(LocalDate bookedFrom) {
        this.bookedFrom = bookedFrom;
    }

    public LocalDate getBookedUntil() {
        return bookedUntil;
    }

    public void setBookedUntil(LocalDate bookedUntil) {
        this.bookedUntil = bookedUntil;
    }

    public String getItemExternalId() {
        return itemExternalId;
    }

    public void setItemExternalId(String itemExternalId) {
        this.itemExternalId = itemExternalId;
    }

    public String getBookingExternalId() {
        return bookingExternalId;
    }

    public void setBookingExternalId(String bookingExternalId) {
        this.bookingExternalId = bookingExternalId;
    }


    public Bookable getBookableType() {
        return bookableType;
    }

    public void setBookableType(Bookable bookableType) {
        this.bookableType = bookableType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
