package cz.cuni.mff.xrg.odcs.commons.dpu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to announced that given {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} should be used as input.
 * Can not be use together with {@link OutputDataUnit}.
 * If DPU contains more than one output the name should be provided for all the
 * output data units.
 * Use only letters [a-z, A-Z], numbers [0-9] and '_' in {@link #name()}.Usage
 * of other chars is not allowed.
 * 
 * @author Petyr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputDataUnit {

    /**
     * Name which identify input. Name is obligatory. {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     * 
     * @return Name of {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     */
    public String name();

    /**
     * @return False the execution failed if there is no suitable {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} that can be used.
     */
    public boolean optional() default false;

    /**
     * Return description that will be presented to the user.
     * 
     * @return {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}'s description
     */
    public String description() default "";

}
