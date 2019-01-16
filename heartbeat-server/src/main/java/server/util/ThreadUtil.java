package server.util;

/**
 * @author zacconding
 * @Date 2019-01-16
 * @GitHub : https://github.com/zacscoding
 */
public class ThreadUtil {

    /**
     * Return stack tract with cursor
     *
     * Support having N stack trace array, where 0 is first call and N is prev invoker
     *
     * 1) cursor == 0
     * => return all
     *
     * 2) cursor == k, where k < 0
     * => [0, k]
     *
     * 3) cursor == n, where k > 0
     * => [N-K ,N]
     */
    public static String getStackTraceString(int cursor) {
        StackTraceElement[] elts = Thread.currentThread().getStackTrace();

        if (elts == null || elts.length == 1) {
            return "";
        }

        int start, size;

        if (cursor >= 0) {
            start = cursor + 2;
            size = elts.length;
        } else {
            start = 2;
            size = start - cursor + 1;
        }

        return getStackTraceString(elts, start, size);
    }


    /**
     * return stack tract with range [start ~ start + size -1]
     */
    public static String getStackTraceString(StackTraceElement[] se, int start, int size) {
        if (se == null) {
            return "";
        }

        if (size < 0) {
            size = 0;
        }

        size = Math.min(size, se.length);

        if (start >= size) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        final String newLine = System.getProperty("line.separator");

        for (int i = start; i < size; i++) {
            if (i != start) {
                sb.append("\t");
            }

            sb.append(se[i].toString());
            if (i != size - 1) {
                sb.append(newLine);
            }
        }

        return sb.toString();
    }

    private ThreadUtil() {
    }
}
