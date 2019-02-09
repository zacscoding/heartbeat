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

    /**
     * Override application.yaml to secret.yaml in IDE
     */
    private static void beforeApplication() {
        try {
            ClassPathResource resource = new ClassPathResource("secret.yaml");
            if (resource.exists()) {
                String configLocation = "classpath:/application.yaml," + resource.getFile().getAbsolutePath();
                log.info("Override config location :: {}", configLocation);
                System.setProperty("spring.config.location", configLocation);
            }
        } catch (Exception e) {
            log.warn("Exception occur while getting secret.yaml maybe running in jar");
        }
    }
}