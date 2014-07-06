package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import eu.unifiedviews.dataunit.DataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;

public class DataUnitTypeResolver {
    public static DataUnit.Type resolveClassToType(Class<?> classType) {
        if (RDFDataUnit.class.isAssignableFrom(classType)) {
            return DataUnit.Type.RDF;
        } else if (FileDataUnit.class.isAssignableFrom(classType)) {
            return DataUnit.Type.FILE;
        } else if (FilesDataUnit.class.isAssignableFrom(classType)) {
            return DataUnit.Type.FILES;
        }
        return null;
    }
}
