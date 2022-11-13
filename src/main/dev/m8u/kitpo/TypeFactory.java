package src.main.dev.m8u.kitpo;

import src.main.dev.m8u.kitpo.builders.MyDatetimeBuilder;
import src.main.dev.m8u.kitpo.builders.MyDoubleBuilder;
import src.main.dev.m8u.kitpo.builders.MyHashableBuilder;

import java.util.ArrayList;


enum MyHashmapStorable {
    MyDouble, MyDatetime
}

public class TypeFactory {

    public static ArrayList<String> getTypeNames() {
        ArrayList<String> list = new ArrayList<>();
        for (MyHashmapStorable type : MyHashmapStorable.values()) {
            list.add(String.valueOf(type));
        }
        return list;
    }

    public static MyHashableBuilder getBuilderByName(String name) {
        if ("MyDouble".equals(name)) {
            return new MyDoubleBuilder();
        } else if ("MyDatetime".equals(name)) {
            return new MyDatetimeBuilder();
        }
        return null;
    }
}
