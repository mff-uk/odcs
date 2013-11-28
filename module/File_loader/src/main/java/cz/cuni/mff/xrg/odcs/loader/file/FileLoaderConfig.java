package cz.cuni.mff.xrg.odcs.loader.file;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class FileLoaderConfig extends DPUConfigObjectBase {

	private String FilePath;

	private RDFFormatType RDFFileFormat;

	private boolean DiffName;

	private boolean validDataBefore;

	public FileLoaderConfig() {
		this.FilePath = "";
		this.RDFFileFormat = RDFFormatType.AUTO;
		this.DiffName = false;
		this.validDataBefore = false;
	}

	public FileLoaderConfig(String FilePath, RDFFormatType RDFFileFormat,
			boolean DiffName, boolean validDataBefore) {
		this.FilePath = FilePath;
		this.RDFFileFormat = RDFFileFormat;
		this.DiffName = DiffName;
		this.validDataBefore = validDataBefore;
	}

	public String getFilePath() {
		return FilePath;
	}

	public RDFFormatType getRDFFileFormat() {
		return RDFFileFormat;
	}

	public boolean isDiffName() {
		return DiffName;
	}

	public boolean isValidDataBefore() {
		return validDataBefore;
	}
	

	@Override
	public boolean isValid() {
		return FilePath != null
				&& RDFFileFormat != null;
	}
}
