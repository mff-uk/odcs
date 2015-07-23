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
package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Test suite for directory name generation by {@link ExecutionContextInfo}.
 * 
 * @author Petyr
 */
public class ExecutionContextInfoTest {

    private Long execId = (long) 1;

    private PipelineExecution execution;

    private ExecutionContextInfo info;

    @Before
    public void init() {
        execution = mock(PipelineExecution.class);
        when(execution.getId()).thenReturn(execId);
        info = new ExecutionContextInfo(execution);
    }

    @Test
    public void getDPUTmpPathTest() {
        DPUInstanceRecord dpu = mock(DPUInstanceRecord.class);
        when(dpu.getId()).thenReturn((long) 11);
        when(dpu.getName()).thenReturn("Q 	CK");

        String expected = File.separator + "1" + File.separator +
                "working" + File.separator +
                "dpu_11" + File.separator +
                "tmp";
        //String value = info.getDPUTmpPath(dpu);

        // TODO Petr: presunout do velkeho test
    }

}
