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
package cz.cuni.mff.xrg.odcs.commons.app.module;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;

/**
 * Can be used to add additional checks for loaded DPU. The validation class
 * will not be shared by multiple threads.
 * 
 * @author Petyr
 * @see DPUModuleManipulator
 */
public interface DPUValidator {

    /**
     * Check if {@link DPUTemplateRecord} and it's instance is functional and
     * can be used. If the DPU is invalid then should throw exception
     * with message for user.
     * 
     * @param dpu
     *            Data object that represent DPU to validate.
     * @param dpuInstance
     *            Instance of DPU to validate.
     * @throws DPUValidatorException
     */
    public void validate(DPUTemplateRecord dpu, Object dpuInstance)
            throws DPUValidatorException;

}
