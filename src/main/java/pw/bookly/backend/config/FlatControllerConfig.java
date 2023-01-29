package pw.bookly.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.api.flatly")
public class FlatControllerConfig {
    private String flatlyBackend;
}
