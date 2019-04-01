package org.fluentlenium.adapter.cucumber.integration.page;

import org.fluentlenium.adapter.cucumber.integration.utils.UrlUtil;
import org.fluentlenium.core.FluentPage;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalPage2 extends FluentPage {

    @Override
    public String getUrl() {
        return UrlUtil.getAbsoluteUrlFromFile("html/index.html");
    }

    @Override
    public void isAt() {
        assertThat(window().title()).contains("Page 2");
    }
}
