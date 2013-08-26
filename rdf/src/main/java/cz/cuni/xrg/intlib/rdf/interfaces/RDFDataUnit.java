package cz.cuni.xrg.intlib.rdf.interfaces;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;

import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

/**
 * Provides method for working with RDF data repository.
 *
 * @author Jiri Tomes
 * @author Petyr
 *
 */
public interface RDFDataUnit extends DataUnit, RDFDataUnitHelper {

	/**
	 * Add one tripple RDF (statement) to the repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	public void addTriple(String namespace, String subjectName,
			String predicateName,
			String objectName);

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI prefix
	 *                            namespace used by all triples.
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value, if during extraction needed
	 *                            detail statistic about RDF triples and
	 *                            detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(FileExtractType extractType,
			String path, String suffix,
			String baseURI,
			boolean useSuffix, boolean useStatisticHandler) throws RDFException;

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param format              Specifies concrete {@link RDFFormat} (e.g.,
	 *                            RDFXML, Turtle, ..) if RDF format can not be
	 *                            detected from file suffix.
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI prefix
	 *                            namespace used by all triples.
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value, if during extraction needed
	 *                            detail statistic about RDF triples and
	 *                            detailed log or not.
	 * @throws RDFException when extraction fail.
	 */
	public void extractFromFile(RDFFormat format, FileExtractType extractType,
			String path, String suffix,
			String baseURI,
			boolean useSuffix, boolean useStatisticHandler) throws RDFException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param directoryPath Path to directory, where file with RDF data will be
	 *                      saved.
	 * @param fileName      Name of file for saving RDF data.
	 * @param formatType    Type of RDF format for saving data (example: TURTLE,
	 *                      RDF/XML,etc.)
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data to file fail.
	 */
	public void loadToFile(String directoryPath,
			String fileName,
			RDFFormatType formatType) throws CannotOverwriteFileException, RDFException;

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param directoryPath    Path to directory, where file with RDF data will
	 *                         be saved.
	 * @param fileName         Name of file for saving RDF data.
	 * @param formatType       Type of RDF format for saving data (example:
	 *                         TURTLE, RDF/XML,etc.)
	 * @param canFileOverWrite boolean value, if existing file can be
	 *                         overwritten.
	 * @param isNameUnique     boolean value, if every pipeline execution has
	 *                         his unique name.
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data to file fail.
	 */
	public void loadToFile(String directoryPath,
			String fileName, RDFFormatType formatType,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, RDFException;

	/**
	 * Return if RDF triple is in repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 * @return true if such statement is in repository, false otherwise.
	 */
	public boolean isTripleInRepository(String namespace, String subjectName,
			String predicateName,
			String objectName);

	/**
	 * Removes all RDF data from repository.
	 */
	public void cleanAllData();

	/**
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepository goal repository where RDF data are added.
	 */
	public void copyAllDataToTargetRepository(RDFDataUnit targetRepo);

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param second Type of repository contains RDF data as implementation of
	 *               RDFDataUnit interface.
	 * @throws IllegalArgumentException if second repository as param is null.
	 */
	public void mergeRepositoryData(RDFDataUnit second) throws IllegalArgumentException;

	/**
	 * Return openRDF repository needed for almost every operation using RDF.
	 *
	 * @return openRDF repository.
	 */
	public Repository getDataRepository();

	/**
	 * Return URI representation of graph where RDF data are stored.
	 *
	 * @return graph with stored data as URI.
	 */
	public URI getDataGraph();

	/**
	 * Set data graph storage for given data in RDF format.
	 *
	 * @param newDataGraph new graph representated as URI.
	 */
	public void setDataGraph(URI newDataGraph);

	/**
	 * Set new data graph as default storage for data in RDF format.
	 *
	 * @param newStringDataGraph String name of graph as URI - starts with
	 *                           prefix http://).
	 */
	public void setDataGraph(String newStringDataGraph);
}
