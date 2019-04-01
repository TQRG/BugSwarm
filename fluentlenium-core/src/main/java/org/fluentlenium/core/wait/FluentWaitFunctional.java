package org.fluentlenium.core.wait;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import org.fluentlenium.core.FluentControl;
import org.openqa.selenium.support.ui.Wait;

/**
 * Functional API of fluent wait.
 *
 * @param <F> the argument to pass to function called
 * @see #until(Function)
 */
public interface FluentWaitFunctional<F> extends Wait<F> {

    /**
     * Wait until the predicate returns true.
     *
     * @param predicate predicate condition to wait for
     */
    void untilPredicate(Predicate<FluentControl> predicate);

    /**
     * Wait until the supplier returns true.
     *
     * @param booleanSupplier supplier condition to wait for.
     */
    void until(Supplier<Boolean> booleanSupplier);

    /**
     * Wait until the function returns a non-null and non-false object.
     *
     * @param function function returning a non-null and non-false object when condition is verified.
     * @param <T>      type of returned object
     * @return object returned by function
     */
    <T> T until(Function<? super F, T> function);
}
