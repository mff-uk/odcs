package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata.AbstractWritableMetadataDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

public class LocalFSFilesDataUnit extends AbstractWritableMetadataDataUnit implements ManageableWritableFilesDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFSFilesDataUnit.class);

    private String workingDirectoryURI;

    private File workingDirectory;

    protected RDFDataUnit backingDataUnit;

    private static int PROPOSED_FILENAME_PART_MAX_LENGTH = 10;

    // This is not nice, but .. 
    private static AtomicInteger fileIndexCounter = new AtomicInteger(0);

    public LocalFSFilesDataUnit(String dataUnitName, String workingDirectoryURIString, RDFDataUnit backingDataUnit, String dataGraph) throws DataUnitException {
        super(dataUnitName, dataGraph);
        this.workingDirectoryURI = workingDirectoryURIString;
        this.workingDirectory = new File(URI.create(workingDirectoryURI));
        this.backingDataUnit = backingDataUnit;
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

    //FilesDataUnit interface
    @Override
    public FilesDataUnit.Iteration getIteration() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit");
        }

        return new SetFileIteration(this);
    }

    //WritableFilesDataUnit interface
    @Override
    public String getBaseFileURIString() {
        return workingDirectoryURI;
    }

    //WritableFilesDataUnit interface
    @Override
    public void addExistingFile(String proposedSymbolicName, String existingFileURI) throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            LOG.info("More than more thread is accessing this data unit");
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

        LOG.info("addExistingFile({}, {}) -> {}", proposedSymbolicName, existingFileURI, getMetadataWriteGraphname().stringValue());

        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            ValueFactory valueFactory = connection.getValueFactory();
            // We must not use blacnk node here, or we should use sparql INSERT!
            //BNode blankNodeId = valueFactory.createBNode();
            Resource blankNodeId = valueFactory.createURI("http://unifiedviews.eu/resource/dataunit/file/" + Integer.toString(fileIndexCounter.incrementAndGet()));

            Statement statement = valueFactory.createStatement(
                    blankNodeId,
                    valueFactory.createURI(MetadataDataUnit.PREDICATE_SYMBOLIC_NAME),
                    valueFactory.createLiteral(proposedSymbolicName)
                    );
            Statement statement2 = valueFactory.createStatement(
                    blankNodeId,
                    valueFactory.createURI(FilesDataUnit.PREDICATE_FILE_URI),
                    valueFactory.createLiteral(existingFile.toURI().toASCIIString())
                    );
            connection.add(statement, getMetadataWriteGraphname());
            connection.add(statement2, getMetadataWriteGraphname());
            connection.commit();
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

    @Override
    public String addNewFile(String symbolicName) throws DataUnitException {
        Path newFile = null;
        String filteredProposedSymbolicName = filterProposedSymbolicName(symbolicName);
        try {
            newFile = Files.createTempFile(workingDirectory.toPath(), filteredProposedSymbolicName, "");
        } catch (IOException ex) {
            throw new DataUnitException("Error when generating filename.", ex);
        }
        String newFileURIString = newFile.toUri().toASCIIString();
        addExistingFile(symbolicName, newFileURIString);
        return newFileURIString;
    }

    //ManageableDataUnit interface
    @Override
    public void clear() {
        FileUtils.deleteQuietly(new File(URI.create(this.workingDirectoryURI)));
        super.clear();
    }

    private String filterProposedSymbolicName(String proposedSymbolicName) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (sb.length() < PROPOSED_FILENAME_PART_MAX_LENGTH && index < proposedSymbolicName.length()) {
            int codePoint = proposedSymbolicName.codePointAt(index);
            if (sb.length() == 0) {
                if (codePoint >= 97 && codePoint <= 122 || // [a-z]
                        codePoint >= 65 && codePoint <= 90 //[A-Z]
                ) {
                    sb.append(proposedSymbolicName.charAt(index));
                }
            } else {
                if (codePoint >= 97 && codePoint <= 122 || // [a-z]
                        codePoint >= 65 && codePoint <= 90 || //[A-Z]
                        codePoint == 95 || // _
                        codePoint >= 48 && codePoint <= 57 // [0-9]
                ) {
                    sb.append(proposedSymbolicName.charAt(index));
                }
            }
            index++;
        }
        return sb.toString();
    }

    @Override
    public RepositoryConnection getConnectionInternal() throws RepositoryException {
        try {
            RepositoryConnection connection = backingDataUnit.getConnection();
            return connection;
        } catch (DataUnitException ex) {
            throw (RepositoryException) ex.getCause();
        }
    }
}
