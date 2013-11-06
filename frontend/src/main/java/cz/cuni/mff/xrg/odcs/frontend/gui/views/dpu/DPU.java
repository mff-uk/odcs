package cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.VaadinView;

/**
 * GUI for DPU Templates page which opens from the main menu. Contains DPU
 * Templates tree, DPU Details, actions buttons.
 *
 *
 * @author Maria Kukhar
 */
//@org.springframework.stereotype.Component
//@Scope("prototype")
//@VaadinView(DPU.NAME)
//class DPU extends ViewComponent {
//	
//	DPUViewImpl view;
//	@Autowired
//	DPUPresenter dpuPresenter;
//	
//	
//
//
//	@Override
//	public boolean saveChanges() {
//		//control of the validity of Name field.
//		if (!view.validate()) {
//			//Notification.show("Failed to save DPURecord", "Mandatory fields should be filled", Notification.Type.ERROR_MESSAGE);
//			return false;
//		}
//		view.saveDPUTemplate();
//		return true;
//	}
//
//	@Override
//	public void enter(ViewChangeEvent event) {
//		view = new DPUViewImpl();
//		dpuPresenter.setView(view);
//		setCompositionRoot(view);
//	}
//	
//}
