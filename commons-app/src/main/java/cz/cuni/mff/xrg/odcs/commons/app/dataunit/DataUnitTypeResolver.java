package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.filelist.FileListDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;

public class DataUnitTypeResolver {
    public static DataUnitType resolveClassToType(Class<?> classType) {
        if (RDFDataUnit.class.isAssignableFrom(classType)) {
            return DataUnitType.RDF;
        } else if (FileDataUnit.class.isAssignableFrom(classType)) {
            return DataUnitType.FILE;
        } else if (FileListDataUnit.class.isAssignableFrom(classType)) {
            return DataUnitType.FILE_LIST;
        }
        return null;
    }
}
