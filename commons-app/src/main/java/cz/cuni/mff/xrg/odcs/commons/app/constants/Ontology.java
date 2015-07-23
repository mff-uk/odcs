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
package cz.cuni.mff.xrg.odcs.commons.app.constants;

/**
 * Contains definition of used predicates.
 *
 * @author Škoda Petr
 */
public class Ontology {

    private Ontology() {

    }

    public static final String BASE_URI = "http://unifiedviews.eu/";

    public static final String ONTOLOGY = "ontology/";

    public static final String RESOURCE = "resource/";

    public static final String INTERNAL = "internal/";

    /**
     * Ontology prefix for predicates in metadata unit.
     */
    public static final String ONTOLOGY_DATAUNIT = BASE_URI + ONTOLOGY + INTERNAL + "data/";

    public static final String PREDICATE_METADATA_CONTEXT_READ = ONTOLOGY_DATAUNIT + "read";

    public static final String PREDICATE_METADATA_CONTEXT_WRITE = ONTOLOGY_DATAUNIT + "write";

    public static final String PREDICATE_METADATA_ENTRY_COUNTER = ONTOLOGY_DATAUNIT + "counter";

    /**
     * Prefix for metadata graphs.
     */
    public static final String SUBJECT_METADATA_GRAPH = BASE_URI + RESOURCE + INTERNAL + "data/";

    /**
     * A static graph used to store informations about data units.
     */
    public static final String GRAPH_METADATA = BASE_URI + RESOURCE + INTERNAL + "data";

}
