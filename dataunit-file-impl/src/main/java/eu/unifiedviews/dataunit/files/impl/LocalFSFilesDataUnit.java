package eu.unifiedviews.dataunit.files.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.commons.dataunit.AbstractWritableMetadataDataUnit;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.commons.dataunit.core.CoreServiceBus;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;

/**
 * Implementation of {@link ManageableWritableFilesDataUnit} on local repository with utilisation of
 * RDF storage for metadata.
 *
 * @author Škoda Petr
 */
class LocalFSFilesDataUnit extends AbstractWritableMetadataDataUnit implements ManageableWritableFilesDataUnit {

    /**
     * How many characters from a symbolic name will be used in newly added file name.
     */
    private static final int MAX_USER_FILENAME_LENGTH = 10;

    private static final Logger LOG = LoggerFactory.getLogger(LocalFSFilesDataUnit.class);

    private final File workingDirectory;

    private final String workingDirectoryURI;

    public LocalFSFilesDataUnit(String dataUnitName, String workingDirectoryURI,
            String writeContextString, CoreServiceBus coreServices) {
        super(dataUnitName, writeContextString, coreServices);

        this.workingDirectory = new File(java.net.URI.create(workingDirectoryURI));
        workingDirectory.mkdirs();
        this.workingDirectoryURI = workingDirectoryURI;
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
        if (connectionSource.isRetryOnFailure()) {
            // Is not safe.
            return new WritableFileIterationEager(this, connectionSource, faultTolerant);
        } else {
            return new WritableFileIterationLazy(this);
        }
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
            throw new DataUnitException("File does not exist: " + existingFileURI + ". File must exists prior being added.");
        }
        if (!existingFile.isFile()) {
            throw new DataUnitException("Only files are permitted to be added. File " + existingFileURI + " is not a proper file.");
        }
        // Create subject and insert data.
        final URI entrySubject = this.creatEntitySubject();
        try {
            faultTolerant.execute((connection) -> {
                addEntry(entrySubject, symbolicName, connection);
                final ValueFactory valueFactory = connection.getValueFactory();
                // Add file uri.
                connection.add(
                        entrySubject,
                        valueFactory.createURI(FilesDataUnit.PREDICATE_FILE_URI),
                        valueFactory.createLiteral(existingFile.toURI().toASCIIString()),
                        getMetadataWriteGraphname()
                );
            });
        } catch (RepositoryException ex) {
            throw new DataUnitException("Problem with repositry.", ex);
        }
    }

    @Override
    public String addNewFile(String symbolicName) throws DataUnitException {
        // Sure that parent directory exists, parent directory could be deleted by clear method.
        workingDirectory.mkdirs();
        // Create a new file, limit used size of the symbolicName.
        final String encodedFileName = URLEncoder.encode(symbolicName).substring(0, symbolicName.length() > MAX_USER_FILENAME_LENGTH ? MAX_USER_FILENAME_LENGTH : symbolicName.length());
        final Path newFile;
        try {
            newFile = Files.createTempFile(workingDirectory.toPath(), encodedFileName, "");
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
        if (!FileUtils.deleteQuietly(new File(java.net.URI.create(this.workingDirectoryURI)))) {
            LOG.warn("Can't delete storage directory");
        }
        // Then rdf.
        super.clear();
    }

}
