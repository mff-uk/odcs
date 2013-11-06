package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ModifiableComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.xpoft.vaadin.VaadinView;

/**
 * Wrapper of MVP classes before the navigation is changed to work on presenters.
 * 
 * @author Bogo
 */
//@Component
//@Scope("prototype")
//@VaadinView(PipelineList.NAME)
//public class PipelineList extends ViewComponent implements ModifiableComponent {
//
//	PipelineListViewImpl view;
//	@Autowired
//	PipelineListPresenter pipelineListPresenter;
//
//	@Override
//	public void enter(ViewChangeEvent event) {
//		view = new PipelineListViewImpl();
//
//		pipelineListPresenter.setView(view);
//		setCompositionRoot(view);
//	}
//
//	@Override
//	public boolean isModified() {
//		return view.isModified(); 
//	}
//	
//	/**
//	 * View name.
//	 */
//        //TODO do we need this?
//	public static final String NAME = "PipelineList";
//}
