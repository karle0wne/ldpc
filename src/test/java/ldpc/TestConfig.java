package ldpc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = {
                "ldpc.service",
                "ldpc.util.service"
        })
public class TestConfig {
}
