package cz.cuni.xrg.intlib.commons.app.rdf;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates unique name for file name.
 *
 * @author Jiri Tomes
 * 
 * Petyr: there should be no need for this class. I see no reason why generate some id base on "nothing".
 */
@Deprecated
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
