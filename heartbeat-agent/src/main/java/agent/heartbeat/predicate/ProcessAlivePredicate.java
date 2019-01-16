package agent.heartbeat.predicate;

/**
 * Process alive predicate
 *
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
public class ProcessAlivePredicate implements AlivePredicate {

    public ProcessAlivePredicate(String processName) {

    }

    @Override
    public boolean test() {
        return false;
    }
}
