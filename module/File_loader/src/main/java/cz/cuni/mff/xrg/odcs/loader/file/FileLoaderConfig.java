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

	/**
	 * True if the input should be copied to the output.
	 */
	private boolean penetrable;
	
	public FileLoaderConfig() {
		this.FilePath = "";
		this.RDFFileFormat = RDFFormatType.AUTO;
		this.DiffName = false;
		this.validDataBefore = false;
		this.penetrable = false;
	}

	public FileLoaderConfig(String FilePath, RDFFormatType RDFFileFormat,
			boolean DiffName, boolean validDataBefore) {
		this.FilePath = FilePath;
		this.RDFFileFormat = RDFFileFormat;
		this.DiffName = DiffName;
		this.validDataBefore = validDataBefore;
		this.penetrable = false;
	}

	/**
	 * Returns the path to file as string value.
	 *
	 * @return the path to file as string value.
	 */
	public String getFilePath() {
		return FilePath;
	}

	/**
	 * Returns selected {@link RDFFormatType} for RDF data.
	 *
	 * @return selected {@link RDFFormatType} for RDF data.
	 */
	public RDFFormatType getRDFFileFormat() {
		return RDFFileFormat;
	}

	/**
	 * Returns true, if each execution produces file with different name, false
	 * otherwise.
	 *
	 * @return true, if each execution produces file with different name, false
	 *         otherwise.
	 */
	public boolean isDiffName() {
		return DiffName;
	}

	/**
	 * Returns true, if data are validated before loading to file, false
	 * otherwise.
	 *
	 * @return true, if data are validated before loading to file, false
	 *         otherwise.
	 */
	public boolean isValidDataBefore() {
		return validDataBefore;
	}

	public void setFilePath(String FilePath) {
		this.FilePath = FilePath;
	}

	public void setRDFFileFormat(RDFFormatType RDFFileFormat) {
		this.RDFFileFormat = RDFFileFormat;
	}

	public void setDiffName(boolean DiffName) {
		this.DiffName = DiffName;
	}

	public void setValidDataBefore(boolean validDataBefore) {
		this.validDataBefore = validDataBefore;
	}	

	public boolean isPenetrable() {
		return penetrable;
	}

	public void setPenetrable(boolean redirectInput) {
		this.penetrable = redirectInput;
	}
		
	/**
	 * Returns true, if DPU configuration is valid, false otherwise.
	 *
	 * @return true, if DPU configuration is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		return FilePath != null
				&& RDFFileFormat != null;
	}
	
}
