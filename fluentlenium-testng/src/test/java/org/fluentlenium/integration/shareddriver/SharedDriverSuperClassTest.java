package org.fluentlenium.integration.shareddriver;

import org.fluentlenium.adapter.util.SharedDriver;
import org.fluentlenium.integration.localtest.LocalFluentCase;
import org.openqa.selenium.Cookie;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withName;

@SharedDriver
class SharedDriverSuperClass extends LocalFluentCase {
}

public class SharedDriverSuperClassTest extends SharedDriverSuperClass {
    @Test
    public void firstMethod() {
        goTo(LocalFluentCase.DEFAULT_URL);
        this.getDriver().manage().addCookie(new Cookie("cookie", "fluent"));
        assertThat($(".small", withName("name"))).hasSize(1);
    }

    @Test
    public void secondMethod() {
        assertThat($(".small", withName("name"))).hasSize(1);
        assertThat(this.getCookie("cookie")).isNull();
    }


}
