package pw.bookly.backend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class Flat {
    private String id;
    private String country;
    private String town;
    private String address;
    private int capacity;
    private int rooms;
    private float footage;
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal price;
    private String contactInfo;
    private String description;
    private String thumbnail;

}
