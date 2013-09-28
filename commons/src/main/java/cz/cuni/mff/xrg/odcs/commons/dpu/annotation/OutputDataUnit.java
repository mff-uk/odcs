package cz.cuni.mff.xrg.odcs.commons.dpu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to announced that given {@link DataUnit} should be used as output. 
 * Can not be use together with {@link InputDataUnit}.
 * 
 * Require using {@link BindDataUnits} annotation to the whole class in order
 * to work.
 * 
 * If DPU contains more than one output the name should be provided for all the
 * output data units.
 * 
 * @author Petyr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OutputDataUnit {

	/**
	 * Name of output {@link DataUnit}.
	 * @return
	 */
	public String name() default "output";
	
	/**
	 * {@link DataUnit}'s description will be visible to the user.
	 * @return
	 */
	public String description() default "";
	
}
