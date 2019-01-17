package server.configuration.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import server.configuration.SlackConfiguration;

/**
 * Slack properties
 *
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Component
@ConditionalOnBean(SlackConfiguration.class)
public class SlackProperties {

    private String slackApi;
    private String slackBotToken;
    private String slackIncomingWebhookUrl;

    @Autowired
    public SlackProperties(@Value("${slackApi}") String slackApi,
        @Value("${slack.bot-token}") String slackBotToken,
        @Value("${slack.web-hook-url}") String slackIncomingWebhookUrl) {

        this.slackApi = slackApi;
        this.slackBotToken = slackBotToken;
        this.slackIncomingWebhookUrl = slackIncomingWebhookUrl;
    }
}
