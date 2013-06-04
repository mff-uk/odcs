package module;

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
		
	public String OnlyThisText;
	
	public boolean OnlyThisSuffix;
}
