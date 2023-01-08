package src.main.dev.m8u.kitpo.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.dev.m8u.kitpo.ChainedHashtable;
import src.main.dev.m8u.kitpo.TypeFactory;
import src.main.dev.m8u.kitpo.builders.MyHashableBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class ChainedHashtablePerformanceTest {

    MyHashableBuilder builder;
    ChainedHashtable hashtable;

    @BeforeEach
    void setUp() {
        builder = TypeFactory.getBuilderByName("MyDouble");
        hashtable = new ChainedHashtable("MyDouble");
    }

    @Test
    void fillPerformance() throws IOException {
        FileOutputStream timeFos = new FileOutputStream("fillPerformance_test_time.txt");

        long start = System.currentTimeMillis(), stop;
        for (int i = 1; i <= 2000000; i++) {
            hashtable.set(builder.createRandom(), null);

            if (i % 10000 == 0) {
                stop = System.currentTimeMillis();
                timeFos.write(((stop - start) + "\n").getBytes());
                start = System.currentTimeMillis();
            }
        }
        timeFos.close();
    }

    @Test
    void expandPerformance() throws IOException {
        FileOutputStream timeFos = new FileOutputStream("expandPerformance_test_time.txt");

        Object key;
        long start, stop;
        boolean expanded;
        for (int i = 1; i <= 2000000; i++) {
            key = builder.createRandom();
            start = System.currentTimeMillis();
            expanded = hashtable.set(key, null);
            stop = System.currentTimeMillis();

            if (expanded)
                timeFos.write((i + " " + (stop - start) + "\n").getBytes());
        }
        timeFos.close();
    }
}
