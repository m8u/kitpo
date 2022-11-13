package src.main.dev.m8u.kitpo.builders;

import src.main.dev.m8u.kitpo.types.MyDouble;

import java.util.concurrent.ThreadLocalRandom;

public class MyDoubleBuilder implements MyHashableBuilder {
    @Override
    public Object createRandom() {
        return new MyDouble(ThreadLocalRandom.current().nextDouble());
    }

    @Override
    public Object parse(String s) {
        return new MyDouble(Double.parseDouble(s));
    }
}
