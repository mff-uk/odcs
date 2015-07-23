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
package cz.cuni.mff.xrg.odcs.rdf.enums;

/**
 * Possibilies how to load RDF data insert part to the SPARQL endpoint.
 * 
 * @author Jiri Tomes
 */
public enum InsertType {

    /**
     * Load RDF data parts which have no errors. Other parts are skiped and
     * warning is given about it.
     */
    SKIP_BAD_PARTS,
    /**
     * If some of parts for loading contains errors. No data parts are loading.
     * Loading failed and it´s thrown LoadException.
     */
    STOP_WHEN_BAD_PART,
    /**
     * If any data part for loading contains errors, process clean all
     * successfully loaded parts and start loading parts again from zero.
     */
    REPEAT_IF_BAD_PART;
}
