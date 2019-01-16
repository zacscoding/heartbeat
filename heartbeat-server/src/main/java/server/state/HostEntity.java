package server.state;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Registered services
 *
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@Entity
public class HostEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Integer pid;
    private String serviceName;
    private String ip;
    // register time
    private long registerTimestamp;
    // last found time at agent
    private long lastAgentTimestamp;
    // last updated time
    private long lastUpdatedTimestamp;
    @Enumerated(EnumType.STRING)
    private HostState hostState;
}
