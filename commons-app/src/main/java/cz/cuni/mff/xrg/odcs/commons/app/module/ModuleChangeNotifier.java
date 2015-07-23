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
package cz.cuni.mff.xrg.odcs.commons.app.module;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;

/**
 * Interface for service that enable publishing of notification about DPU
 * updates and changes.
 * 
 * @author Petyr
 */
public interface ModuleChangeNotifier {

    /**
     * Mark given DPU as updated. So the given {@link DPUTemplateRecord} should contains new jar-file name.
     * 
     * @param dpu
     *            DPU that has been updated.
     */
    public void updated(DPUTemplateRecord dpu);

    /**
     * Mark given DPU as new. This says that the {@link DPUTemplateRecord} is
     * new and it should be loaded from database into application.
     * 
     * @param dpu
     *            DPU that has been newly added.
     */
    public void created(DPUTemplateRecord dpu);

    /**
     * Notify listeners that given DPU should be unloaded from system.
     * 
     * @param dpu
     *            DPU that has been deleted.
     */
    public void deleted(DPUTemplateRecord dpu);

}
