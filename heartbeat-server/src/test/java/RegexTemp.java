import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2019-01-17
 * @GitHub : https://github.com/zacscoding
 */
public class RegexTemp {

    @Test
    public void regex() {
        String text = "test test\ttest  \ntest";
        StringTokenizer st = new StringTokenizer(text);
        while(st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }

    private void tests(String regex) {
        Pattern pattern = Pattern.compile(regex);
        String[] samples = {
            "!",
            "!command",
            "! comd",
            "!  sadfasdf",
            "asdfasdf",
            "stop",
            "stopping",
            "non stop"
        };

        for (String sample : samples) {
            System.out.printf("## Test : %s > %s\n", sample, pattern.matcher(sample).matches());
        }
    }
}
