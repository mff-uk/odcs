package cz.cuni.mff.xrg.odcs.files.helper;

import eu.unifiedviews.dataunit.DataUnitException;

public interface WritableVirtualPathHelper extends VirtualPathHelper {
    void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException;
}
