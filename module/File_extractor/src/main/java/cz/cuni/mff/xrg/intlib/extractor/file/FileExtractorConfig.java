package cz.cuni.mff.xrg.intlib.extractor.file;

import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * File extractor configuration.
 *
 * @author Petyr
 *
 */
public class FileExtractorConfig implements DPUConfigObject {

	public String Path;

	public String FileSuffix;

	public String RDFFormatValue;
	
	public FileExtractType fileExtractType;

	public boolean OnlyThisSuffix;

	public boolean UseStatisticalHandler;

	@Override
	public boolean isValid() {
		return Path != null && 
				FileSuffix != null &&
				RDFFormatValue != null && 
				fileExtractType != null;
	}
}
