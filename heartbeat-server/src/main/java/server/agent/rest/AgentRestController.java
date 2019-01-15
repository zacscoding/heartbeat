package server.agent.rest;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.agent.Heartbeat;
import server.agent.HeartbeatHandler;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
public class AgentRestController {

    private HeartbeatHandler heartbeatHandler;

    @Autowired
    public AgentRestController(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
    }

    /**
     * Receive heart beat
     */
    @GetMapping("/heartbeat")
    public ResponseEntity heartbeat(HttpServletRequest request) {
        Heartbeat heartbeat = convertHeartBeat(request);
        log.info("Receive heartbeat : {}", heartbeat);
        heartbeatHandler.handleHeartBeat(heartbeat);

        return ResponseEntity.ok().build();
    }

    /**
     * extract heartbeat from query string & header
     */
    private Heartbeat convertHeartBeat(HttpServletRequest request) {
        return Heartbeat.builder()
            .serviceName(request.getParameter("serviceName"))
            .clientId(request.getParameter("clientId"))
            .userAgent(request.getHeader("user-agent"))
            .beatInterval(Long.parseLong(request.getHeader("beat-interval")))
            .timestamp(System.currentTimeMillis())
            .build();
    }
}