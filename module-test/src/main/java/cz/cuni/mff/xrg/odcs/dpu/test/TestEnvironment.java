package cz.cuni.mff.xrg.odcs.dpu.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPU;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.test.context.TestContext;
import cz.cuni.mff.xrg.odcs.dpu.test.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.dpu.test.data.VirtuosoConfig;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Hold environment used to test DPU.
 * 
 * @author Petyr
 * 
 */
public class TestEnvironment {

	private static final Logger LOG = LoggerFactory
			.getLogger(TestEnvironment.class);

	/**
	 * Configuration used to access virtuoso. If tests use Virtuoso then this
	 * must be set before first test. This value is shared by multiple tests.
	 */
	public static VirtuosoConfig virtuosoConfig = new VirtuosoConfig();

	/**
	 * Context used for testing.
	 */
	private final TestContext context;

	/**
	 * Working directory.
	 */
	private final File workingDirectory;

	/**
	 * Used {@link ManagableDataUnit}s
	 */
	private final LinkedList<ManagableDataUnit> dataUnits = new LinkedList<>();

	/**
	 * Directories for input {@link ManagableDataUnit}s.
	 */
	private final HashMap<String, ManagableDataUnit> inputDataUnits = new HashMap<>();

	/**
	 * Directories for output {@link ManagableDataUnit}s.
	 */
	private final HashMap<String, ManagableDataUnit> outputDataUnits = new HashMap<>();

	/**
	 * Factory for {@link DataUnit}s classes.
	 */
	private final DataUnitFactory dataUnitFactory;
	
	private TestEnvironment(File workingDirectory) {
		this.context = new TestContext();
		this.workingDirectory = workingDirectory;
		this.dataUnitFactory = new DataUnitFactory(workingDirectory);
	}

	/**
	 * Create test environment. As working directory is used tmp file.
	 * 
	 * @return Test environment.
	 */
	public static TestEnvironment create() {
		// we use tmp path and time to create tmp directory
		return create(FileUtils.getTempDirectory());
	}

	/**
	 * Create test environment.
	 * 
	 * @param directory Working directory.
	 * @return Test environment.
	 */
	public static TestEnvironment create(File directory) {
		final String testDirName = "odcs_test_"
				+ Long.toString((new Date()).getTime());
		
		return new TestEnvironment(new File(directory, testDirName));
	}

	// - - - - - - - - - methods for environment setup - - - - - - - - - //

	/**
	 * Set path that is used like jar-path during execution.
	 * 
	 * @param jarPath
	 */
	public void setJarPath(String jarPath) {
		context.setJarPath(jarPath);
	}

	/**
	 * Set time for last execution.
	 * 
	 * @param lastExecution 
	 */
	public void setLastExecution(Date lastExecution) {
		context.setLastExecution(lastExecution);
	}

	/**
	 * @param workingDirectory the workingDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setWorkingDirectory(File workingDirectory) {
		context.setWorkingDirectory(workingDirectory);
	}

	/**
	 * @param resultDirectory the resultDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setResultDirectory(File resultDirectory) {
		context.setResultDirectory(resultDirectory);
	}

	/**
	 * @param globalDirectory the globalDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setGlobalDirectory(File globalDirectory) {
		context.setGlobalDirectory(globalDirectory);
	}

	/**
	 * @param userDirectory the userDirectory to set, use null to use
	 * subdirectory in {@link#rootDirectory}
	 */
	public void setUserDirectory(File userDirectory) {
		context.setUserDirectory(userDirectory);
	}
	
	/**
	 * Set given {@link ManagableDataUnit} as an input. If there already is
	 * another value for given name it is overridden. The old
	 * {@link ManagableDataUnit} is not released.
	 * 
	 * @param name Name of dataUnit.
	 * @param dataUnit
	 */
	public void addInput(String name, ManagableDataUnit dataUnit) {
		inputDataUnits.put(name, dataUnit);
	}

	/**
	 * Set {@link ManagableDataUnit} where should the data, from output
	 * DataUnit, be stored. If there is other setting for given name then it is
	 * overwritten.
	 * 
	 * The data in given {@link ManagableDataUnit} may not be accessible after
	 * call of {@link #release()}.
	 * 
	 * If there already is another value for given name it is overridden. The
	 * old {@link ManagableDataUnit} is not released.
	 * 
	 * @param name Name of dataUnit.
	 * @param dataUnit
	 */
	public void addOutput(String name, ManagableDataUnit dataUnit) {
		outputDataUnits.put(name, dataUnit);
	}

	/**
	 * Create input {@link RdfDataUnit} that is used in test environment.
	 * 
	 * @param name Name.
	 * @param useVirtuoso If true then Virtuoso is used as a storage.
	 * @return Created input data unit.
	 * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
	 */
	public RDFDataUnit createRdfInput(String name, boolean useVirtuoso)
			throws RDFException {
		RDFDataUnit rdf = dataUnitFactory.createRdfDataUnit(name, useVirtuoso);
		addInput(name, rdf);
		return rdf;
	}

