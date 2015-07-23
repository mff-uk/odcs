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
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.xstream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author mvi
 *
 */
public class AllowedFieldsFilter implements MemberFilter {
	
	private final Map<Class, Set<String>> allowedFieldList = new HashMap<Class, Set<String>>();

	@Override
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		if (!allowedFieldList.keySet().contains(definedIn)) {
			return true;
		} else if (allowedFieldList.get(definedIn).contains(fieldName)) {
			return true;
		}
		return false;
	}

	/**
     * Add given value into the name allowed list.
     * 
     * @param classToFiler
     * @param fieldName
     */
    public void add(Class classToFiler, String fieldName) {
    	Set<String> classAllowedFields = allowedFieldList.get(classToFiler);
    	if (classAllowedFields == null) {
			classAllowedFields = new HashSet<String>();
			allowedFieldList.put(classToFiler, classAllowedFields);
		}
        classAllowedFields.add(fieldName);
    }
}
