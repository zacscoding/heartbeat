package server.message.slack;

import lombok.extern.slf4j.Slf4j;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import server.configuration.SlackConfiguration;
import server.configuration.properties.SlackProperties;

/**
 * Slack WebHooks
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component
@ConditionalOnBean(SlackConfiguration.class)
public class SlackWebHooks {

    private RestTemplate restTemplate;
    private SlackProperties slackProperties;

    @Autowired
    public SlackWebHooks(SlackProperties slackProperties, RestTemplate restTemplate) {
        this.slackProperties = slackProperties;
        this.restTemplate = restTemplate;
    }

    public void invokeSlackWebHooks(RichMessage message) {
        try {
            restTemplate.postForEntity(
                slackProperties.getSlackIncomingWebhookUrl(),
                message.encodedMessage(),
                String.class
            );
        } catch (Exception e) {
            log.error("Exception occur while invoke web hooks", e);
        }
    }
}
