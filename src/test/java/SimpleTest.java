import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.Test;

import java.util.*;

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

    @Test
    void testContinue() {
        List<Integer> no = new ArrayList<>(Arrays.asList(2, 11, 20));

        for (int i = 0; i < 27; i++) {
            if (no.contains(i)) continue;
            System.out.println(i);
        }
    }

    @Test
    void testCompare() {
        Set<Integer> rawSlots = new HashSet<>(Arrays.asList(32, 33, 34, 36, 38, 39, 40, 41, 42, 43, 45, 46, 47, 52, 53, 27, 28, 29, 62, 30, 31));
        // [27, 28, 29, 30, 31, 32, 33, 34, 36, 38, 39, 40, 41, 42, 43, 45, 46, 47, 52, 53, 62]

        Integer min = Collections.min(rawSlots);

        if (min == InventoryView.OUTSIDE || min == -1) {
            return;
        }

        Integer max = Collections.max(rawSlots);

        System.out.println(max);
    }
}
