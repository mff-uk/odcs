/*******************************************************************************
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
 *******************************************************************************/
/*******************************************************************************
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
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;

/**
 * Test suite for {@link DPUInstanceRecord} class.
 * 
 * @author Jan Vojt
 */
public class DPUInstanceRecordTest {

    /**
     * Tested instance.
     */
    private DPUInstanceRecord instance;

    @Before
    public void setUp() throws ModuleException {
        instance = new DPUInstanceRecord();
    }

    @Test
    public void testCopy() throws ModuleException {
        // initialize contained objects
        DPUTemplateRecord dpu = new DPUTemplateRecord();
        String rawConfig = "<xml><a>value</a></xml";

        instance.setName("testname");
        instance.setDescription("testdescription");
        instance.setRawConf(rawConfig);
        instance.setTemplate(dpu);

        DPUInstanceRecord copy = new DPUInstanceRecord(instance);

        assertNotSame(instance, copy);
        assertNotNull(copy);
        assertEquals(instance.getName(), copy.getName());
        assertEquals(instance.getDescription(), copy.getDescription());
        assertEquals(instance.getJarPath(), copy.getJarPath());
        assertEquals(instance.getRawConf(), copy.getRawConf());

        // DPU template is never copied!!
        assertSame(instance.getTemplate(), copy.getTemplate());
    }
}
