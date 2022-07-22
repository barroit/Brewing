import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

public class ConsumerTest {
    @Test
    void test() {
        consumer().accept(new C());
    }

    Consumer<C> consumer() {
        return C::exec;
    }
}

class C {
    public void exec() {
        System.out.println(11111111111L);
    }
}