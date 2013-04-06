package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.load;

import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.ProcessingContext;
import java.util.Map;

/**
 * Context used by {@link Loader}s for the loading process.
 *
 * @see Loader
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class LoadContext extends ProcessingContext {

    public LoadContext(String ID, Map<String, Object> customData) {
        super(ID, customData);
    }
}
