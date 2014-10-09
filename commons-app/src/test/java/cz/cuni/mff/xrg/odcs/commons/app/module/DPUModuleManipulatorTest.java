package cz.cuni.mff.xrg.odcs.commons.app.module;

import static org.junit.Assert.*;

import org.junit.Test;

public class DPUModuleManipulatorTest {

    @Test
    public void testGetDirectoryName() throws DPUCreateException {
        String[] allowedFormat = new String[] {
                "name-1.0.jar",
                "name-1.0.0.jar",
                "name-1.0.0-SNAPSHOT.jar",
                "name-1.0.0-xxx.jar",
                "name-1.0.0xxx.jar"
        };
        
        for (String jarFileName : allowedFormat) {
            assertNotNull(DPUModuleManipulator.getDirectoryName(jarFileName));
        }
        
        // (.+)+-(\\d(.\\d+)*)(-.+)*\\.jar
        assertEquals("name", DPUModuleManipulator.getDirectoryName("name-1.0.1.jar"));
        assertEquals("name", DPUModuleManipulator.getDirectoryName("name-1.0.1-SNAPSHOT.jar"));
        
        try {
            assertEquals("name", DPUModuleManipulator.getDirectoryName("name.jar"));
            fail("Should throw DPUCreateException because of wrong format.");
        } catch (DPUCreateException e) {
            // ok
        }
        
        try {
            assertEquals("name", DPUModuleManipulator.getDirectoryName("name-1.jar"));
            fail("Should throw DPUCreateException because of wrong format.");
        } catch (DPUCreateException e) {
            // ok
        }
        
        assertEquals("name", DPUModuleManipulator.getDirectoryName("name-1.0.jar"));
        assertEquals("name", DPUModuleManipulator.getDirectoryName("name-1.0-SNAPSHOT.jar"));
        
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0.1-SNAPSHOT.jar"));
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0-SNAPSHOT.jar"));
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0.1.jar"));
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0.jar"));
        
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0.0-RELEASE.jar"));
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0.0-xxxx.jar"));
        assertEquals("uv-x-name", DPUModuleManipulator.getDirectoryName("uv-x-name-1.0.0xxx.jar"));
        // (-SNAPSHOT)?
    }
}
