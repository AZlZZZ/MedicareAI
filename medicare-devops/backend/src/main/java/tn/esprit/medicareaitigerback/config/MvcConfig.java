package tn.esprit.medicareaitigerback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private static final String FILE = "file:";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200",
                        "https://36xzjggk-4000.euw.devtunnels.ms/",
                        "http://localhost:4000") // ton front
                .allowedMethods("*")
                .allowCredentials(true); // ⚠️ très important
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/patient/pdp/**")
                .addResourceLocations(FILE + uploadDir + "/patient/pdp/");
        registry
                .addResourceHandler("/doctor/pdp/**")
                .addResourceLocations(FILE + uploadDir + "/doctor/pdp/");

        registry
                .addResourceHandler("/agent/pdp/**")
                .addResourceLocations(FILE + uploadDir + "/gent/pdp/");

    }
}
