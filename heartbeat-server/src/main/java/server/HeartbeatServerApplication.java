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
        args = new String[]{"--spring.config.location=classpath:/applicatin.yaml"};
        args = replaceConfigLocation(args);
        SpringApplication.run(HeartbeatServerApplication.class, args);
    }

    /**
     * Override secret.yaml in IDE
     */
    private static String[] replaceConfigLocation(String[] originArgs) {
        String configLocation = null;

        try {
            ClassPathResource resource = new ClassPathResource("secret.yaml");
            if (resource.exists()) {
                configLocation = "classpath:/application.yaml,classpath:/secret.yaml";
            }
        } catch (Exception e) {
            log.warn("Exception occur while getting secret.yaml maybe running in jar");
            configLocation = null;
        }

        if (configLocation == null) {
            return originArgs;
        }

        String configLocationArg = "--spring.config.location=" + configLocation;

        if (originArgs == null || originArgs.length == 0) {
            return new String[]{
                configLocationArg
            };
        }

        String[] result = new String[originArgs.length + 1];
        for (int i = 0; i < originArgs.length; i++) {
            if (originArgs[i].startsWith("--spring.config.location=")) {
                originArgs[i] = configLocationArg;
                return originArgs;
            }
            result[i] = originArgs[i];
        }

        result[originArgs.length] = configLocation;

        return result;
    }
}