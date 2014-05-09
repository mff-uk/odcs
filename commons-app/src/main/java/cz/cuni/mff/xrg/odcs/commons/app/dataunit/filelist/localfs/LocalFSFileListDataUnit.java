package cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.localfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Iterator;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.ManageableWritableFileListDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.RDFData;

public class LocalFSFileListDataUnit implements ManageableWritableFileListDataUnit {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFSFileListDataUnit.class);

    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;

    private String dataUnitName;

    private ManagableRdfDataUnit backingStore;

    private File workingDirectory;

    private String workingDirectoryCannonicalPath;

    private String workingDirectoryURI;

    private static String URI_PREFIX = "http://linked.opendata.cz/resource/odcs/internal/filelist/";

    private static String SYMBOLIC_NAME_LOCAL_NAME = "symbolicName";

    private static String FILE_EXISTS_ASK_QUERY = "PREFIX filelist: <" + URI_PREFIX + "> ASK { ?pathUri filelist:symbolicName \"%s\" }";

    public LocalFSFileListDataUnit(String globalWorkingDirectory, String dataUnitName) throws DataUnitCreateException {
        try {
            this.workingDirectory = Files.createTempDirectory(FileSystems.getDefault().getPath(globalWorkingDirectory), "").toFile();
            this.workingDirectoryCannonicalPath = workingDirectory.getCanonicalPath();
            this.workingDirectoryURI = workingDirectory.toURI().toASCIIString();
            this.backingStore = rdfDataUnitFactory.create(dataUnitName, workingDirectoryURI);
        } catch (IOException ex) {
            throw new DataUnitCreateException("Error creating data unit.", ex);
        }
    }

    //DataUnit interface
    @Override
    public DataUnitType getType() {
        return DataUnitType.FILE_LIST;
    }

    //DataUnit interface
    @Override
    public boolean isType(DataUnitType dataUnitType) {
        return this.getType().equals(dataUnitType);
    }

    //DataUnit interface
    @Override
    public String getDataUnitName() {
        return dataUnitName;
    }

    //DataUnit interface
    @Override
    public void addAll(DataUnit unit) {
        // TODO Auto-generated method stub
    }

    //FileListDataUnit interface
    @Override
    public RDFData getRDFData() {
        return backingStore;
    }

    //FileListDataUnit interface
    @Override
    public Iterator<FileListDataUnitEntry> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    //WritableFileListDataUnit interface
    @Override
    public String getBasePath() {
        return workingDirectoryCannonicalPath;
    }

    //WritableFileListDataUnit interface
    @Override
    public void addExistingFile(String proposedSymbolicName, String existingFileFullPath) {
        File existingFile = new File(existingFileFullPath);
        if (!existingFile.exists()) {
            throw new IllegalArgumentException("File does not exist: " + existingFileFullPath + ". File must exists prior being added.");
        }
        if (!existingFile.isFile()) {
            throw new IllegalArgumentException("Only files are permitted to be added. File " + existingFileFullPath + " is not a proper file.");
        }
        RepositoryConnection connection = null;
        try {
            connection = backingStore.getConnection();
            connection.begin();
            BooleanQuery fileExistsQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, String.format(FILE_EXISTS_ASK_QUERY, proposedSymbolicName));
            if (fileExistsQuery.evaluate()) {
                connection.rollback();
                throw new IllegalArgumentException("File with symbolic name "
                        + proposedSymbolicName + " already exists in scope of this data unit. Symbolic name must be unique.");
            }
            ValueFactory valueFactory = connection.getValueFactory();
            Statement statement = valueFactory.createStatement(
                    valueFactory.createURI(existingFile.toURI().toASCIIString()),
                    valueFactory.createURI(URI_PREFIX, SYMBOLIC_NAME_LOCAL_NAME),
                    valueFactory.createLiteral(proposedSymbolicName)
                    );
            connection.add(statement, backingStore.getDataGraph());
            connection.commit();
        } catch (RepositoryException ex) {
            // TODO michal.klempa
        } catch (QueryEvaluationException ex) {
            // TODO michal.klempa
        } catch (MalformedQueryException ex) {
            // TODO michal.klempa
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

    //WritableFileListDataUnit interface
    @Override
    public String createFilename() {
        // TODO Auto-generated method stub
        return null;
    }

    //WritableFileListDataUnit interface
    @Override
    public String createFilename(String proposedSymbolicName) {
        // TODO Auto-generated method stub
        return null;
    }

    //ManageableDataUnit interface
    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    //ManageableDataUnit interface
    @Override
    public void isReleaseReady() {
        // TODO Auto-generated method stub

    }

    //ManageableDataUnit interface
    @Override
    public void release() {
        // TODO Auto-generated method stub

    }

    //ManageableDataUnit interface
    @Override
    public void merge(DataUnit unit) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }
}
