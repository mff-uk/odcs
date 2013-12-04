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
public class FileCsvExtractorConfig extends DPUConfigObjectBase {

//    public String Path = "e://eea//comsode//dataset//political-dump.csv";
    public String Path = "e://eea//comsode//dataset//procurements-dump.csv";
	public String FileSuffix = "";

	//public String Path = "e://eea//comsode//dataset//organisations-dump.csv";
	public RDFFormatType RDFFormatValue = RDFFormatType.AUTO;
	public FileExtractType fileExtractType = FileExtractType.PATH_TO_FILE;
	public boolean OnlyThisSuffix = false;
	public boolean UseStatisticalHandler = true;
	public boolean failWhenErrors = false;
    //TODO zapracovat
    int DebugProcessOnlyNItems = 2;
    //TODO zapracovat
    int batchSize = 5;

	@Override
	public boolean isValid() {
		return Path != null
				&& FileSuffix != null
				&& RDFFormatValue != null
				&& fileExtractType != null;
	}
}
