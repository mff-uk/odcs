package cz.cuni.mff.xrg.odcs.files.maphelper;

import java.util.Map;

import eu.unifiedviews.dataunit.files.FilesDataUnit;

public class MapHelpers {

    private static final MapHelpers selfie = new MapHelpers();
    
    private MapHelpers() {
        
    }
    
    private class MapHelperImpl implements MapHelper {
        private FilesDataUnit dataUnit;
        
        public MapHelperImpl() {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public Map<String, String> getMap(String symbolicName, String mapName) {
            return null;
        }
        
    }
}
