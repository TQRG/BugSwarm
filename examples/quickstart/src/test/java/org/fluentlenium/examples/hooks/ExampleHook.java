package org.fluentlenium.examples.hooks;

import com.google.common.base.Supplier;
import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.hook.BaseHook;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class ExampleHook extends BaseHook<ExampleHookOptions> {
    public ExampleHook(FluentControl control, ComponentInstantiator instantiator,
            Supplier<WebElement> elementSupplier, Supplier<ElementLocator> locatorSupplier,
            Supplier<String> toStringSupplier, ExampleHookOptions options) {
        super(control, instantiator, elementSupplier, locatorSupplier, toStringSupplier, options);
    }

    @Override
    public void click() {
        System.out.println(getOptions().getMessage() + ": before click!");
        super.click();
        System.out.println(getOptions().getMessage() + "ExampleHook: after click!");
    }
}
