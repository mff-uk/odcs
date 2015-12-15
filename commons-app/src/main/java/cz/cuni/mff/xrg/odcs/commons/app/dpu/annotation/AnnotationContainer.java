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
package cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation;

import java.lang.reflect.Field;

/**
 * Simple class used to store field with it's respective annotation. Does
 * not contains all annotations.
 * 
 * @author Petyr
 * @param <T>
 */
public class AnnotationContainer<T> {

    /**
     * Field.
     */
    private final Field field;

    /**
     * Field's annotation.
     */
    public T annotation;

    /**
     * Create AnnotationContainer.
     * 
     * @param field
     *            Field.
     * @param annotation
     *            Annotation of given filed.
     */
    public AnnotationContainer(Field field, T annotation) {
        this.field = field;
        this.annotation = annotation;
    }

    /**
     * @return Filed on which is the annotation.
     */
    public Field getField() {
        return field;
    }

    /**
     * @return Represented annotation.
     */
    public T getAnnotation() {
        return annotation;
    }

}
