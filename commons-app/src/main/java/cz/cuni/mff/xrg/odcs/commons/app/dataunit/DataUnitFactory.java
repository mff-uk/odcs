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
package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import java.io.File;

import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import eu.unifiedviews.commons.rdf.repository.RDFException;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Can be used to build data units.
 *
 * @author Škoda Petr
 */
public interface DataUnitFactory {

    /**
     *
     * @param type Type of data unit to create.
     * @param executionId Unique execution id.
     * @param dataUnitUri URI of given DataUnit.
     * @param dataUnitName Name of dataUnit (given by DPU's DataUnit annotation).
     * @param dataUnitDirectory DataUnit's working directory.
     * @return
     * @throws RDFException
     * @throws DataUnitException
     */
    ManagableDataUnit create(ManagableDataUnit.Type type, Long executionId, String dataUnitUri, String dataUnitName, File dataUnitDirectory) throws RDFException, DataUnitException;

}
