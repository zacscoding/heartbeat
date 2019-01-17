package server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@SpringBootApplication
public class HeartbeatServerApplication {

    public static void main(String[] args) {
        beforeApplication();
        SpringApplication.run(HeartbeatServerApplication.class, args);
    }

    private static void beforeApplication() {
        try {
            ClassPathResource resource = new ClassPathResource("secret.yaml");
            if (resource.exists()) {
                System.setProperty("spring.config.location", resource.getFile().getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Exception occur while getting secret.yaml file");
        }
    }
}
