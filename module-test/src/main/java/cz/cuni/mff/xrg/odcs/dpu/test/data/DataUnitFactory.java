package cz.cuni.mff.xrg.odcs.dpu.test.data;

import static cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment.virtuosoConfig;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;

import java.io.File;
import java.util.Properties;

import org.openrdf.repository.RepositoryException;

/**
 * Create {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}s that can be used
 * in {@link cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment}.
 *
 * @author Petyr
 */
public class DataUnitFactory {

	/**
	 * Counter for dataUnits id's and directories.
	 */
	private int dataUnitIdCounter = 0;

	/**
	 * Working directory.
	 */
	private final File workingDirectory;

	/**
	 * Create a {@link DataUnitFactory} that use given directory as working
	 * directory.
	 *
	 * @param workingDirectory Directory where to create working subdirectories
	 *                         for
	 *                         {@link cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit}
	 *                         that use local storage as RDF repository.
	 */
	public DataUnitFactory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/**
	 * Create RDF data unit.
	 *
	 * @param name Name of the DataUnit.
	 * @param useVirtuoso False to use local repository, True to use Virtuoso.
	 * @return New {@link ManagableRdfDataUnit}.
	 * @throws RepositoryException 
	 */
	public ManagableRdfDataUnit createRDFDataUnit(String name,
			boolean useVirtuoso) {
		if (useVirtuoso) {
			return createVirtuosoRDFDataUnit(name);
		} else {
			return createLocalRDFDataUnit(name);
		}
	}

	/**
	 * Create RDF data unit with given name that is stored in local file.
	 *
	 * @param name Name of DataUnit.
	 * @return New {@link ManagableRdfDataUnit}.
	 */
	private ManagableRdfDataUnit createLocalRDFDataUnit(String name) {
		final String number = Integer.toString(dataUnitIdCounter++);
		final String repoPath = workingDirectory.toString()
				+ File.separatorChar + "dataUnit" + File.separatorChar + number;
		final String id = "dpu-test_" + number + "_" + name;
		final String namedGraph = GraphUrl.translateDataUnitId(id);

		return RDFDataUnitFactory.createLocalRDFRepo(repoPath, name,
				namedGraph);
	}

	/**
	 * Create RDF data unit with given name that is stored in virtuoso.
	 *
	 * @param name Name of DataUnit.
	 * @return New {@link ManagableRdfDataUnit}.
	 * @throws RepositoryException 
	 */
	private ManagableRdfDataUnit createVirtuosoRDFDataUnit(String name) {
		final String number = Integer.toString(dataUnitIdCounter++);
		final String id = "dpu-test_" + number + "_" + name;
		final String namedGraph = GraphUrl.translateDataUnitId(id);
		final String dataUnitName = id;

		return RDFDataUnitFactory.createVirtuosoRDFRepo(virtuosoConfig.host,
				virtuosoConfig.port, virtuosoConfig.user,
				virtuosoConfig.password, dataUnitName, namedGraph
				);
	}

}
