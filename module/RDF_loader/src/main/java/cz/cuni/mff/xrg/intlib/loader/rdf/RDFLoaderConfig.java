package cz.cuni.mff.xrg.intlib.loader.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.rdf.enums.InsertType;

import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;

/**
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class RDFLoaderConfig implements DPUConfigObject {

	public String SPARQL_endpoint = "";

	public String Host_name = "";

	public String Password = "";

	public List<String> GraphsUri = new LinkedList<>();

	public WriteGraphType graphOption = WriteGraphType.OVERRIDE;

	public InsertType insertOption = InsertType.STOP_WHEN_BAD_PART;

	public long chunkSize = 100;

	@Override
	public boolean isValid() {
		return SPARQL_endpoint != null
				&& Host_name != null
				&& Password != null
				&& GraphsUri != null
				&& graphOption != null;
	}
}
