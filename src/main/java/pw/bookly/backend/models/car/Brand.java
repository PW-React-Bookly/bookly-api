package pw.bookly.backend.models.car;

import lombok.Value;

@Value
public class Brand {

    String id;
    String name;
    String country;
    String logo;
    String logoId;
}
