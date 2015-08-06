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

import java.util.List;
import java.util.Locale;

import cz.cuni.mff.xrg.odcs.commons.app.properties.RuntimeProperty;

/**
 * Facade for fetching persisted entities.
 *
 * @author mvi
 *
 */
public interface RuntimePropertiesFacade extends Facade {

    /**
     * Returns List of all runtime properties currently persisted in database
     *
     * @return List of all runtime properties
     */
    public List<RuntimeProperty> getAllRuntimeProperties();

    /**
     * Saves any modifications made to the property into the database.
     *
     * @param property
     */
    public void save(RuntimeProperty property);

    /**
     * Deletes property from database
     *
     * @param property
     */
    public void delete(RuntimeProperty property);

    /**
     * Returns property with selected name
     *
     * @param name
     * @return
     */
    public RuntimeProperty getByName(String name);
}
