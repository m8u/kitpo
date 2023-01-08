package src.main.dev.m8u.kitpo.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.dev.m8u.kitpo.ChainedHashtable;
import src.main.dev.m8u.kitpo.types.MyDatetime;
import src.main.dev.m8u.kitpo.types.MyDouble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChainedHashtableMethodsTest {

    ChainedHashtable hashtable;

    @BeforeEach
    void setUp() {
        hashtable = new ChainedHashtable("MyDouble");
    }

    @Test
    void setAndGet() throws Exception {
        hashtable.set(new MyDouble(0.123), "qwerty");
        hashtable.set(new MyDouble(123456789987654321.12345678987654321), 1);
        hashtable.set(new MyDouble(0.000000000000000000000001), true);
        hashtable.set(new MyDouble(-12345.0),
                new MyDatetime(2000, 10, 10, 10, 10, 10));

        assertEquals("qwerty", hashtable.get(new MyDouble(0.123)));
        assertEquals(1, hashtable.get(new MyDouble(123456789987654321.12345678987654321)));
        assertEquals(true, hashtable.get(new MyDouble(0.000000000000000000000001)));
        assertEquals(new MyDatetime(2000, 10, 10, 10, 10, 10),
                hashtable.get(new MyDouble(-12345.0)));
    }

    @Test
    void setAndExpand() {
        for (int v = 0; v < ChainedHashtable.INITIAL_SIZE * ChainedHashtable.Chain.CHAIN_MAX_LENGTH + 1; v++) {
            hashtable.set(new MyDouble(v), "");
        }
        assertEquals(ChainedHashtable.INITIAL_SIZE * 2, hashtable.getCapacity());
    }

    @Test
    void remove() throws Exception {
        hashtable.set(new MyDouble(0.123), "qwerty");
        hashtable.set(new MyDouble(123456789987654321.12345678987654321), 1);
        hashtable.set(new MyDouble(0.000000000000000000000001), true);
        hashtable.set(new MyDouble(-12345.0),
                new MyDatetime(2000, 10, 10, 10, 10, 10));

        assertEquals(true, hashtable.remove(new MyDouble(0.000000000000000000000001)));

        assertEquals(null, hashtable.get(new MyDouble(0.000000000000000000000001)));
        assertEquals("qwerty", hashtable.get(new MyDouble(0.123)));
        assertEquals(1, hashtable.get(new MyDouble(123456789987654321.12345678987654321)));
        assertEquals(new MyDatetime(2000, 10, 10, 10, 10, 10),
                hashtable.get(new MyDouble(-12345.0)));
    }

    @Test
    void testToString() throws Exception {
        hashtable.set(new MyDouble(0.1), "qwerty");
        hashtable.set(new MyDouble(0.2), 1);
        hashtable.set(new MyDouble(0.3), true);
        hashtable.set(new MyDouble(0.4),
                new MyDatetime(2000, 10, 10, 10, 10, 10));
        hashtable.set(new MyDouble(0.5), new ChainedHashtable("MyDatetime"));

        assertEquals("[{0.2:1}, {0.3:true}, {0.4:2000-10-10 10:10:10}, {0.1:qwerty}{0.5:[, , , ]}]",
                hashtable.toString());
    }

    @Test
    void iterator() {
        for (int v = 0; v < 21; v++) {
            hashtable.set(new MyDouble(v), "");
        }
        List<MyDouble> expected = Arrays.asList(
                new MyDouble(6),
                new MyDouble(17),
                new MyDouble(20),
                new MyDouble(7),
                new MyDouble(10),
                new MyDouble(18),
                new MyDouble(0),
                new MyDouble(8),
                new MyDouble(11),
                new MyDouble(19),
                new MyDouble(1),
                new MyDouble(9),
                new MyDouble(12),
                new MyDouble(2),
                new MyDouble(13),
                new MyDouble(3),
                new MyDouble(14),
                new MyDouble(4),
                new MyDouble(15),
                new MyDouble(5),
                new MyDouble(16)
        );
        ArrayList<MyDouble> actual = new ArrayList<>();
        for (ChainedHashtable.Chain chain : hashtable) {
            for (ChainedHashtable.ChainNode node : chain) {
                actual.add((MyDouble) node.getKey());
            }
        }

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void saveAndLoad() throws Exception {
        for (int v = 0; v < 21; v++) {
            hashtable.set(new MyDouble(v), "");
        }
        File file = Files.createTempFile(null, null).toFile();
        hashtable.saveAsJSON(new FileOutputStream(file));
        ChainedHashtable loadedHashtable = ChainedHashtable.loadFromJSON(new FileInputStream(file));

        assertEquals(hashtable.toString(), loadedHashtable.toString());
    }
}