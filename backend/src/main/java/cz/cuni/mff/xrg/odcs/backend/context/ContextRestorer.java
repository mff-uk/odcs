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
package cz.cuni.mff.xrg.odcs.backend.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;

/**
 * Reconstruct given {@link Context} based on {@link ExecutionContextInfo} and prepare it for usage.
 * 
 * @author Petyr
 */
class ContextRestorer {

    private static final Logger LOG = LoggerFactory.getLogger(ContextRestorer.class);

    /**
     * Restore data of given context. If there is some data already
     * loaded then does not load them again otherwise nothing happen.
     * 
     * @param context
     */
    public void restore(Context context) throws DataUnitException {
        // we can assume that file exist .. as HDD is persistent
        // so only DataUnits leave to load
        LOG.trace("Context.restore called ...");
        context.getInputsManager().reload();
        context.getOutputsManager().reload();
    }

}
