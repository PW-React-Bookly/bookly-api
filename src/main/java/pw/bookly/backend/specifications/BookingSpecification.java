package pw.bookly.backend.specifications;

import org.springframework.data.jpa.domain.Specification;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.models.User;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class BookingSpecification implements Specification<Booking> {

    private final BookingFilters filters;

    public BookingSpecification(BookingFilters filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Booking, User> groupJoin = root.join("user");
        if (filters.getFirstName() != null) {
            predicates.add(cb.equal(groupJoin.get("firstName"), filters.getFirstName()));
        }
        if (filters.getLastName() != null) {
            predicates.add(cb.equal(groupJoin.get("lastName"), filters.getLastName()));
        }
        if (filters.getBookable() != null) {
            predicates.add(cb.equal(root.get("bookableType"), filters.getBookable()));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
