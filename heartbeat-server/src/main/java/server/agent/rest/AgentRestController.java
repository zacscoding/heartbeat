package server.agent.rest;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.agent.Heartbeat;
import server.agent.HeartbeatHandler;
import server.state.HostEntity;
import server.state.repository.HostEntityRepository;
import server.util.ServletHelper;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
public class AgentRestController {

    private HeartbeatHandler heartbeatHandler;
    private HostEntityRepository hostEntityRepository;

    @Autowired
    public AgentRestController(HeartbeatHandler heartbeatHandler, HostEntityRepository hostEntityRepository) {
        this.heartbeatHandler = heartbeatHandler;
        this.hostEntityRepository = hostEntityRepository;
    }

    /**
     * Receive heart beat
     */
    @GetMapping("/heartbeat")
    public ResponseEntity heartbeat() {
        Heartbeat heartbeat = extractHeartBeat(ServletHelper.getHttpServletRequest());

        log.info("Receive heartbeat : {}", heartbeat);
        heartbeatHandler.handleHeartBeat(heartbeat);

        return ResponseEntity.ok().build();
    }

    /**
     * extract heartbeat from query string & header
     */
    private Heartbeat extractHeartBeat(HttpServletRequest request) {
        return Heartbeat.builder()
            .serviceName(request.getParameter("serviceName"))
            .clientId(request.getParameter("clientId"))
            .pid(Integer.parseInt(request.getParameter("pid")))
            .userAgent(request.getHeader("user-agent"))
            .beatInterval(Long.parseLong(request.getHeader("beat-interval")))
            .timestamp(System.currentTimeMillis())
            .ip(ServletHelper.getIpAddress(request))
            .build();
    }
}
