package cz.cuni.mff.xrg.odcs.dataunit.file.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileHandler;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for {@link FileDataUnitImpl}.
 * @author Petyr
 */
public class FileDataUnitImplTest {
	
	/**
	 * Just test the ctor and getters for name and type.
	 */
	@Test
	public void testCtor() {
		final File file = new File("/path");
		final String name = "name";
		FileDataUnit dataUnit = new FileDataUnitImpl(name, file);
		
		Assert.assertEquals(name, dataUnit.getDataUnitName());
		Assert.assertEquals(DataUnitType.FILE, dataUnit.getType());
	}
	
	@Test
	public void testEmptyClear() {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-empty");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// test clean
		dataUnit.clean();
		// delete
		dataUnit.delete();
	}
	
	@Test
	public void testCreateCreate() throws DataUnitException {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-create");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// add some data, that will create the directory
		FileHandler holder = dataUnit.create("first", true);
		// the file should have been created
		Assert.assertTrue(directory.exists());
		Assert.assertTrue(holder.asFile().exists());		
		dataUnit.delete();
		Assert.assertFalse(directory.exists());
	}

	@Test
	public void testCreateNotCreate() throws DataUnitException {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-not-create");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// add some data, that will create the directory
		FileHandler holder = dataUnit.create("first", false);
		// no file should be created, just the folder
		Assert.assertTrue(directory.exists());
		Assert.assertFalse(holder.asFile().exists());		
		dataUnit.delete();
		Assert.assertFalse(directory.exists());
	}
	
	@Test
	public void testDeleteHandler() throws DataUnitException {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-delete-handler");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// add some data, that will create the directory
		FileHandler first = dataUnit.create("first", true);
		FileHandler second = dataUnit.create("second", false);
		// the file should have been created
		Assert.assertTrue(directory.exists());
		Assert.assertTrue(first.asFile().exists());
		Assert.assertFalse(second.asFile().exists());
		// delete the handler
		dataUnit.delete(first);
		dataUnit.delete(second);
		// none of them should exist now, and non existing should not throw
		Assert.assertTrue(directory.exists());
		Assert.assertFalse(first.asFile().exists());
		Assert.assertFalse(second.asFile().exists());
		// delete 
		dataUnit.delete();
		Assert.assertFalse(directory.exists());
	}

	@Test
	public void testIterator() throws DataUnitException {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-iterator");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// add some data, that will create the directory
		LinkedList<FileHandler> handlers = new LinkedList<>();
		handlers.add(dataUnit.create("1", true));
		handlers.add(dataUnit.create("2", false));
		handlers.add(dataUnit.create("3", false));		
		// check that the directory exists
		Assert.assertTrue(directory.exists());
		// test the content of data unit
		for (FileHandler item : dataUnit) {
			Assert.assertNotNull(item);
		}
		// delete the dataunit
		dataUnit.delete();
		// tests that the directory has been deleted
		Assert.assertFalse(directory.exists());
	}	
	
	@Test
	public void testMerge() {

	}

	@Test
	public void testMergeIterator() {

	}
	
	
	@Test
	public void testDelete() throws IOException, DataUnitException {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-delete");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// add some data, that will create the directory
		dataUnit.create("first", true);
		// check that the directory exists
		Assert.assertTrue(directory.exists());
		// delete the dataunit
		dataUnit.delete();
		// tests that the directory has been deleted
		Assert.assertFalse(directory.exists());
	}
	
	@Test
	public void testClean() throws DataUnitException {
		// the temp directory will be created, the test sub directory will be not
		final File directory = new File(FileUtils.getTempDirectory(), "test-clean");
		Assert.assertFalse(directory.exists());
		// create data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);
		// add some data, that will create the directory
		dataUnit.create("first", true);
		// check that the directory exists
		Assert.assertTrue(directory.exists());
		// check that something is in the data unit
		Assert.assertTrue(dataUnit.iterator().hasNext());
		// delete the dataunit
		dataUnit.delete();
		// tests that the directory has been deleted
		Assert.assertFalse(directory.exists());
		Assert.assertFalse(dataUnit.iterator().hasNext());
	}
	
	@Test
	public void testLoad() throws IOException {
		final File directory = new File(FileUtils.getTempDirectory(), "test-load");
		final File directoryToLoad = new File(FileUtils.getTempDirectory(), "test-to-load");
		directoryToLoad.mkdirs();
		// prepare data unit
		ManageableFileDataUnit dataUnit = new FileDataUnitImpl("", directory);		
		// create files the directoryToLoad
		FileUtils.touch(new File(directoryToLoad, "1"));
		FileUtils.touch(new File(directoryToLoad, "2.dat"));
		// load the directory
		dataUnit.load(directoryToLoad);		
		// check the content
		
		// delete the dataunit
		dataUnit.delete();
		// tests that the directory has been deleted
		Assert.assertFalse(directory.exists());
		Assert.assertFalse(dataUnit.iterator().hasNext());
		// the added directory should still exist
		Assert.assertTrue(directoryToLoad.exists());
		FileUtils.deleteDirectory(directoryToLoad);
	}
	
}
