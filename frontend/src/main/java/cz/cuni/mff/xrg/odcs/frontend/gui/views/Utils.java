package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;

/**
 *
 * @author tomasknap
 */
public class Utils {
    //public static final int PAGE_LENGTH = 20;
	
	public static int getPageLength() {
		Integer rows = App.getApp().getAuthCtx().getUser().getTableRows();
		if(rows == null) {
			rows = 20;
		}
		return rows;
	}
}
