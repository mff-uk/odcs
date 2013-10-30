package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ModifiableComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.xpoft.vaadin.VaadinView;

@Component
@Scope("prototype")
@VaadinView(PipelineList.NAME)
public class PipelineList extends ViewComponent implements ModifiableComponent {

	PipelineListModel model;
	PipelineListViewImpl view;

	@Override
	public void enter(ViewChangeEvent event) {
		model = new PipelineListModel();
		view = new PipelineListViewImpl();

		PipelineListPresenter presenter = new PipelineListPresenter(model, view);
		setCompositionRoot(view);
	}

	@Override
	public boolean isModified() {
		return view.isModified(); 
	}
//	/**
//	 * View name.
//	 */
	public static final String NAME = "PipelineList";
}
