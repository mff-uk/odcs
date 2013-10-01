package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

/**
 * File extractor configuration.
 *
 * @author Petyr
 *
 */
public class FileExtractorConfig extends DPUConfigObjectBase {

	public String Path = "";

	public String FileSuffix = "";

	public RDFFormatType RDFFormatValue = RDFFormatType.AUTO;
	
	public FileExtractType fileExtractType = FileExtractType.PATH_TO_FILE;

	public boolean OnlyThisSuffix = false;

	public boolean UseStatisticalHandler = false;

	@Override
	public boolean isValid() {
		return Path != null && 
				FileSuffix != null &&
				RDFFormatValue != null && 
				fileExtractType != null;
	}
}
