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
package cz.cuni.mff.xrg.odcs.backend.logback;

import java.util.HashMap;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogRead;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;

/**
 * Class used to manage the values of last {@link Log#relativeId}.
 * 
 * @author Škoda Petr <skodapetr@gmail.com>
 */
public class RelativeIdHolder {

    /**
     * Holder for relative IDs.
     */
    class IdHolder {

        public Long id;

        public IdHolder(Long id) {
            if (id == null) {
                // new execution
                this.id = 1l;
            } else {
                // continue with existing
                this.id = id;
            }
        }

    }

    private final DbLogRead logRead;

    private final HashMap<Long, IdHolder> relativeIds = new HashMap<>();

    public RelativeIdHolder(DbLogRead logRead) {
        this.logRead = logRead;
    }

    /**
     * This method is not thread save!
     * 
     * @param execution
     * @return Relative id for next log for given execution.
     */
    public Long getNextId(Long execution) {
        IdHolder holder = relativeIds.get(execution);
        if (holder == null) {
            // check for size
            if (relativeIds.size() > 100) {
                // delete all
                relativeIds.clear();
            }
            // create new
            holder = new IdHolder(logRead.getLastRelativeIndex(execution));
            relativeIds.put(execution, holder);
        }
        return holder.id++;
    }

    /**
     * Delete all the counters from application cache. This will result in query
     * into database for every call of {@link #getNextId(java.lang.Long)} for
     * certain execution id.
     */
    public void resetIdCounters() {
        relativeIds.clear();
    }

}
