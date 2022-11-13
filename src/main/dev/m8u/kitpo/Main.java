package src.main.dev.m8u.kitpo;

import src.main.dev.m8u.kitpo.builders.MyHashableBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        test("MyDouble", 8000);
        test("MyDouble", 10000);
        test("MyDatetime", 8000);
        test("MyDatetime", 10000);

        GUI gui = new GUI();
        gui.setVisible(true);
    }

    private static void test(String typeName, int items) throws IOException {
        MyHashableBuilder builder = TypeFactory.getBuilderByName(typeName);
        ChainedHashtable hashtable = new ChainedHashtable(typeName);
        FileOutputStream timeFos = new FileOutputStream("%s_%d_avg_set_time.txt".formatted(typeName, items));
        ArrayList<Long> timeMA = new ArrayList<>();
        int timeMAWindow = 100;
        double chainLengthSum = 0;
        double occupancySum = 0;
        for (int i = 1; i <= items; i++) {
            Object key = builder.createRandom();
            long start = System.currentTimeMillis();
            hashtable.set(key, null);
            long stop = System.currentTimeMillis();
//            if (i % (items/500) == 0) {
//                System.out.println(i + " (" + (int) (((double) i / items) * 100) + "%)");
//            }
            timeMA.add(stop - start);
            if (timeMA.size() > timeMAWindow)
                timeMA.remove(0);
            timeFos.write(((double)timeMA.stream().reduce(Long::sum).get() / timeMAWindow + "\n").getBytes());
            chainLengthSum += hashtable.getAverageChainLength();
            occupancySum += hashtable.getOccupancy();
        }
        System.out.printf("""
                        ============ %s (%d) test complete ============
                        Final capacity: %d
                        Final avg. chain length: %.2f
                        Final occupancy: %.2f%%
                        All-time avg. chain length: %.2f
                        All-time avg. occupancy: %.2f%%
                        
                        """,
                typeName, items,
                hashtable.getCapacity(),
                hashtable.getAverageChainLength(),
                hashtable.getOccupancy() * 100,
                chainLengthSum / items,
                occupancySum / items * 100);
    }
}
