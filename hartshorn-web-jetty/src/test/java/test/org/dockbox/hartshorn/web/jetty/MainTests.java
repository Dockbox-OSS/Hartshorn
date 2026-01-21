package test.org.dockbox.hartshorn.web.jetty;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTests {

    @Test
    void sampleAssertion() {
        assertThat("Hello, Hartshorn Web (Jetty)!").isNotEmpty();
    }
}