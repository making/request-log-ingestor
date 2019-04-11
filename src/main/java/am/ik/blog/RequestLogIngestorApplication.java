package am.ik.blog;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBinding(Sink.class)
public class RequestLogIngestorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestLogIngestorApplication.class, args);
    }

    @Bean
    public MeterFilter meterFilter() {
        return MeterFilter.deny(id -> {
            String uri = id.getTag("uri");
            return uri != null && (uri.startsWith("/actuator") || uri.startsWith("/cloudfoundryapplication"));
        });
    }
}
