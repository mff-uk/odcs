package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
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
		User user = authCtx.getUser();
		Integer rows = user == null 
				? null : user.getTableRows();
		
		if(rows == null) {
			rows = 20;
		}
		return rows;
	}
	
	public static int getColumnMaxLenght() {
		return 100;
	}
}
