package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

public class LocalFSFilesDataUnit implements ManageableWritableFilesDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFSFilesDataUnit.class);

    private String dataUnitName;

    private ManagableRdfDataUnit backingStore;

    private File workingDirectory;

    private String workingDirectoryURI;

    private Set<String> generatedFilenames = java.util.Collections.<String> synchronizedSet(new HashSet<String>());

    private Thread ownerThread;

    private static String FILE_EXISTS_ASK_QUERY = "ASK { ?pathUri <" + FilesDataUnit.PREDICATE_SYMBOLIC_NAME + ">\"%s\" }";

    private static int PROPOSED_FILENAME_PART_MAX_LENGTH = 10;

    public LocalFSFilesDataUnit(RDFDataUnitFactory rdfDataUnitFactory, String globalWorkingDirectory, String pipelineId, String dataUnitName) throws DataUnitException {
        try {
            this.dataUnitName = dataUnitName;
            this.workingDirectory = Files.createTempDirectory(FileSystems.getDefault().getPath(globalWorkingDirectory), "").toFile();
            this.workingDirectoryURI = workingDirectory.toURI().toASCIIString();
            this.backingStore = rdfDataUnitFactory.create(pipelineId, dataUnitName, workingDirectoryURI);
            this.ownerThread = Thread.currentThread();
        } catch (IOException ex) {
            throw new DataUnitException("Error creating data unit.", ex);
        }
    }

    //DataUnit interface
    @Override
    public ManagableDataUnit.Type getType() {
        return ManagableDataUnit.Type.FILES;
    }

    //DataUnit interface
    @Override
    public boolean isType(ManagableDataUnit.Type dataUnitType) {
        return this.getType().equals(dataUnitType);
    }

    //DataUnit interface
    @Override
    public String getName() {
        return dataUnitName;
    }

    //MetadataDataUnit interface
    @Override
    public RepositoryConnection getConnection() throws DataUnitException {
        return backingStore.getConnection();
    }

    //MetadataDataUnit interface
    @Override
    public Set<org.openrdf.model.URI> getMetadataGraphnames() throws DataUnitException {
        return backingStore.getDataGraphnames();
    }

    //FilesDataUnit interface
    @Override
    public FilesDataUnit.Iteration getIteration() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return new WritableFileIterationImpl(backingStore);
    }

    //WritableFilesDataUnit interface
    @Override
    public String getBaseFileURIString() {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return workingDirectoryURI;
    }

    //WritableFilesDataUnit interface
    @Override
    public void addExistingFile(String proposedSymbolicName, String existingFileURI) throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        File existingFile = new File(URI.create(existingFileURI));
        if (!existingFile.exists()) {
            throw new IllegalArgumentException("File does not exist: " + existingFileURI + ". File must exists prior being added.");
        }
        if (!existingFile.isFile()) {
            throw new IllegalArgumentException("Only files are permitted to be added. File " + existingFileURI + " is not a proper file.");
        }
//        try {
//            if (!FileSystems.getDefault().getPath(existingFile.getCanonicalPath()).startsWith(workingDirectoryCannonicalPath)) {
//                throw new IllegalArgumentException("Only files under the " + workingDirectoryCannonicalPath + " are permitted to be added. File " + existingFileFullPath + " is not located there.");
//            }
//        } catch (IOException ex) {
//            throw new IllegalArgumentException("Only files under the " + workingDirectoryCannonicalPath + " are permitted to be added. File " + existingFileFullPath + " is not located there.", ex);
//        }

        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = backingStore.getConnection();
            connection.begin();
            // TODO michal.klempa - add one query at isReleaseReady instead of this
