package server.state.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import server.state.HostEntity;

/**
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class HostEntityRepositoryTest {

    @Autowired
    HostEntityRepository hostEntityRepository;

    @Before
    public void setUp() {
        hostEntityRepository.deleteAll();
    }

    @Test
    public void findByServiceName() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            hostEntityRepository.save(
                HostEntity.builder()
                    .serviceName("Service" + i)
                    .build()
            );
        });

        for (int i = 1; i <= 10; i++) {
            String serviceName = "Service" + i;
            Optional<HostEntity> optional = hostEntityRepository.findByServiceName(serviceName);
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get().getServiceName()).isEqualTo(serviceName);
        }

        for (int i = 11; i <= 12; i++) {

            Optional<HostEntity> optional = hostEntityRepository.findByServiceName("Service" + i);
            assertThat(optional.isPresent()).isFalse();
        }
    }
}
