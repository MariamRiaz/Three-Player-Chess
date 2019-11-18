import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Tests {

    @Test
    public void intialTest() {
        int a = 5;
        int b = 10;
        assertEquals("5 + 10 must be equal to 10", 15, a+b);
    }
}
