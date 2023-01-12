package pw.bookly.backend.specifications;

import pw.bookly.backend.models.Bookable;

public class BookingFilters {
    private String firstName;
    private String lastName;
    private Bookable bookable;

    public BookingFilters(String firstName, String lastName, Bookable bookable) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bookable = bookable;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Bookable getBookable() {
        return bookable;
    }

    public void setBookable(Bookable bookable) {
        this.bookable = bookable;
    }
}
