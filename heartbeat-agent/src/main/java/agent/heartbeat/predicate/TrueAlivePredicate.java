package agent.heartbeat.predicate;

/**
 * Always return true if called isAlive()
 *
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
public class TrueAlivePredicate implements AlivePredicate {

    private static TrueAlivePredicate INSTANCE = null;

    public static TrueAlivePredicate getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TrueAlivePredicate();
        }

        return INSTANCE;
    }


    private TrueAlivePredicate() {
    }

    @Override
    public boolean test() {
        return true;
    }
}
