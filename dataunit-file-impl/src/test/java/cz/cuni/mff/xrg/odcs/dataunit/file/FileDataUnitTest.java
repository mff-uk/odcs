package cz.cuni.mff.xrg.odcs.dataunit.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitAccessException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for {@link FileDataUnit}.
 * 
 * @author Petyr
 */
public class FileDataUnitTest {
	
	protected File dirToUse;
	
	protected File toDelete;
	
	@Before
	public void init() {
		this.toDelete = new File(FileUtils.getTempDirectory(), "odcs-file-test");
		this.dirToUse = new File(this.toDelete, 
				Long.toString((new Date()).getTime()));
	}
	
	@After
	public void clean() throws IOException {
		if (toDelete.exists()) {
			FileUtils.deleteDirectory(toDelete);
		}
	}	
	
	@Test
	public void testMerge() throws DataUnitException {
		ManageableFileDataUnit source = new FileDataUnitImpl("source", 
				new File(dirToUse, "source"));
		final String userData = "user data";
		source.getRootDir().addNewDirectory("myDir").setUserData(userData);
				
		ManageableFileDataUnit target = new FileDataUnitImpl("source", 
				new File(dirToUse, "source"));		
		
		// merge
		target.merge(source);
		
		// test content
		assertNotNull(target.getRootDir().getByName("myDir"));
		assertEquals(userData, 
				target.getRootDir().addNewDirectory("myDir").getUserData());
		
		// also we can not modify the original data
		DirectoryHandler dir = target.getRootDir().addNewDirectory("myDir");
		try {
			dir.addNewFile("fail");
			// this should thrown!
			fail("The addNewFile on merged data should failed!");
		} catch (DataUnitAccessException e) {
			// ok
		}
		
	}
	
}
