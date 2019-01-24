package agent;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Agent console output logger
 *
 * @GitHub : https://github.com/zacscoding
 */
public class AgentLogger {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final PrintWriter pw = new PrintWriter(System.err);

    public static void info(Object message) {
        info("heartbeat : INFO", message);
    }

    public static void info(String id, Object message) {
        println(parse(id, message));
    }

    public static void error(Object message) {
        error(message, null);
    }

    public static void error(Object message, Throwable t) {
        error("heartbeat : ERROR", message, t);
    }


    public static void error(String id, Object message, Throwable t) {
        println(parse(id, message));
        if (t != null) {
            println(getStackTrace(t));
        }
    }

    private static void println(String message) {
        println(pw, message);
    }

    private static void println(PrintWriter pw, String message) {
        try {
            if (pw != null) {
                pw.println(message);
                pw.flush();
            }
        } catch (Throwable t) {

        }
    }

    private static String parse(String id, Object message) {
        String messageVal = message == null ? "null" : message.toString();
        if (id == null) {
            id = "heartbeat";
        }

        return new StringBuilder(20 + id.length() + messageVal.length())
            .append(sdf.format(new Date()))
            .append(' ').append('[').append(id).append(']').append(' ')
            .append(messageVal)
            .toString();
    }

    private static String getStackTrace(Throwable t) {
        String newLine = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append(t.toString() + newLine);
        StackTraceElement[] se = t.getStackTrace();
        if (se != null) {
            for (int i = 0; i < se.length; i++) {
                if (se[i] != null) {
                    sb.append("\t" + se[i].toString());
                    if (i != se.length - 1) {
                        sb.append(newLine);
                    }
                }
            }
        }

        return sb.toString();
    }
}
