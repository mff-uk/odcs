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
package cz.cuni.mff.xrg.odcs.commons.app.data.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.data.EdgeCompiler;

/**
 * Default error handler for {@link EdgeCompiler}.
 * 
 * @author Petyr
 */
public class LogAndIgnore implements EdgeCompiler.ErrorHandler {

    private final static Logger LOG = LoggerFactory.getLogger(LogAndIgnore.class);

    @Override
    public void sourceIndexOutOfRange() {
        LOG.warn("Source index out of range, mapping has been ignored");
    }

    @Override
    public void targetIndexOutOfRange() {
        LOG.warn("Target index out of range, mapping has been ignored");
    }

    @Override
    public void unknownCommand(String item) {
        LOG.warn("Unknown command: {}", item);
    }

    @Override
    public void invalidMapping(String item) {
        LOG.warn("Invalid mapping: {}", item);
    }

}
