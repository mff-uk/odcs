package cz.cuni.mff.xrg.odcs.files.helper;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

public interface WritableVirtualPathHelper extends VirtualPathHelper {
    void setVirtualPath(String symbolicName, String virtualPath) throws DataUnitException;
}
