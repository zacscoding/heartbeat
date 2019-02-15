package server.alert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import server.state.HostEntity;
import server.state.HostState;
import server.state.repository.HostEntityRepository;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class BotCommandHandler {

    private HostEntityRepository hostEntityRepository;
    private BotCommandReplier replier;

    public BotCommandHandler(HostEntityRepository hostEntityRepository, BotCommandReplier replier) {
        Objects.requireNonNull(hostEntityRepository, "hostEntityRepository must be not null");
        Objects.requireNonNull(replier, "replier must be not null");

        this.hostEntityRepository = hostEntityRepository;
        this.replier = replier;
    }

    /**
     * Handle commands
     *
     * @param context    : context for alert such as WebSocketSession, Event
     * @param argsString : full command text with args except for "!"
     */
    public void handleCommand(Map<String, Object> context, String argsString) {
        Objects.requireNonNull(context, "context must be not null");
        Objects.requireNonNull(argsString, "argsString must be not null");

        String[] args = argsString.split("\\s+");
        String command = args[0];

        BotCommandType type = BotCommandType.getType(command);

        switch (type) {
            case SERVER:
                handleServerCommand(context, args);
                break;
            case SERVERS:
                handleServersCommand(context);
                break;
            case HELP:
            case UNKNOWN:
                handleHelpCommand(context);
        }
    }

    /**
     * Handle "!servers" command
     */
    private void handleServersCommand(Map<String, Object> context) {
        try {
            List<HostEntity> entities = hostEntityRepository.findAll();
            /*if (entities.isEmpty()) {
                entities.add(createDummyEntity("ElasticsearchService01", true));
                entities.add(createDummyEntity("KafkaBrokers01", false));
                entities.add(createDummyEntity("Zookeeper01", false));
            }*/

            replier.replyServersStateMessage(context, entities);
        } catch (Throwable e) {
            log.warn("Exception occur while handle servers command", e);
            replier.replyErrorMessage(context, e);
        }
    }

    /**
     * Handle "!server" command
     */
    private void handleServerCommand(Map<String, Object> context, String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("needs server name e.g) !server Service01");
            }

            String serviceName = args[1].trim();

            boolean isLike = false;
            // prefix
            if (serviceName.length() > 1 && serviceName.charAt(0) == '%') {
                isLike = true;
                serviceName = serviceName.substring(1);
            }

            // suffix
            if (serviceName.length() > 1 && serviceName.charAt(serviceName.length() - 1) == '%') {
                isLike = true;
                serviceName = serviceName.substring(0, serviceName.length() - 1);
            }

            List<HostEntity> entities = null;

            if ("%".equals(serviceName)) {
                entities = hostEntityRepository.findAll();
            } else if (isLike) {
                entities = hostEntityRepository.findByServiceNameContaining(serviceName);
            } else {
                Optional<HostEntity> hostEntityOptional = hostEntityRepository.findByServiceName(serviceName);
                if (!hostEntityOptional.isPresent()) {
                    throw new Exception("not found `" + serviceName + "`");
                }
                entities = Arrays.asList(hostEntityOptional.get());
            }

            replier.replyServersStateMessage(context, entities);
        } catch (Exception e) {
            log.warn("Exception occur while handle server command", e);
            replier.replyErrorMessage(context, e);
        }
    }

    /**
     * Handle "!help" command
     */
    private void handleHelpCommand(Map<String, Object> context) {
        replier.replyHelpMessage(context);
    }

    /**
     * Create dummy HostEntity for dev
     */
    private HostEntity createDummyEntity(String serviceName, boolean working) {
        HostEntity entity = new HostEntity();

        entity.setServiceName(serviceName);
        if (working) {
            entity.setHostState(HostState.HEALTHY);
        } else {
            entity.setHostState(HostState.HEARTBEAT_LOST);
        }

        entity.setPid(2223);
        entity.setLastAgentTimestamp(System.currentTimeMillis());
        entity.setIp("192.168.79." + new Random().nextInt(254) + 2);

        return entity;
    }
}