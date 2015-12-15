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
package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import java.util.EnumSet;

/**
 * Set of possible states during pipeline execution.
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
public enum PipelineExecutionStatus {

    /**
     * Pipeline is scheduled for run and will run as soon as possible.
     */
    QUEUED,
    /**
     * Pipeline is recently running.
     */
    RUNNING,
    /**
     * Pipeline is being cancelled on user request.
     */
    CANCELLING,
    /**
     * Pipeline execution end because user cancel it.
     */
    CANCELLED,
    /**
     * Pipeline execution failed.
     */
    FAILED,
    /**
     * Pipeline execution has been successful and there were no WARN+ messages
     * or logs.
     */
    FINISHED_SUCCESS,
    /**
     * Pipeline execution has been successful but there ase some WARN+ record.
     */
    FINISHED_WARNING;

    /**
     * Set of execution statuses hinting that execution was completed, or
     * failed. Thus execution canceled by user request are not included. All
     * executions with this status have a valid duration.
     */
    public static final EnumSet<PipelineExecutionStatus> FINISHED = EnumSet.of(
            FINISHED_SUCCESS,
            FINISHED_WARNING,
            FAILED
            );
}
