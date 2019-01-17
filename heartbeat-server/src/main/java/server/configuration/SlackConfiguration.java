package server.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Slack configuration
 *
 * - enable slack components
 * - enable jbot slack components
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
@Configuration
@ConditionalOnProperty(name = "slack.enabled", havingValue = "true")
@ComponentScan(basePackages = {"me.ramswaroop.jbot"})
public class SlackConfiguration {
}
