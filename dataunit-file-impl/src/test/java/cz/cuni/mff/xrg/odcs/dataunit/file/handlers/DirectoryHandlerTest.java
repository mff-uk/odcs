package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link DirectoryHandler}.
 * 
 * @author Petyr
 */
public class DirectoryHandlerTest {

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
	public void testCtor() {
		assertFalse(dirToUse.exists());
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		assertTrue(dirToUse.exists());
	}
	
	@Test
	public void testAddNewFile() throws DataUnitException {
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		FileHandler file = handler.addNewFile("myFile");
		assertTrue(file.asFile().exists());
		assertEquals("myFile", file.getName());
	}
        
        @Test
	public void testAddNewFile2() throws DataUnitException {
                //adds name containing forward slash
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		FileHandler file = handler.addNewFile("http://myFile/x.txt");
		assertTrue(file.asFile().exists());
		assertEquals("/httpmyFilex.txt", file.getRootedPath());
                assertEquals("httpmyFilex.txt", file.getName());
	}

	@Test
	public void testAddNewDirectory() throws DataUnitException {
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		DirectoryHandler dir = handler.addNewDirectory("myDir");
		assertTrue(dir.asFile().exists());
		assertTrue(dir.asFile().isDirectory());
		assertEquals("myDir", dir.getName());
	}

	@Test
	public void testSubDirectory() throws DataUnitException {
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		DirectoryHandler dir = handler.addNewDirectory("myDir");
		FileHandler file = dir.addNewFile("myFile");
		// test
		assertEquals(1, handler.size());
		assertEquals(1, dir.size());
		assertTrue(file.asFile().exists());
		// delete the dir and file as well
		handler.clear();
		// test that the file has been deleted
		assertTrue(handler.isEmpty());
		assertFalse(dir.asFile().exists());
		assertFalse(file.asFile().exists());
	}
	
	@Test
	public void testGetByRootedName() throws DataUnitException {
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		DirectoryHandler dir = handler.addNewDirectory("myDir");
		FileHandler file = dir.addNewFile("myFile");
		// test the root path
		assertEquals("/myDir/myFile", file.getRootedPath());
		// test getter for wrong values
		assertEquals(file, handler.getByRootedName(file.getRootedPath()));
		assertEquals(file, dir.getByRootedName("/myFile"));
		assertEquals(null, dir.getByRootedName("/NotExistingDir/subFile"));
		assertEquals(null, handler.getByRootedName(""));
		assertEquals(null, handler.getByRootedName(null));
		assertEquals(null, handler.getByRootedName("//"));

		handler.clear();
	}
	
	@Test
	public void testAddExistingFile() 
			throws FileDataUnitException, DataUnitException {
		// we need some file .. 
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);

		final String fileName = "odcs-file-test-add-" + (new Date()).getTime();
		final File testFile = new File(FileUtils.getTempDirectory(), fileName);
		// use FileHandler to set content
		FileHandler testHandler = new FileHandlerImpl(testFile, null, fileName, false);
		final String content = "my content";
		testHandler.setContent(content);
		// add to the handler
		FileHandler hardLink = handler.addExistingFile(testFile, 
				new OptionsAdd(false));
		// release original file
		testHandler.asFile().delete();
		// test
		assertEquals(content, hardLink.getContent());
		// the file should be differ as we copy it
		assertNotEquals(testFile, hardLink.asFile());		
	}
	
	@Test
	public void testAddExistingDir() 
			throws FileDataUnitException, DataUnitException, IOException {
		// we need some file .. 
		DirectoryHandler handler = new DirectoryHandlerImpl(dirToUse);
		
		// prepare the directory that we add to the handler later
		final String dirName = "odcs-file-test/sample-dir-" + (new Date()).getTime();
		final File root = new File(FileUtils.getTempDirectory(), dirName);
		final File subDir = new File(root, "subDir");
		subDir.mkdirs();
		final File subFile = new File(root, "subFile");
		// use FileHandler to set content
		FileHandler fileHandler = new FileHandlerImpl(subFile, null, "subFile", false);
		final String content = "my content";
		fileHandler.setContent(content);
		
		// now add this structure
		DirectoryHandler dir = handler.addExistingDirectory(root, 
				new OptionsAdd(false));

		// release original structure, as we copy it in addExistingDirectory
		FileUtils.deleteDirectory(root);
	
		// check the directory
		assertEquals(2, dir.size());
		assertNotEquals(root, dir.asFile());
		
		// get handler by name
		FileHandler fileTestHandler = dir.addNewFile("subFile");
		assertNotNull(fileTestHandler);
		assertEquals(content, fileTestHandler.getContent());
	}	

	@Test 
	public void testAddAllAsLink() throws DataUnitException {
		DirectoryHandler source 
				= new DirectoryHandlerImpl(new File(dirToUse, "source"));
		// file to link 
		final File toLink = new File(dirToUse, "toLink");
		toLink.mkdirs();
		// prepare source
		final DirectoryHandler sourceDir 
				= source.addNewDirectory("directory");
		final FileHandler sourceFile 
				= source.addNewFile("file");
		final DirectoryHandler sourceLink 
				= source.addExistingDirectory(toLink, new OptionsAdd(true));
		// test link rooted path
		assertEquals("/toLink", sourceLink.getRootedPath());
		
		// target
		DirectoryHandler target 
				= new DirectoryHandlerImpl(new File(dirToUse, "target"));
		target.addAll(source);

		// check that the target get's all the data
		assertEquals(3, target.size());
		assertNotNull(target.getByName("directory"));
		assertNotNull(target.getByName("file"));
		assertNotNull(target.getByName("toLink"));
		
		// and also check rooted path
		assertEquals("/directory", target.getByName("directory").getRootedPath());
		assertEquals("/file", target.getByName("file").getRootedPath());
		assertEquals("/toLink", target.getByName("toLink").getRootedPath());
	}
	
}
