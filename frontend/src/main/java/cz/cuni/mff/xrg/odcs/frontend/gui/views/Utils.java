package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author tomasknap
 */
@Component
public class Utils {
	
	@Autowired
	private AuthenticationContext authCtx;
	
	public int getPageLength() {
		Integer rows = authCtx.getUser().getTableRows();
		if(rows == null) {
			rows = 20;
		}
		return rows;
	}
}
