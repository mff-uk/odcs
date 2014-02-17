package cz.cuni.mff.xrg.odcs.commons.dpu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to announced that given
 * {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} should be used as output.
 * Can not be use together with {@link InputDataUnit}.
 *
 * If DPU contains more than one output the name should be provided for all the
 * output data units.
 *
 * Use only letters [a-z, A-Z], numbers [0-9] and '_' in {@link #name()}.Usage
 * of other chars is not allowed.
 *
 * @author Petyr
 * @author Jiri Tomes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OutputDataUnit {

	/**
	 *
	 * @return Name of output
	 *         {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
	 */
	public String name() default "output";

	/**
	 *
	 * @return {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}'s description
	 *         will be visible to the user.
	 */
	public String description() default "";

	/**
	 *
	 * @return False the execution failed if there is no suitable DataUnit that
	 *         can be used.
	 */
	public boolean optional() default false;
}
