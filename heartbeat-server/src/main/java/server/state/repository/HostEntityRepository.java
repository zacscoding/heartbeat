package server.state.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import server.state.HostEntity;

/**
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
public interface HostEntityRepository extends JpaRepository<HostEntity, Long> {

    Optional<HostEntity> findByServiceName(String serviceName);
}
