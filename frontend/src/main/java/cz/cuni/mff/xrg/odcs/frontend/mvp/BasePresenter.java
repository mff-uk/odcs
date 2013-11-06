package cz.cuni.mff.xrg.odcs.frontend.mvp;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
/**
 *
 * @author Bogo
 */
public abstract class BasePresenter extends ViewComponent {

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		Class presenterClass = this.getClass();
		Model modelAnnotation = (Model) presenterClass.getAnnotation(Model.class);
		Class modelClass = modelAnnotation.value();
		MVPModel model = (MVPModel) App.getApp().getBean(modelClass);
		
		View viewAnnotation = (View) presenterClass.getAnnotation(View.class);
		Class viewClass = viewAnnotation.value();
		MVPView view = (MVPView) App.getApp().getBean(viewClass);
		
		this.setModel(model);
		this.setView(view);
		
		setCompositionRoot((Component) view);
	}
	
	public abstract void setModel(MVPModel model);
	
	public abstract void setView(MVPView view);
	
}
