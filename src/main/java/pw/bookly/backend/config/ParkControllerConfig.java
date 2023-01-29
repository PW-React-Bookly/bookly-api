package pw.bookly.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.api.parkly")
public class ParkControllerConfig {
    private String parklyBackend;
}
