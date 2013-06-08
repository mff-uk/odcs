package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFFormatType;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class FileLoaderConfig implements Config {
	
    public String DirectoryPath;
    
    public String FileName;
    
    public RDFFormatType RDFFileFormat;
    
    public Boolean DiffName;
    
}
