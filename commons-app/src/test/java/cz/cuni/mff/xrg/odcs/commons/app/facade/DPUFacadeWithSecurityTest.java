package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUType;
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
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for DPU facade interface. Each test is run in own transaction,
 * which is rolled back in the end.
 *
 * @author michal.klempa@eea.sk
 */
//@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml","classpath:commons-app-test-context-security.xml"})
//@RunWith(SpringJUnit4ClassRunner.class)
//@TransactionConfiguration(defaultRollback = true)
public class DPUFacadeWithSecurityTest { // extends DPUFacadeTest {
	
//	@Autowired
//	@Qualifier("authenticationManager")
//	private AuthenticationManager authManager;
//	
//	@Autowired
//	private UserFacade userFacade;
	
//	@Before
//	public void before() {
//		if (SecurityContextHolder.getContext().getAuthentication() == null) {
//			User user = userFacade.getUserByUsername("jdoe");
//			TestingAuthenticationToken token = new TestingAuthenticationToken(user,user.getPassword());
//			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(token));
//		}
//	}
//	
//    @Autowired
//    private DPUFacade dpuFacade;	
//	
//    @Test
//    @Transactional
//    public void testCreateTemplate() {
//        System.out.println("createTemplate");
//        DPUTemplateRecord templateRecord = dpuFacade.createTemplate("testName", DPUType.EXTRACTOR);
//        assertNotNull(templateRecord);
//        assertEquals(DPUType.EXTRACTOR, templateRecord.getType());
//        assertEquals("testName", templateRecord.getName());
//    }	
	
}
