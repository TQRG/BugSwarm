package org.fluentlenium.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.io.IOUtils;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.integration.localtest.LocalFluentCase;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlDumpTest extends LocalFluentCase {

    private final Path tempDir;

    public HtmlDumpTest() throws IOException {
        setHtmlDumpMode(TriggerMode.ON_FAIL);
        tempDir = Files.createTempDirectory("tempfiles");
    }

    @Test
    public void checkHtmlIsDumped() throws IOException {
        goTo(DEFAULT_URL);


        File file = new File(tempDir.toFile(), "test.html");
        try {
            takeHtmlDump(file.getAbsolutePath());
            FileInputStream fis = new FileInputStream(file);
            String html = IOUtils.toString(fis, "UTF-8");
            assertThat(html).isEqualTo(this.findFirst("html").html());
            assertThat(html).isNotEmpty();
        } finally {
            file.delete();
        }
    }
}
