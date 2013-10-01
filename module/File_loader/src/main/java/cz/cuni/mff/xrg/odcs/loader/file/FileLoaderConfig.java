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

	public String FilePath = "";

	public RDFFormatType RDFFileFormat = RDFFormatType.AUTO;

	public boolean DiffName = false;

	@Override
	public boolean isValid() {
		return FilePath != null
				&& RDFFileFormat != null;
	}
}
