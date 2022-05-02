import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SimpleTest {
    @Test
    void testStreamMapMethod() {
        String[] configname = {"test1", "test2"};
        Arrays.stream(configname).map(v -> v + ".yml").toList().forEach(System.out::println);
    }

    @Test
    void testRegularExpression() {
        assert !"test.yml.yml.a".matches(".*.(yml|yaml)$");
    }

    @Test
    void testRegularExpressionV2() {
        String[] configname = {"plugin.yml", "plugin-a", "plugin_a", "plugin.ttt"};
        Arrays.stream(configname).toList().forEach(v -> {
            System.out.println(v.matches("([\\w-]*)\\.yml|yaml$"));
            System.out.println(v.replaceAll("([\\w-]*)\\.yml|yaml$", "$1") + ".yml");
        });
    }
}
