package cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.localfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.files.ManageableWritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.metadata.AbstractWritableMetadataDataUnit;
import eu.unifiedviews.commons.rdf.ConnectionSource;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

public class LocalFSFilesDataUnit extends AbstractWritableMetadataDataUnit implements ManageableWritableFilesDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFSFilesDataUnit.class);

    private static final int PROPOSED_FILENAME_PART_MAX_LENGTH = 10;

    private final File workingDirectory;

    private final String workingDirectoryURI;

    /**
     * RDF data unit with metadata. This is only use to get a connection to the rdf storage!
     *
     * TODO Petr Škoda: remove and replace with some connection source.
     */
    protected RDFDataUnit backingDataUnit;

    public LocalFSFilesDataUnit(String dataUnitName, String workingDirectoryURIString,
            RDFDataUnit backingDataUnit, String dataGraph) throws DataUnitException {
        super(dataUnitName, dataGraph);
        this.workingDirectoryURI = workingDirectoryURIString;
        this.workingDirectory = new File(java.net.URI.create(workingDirectoryURI));
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
        checkForMultithreadAccess();
        return new SetFileIteration(this);
    }

    //WritableFilesDataUnit interface
    @Override
    public String getBaseFileURIString() {
        return workingDirectoryURI;
    }

    //WritableFilesDataUnit interface
    @Override
    public void addExistingFile(final String symbolicName, final String existingFileURI) throws DataUnitException {
        checkForMultithreadAccess();
        final File existingFile = new File(java.net.URI.create(existingFileURI));
        if (!existingFile.exists()) {
            throw new DataUnitException(
                    "File does not exist: " + existingFileURI + ". File must exists prior being added.");
        }
        if (!existingFile.isFile()) {
            throw new DataUnitException(
                    "Only files are permitted to be added. File " + existingFileURI + " is not a proper file.");
        }
        ConnectionSource cs = null;
        RepositoryConnection connection = null;
        try {
            // TODO michal.klempa think of not connecting everytime
            connection = getConnectionInternal();
            connection.begin();
            final ValueFactory valueFactory = connection.getValueFactory();
            final URI subject = addEntry(symbolicName, connection);
            // Add file uri.
            connection.add(
                    subject,
                    valueFactory.createURI(FilesDataUnit.PREDICATE_FILE_URI),
                    valueFactory.createLiteral(existingFile.toURI().toASCIIString()),
                    getMetadataWriteGraphname()
            );
            connection.commit();
        } catch (RepositoryException ex) {
            throw new DataUnitException("Error when adding file.", ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                }
            }
        }
        // --------------------------------------------------------------------------------------------------
    }

    @Override
    public String addNewFile(String symbolicName) throws DataUnitException {
        // Create a new file.
        Path newFile = null;
        String filteredProposedSymbolicName = filterProposedSymbolicName(symbolicName);
        try {
            newFile = Files.createTempFile(workingDirectory.toPath(), filteredProposedSymbolicName, "");
        } catch (IOException ex) {
            throw new DataUnitException("Error when generating filename.", ex);
        }
        // And now add it as existing file.
        final String newFileURIString = newFile.toUri().toASCIIString();
        addExistingFile(symbolicName, newFileURIString);
        return newFileURIString;
    }

    //ManageableDataUnit interface
    @Override
    public void clear() throws DataUnitException {
        // We also delte data under
        FileUtils.deleteQuietly(new File(java.net.URI.create(this.workingDirectoryURI)));
        // Then rdf.
        super.clear();
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

    /**
     *
     * @param proposedSymbolicName
     * @return Suitable (save) symbolic name.
     */
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

}
