package server.alert;

import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import server.state.HostEntity;
import server.state.HostState;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Ignore
public class MessageConverterConsoleTest {


    @Test
    public void convertServersState() {
        List<HostEntity> entities = new ArrayList<>();
        HostEntity entity1 = new HostEntity();
        entity1.setServiceName("Berith-Api[DEV]");
        entity1.setHostState(HostState.HEALTHY);
        entity1.setPid(4234);
        entity1.setIp("127.0.0.1");
        entity1.setLastAgentTimestamp(System.currentTimeMillis());
        entities.add(entity1);

        HostEntity entity2 = new HostEntity();
        entity2.setServiceName("Berith-Wallet[DEV]");
        entity2.setHostState(HostState.HEARTBEAT_LOST);
        entity2.setPid(24816);
        entity2.setIp("192.168.5.78");
        entity2.setLastAgentTimestamp(System.currentTimeMillis());
        entities.add(entity2);

        TestMessageConverter messageConverter = new TestMessageConverter();
        String text = messageConverter.convertServersState(entities);
        System.out.println(text);
    }

    private static class TestMessageConverter extends MessageConverter {

    }
}