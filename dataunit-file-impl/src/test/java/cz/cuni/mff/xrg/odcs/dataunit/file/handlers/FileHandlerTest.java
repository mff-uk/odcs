package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test suite for {@link FileHandler}.
 * 
 * @author Petyr
 */
public class FileHandlerTest {

    protected File fileToUse;

    protected File toDelete;

    @Before
    public void init() {
        this.toDelete = new File(FileUtils.getTempDirectory(), "odcs-file-test");
        this.fileToUse = new File(this.toDelete,
                Long.toString((new Date()).getTime()));
    }

    @After
    public void clean() throws IOException {
        if (toDelete.exists()) {
            FileUtils.deleteDirectory(toDelete);
        }
    }

    @Test
    public void testCreate() {
        assertFalse(fileToUse.exists());
        FileHandler handler = new FileHandlerImpl(fileToUse, null, fileToUse.getName(), false);
        assertTrue(fileToUse.exists());
    }

    @Test
    public void testCreateLink() {
        assertFalse(fileToUse.exists());
        FileHandler handler = new FileHandlerImpl(fileToUse, null, fileToUse.getName(), true);
        assertFalse(fileToUse.exists());
    }

    @Test
    public void testContent() throws IOException, FileDataUnitException {
        FileHandler handler = new FileHandlerImpl(fileToUse, null, fileToUse.getName(), false);

        final String content = "my new test content ...";
        handler.setContent(content);
        assertEquals(content, handler.getContent());

        // now lets create a new, with as link .. 
        FileHandler link = new FileHandlerImpl(fileToUse, null, fileToUse.getName(), true);
        assertEquals(content, link.getContent());
    }

    @Test
    public void testUserData() {
        FileHandler handler = new FileHandlerImpl(fileToUse, null, fileToUse.getName(), true);
        final String userDate = "my new test content ...";
        handler.setUserData(userDate);
        assertEquals(userDate, handler.getUserData());
    }

}
