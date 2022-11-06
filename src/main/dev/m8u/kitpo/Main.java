package src.main.dev.m8u.kitpo;

public class Main {
    public static void main(String[] args) throws Exception {
        testDouble();
        testDatetime();

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

    static void testDatetime() throws Exception {
        ChainedHashtableStorableBuilder keyBuilder = TypeFactory.getBuilderByName("String");
        ChainedHashtableStorableBuilder valueBuilder = TypeFactory.getBuilderByName("Datetime");
        ChainedHashtable<Object, Object> hashtable = new ChainedHashtable<>("String", "Datetime");
        hashtable.set(keyBuilder.parseValue("one"), valueBuilder.parseValue("2022-11-06 13:44:00"));
        hashtable.set(keyBuilder.parseValue("two"), valueBuilder.parseValue("2021-10-05 12:43:59"));
        hashtable.set(keyBuilder.parseValue("three"), valueBuilder.parseValue("2020-09-04 11:42:58"));
        hashtable.set(keyBuilder.parseValue("four"), valueBuilder.parseValue("2019-08-03 10:41:57"));
        hashtable.set(keyBuilder.parseValue("five"), valueBuilder.parseValue("2018-07-02 09:40:56"));
        hashtable.set(keyBuilder.parseValue("six"), valueBuilder.parseValue("2017-06-01 08:39:55"));
        hashtable.set(keyBuilder.parseValue("seven"), valueBuilder.parseValue("2016-05-31 07:38:54"));
        hashtable.set(keyBuilder.parseValue("eight"), valueBuilder.parseValue("2015-04-30 06:37:53"));
        hashtable.set(keyBuilder.parseValue("nine"), valueBuilder.parseValue("2014-03-29 05:36:52"));
        hashtable.set(keyBuilder.parseValue("two"), valueBuilder.parseValue("2013-02-28 04:35:51"));
        hashtable.remove(keyBuilder.parseValue("one"));
        if (!hashtable.toString().equals("[, , {five:2018-07-02 09:40:56}{six:2017-06-01 08:39:55}{nine:2014-03-29 05:36:52}, , " +
                "{two:2013-02-28 04:35:51}, {seven:2016-05-31 07:38:54}, {three:2020-09-04 11:42:58}{four:2019-08-03 10:41:57}, {eight:2015-04-30 06:37:53}]")
                || !hashtable.get(keyBuilder.parseValue("five")).toString()
                    .equals(new Datetime(2018, 7, 2, 9, 40, 56).toString())) {
            System.out.println("testDatetime failed");
        } else {
            System.out.println("testDatetime successful");
        }
    }
}
