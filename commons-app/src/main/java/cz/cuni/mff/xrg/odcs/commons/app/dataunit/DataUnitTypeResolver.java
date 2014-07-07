package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

public class DataUnitTypeResolver {
    public static ManagableDataUnit.Type resolveClassToType(Class<?> classType) {
        if (RDFDataUnit.class.isAssignableFrom(classType)) {
            return ManagableDataUnit.Type.RDF;
        } else if (FileDataUnit.class.isAssignableFrom(classType)) {
            return ManagableDataUnit.Type.FILE;
        } else if (FilesDataUnit.class.isAssignableFrom(classType)) {
            return ManagableDataUnit.Type.FILES;
        }
        return null;
    }
}
