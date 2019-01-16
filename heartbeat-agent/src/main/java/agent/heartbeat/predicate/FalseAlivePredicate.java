package agent.heartbeat.predicate;

/**
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
public class FalseAlivePredicate implements AlivePredicate {

    private static FalseAlivePredicate INSTANCE = null;

    public static FalseAlivePredicate getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FalseAlivePredicate();
        }

        return INSTANCE;
    }


    private FalseAlivePredicate() {
    }

    @Override
    public boolean test() {
        return false;
    }
}