//            BooleanQuery fileExistsQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, String.format(FILE_EXISTS_ASK_QUERY, proposedSymbolicName));
//            if (fileExistsQuery.evaluate()) {
//                connection.rollback();
//                throw new IllegalArgumentException("File with symbolic name "
//                        + proposedSymbolicName + " already exists in scope of this data unit. Symbolic name must be unique.");
//            }
            ValueFactory valueFactory = connection.getValueFactory();
            BNode blankNodeId = valueFactory.createBNode();
            Statement statement = valueFactory.createStatement(
                    blankNodeId,
                    valueFactory.createURI(FilesDataUnit.PREDICATE_SYMBOLIC_NAME),
                    valueFactory.createLiteral(proposedSymbolicName)
                    );
            Statement statement2 = valueFactory.createStatement(
                    blankNodeId,
                    valueFactory.createURI(FilesDataUnit.PREDICATE_FILE_URI),
                    valueFactory.createLiteral(existingFile.toURI().toASCIIString())
                    );
            connection.add(statement, backingStore.getBaseDataGraphURI());
            connection.add(statement2, backingStore.getBaseDataGraphURI());
            connection.commit();
            generatedFilenames.remove(existingFileURI);
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding file.", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }

    //WritableFilesDataUnit interface
    @Override
    public String createFile() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return this.createFile("");
    }

    //WritableFilesDataUnit interface
    @Override
    public String createFile(String proposedSymbolicName) throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        Path newFile = null;
        String filteredProposedSymbolicName = filterProposedSymbolicName(proposedSymbolicName);
        try {
            newFile = Files.createTempFile(workingDirectory.toPath(), filteredProposedSymbolicName, "");
            generatedFilenames.add(newFile.toUri().toASCIIString());
        } catch (IOException ex) {
            throw new DataUnitException("Error when generating filename.", ex);
        }
        return newFile.toUri().toASCIIString();
    }

    //ManageableDataUnit interface
    @Override
    public void clear() {
        FileUtils.deleteQuietly(this.workingDirectory);
        backingStore.clear();
    }

    //ManageableDataUnit interface
    @Override
    public void isReleaseReady() {
        if (generatedFilenames.size() > 0) {
            LOG.error("{} file names have been generated but never added as existing files after DPU execution. dataUnitName '{}'.",
                    generatedFilenames.size(), this.getName());
        }
        backingStore.isReleaseReady();
    }

    //ManageableDataUnit interface
    @Override
    public void release() {
        backingStore.release();
    }

    //ManageableDataUnit interface
    @Override
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException, DataUnitException {
        if (!this.getClass().equals(otherDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + this.getClass().getCanonicalName()
                    + " and it cannot merge other DataUnit of class "
                    + otherDataUnit.getClass().getCanonicalName() + ".");
        }

        final LocalFSFilesDataUnit otherFilesDataUnit = (LocalFSFilesDataUnit) otherDataUnit;
        backingStore.merge(otherFilesDataUnit.backingStore);
    }

    @Override
    public void load() {
        backingStore.load();
    }

    @Override
    public void store() {
        backingStore.store();
    }

    private String filterProposedSymbolicName(String proposedSymbolicName) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while ((sb.length() < PROPOSED_FILENAME_PART_MAX_LENGTH) || (index < proposedSymbolicName.length())) {
            int codePoint = proposedSymbolicName.codePointAt(index);
            if (sb.length() == 0) {
                if (((codePoint >= 97) && (codePoint <= 122)) || // [a-z]
                        ((codePoint >= 65) && (codePoint <= 90)) //[A-Z]
                ) {
                    sb.append(proposedSymbolicName.charAt(index));
                }
            } else {
                if (((codePoint >= 97) && (codePoint <= 122)) || // [a-z]
                        ((codePoint >= 65) && (codePoint <= 90)) || //[A-Z]
                        (codePoint == 95) || // _
                        ((codePoint >= 48) && (codePoint <= 57)) // [0-9]
                ) {
                    sb.append(proposedSymbolicName.charAt(index));
                }
            }
            index++;
        }
        return sb.toString();
    }

    @Override
    public String addNewFile(String symbolicName) throws DataUnitException {
        throw new DataUnitException("Not supported");
    }

    @Override
    public void addEntry(String symbolicName) throws DataUnitException {
        throw new DataUnitException("Not supported");
    }

}
