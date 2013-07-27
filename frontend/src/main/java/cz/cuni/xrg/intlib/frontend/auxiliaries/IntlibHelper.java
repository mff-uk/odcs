package cz.cuni.xrg.intlib.frontend.auxiliaries;

import com.vaadin.server.ThemeResource;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;

/**
 * Class with helper methods used in frontend.
 *
 * @author Bogo
 */
public class IntlibHelper {
    
    /**
     * Gets corresponding icon for given {@link ExecutionStatus}.
     *
     * @param status Status to get icon for.
     * @return Icon for given status.
     */
    public static ThemeResource getIconForExecutionStatus(PipelineExecutionStatus status) {
        ThemeResource img = null;
        switch (status) {
                    case FINISHED_SUCCESS:
                        img = new ThemeResource("icons/ok.png");
                        break;
                    case FINISHED_WARNING:
                        img = new ThemeResource("icons/warning.png");
                        break;
                    case FAILED:
                        img = new ThemeResource("icons/error.png");
                        break;
                    case RUNNING:
                        img = new ThemeResource("icons/running.png");
                        break;
                    case SCHEDULED:
                        img = new ThemeResource("icons/scheduled.png");
                        break;
                    case CANCELLED:
                        img = new ThemeResource("icons/cancelled.png");
                        break;
                    default:
                        //no icon
                        break;
                }
        return img;
    }
    
}
