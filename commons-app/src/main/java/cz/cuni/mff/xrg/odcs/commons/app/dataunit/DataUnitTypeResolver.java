package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

public class DataUnitTypeResolver {
    
    public static ManagableDataUnit.Type resolveClassToType(Class<?> classType) {
        if (RDFDataUnit.class.isAssignableFrom(classType)) {
            return ManagableDataUnit.Type.RDF;
        } else if (FilesDataUnit.class.isAssignableFrom(classType)) {
            return ManagableDataUnit.Type.FILES;
        }
        return null;
    }

}
