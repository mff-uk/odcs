/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
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
