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

/**
 * Base exception used by {@link cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade} and related classes.
 * 
 * @author Petyr
 */
public class ModuleException extends Exception {

    /**
     * @param cause
     *            Cause of this exception.
     */
    public ModuleException(Throwable cause) {
        super(cause);
    }

    /**
     * @param cause
     *            Cause of this exception.
     */
    public ModuleException(String cause) {
        super(cause);
    }

    /**
     * @param message
     *            Description of action that throws.
     * @param cause
     *            Cause of this exception.
     */
    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

}
