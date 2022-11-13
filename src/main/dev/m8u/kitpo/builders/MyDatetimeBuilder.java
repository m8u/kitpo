package src.main.dev.m8u.kitpo.builders;

import src.main.dev.m8u.kitpo.types.MyDatetime;
import java.util.concurrent.ThreadLocalRandom;

public class MyDatetimeBuilder implements MyHashableBuilder {

    @Override
    public Object createRandom() {
        MyDatetime datetime;
        while (true) {
            try {
                datetime = new MyDatetime(
                        ThreadLocalRandom.current().nextInt(1900, 2030),
                        ThreadLocalRandom.current().nextInt(1, 12+1),
                        ThreadLocalRandom.current().nextInt(1, 31+1),
                        ThreadLocalRandom.current().nextInt(0, 23+1),
                        ThreadLocalRandom.current().nextInt(0, 59+1),
                        ThreadLocalRandom.current().nextInt(0, 59+1)
                        );
                break;
            } catch (Exception ex) {}
        }
        return datetime;
    }

    @Override
    public Object parse(String s) throws Exception {
        String[] minusSplit = s.split("-");
        int year = Integer.parseInt(minusSplit[0]);
        int month = Integer.parseInt(minusSplit[1]);
        String[] whitespaceSplit = minusSplit[2].split(" ");
        int day = Integer.parseInt(whitespaceSplit[0]);
        String[] colonSplit = whitespaceSplit[1].split(":");
        int hour = Integer.parseInt(colonSplit[0]);
        int minute = Integer.parseInt(colonSplit[1]);
        int second = Integer.parseInt(colonSplit[2]);

        return new MyDatetime(year, month, day, hour, minute, second);
    }
}
