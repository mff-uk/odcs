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
package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import java.util.Date;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;

/**
 * Interface providing access to {@link Schedule} data objects.
 * 
 * @author Jan Vojt
 */
public interface DbSchedule extends DbAccess<Schedule> {

    /**
     * Returns list of all Plans currently persisted in database.
     * 
     * @return list of scheduled jobs
     * @deprecated may be slow for many schedules, use paging instead
     */
    @Deprecated
    public List<Schedule> getAllSchedules();

    /**
     * Fetches all {@link Schedule}s planned for given pipeline.
     * 
     * @param pipeline
     * @return all {@link Schedule}s planned for given pipeline.
     */
    public List<Schedule> getSchedulesFor(Pipeline pipeline);

    /**
     * Fetches all {@link Schedule}s that should be activated after given
     * pipeline execution.
     * 
     * @param pipeline
     *            pipeline to follow
     * @param enabled
     *            <ul>
     *            <li>if true return only followers with enabled schedules,</li>
     *            <li>if false return only followers with disabled schedules,</li>
     * @return schedules configured to follow given pipeline
     */
    public List<Schedule> getFollowers(Pipeline pipeline, boolean enabled);

    /**
     * Fetches all {@link Schedule}s which are activated in
     * certain time and the execution for the scheduled pipeline
     * isn't already queued or running.
     * 
     * @return list of schedules
     */
    public List<Schedule> getAllTimeBasedNotQueuedRunning();

    /**
     * Fetches active (enabled) {@link Schedule}s which are activated based on
     * pipelines executions.
     * 
     * @return list of schedules
     */
    public List<Schedule> getActiveRunAfterBased();

    /**
     * Return times of last executions (or null if there has been no successful
     * execution) of run-after pipelines for runAfter base schedule.
     * 
     * @param schedule
     * @return list of timestamps
     */
    public List<Date> getLastExecForRunAfter(Schedule schedule);

}
