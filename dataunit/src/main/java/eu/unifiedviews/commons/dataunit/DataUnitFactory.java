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
package eu.unifiedviews.commons.dataunit;

import eu.unifiedviews.commons.dataunit.core.CoreServiceBus;

/**
 * Interface for {@link ManagableDataUnit}.
 *
 * @author Škoda Petr
 */
public interface DataUnitFactory {

    /**
     *
     * @param name
     * @param uri Uri of data unit.
     * @param directoryUri Working directory for data unit.
     * @param coreServices
     * @return Newly create data unit. (not loaded).
     */
    ManagableDataUnit create(String name, String uri, String directoryUri, CoreServiceBus coreServices);

}
