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
package cz.cuni.mff.xrg.odcs.commons.app.execution.server;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

public interface DbExecutionServer extends DbAccess<ExecutionServer> {

    /**
     * Get backend execution server entry via its ID
     * 
     * @param backendId
     * @return Backend execution server
     */
    ExecutionServer getExecutionServer(String backendId);

    /**
     * Get list of all executions backend servers registered in database
     * 
     * @return List of all backend servers
     */
    List<ExecutionServer> getAllExecutionServers();

    /**
     * Allocates QUEUED pipeline executions in database to backend execution server with given ID
     * This method is transactional and guarantees that each pipeline execution is taken only once only by one backend
     * Once allocated execution is further processed only by the allocating backend
     * Other backends will never touch it
     * 
     * @param backendID
     *            Backend ID to allocate executions to
     * @param limit
     *            Limit of executions to be allocated
     * @return Number of allocated executions for backend
     */
    int allocateQueuedExecutionsForBackendByPriority(String backendID, int limit);

    /**
     * Get count of unallocated QUEUED executions with IGNORE priority
     * 
     * @return Count of unallocated QUEUED executions with IGNORE priority
     */
    long getCountOfUnallocatedQueuedExecutionsWithIgnorePriority();

}
