package cz.cuni.xrg.intlib.commons.dpu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to announced that given {@link DataUnit} should be used as input. 
 * Can not be use together with {@link OutputDataUnit}.
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
public @interface InputDataUnit {

	/**
	 * Name which identify input {@link DataUnit}.
	 * @return
	 */
	public String name() default "input";
	
	/**
	 * If false then only {@link DataUnit} with name equal to the
	 * {@link #name} can be used. Otherwise the {@link #name} determine only 
	 * preference.
	 * @return
	 */
	public boolean relaxed() default true;
	
	/**
	 * If false the execution failed if there is no suitable DataUnit
	 * that can be used.
	 * @return
	 */
	public boolean optional() default false;
	
	/**
	 * {@link DataUnit}'s description will be visible to the user.
	 * @return
	 */
	public String description() default "";
	
}
