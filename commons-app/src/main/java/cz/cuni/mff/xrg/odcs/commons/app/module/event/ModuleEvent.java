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
package cz.cuni.mff.xrg.odcs.commons.app.module.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for modules related events.
 * 
 * @author Petyr
 */
public abstract class ModuleEvent extends ApplicationEvent {

    /**
     * DPU's relative directory name.
     */
    private final String directoryName;

    /**
     * @param source
     *            Event source.
     * @param directoryName
     *            DPU's directory name.
     */
    public ModuleEvent(Object source, String directoryName) {
        super(source);
        this.directoryName = directoryName;
    }

    /**
     * @return DPU's directory name.
     */
    public String getDirectoryName() {
        return directoryName;
    }

}
