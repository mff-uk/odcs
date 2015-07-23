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
package eu.unifiedviews.dataunit.files.impl;

import eu.unifiedviews.dataunit.files.FilesDataUnit;

/**
 * Holds basic informations about a single file.
 * 
 * @author Michal Klempa
 */
class FilesDataUnitEntryImpl implements FilesDataUnit.Entry {

    private final String symbolicName;

    private final String fileURIString;

    public FilesDataUnitEntryImpl(String symbolicName, String fileURIString) {
        this.symbolicName = symbolicName;
        this.fileURIString = fileURIString;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public String getFileURIString() {
        return fileURIString;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[symbolicName=" + symbolicName + ",filesystemURI=" + fileURIString + "]";
    }
   
}
