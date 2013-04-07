package cz.cuni.xrg.intlib.commons.loader;

import cz.cuni.xrg.intlib.commons.event.ProcessingContext;

import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Context used by {@link Load}s for the loading process.
 *
 * @see Load
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadContext extends ProcessingContext {

	// Repository repository, URI graph,
	
    public LoadContext(String ID, Map<String, Object> customData) {
        super(ID, customData);
    }
}
