package server.agent.rest;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.agent.Heartbeat;
import server.agent.HeartbeatHandler;
import server.util.ServletHelper;

/**
 * @author zacconding
 * @Date 2019-01-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
public class AgentController {

    private HeartbeatHandler heartbeatHandler;

    @Autowired
    public AgentController(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
    }

    /**
     * Receive heart beat
     */
    @GetMapping("/heartbeat")
    public ResponseEntity heartbeat() {
        Heartbeat heartbeat = extractHeartbeat(null);

        log.info("Receive heartbeat : {}", heartbeat);
        heartbeatHandler.handleHeartBeat(heartbeat);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/heartbeat")
    public ResponseEntity heartbeatPost(@RequestBody Heartbeat heartbeat) {
        heartbeat = extractHeartbeat(heartbeat);
        log.info("Receive heartbeat .. {}", heartbeat);

        heartbeatHandler.handleHeartBeat(heartbeat);
        return ResponseEntity.ok().build();
    }

    /**
     * 1) Get request > extract heartbeat from query string
     * 2) Post request > append header info
     */
    private Heartbeat extractHeartbeat(Heartbeat heartbeat) {
        HttpServletRequest request = ServletHelper.getHttpServletRequest();

        // get request > extract from query strings
        if (heartbeat == null) {
            heartbeat = Heartbeat.builder()
                .serviceName(request.getParameter("serviceName"))
                .clientId(request.getParameter("clientId"))
                .pid(Integer.parseInt(request.getParameter("pid")))
                .beatInterval(Long.parseLong(request.getHeader("beat-interval")))
                .build();
        }

        // common
        heartbeat.setUserAgent(ServletHelper.getHeader(request, "user-agent"));
        heartbeat.setIp(ServletHelper.getIpAddress(request));
        heartbeat.setTimestamp(System.currentTimeMillis());

        return heartbeat;
    }
}
