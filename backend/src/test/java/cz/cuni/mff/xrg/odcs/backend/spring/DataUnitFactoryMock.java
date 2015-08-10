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
package cz.cuni.mff.xrg.odcs.backend.spring;

import static org.mockito.Mockito.mock;

import java.io.File;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.DataUnitFactory;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;

/**
 * Dummy {@link DataUnitFactory}. Does not create any real data unit.
 * 
 * @author Petyr
 */
public class DataUnitFactoryMock implements DataUnitFactory {

    @Override
    public ManagableDataUnit create(ManagableDataUnit.Type type,
            Long executionId,
            String dataUnitUri,
            String dataUnitName,
            File dataUnitDirectory) {
        // just return mocked object
        return mock(ManagableDataUnit.class);
    }

}
