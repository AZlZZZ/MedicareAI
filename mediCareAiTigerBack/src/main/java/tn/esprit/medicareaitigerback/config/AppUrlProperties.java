package tn.esprit.medicareaitigerback.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.url")
public class AppUrlProperties {
    private String resetPassword;
    private String baseUrl;
}
