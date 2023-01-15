package pw.bookly.backend.dao;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import pw.bookly.backend.models.Booking;
import pw.bookly.backend.models.QBooking;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        QuerydslPredicateExecutor<Booking>, QuerydslBinderCustomizer<QBooking> {

    @Override
    default void customize(QuerydslBindings bindings, QBooking root) {
        bindings.bind(root.user.firstName).as("firstName").first(StringExpression::containsIgnoreCase);
        bindings.bind(root.user.lastName).as("lastName").first(StringExpression::containsIgnoreCase);
        bindings.bind(String.class)
                .first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}
