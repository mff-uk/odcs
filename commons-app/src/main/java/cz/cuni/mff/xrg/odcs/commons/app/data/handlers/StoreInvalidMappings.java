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
package cz.cuni.mff.xrg.odcs.commons.app.data.handlers;

import java.util.LinkedList;
import java.util.List;

/**
 * Error handler for {@link cz.cuni.mff.xrg.odcs.commons.app.data.EdgeCompiler},
 * that store all the invalid mappings.
 * 
 * @author Petyr
 */
public class StoreInvalidMappings extends LogAndIgnore {

    private final List<String> invalidMapping = new LinkedList<>();

    @Override
    public void invalidMapping(String item) {
        super.invalidMapping(item);
        // add to the list
        invalidMapping.add(item);
    }

    /**
     * @return List of examined invalid mappings.
     */
    public List<String> getInvalidMapping() {
        return invalidMapping;
    }

}