	/**
	 * Create input {@link RdfDataUnit} and populate it with data from given
	 * file. Created {@link RdfDataUnit} is used in test environment.
	 * 
	 * The data are loaded from file in test\resources.
	 * 
	 * @param name Name.
	 * @param useVirtuoso If true then Virtuoso is used as a storage.
	 * @param resourceName Name of resource file. The path to the resource file should be relative with respect to src/test/resources folder
	 * @param format Format of input file.
	 * @return Created input data unit.
	 * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
	 */
	public RDFDataUnit createRdfInputFromResource(String name,
			boolean useVirtuoso,
			String resourceName,
			RDFFormat format) throws RDFException {
		RDFDataUnit rdf = dataUnitFactory.createRdfDataUnit(name, useVirtuoso);
		// construct path to the resource
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(resourceName);
		// check ..
		if (url == null) {
			throw new RDFException("Missing input file in resource for: "
					+ resourceName);
		}
		File inputFile = new File(url.getPath());
		// return file
		rdf.addFromFile(inputFile, format);
		addInput(name, rdf);
		return rdf;
	}

	/**
	 * Create output {@link RdfDataUnit}, add it to the test environment and
	 * return it.
	 * 
	 * @param name Name.
	 * @param useVirtuoso If true then Virtuoso is used as a storage.
	 * @return Created RDFDataUnit.
	 * @throws cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException
	 */
	public RDFDataUnit createRdfOutput(String name, boolean useVirtuoso)
			throws RDFException {
		RDFDataUnit rdf = dataUnitFactory.createRdfDataUnit(name, useVirtuoso);
		addOutput(name, rdf);
		return rdf;
	}

	// - - - - - - - - - method for test execution - - - - - - - - - //

	/**
	 * Run given DPU in the test environment. The test environment is not reset
	 * before or after the test. If the test working directory should be deleted
	 * then is deleted at the end of this method same as all the
	 * {@link DataUnit}s
	 * 
	 * Any thrown exception is passed. In every case the {@link #release()}
	 * method must be called in order to release test data.
	 * 
	 * @param dpuInstance Instance of DPU to run.
	 * @return False if the execution failed by sending error message
	 * @throws java.lang.Exception
	 */
	public boolean run(DPU dpuInstance) throws Exception {
		// prepare dpu instance - set annotations
		connectDataUnits(dpuInstance);

		// execute
		dpuInstance.execute(context);

		return context.isPublishedError();
	}

	/**
	 * Delete testing data and release {@link ManagableDataUnit}s. Unused
	 * {@link ManagableDataUnit} are not deleted.
	 */
	public void release() {
		// release all DataUnits ..
		for (ManagableDataUnit item : dataUnits) {
			if (item != null) {
				item.delete();
			}
		}
		dataUnits.clear();
		// wait for some time .. so DataUnits can release their contexts
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		// delete working directory ..
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(workingDirectory);
		} catch (IOException e) {
			LOG.error("Failed to delete working directory.", e);
		}
	}

	// - - - - - - - - - methods for examining the results - - - - - - - - - //

	/**
	 * Return context used during tests. Return null before call of
	 * {@link #run(Class)} method.
	 * 
	 * @return
	 */
	public TestContext getContext() {
		return context;
	}

	// - - - - - - - - - - - - methods for dpu setup - - - - - - - - - - - - //

	private ManagableDataUnit getInputDataUnit(Field field, String name) {
		if (inputDataUnits.containsKey(name)) {
			// check type
			Class<?> fc = field.getType();
			Class<?> rc = inputDataUnits.get(name).getClass();

			if (fc.isAssignableFrom(rc)) {
				// class match
				return inputDataUnits.get(name);
			} else {
				// miss match ..
				return null;
			}
		}
		return null;
	}

	private ManagableDataUnit getOutputDataUnit(Field field, String name) {
		if (outputDataUnits.containsKey(name)) {
			// check type
			Class<?> fc = field.getType();
			Class<?> rc = outputDataUnits.get(name).getClass();

			if (fc.isAssignableFrom(rc)) {
				// class match
				return outputDataUnits.get(name);
			} else {
				// miss match ..
				return null;
			}
		}
		return null;
	}

	private void connectDataUnits(DPU dpuInstance) throws Exception {
		// add inputs
		List<AnnotationContainer<InputDataUnit>> inputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, InputDataUnit.class);
		for (AnnotationContainer<InputDataUnit> item : inputAnnotations) {
			ManagableDataUnit dataUnit = getInputDataUnit(item.getField(),
					item.annotation.name());
			if (dataUnit == null && !item.getAnnotation().optional()) {
				// missing non option dataUnit
				throw new Exception(
						"Test failure missing import mandatory DataUnit: "
								+ item.getAnnotation().name());
			}

			item.getField().set(dpuInstance, dataUnit);
			// ...
			dataUnits.add(dataUnit);
		}

		// add outputs
		List<AnnotationContainer<OutputDataUnit>> outputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, OutputDataUnit.class);
		for (AnnotationContainer<OutputDataUnit> item : outputAnnotations) {
			ManagableDataUnit dataUnit = getOutputDataUnit(item.getField(),
					item.annotation.name());
			item.getField().set(dpuInstance, dataUnit);
			if (dataUnit == null) {
				throw new Exception("Can not bind 'null' to output DataUnit: "
						+ item.getAnnotation().name());
			}
			// ...
			dataUnits.add(dataUnit);
		}
	}

}
