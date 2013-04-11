package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.event.ProcessingContext;
import java.util.Map;

/**
 * Context used by {@link Transform}s for the transformation process.
 *
 * @see Transform
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class TransformContext extends ProcessingContext {

	//Repository repository, URI graph
	
    public TransformContext(String id, Map<String, Object> customData) {
        super(id, customData);
    }
}