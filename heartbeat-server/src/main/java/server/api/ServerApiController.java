package server.api;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.state.HostEntity;
import server.state.repository.HostEntityRepository;
import server.util.ServletHelper;

/**
 * Server api controller
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
@RequestMapping("/api/**")
public class ServerApiController {


    private HostEntityRepository hostEntityRepository;

    @Autowired
    public ServerApiController(HostEntityRepository hostEntityRepository) {
        this.hostEntityRepository = hostEntityRepository;
    }

    @DeleteMapping("/hosts")
    public ResponseEntity deleteHost(@RequestBody String serviceName) {
        log.info("Try to remove service. name : {} / ip : {}", serviceName, ServletHelper.getIpAddress());

        Optional<HostEntity> hostEntityOptional = hostEntityRepository.findByServiceName(serviceName);
        if (!hostEntityOptional.isPresent()) {
            log.warn("Not found : {}", serviceName);
            return ResponseEntity.notFound().build();
        }

        log.info(">> Delete service : {}", hostEntityOptional.get());
        hostEntityRepository.deleteById(hostEntityOptional.get().getId());
        return ResponseEntity.ok().build();
    }
}
