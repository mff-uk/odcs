package cz.cuni.mff.xrg.odcs.frontend.navigation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify address under which the given object is accessible. Can be used
 * for {@link View} and {@link Presenter}.
 * 
 * @author Petyr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Address {
    
    /**
     * Address under which the view can be accessed.
     * @return 
     */
    public String url() default "";
    
}
