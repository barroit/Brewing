import org.junit.jupiter.api.Test;

public class SimpleTest {

    @Test
    void testReplaceAll() {
        String s = "level.level_default";
        System.out.println(s.replaceAll("[.]", "/"));
    }
}
