package cz.cuni.mff.xrg.intlib.extractor.file;

import cz.cuni.xrg.intlib.commons.configuration.Config;

/**
 * File extractor configuration.
 *
 * @author Petyr
 *
 */
public class FileExtractorConfig implements Config {

	public String Path;

	public String FileSuffix;

	public String RDFFormatValue;
	
	public FileExtractType fileExtractType;

	public boolean OnlyThisSuffix;

	public boolean UseStatisticalHandler;
}
