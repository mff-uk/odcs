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
package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.properties.DbRuntimeProperties;
import cz.cuni.mff.xrg.odcs.commons.app.properties.RuntimeProperty;

/**
 * Facade for fetching persisted entities.
 *
 * @author mvi
 */
@Transactional(readOnly = true)
public class RuntimePropertiesFacadeImpl implements RuntimePropertiesFacade {

    @Autowired
    private DbRuntimeProperties runtimePropertiesDao;

    @Override
    public List<RuntimeProperty> getAllRuntimeProperties() {
        return runtimePropertiesDao.getAll();
    }

    @Override
    public RuntimeProperty getByName(String name) {
        return runtimePropertiesDao.getByName(name);
    }

    @PreAuthorize("hasRole('runtimeProperties.edit')")
    @Transactional
    @Override
    public void save(RuntimeProperty property) {
        runtimePropertiesDao.save(property);
    }

    @PreAuthorize("hasRole('runtimeProperties.edit')")
    @Transactional
    @Override
    public void delete(RuntimeProperty property) {
        runtimePropertiesDao.delete(property);
    }
}
