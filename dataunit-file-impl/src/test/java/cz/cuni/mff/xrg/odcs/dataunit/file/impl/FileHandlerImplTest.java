package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileHandler;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for {@link FileHandlerImpl}.
 * @author Petyr
 */
public class FileHandlerImplTest {
	
	public void testCtor() throws IOException {
		final File tempFile = File.createTempFile("odcs-file-dataUnit", null);
		final String name = "my-data-unit";
		
		FileHandler handler = new FileHandlerImpl(tempFile, name);
		Assert.assertEquals(tempFile, handler.asFile());
		Assert.assertEquals(name, handler.getName());
	}
	
	@Test
	public void testContent() throws IOException, DataUnitException {
		// get some temp file
		final File tempFile = File.createTempFile("odcs-file-dataUnit", null);
		final String expectedContent = "some file content .. ";
		
		FileHandler handler = new FileHandlerImpl(tempFile, "name");
		// set the content
		handler.setContent(expectedContent);
		// get te content		
		final String content = handler.asString();
		// assert
		Assert.assertEquals(expectedContent, content);
		
		// the temp file will be deleted by the system
	}
	
	@Test
	public void testContentNew() throws IOException, DataUnitException {
		// get some temp file
		final File tempFile = File.createTempFile("odcs-file-dataUnit", null);
		final String expectedContent = "some file content .. ";
		
		FileHandler handler = new FileHandlerImpl(tempFile, "name");
		// set the content
		handler.setContent(expectedContent);
		// create new handler for the same file
		FileHandler newHandler = new FileHandlerImpl(tempFile, "name");
		final String content = newHandler.asString();
		// asserts
		Assert.assertEquals(expectedContent, content);
		
		// the temp file will be deleted by the system
	}	
	
}
