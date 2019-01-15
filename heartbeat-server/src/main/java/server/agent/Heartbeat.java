package server.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "serviceName")
@ToString
@Builder
public class Heartbeat {

    private String serviceName;
    private String clientId;
    private String userAgent;
    private long beatInterval;
    private long timestamp;
}