package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.navigator.View;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;

/**
 * Factory for classes in {@link cz.cuni.xrg.intlib.frontend.gui.views} package.
 * 
 * @author Petyr
 *
 */
public class ViewsFactory {
	
	/**
	 * Create and return view. 
	 * @param view Determine view.
	 * @return View or null in case of bad view.
	 */
	public static ViewComponent create(ViewNames view) {
		switch(view) {
		case INITIAL:
			return new Initial();
		case ADMINISTRATOR:
			return new Settings();
		case DATA_BROWSER:
			return new DataBrowser();
		case DPU:
			return new DPU();
		case EXECUTION_MONITOR:
			return new ExecutionMonitor();
		case PIPELINE_EDIT:
		case PIPELINE_EDIT_NEW:
			return new PipelineEdit();
		case PIPELINE_LIST:
			return new PipelineList();
		case SCHEDULER:
			return new Scheduler();
                case LOGIN:
                        return new Login();
		default:
			return null;
		}
		
	}
	
	public static ViewNames getViewName(View view) {
		if(view.getClass() == PipelineList.class) {
			return ViewNames.PIPELINE_LIST;
		} else if(view.getClass() == DPU.class) {
			return ViewNames.DPU;
		} else {
			return ViewNames.INITIAL;
		}
	}
	
}
