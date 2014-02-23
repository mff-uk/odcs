package cz.cuni.mff.xrg.odcs.commons.dpu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark DPU as a Loader. Use on main DPU class ie. 
 * {@link cz.cuni.mff.xrg.odcs.commons.dpu.DPU}.
 * 
 * @author Petyr
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsLoader {

}
