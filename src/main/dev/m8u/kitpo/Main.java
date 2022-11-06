package src.main.dev.m8u.kitpo;

public class Main {
    public static void main(String[] args) throws Exception {
        testDouble();

        GUI gui = new GUI();
        gui.setVisible(true);
    }

    static void testDouble() throws Exception {
        ChainedHashtableStorableBuilder keyBuilder = TypeFactory.getBuilderByName("String");
        ChainedHashtableStorableBuilder valueBuilder = TypeFactory.getBuilderByName("Double");
        ChainedHashtable<Object, Object> hashtable = new ChainedHashtable<>("String", "Double");
        hashtable.set(keyBuilder.parseValue("one"), valueBuilder.parseValue("1.01"));
        hashtable.set(keyBuilder.parseValue("two"), valueBuilder.parseValue("2.02"));
        hashtable.set(keyBuilder.parseValue("three"), valueBuilder.parseValue("3.03"));
        hashtable.set(keyBuilder.parseValue("four"), valueBuilder.parseValue("4.0"));
        hashtable.set(keyBuilder.parseValue("five"), valueBuilder.parseValue("5.0"));
        hashtable.set(keyBuilder.parseValue("six"), valueBuilder.parseValue("6.0"));
        hashtable.set(keyBuilder.parseValue("seven"), valueBuilder.parseValue("7.0"));
        hashtable.set(keyBuilder.parseValue("eight"), valueBuilder.parseValue("8.0"));
        hashtable.set(keyBuilder.parseValue("nine"), valueBuilder.parseValue("9.0"));
        hashtable.set(keyBuilder.parseValue("two"), valueBuilder.parseValue("2.00002"));
        hashtable.remove(keyBuilder.parseValue("one"));
        if (!hashtable.toString().equals("[, , {five:5.0}{six:6.0}{nine:9.0}, , {two:2.00002}, {seven:7.0}, {three:3.03}{four:4.0}, {eight:8.0}]")
                || !hashtable.get(keyBuilder.parseValue("five")).equals(5.0)) {
            System.out.println("testDouble failed");
        } else {
            System.out.println("testDouble successful");
        }
    }

}
