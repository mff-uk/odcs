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

	private String Path;

	private String FileSuffix;

	private RDFFormatType RDFFormatValue;

	private FileExtractType fileExtractType;

	private boolean OnlyThisSuffix;

	private boolean UseStatisticalHandler;

	private boolean failWhenErrors;

	public FileExtractorConfig() {
		this.Path = "";
		this.FileSuffix = "";
		this.RDFFormatValue = RDFFormatType.AUTO;
		this.fileExtractType = FileExtractType.PATH_TO_FILE;
		this.OnlyThisSuffix = false;
		this.UseStatisticalHandler = true;
		this.failWhenErrors = false;
	}

	public FileExtractorConfig(String Path, String FileSuffix,
			RDFFormatType RDFFormatValue, FileExtractType fileExtractType,
			boolean OnlyThisSuffix, boolean UseStatisticalHandler,
			boolean failWhenErrors) {

		this.Path = Path;
		this.FileSuffix = FileSuffix;
		this.RDFFormatValue = RDFFormatValue;
		this.fileExtractType = fileExtractType;
		this.OnlyThisSuffix = OnlyThisSuffix;
		this.UseStatisticalHandler = UseStatisticalHandler;
		this.failWhenErrors = failWhenErrors;
	}

	public String getPath() {
		return Path;
	}

	public String getFileSuffix() {
		return FileSuffix;
	}

	public RDFFormatType getRDFFormatValue() {
		return RDFFormatValue;
	}

	public FileExtractType getFileExtractType() {
		return fileExtractType;
	}

	public boolean isOnlyThisSuffix() {
		return OnlyThisSuffix;
	}

	public boolean isUseStatisticalHandler() {
		return UseStatisticalHandler;
	}

	public boolean isFailWhenErrors() {
		return failWhenErrors;
	}

	
	@Override
	public boolean isValid() {
		return Path != null
				&& FileSuffix != null
				&& RDFFormatValue != null
				&& fileExtractType != null;
	}
}
