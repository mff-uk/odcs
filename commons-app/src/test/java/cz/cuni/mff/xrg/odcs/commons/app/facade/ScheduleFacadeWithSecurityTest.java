package cz.cuni.mff.xrg.odcs.commons.app.facade;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import cz.cuni.mff.xrg.odcs.commons.app.user.User;

/**
 * Test suite for schedule facade interface. Each test is run in own
 * transaction, which is rolled back in the end.
 *
 * @author michal.klempa@eea.sk
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml","classpath:commons-app-test-context-security.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
public class ScheduleFacadeWithSecurityTest extends ScheduleFacadeTest {
	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authManager;
	
	@Autowired
	private UserFacade userFacade;

	
	@Before
	public void before() {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userFacade.getUserByUsername("jdoe");
			TestingAuthenticationToken token = new TestingAuthenticationToken(user,user.getPassword());
			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(token));
		}
	}
}
