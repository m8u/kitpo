package src.main.dev.m8u.kitpo.builders;

public interface MyHashableBuilder {
    Object createRandom();
    Object parse(String s) throws Exception;
}
