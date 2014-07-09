package cz.cuni.mff.xrg.odcs.files.maphelper;

import java.util.Map;

public interface WritableMapHelper {
    void putMap(String symbolicName, String mapName, Map<String, String> map);
}
