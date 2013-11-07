package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;

/**
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class RDFLoaderConfig extends DPUConfigObjectBase {

	public String SPARQL_endpoint = "";

	public String Host_name = "";

	public String Password = "";

	public List<String> GraphsUri = new LinkedList<>();

	public WriteGraphType graphOption = WriteGraphType.OVERRIDE;

	public InsertType insertOption = InsertType.STOP_WHEN_BAD_PART;

	public long chunkSize = 100;

	public boolean validDataBefore = false;

	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null
				&& Host_name != null
				&& Password != null
				&& GraphsUri != null
				&& graphOption != null;
	}
}
