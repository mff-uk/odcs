package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.core;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

/**
 * File extractor configuration.
 * 
 * @author Jan Marcek
 * 
 */
public class CsvPoliticalExtractorConfig extends DPUConfigObjectBase {

    public String Path = "file:/e:/eea/comsode/dataset/political-dump.csv";
    public String TargetRDF = "e:/eea/comsode/rdf";
    public RDFFormatType RDFFormatValue = RDFFormatType.AUTO;
    public String FileSuffix = "";

    public FileExtractType fileExtractType = FileExtractType.PATH_TO_FILE;
    public boolean OnlyThisSuffix = false;
    public boolean UseStatisticalHandler = true;
    public boolean failWhenErrors = false;
    public Integer DebugProcessOnlyNItems = new Integer(10000);
    public Integer BatchSize = new Integer(10000);

    @Override
    public boolean isValid() {
        return Path != null && TargetRDF != null && FileSuffix != null && RDFFormatValue != null && fileExtractType != null && DebugProcessOnlyNItems != null
                && BatchSize != null;
    }
}
