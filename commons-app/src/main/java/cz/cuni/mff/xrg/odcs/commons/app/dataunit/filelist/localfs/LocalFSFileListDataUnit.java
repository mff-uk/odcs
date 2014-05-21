package cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.localfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.ontology.OdcsTerms;
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

    private Set<String> generatedFilenames = java.util.Collections.<String> synchronizedSet(new HashSet<String>());

    private Thread ownerThread;

    private static String FILE_EXISTS_ASK_QUERY = "ASK { ?pathUri <" + OdcsTerms.DATA_UNIT_FILELIST_SYMBOLIC_NAME_PREDICATE + ">\"%s\" }";

    private static int PROPOSED_FILENAME_PART_MAX_LENGTH = 10;

    public LocalFSFileListDataUnit(String globalWorkingDirectory, String dataUnitName) throws DataUnitCreateException {
        try {
            this.dataUnitName = dataUnitName;
            this.workingDirectory = Files.createTempDirectory(FileSystems.getDefault().getPath(globalWorkingDirectory), "").toFile();
            this.workingDirectoryCannonicalPath = workingDirectory.getCanonicalPath();
            this.workingDirectoryURI = workingDirectory.toURI().toASCIIString();
            this.backingStore = rdfDataUnitFactory.create(dataUnitName, workingDirectoryURI);
            this.ownerThread = Thread.currentThread();
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

    //FileListDataUnit interface
    @Override
    public RDFData getRDFData() {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return backingStore;
    }

    //FileListDataUnit interface
    @Override
    public FileListIteration getFileList() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return new FileListIterationImpl(backingStore, OdcsTerms.DATA_UNIT_FILELIST_SYMBOLIC_NAME_PREDICATE);
    }

    //WritableFileListDataUnit interface
    @Override
    public String getBasePath() {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return workingDirectoryCannonicalPath;
    }

    //WritableFileListDataUnit interface
    @Override
    public void addExistingFile(String proposedSymbolicName, String existingFileFullPath) throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

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
                    valueFactory.createURI(OdcsTerms.DATA_UNIT_FILELIST_SYMBOLIC_NAME_PREDICATE),
                    valueFactory.createLiteral(proposedSymbolicName)
                    );
            connection.add(statement, backingStore.getWriteContext());
            connection.commit();
            generatedFilenames.remove(existingFileFullPath);
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException ex) {
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

    //WritableFileListDataUnit interface
    @Override
    public String createFile() throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        return this.createFile("");
    }

    //WritableFileListDataUnit interface
    @Override
    public String createFile(String proposedSymbolicName) throws DataUnitException {
        if (!ownerThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Constraint violation, only one thread can access this data unit");
        }

        Path newFile = null;
        String filteredProposedSymbolicName = filterProposedSymbolicName(proposedSymbolicName);
        try {
            newFile = Files.createTempFile(workingDirectory.toPath(), filteredProposedSymbolicName, "");
            generatedFilenames.add(newFile.toString());
        } catch (IOException ex) {
            throw new DataUnitException("Error when generating filename.", ex);
        }
        return newFile.toString();
    }

    //ManageableDataUnit interface
    @Override
    public void clear() {
        ManagableRdfDataUnit tempDataUnit = rdfDataUnitFactory.create(dataUnitName, workingDirectoryURI);
        FileListIteration iteration = new FileListIterationImpl(tempDataUnit, OdcsTerms.DATA_UNIT_FILELIST_SYMBOLIC_NAME_PREDICATE);
        try {
            while (iteration.hasNext()) {
                FileListDataUnitEntry entry = iteration.next();
                Files.delete(Paths.get(entry.getFilesystemURI()));
            }
        } catch (DataUnitException ex) {
            LOG.error("Error in clear", ex);
        } catch (IOException ex) {
            LOG.error("Error in clear", ex);
        } finally {
            try {
                iteration.close();
                tempDataUnit.release();
            } catch (DataUnitException ex) {
                LOG.warn("Error in close", ex);
            }
        }
        backingStore.clear();
    }

    //ManageableDataUnit interface
    @Override
    public void isReleaseReady() {
        if (generatedFilenames.size() > 0) {
            LOG.error("{} file names have been generated but never added as existing files after DPU execution. dataUnitName '{}'.", generatedFilenames.size(), this.getDataUnitName());
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
    public void merge(DataUnit otherDataUnit) throws IllegalArgumentException {
        if (!this.getClass().equals(otherDataUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible DataUnit class. This DataUnit is of class "
                    + this.getClass().getCanonicalName() + " and it cannot merge other DataUnit of class " + otherDataUnit.getClass().getCanonicalName() + ".");
        }

        final LocalFSFileListDataUnit otherFileListDataUnit = (LocalFSFileListDataUnit) otherDataUnit;
        otherFileListDataUnit.getRDFData(); // Just a marker line to drawn attention here when someone search for usages of getter method
        backingStore.merge(otherFileListDataUnit.backingStore);
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
}
