package org.fluentlenium.integration;

import org.fluentlenium.integration.localtest.LocalFluentCase;
import org.junit.Test;

import static org.fest.assertions.fluentlenium.FluentLeniumAssertions.assertThat;

public class FluentWebElementFestAssertTest extends LocalFluentCase {

    @Test
    public void testIsEnabledOk() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#name")).isEnabled();
    }

    @Test(expected = AssertionError.class)
    public void testIsEnabledKo() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#disabled")).isEnabled();
    }


    @Test
    public void testIsNotEnabledOk() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#disabled")).isNotEnabled();
    }


    @Test(expected = AssertionError.class)
    public void testIsNotEnabledKo() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#name")).isNotEnabled();

    }

    @Test
    public void testIsDisplayedOk() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#disabled")).isDisplayed();
    }

    @Test(expected = AssertionError.class)
    public void testIsDisplayedKo() throws Exception {
        goTo(DEFAULT_URL);
        executeScript("document.getElementById(\"disabled\").style.display=\"none\";");
        assertThat(findFirst("#disabled")).isDisplayed();
    }

    @Test
    public void testIsNotDisplayed() throws Exception {
        goTo(DEFAULT_URL);
        executeScript("document.getElementById(\"disabled\").style.display=\"none\";");
        assertThat(findFirst("#disabled")).isNotDisplayed();

    }

    @Test(expected = AssertionError.class)
    public void testIsSelected() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#disabled")).isNotDisplayed();
    }

    @Test
    public void testIsNotSelectedOk() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#disabled")).isNotSelected();
    }

    @Test(expected = AssertionError.class)
    public void testIsNotSelectedKo() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#selected")).isNotSelected();

    }

    @Test
    public void testIsSelectedOk() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#selected")).isSelected();

    }

    @Test(expected = AssertionError.class)
    public void testIsSelectedKo() throws Exception {
        goTo(DEFAULT_URL);
        assertThat(findFirst("#disabled")).isSelected();

    }

}
