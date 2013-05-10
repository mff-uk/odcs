package cz.cuni.xrg.intlib.backend.repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates unique name for file name.
 *
 * @author Jiri Tomes
 */
public class UniqueNameGenerator {

    private static Map<String, Integer> map = new HashMap<>();

    public static String getNextName(String name) {
        int value = 1;

        if (!map.containsKey(name)) {
            map.put(name, value);
        } else {
            value = map.remove(name);
            value++;
            map.put(name, value);

        }

        String newName = name + "-" + String.valueOf(value);

        return newName;
    }
}
