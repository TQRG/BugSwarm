package org.fluentlenium.adapter.testng.integration.shareddriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

import java.util.ArrayList;
import java.util.List;

import org.fluentlenium.adapter.testng.integration.localtest.IntegrationFluentTestNg;
import org.fluentlenium.configuration.ConfigurationProperties;
import org.fluentlenium.configuration.FluentConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@FluentConfiguration(driverLifecycle = ConfigurationProperties.DriverLifecycle.THREAD)
public class DriverPerThreadTest extends IntegrationFluentTestNg {
    private List<String> hwnds = new ArrayList<>();

    @Test(invocationCount = 2, threadPoolSize = 2)
    public void firstMethod() {
        goTo(IntegrationFluentTestNg.DEFAULT_URL);
        assertThat($(".small", withName("name"))).hasSize(1);
        hwnds.add(getDriver().getWindowHandle());
    }

    @AfterClass()
    public void checkHwnds() {
        assertThat(hwnds.stream().distinct().count()).isEqualTo(2);
    }
}
