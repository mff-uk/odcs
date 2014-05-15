package cz.cuni.mff.xrg.odcs.commons.app.dataunit.filelist.localfs;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.filelist.WritableFileListDataUnit;

@ContextConfiguration(locations = { "classpath:commons-app-test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class LocalFSFileListDataUnitTestDoesntPassTest {

    private Map<String, String> addSomeFiles(WritableFileListDataUnit unit) throws IOException, DataUnitException {
        Map<String, String> result = new LinkedHashMap<>();
        File f1 = new File(unit.getBasePath() + '/' + "myCrazyName");
        PrintWriter fw1 = new PrintWriter(new FileWriter(f1));
        fw1.print("teststring");
        fw1.close();
        
        result.put("1", f1.getCanonicalPath());
        
        unit.addExistingFile("1", f1.getCanonicalPath());
        
        return result;
    }
    
    @Test
    public void testGetRDFData() throws IOException, RepositoryException, DataUnitException {
        String workingDir =Files.createTempDirectory("").toString();
        LocalFSFileListDataUnit unit = new LocalFSFileListDataUnit(workingDir, "fdsfdsfds");
        assertTrue(unit.getBasePath().startsWith(workingDir));
        assertEquals("fdsfdsfds", unit.getDataUnitName());
        assertNotNull(unit.getRDFData());
        assertTrue(unit.getRDFData().getContexts().size() == 1);
        assertNotNull(unit.getRDFData().getConnection());
        
        Map<String, String> addedFiles = addSomeFiles(unit);
        
        assertTrue(unit.getRDFData().getContexts().size() == 1);
        assertTrue(unit.getRDFData().getConnection().size(unit.getRDFData().getContexts().toArray(new URI[0])) == addedFiles.size());
        RepositoryConnection con = unit.getRDFData().getConnection();
        ValueFactory vF = con.getValueFactory();
        for (String symb: addedFiles.keySet()) {
            RepositoryResult<Statement> it = con.getStatements(vF.createURI("file:///" + addedFiles.get(symb)), null, null, false, unit.getRDFData().getContexts().toArray(new URI[0]));
            assertTrue(it.hasNext());
            Statement st = it.next();
            assertTrue(st.getObject().stringValue().equals(symb));
        }
    }
    
    @Test
    public void duplicateSymbolicNameTest() {
        
    }

    @Test
    public void testGetFileList() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBasePath() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddExistingFile() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateFilename() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateFilenameString() {
        fail("Not yet implemented");
    }

    @Test
    public void testClear() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsReleaseReady() {
        fail("Not yet implemented");
    }

    @Test
    public void testRelease() {
        fail("Not yet implemented");
    }

    @Test
    public void testMerge() {
        fail("Not yet implemented");
    }

}
