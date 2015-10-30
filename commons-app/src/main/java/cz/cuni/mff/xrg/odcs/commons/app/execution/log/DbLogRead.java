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
package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import java.util.Date;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Logs are using special row as workaround for LIMIT, that can cause
 * full result scan. This require support from whole respective DAO layer.
 *
 * @author Petyr
 */
public interface DbLogRead extends DbAccessRead<Log> {

    /**
     * Delete all logs that are older then given date.
     *
     * @param date
     *            Date threshold.
     */
    void prune(Date date);

    /**
     * Retrieve the highest relative log index for given execution. If there are
     * no logs for given execution then return null.
     *
     * @param executionId
     *            Execution id.
     * @return Can be null.
     */
    Long getLastRelativeIndex(Long executionId);

    /**
     * Delete all logs that are older then given date.
     *
     * @param date
     *            Date threshold.
     */
    void delete(PipelineExecution execution);

}
