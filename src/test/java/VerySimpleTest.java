import org.junit.jupiter.api.Test;

public class VerySimpleTest {

    @Test
    void TestStupidIf() {
        int a = 3;

        if (a < 4) {
            a++;
            System.out.println("#1 if");
        }

        if (a == 4) {
            System.out.println("ok");
        }

    }
}
