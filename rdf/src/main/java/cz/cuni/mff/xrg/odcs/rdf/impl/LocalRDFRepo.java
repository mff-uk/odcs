package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.*;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.LoggerFactory;

/**
 * Implementation of local RDF repository - RDF data are saved in files on hard
 * disk in computer, intermediate results are keeping in computer memory.
 *
 * @author Jiri Tomes
 */
public class LocalRDFRepo extends BaseRDFRepo {

	/**
	 * Default name for data file.
	 */
	private final static String dumpName = "dump_dat.ttl";

	/**
	 * How many triples is possible to merge at once.
	 */
	private final static long DEFAULT_MERGE_PART_SIZE = 1000;

	/**
	 * Directory root, where is repository stored.
	 */
	private File WorkingRepoDirectory;

	/**
	 * Public constructor - create new instance of repository in defined
	 * repository Path.
	 *
	 * @param repositoryPath
	 * @param fileName
	 * @param dataUnitName   DataUnit's name. If not used in Pipeline can be
	 *                       empty String.
	 */
	public LocalRDFRepo(String repositoryPath, String fileName,
			String namedGraph, String dataUnitName) {
		callConstructorSetting(repositoryPath, fileName, namedGraph,
				dataUnitName);
	}

	private void callConstructorSetting(String repoPath, String fileName,
			String namedGraph, String dataUnitName) {
		setReadOnly(false);

		File dataFile = new File(repoPath, fileName);

		NativeStore nativeStore = new NativeStore(dataFile);
		nativeStore.setForceSync(false);

		logger = LoggerFactory.getLogger(LocalRDFRepo.class);
		repository = new SailRepository(nativeStore);
		repository.setDataDir(dataFile);

		setDataGraph(namedGraph);
		WorkingRepoDirectory = dataFile.getParentFile();

		this.dataUnitName = dataUnitName;

		try {
			repository.initialize();
			logger.info("New local repository with data graph <"
					+ getDataGraph().stringValue()
					+ "> successfully incicialized.");

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());

		}
	}

	/**
	 * Set new graph as default for working data in RDF format.
	 *
	 * @param defaultGraph String name of graph as URI - starts with prefix
	 *                     http://).
	 */
	@Override
	public void setDataGraph(String newStringDataGraph) {
		setDataGraph(createNewGraph(newStringDataGraph));
	}

	@Override
	public void delete() {
		if (repository.isInitialized()) {
			cleanAllData();
		}
		File dataDir = repository.getDataDir();
		release();
		while (repository.isInitialized()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
				logger.debug("Thread sleeping interupted " + ex.getMessage());
			}
		}
		deleteDataDirectory(dataDir);
	}

	@Override
	public void release() {
		logger.info("Releasing DPU LocalRdf: {}", WorkingRepoDirectory
				.toString());
		shutDown();
		logger.info("Relelased LocalRdf: {}", WorkingRepoDirectory
				.toString());
	}

	@Override
	public DataUnitType getType() {
		return DataUnitType.RDF_Local;
	}

	/**
	 * Load data from given file into repository.
	 *
	 * @param file
	 */
	@Override
	public void load(File directory) {
		logger.info(
				"LOAD INPUT graph <" + graph.stringValue() + "> in dir: " + directory
				.toString());

		File file = getFileForDirectory(directory);

		final String suffix = "";
		final String baseURI = "";
		final boolean useSuffix = false;
		final boolean useStatisticHandler = true;

		try {
			extractFromFile(FileExtractType.PATH_TO_FILE, null,
					file.getAbsolutePath(), suffix,
					baseURI,
					useSuffix, useStatisticHandler);

		} catch (RDFException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Save data from repository into given file.
	 *
	 * @param directory
	 */
	@Override
	public void save(File directory) {

		logger.info(
				"Save OUTPUT graph <" + graph.stringValue() + ">- in dir: " + directory
				.toString());
		File file = getFileForDirectory(directory);

		RDFFormat format = RDFFormat.forFileName(file.getAbsolutePath(),
				RDFFormat.RDFXML);

		RDFFormatType formatType = RDFFormatType.getTypeByRDFFormat(format);

		logger.debug("saving to directory:" + directory.getAbsolutePath());

		try {
			loadToFile(file.getAbsolutePath(), formatType, true, false);
		} catch (CannotOverwriteFileException | RDFException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param second Type of repository contains RDF data as implementation of
	 *               RDFDataUnit interface.
	 * @throws IllegalArgumentException if second repository as param is null.
	 */
	@Override
	public void mergeRepositoryData(RDFDataUnit second) throws IllegalArgumentException {

		if (second == null) {
			throw new IllegalArgumentException(
					"Instance of RDFDataRepository is null");
		}

		RepositoryConnection targetConnection = null;

		RepositoryResult<Statement> lazySource = null;

		try {
			lazySource = second.getRepositoryResult();

			targetConnection = repository.getConnection();

			if (targetConnection != null) {

				logger.info("Merging " + second.getTripleCount()
						+ " triples from <" + second.getDataGraph()
						.stringValue() + "> "
						+ "TO <" + getDataGraph().stringValue() + ">.");

				long addedParts = 0;
				long partsCount = getPartsCount(second, DEFAULT_MERGE_PART_SIZE);

				List<Statement> statements = getNextDataPart(lazySource,
						DEFAULT_MERGE_PART_SIZE);

				while (!statements.isEmpty()) {
					addedParts++;

					final String processing = getPartsProcessing(addedParts,
							partsCount);

					mergeNextDataPart(targetConnection, statements,
							processing);

					statements = getNextDataPart(lazySource,
							DEFAULT_MERGE_PART_SIZE);
				}


				logger.info("Merged SUCESSFULL");
			}


		} catch (RepositoryException ex) {
			logger.error(ex.getMessage(), ex);

		} finally {
			try {
				if (lazySource != null) {
					lazySource.close();
				}
				if (targetConnection != null) {
					targetConnection.close();
				}
			} catch (RepositoryException ex) {
				logger.error(ex.getMessage(), ex);
			}

		}
	}

	private List<Statement> getNextDataPart(
			RepositoryResult<Statement> lazySource,
			long chunkSize) throws RepositoryException {

		List<Statement> result = new ArrayList<>();

		long count = 0;

		while (lazySource.hasNext()) {
			if (count < chunkSize) {
				Statement nextStatement = lazySource.next();
				result.add(nextStatement);
				count++;
			} else {
				break;
			}
		}

		return result;
	}

	private String getPartsProcessing(long addedParts, long partsCount) {
		final String processing = String.valueOf(addedParts) + "/" + String
				.valueOf(partsCount);

		return processing;
	}

	private void mergeNextDataPart(
			RepositoryConnection targetConnection, List<Statement> statements,
			String processing)
			throws RepositoryException {

		addStatementsCollection(targetConnection, statements);
		statements.clear();
		logger.debug(
				"Merging data part " + processing + " were successful");
	}

	private void addStatementsCollection(RepositoryConnection targetConnection,
			List<Statement> statements) throws RepositoryException {
		if (graph != null) {
			targetConnection.add(statements, graph);
		} else {
			targetConnection.add(statements);
		}
	}

	/**
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepository goal repository where RDF data are added.
	 */
	@Override
	public void copyAllDataToTargetDataUnit(RDFDataUnit targetRepo) {

		if (targetRepo == null) {
			throw new IllegalArgumentException(
					"Instance of RDFDataRepository is null");
		}
		Repository targetRepository = targetRepo.getDataRepository();

		RepositoryConnection sourceConnection = null;
		RepositoryConnection targetConnection = null;

		try {
			sourceConnection = repository.getConnection();

			if (!sourceConnection.isEmpty()) {

				List<Statement> sourceStatemens = this.getTriples();

				targetConnection = targetRepository.getConnection();

				Resource targetGraph = targetRepo.getDataGraph();

				if (targetGraph != null) {
					targetConnection.add(sourceStatemens, targetGraph);
				} else {
					targetConnection.add(sourceStatemens);
				}

			}
		} catch (RepositoryException ex) {

			logger.debug(ex.getMessage(), ex);

		} finally {
			if (sourceConnection != null) {
				try {
					sourceConnection.close();
				} catch (RepositoryException ex) {
					logger.debug(ex.getMessage(), ex);
				}
			}
			if (targetConnection != null) {
				try {
					targetConnection.close();
				} catch (RepositoryException ex) {
					logger.debug(ex.getMessage(), ex);
				}
			}
		}

	}

	private File getFileForDirectory(File directory) {

		if (!directory.exists()) {
			directory.mkdirs();
		}

		File file = new File(directory, dumpName);
		return file;
	}

	public File getWorkingRepoDirectory() {
		return WorkingRepoDirectory;
	}

	private void deleteDataDirectory(File dir) {

		if (dir == null) {
			return;
		}

		if (dir.isFile()) {
			dir.delete();
		} else if (dir.isDirectory()) {
			File[] files = dir.listFiles();

			if (files != null) {
				for (File file : files) {
					deleteDataDirectory(file);
				}

				dir.delete();
			}
		}
	}
}