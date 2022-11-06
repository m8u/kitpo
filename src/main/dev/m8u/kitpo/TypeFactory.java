package src.main.dev.m8u.kitpo;

import java.util.ArrayList;
import java.util.Arrays;

public class TypeFactory {

     public static ArrayList<String> getTypeNames() {
        return new ArrayList<>(Arrays.asList(
                "String", "Double", "Datetime"
        ));
    }

     public static ChainedHashtableStorableBuilder getBuilderByName(String name) {
        return switch (name) {
            case "String" -> s -> s;
            case "Double" -> Double::parseDouble;
            case "Datetime" -> Datetime::parseValue;
            default -> null;
        };
    }
}
