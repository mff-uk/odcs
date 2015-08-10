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
package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.ArrayList;

import ch.qos.logback.classic.Level;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 * 
 * @author Jan Vojt
 */
public interface LogFacade extends Facade {

    /**
     * Return true if there exist logs with given level for given DPU instance
     * of given pipeline execution.
     * 
     * @param exec
     * @param level
     * @return true if logs exist, false otherwise
     */
    boolean existLogsGreaterOrEqual(PipelineExecution exec, Level level);

    /**
     * Return list of all usable log's levels without aggregations. Ordered
     * descending by priority.
     * 
     * @return list of all log levels
     */
    ArrayList<Level> getAllLevels();

}
