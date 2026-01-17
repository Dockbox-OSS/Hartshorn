package test.${package}.${moduleId};

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTests {

    @Test
    void sampleAssertion() {
        assertThat("Hello, ${name}!").isNotEmpty();
    }
}