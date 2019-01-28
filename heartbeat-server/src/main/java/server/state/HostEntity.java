package server.state;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.context.annotation.Lazy;

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
@Table(name = "host_entity")
public class HostEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "pid")
    private Integer pid;

    @Column(name = "service_name")
    @Unique
    private String serviceName;

    @Column(name = "ip")
    private String ip;

    // register time
    @Column(name = "register_timestamp")
    private long registerTimestamp;

    // last found time at agent
    @Column(name = "last_agent_timestamp")
    private long lastAgentTimestamp;

    // last updated time
    @Column(name = "last_updated_timestamp")
    private long lastUpdatedTimestamp;

    @Enumerated(EnumType.STRING)
    @Lazy()
    @Column(name = "host_state")
    private HostState hostState;
}
