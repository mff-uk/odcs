package cz.cuni.mff.xrg.odcs.frontend;

import com.vaadin.navigator.Navigator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;

/**
 *
 * @author Bogo
 */
public class ViewNavigator {
	
	private Navigator getNavigator() {
		return App.getApp().getNavigator();
	}
	
	public void navigateTo(String where) {
		getNavigator().navigateTo(where);
	}
	
	public void navigateTo(String where, Object parameter) {
		navigateTo(where + "/" + parameter);
	}
	
	public interface Navigatable {
		public void navigation(String where);
		public void navigation(String where, Object parameter);
	}
	
}
