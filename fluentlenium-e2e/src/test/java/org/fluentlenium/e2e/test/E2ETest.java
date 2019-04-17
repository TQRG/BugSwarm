package org.fluentlenium.e2e.test;

import static org.openqa.selenium.remote.DesiredCapabilities.chrome;

import java.net.MalformedURLException;
import java.net.URL;

import org.fluentlenium.adapter.testng.FluentTestNg;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

public class E2ETest extends FluentTestNg {
    @Override
    public WebDriver newWebDriver() {
        try {
            return new Augmenter().augment(new RemoteWebDriver(new URL(System.getenv("browserstackurl")), chrome()));
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
