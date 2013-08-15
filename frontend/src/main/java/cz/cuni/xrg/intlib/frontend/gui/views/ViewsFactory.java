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
		case Initial:
			return new Initial();
		case Administrator:
			return new Settings();
		case DataBrowser:
			return new DataBrowser();
		case DPU:
			return new DPU();
		case ExecutionMonitor:
			return new ExecutionMonitor();
		case PipelineEdit:
		case PipelineEdit_New:
			return new PipelineEdit();
		case PipelineList:
			return new PipelineList();
		case Scheduler:
			return new Scheduler();
                case Login:
                        return new Login();
		default:
			return null;
		}
		
	}
	
	public static ViewNames getViewName(View view) {
		if(view.getClass() == PipelineList.class) {
			return ViewNames.PipelineList;
		} else if(view.getClass() == DPU.class) {
			return ViewNames.DPU;
		} else {
			return ViewNames.Initial;
		}
	}
	
}
