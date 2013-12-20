package cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation;

import java.lang.reflect.Field;

/**
 * Simple class used to store field with it's respective annotation. Does
 * not contains all annotations.
 * 
 * @author Petyr
 * @param <T>
 *
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
	
	public Field getField() {
		return field;
	}

	public T getAnnotation() {
		return annotation;
	}

	public AnnotationContainer(Field field, T annotation) {
		this.field = field;
		this.annotation = annotation;
	}
}
