package org.fluentlenium.integration;

import org.fluentlenium.integration.util.adapter.FluentTest;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class AfterTest extends FluentTest {

    @Override
    public WebDriver newWebDriver() {
        return null;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Test
    public void whenDriverIsNullThenItIsHandle() {
        // after();
    }
}
