package org.dockbox.hartshorn.config.beta;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.IncludeRule;

public final class DefinedSerializerConfiguration implements SerializerConfigurer {

    private IncludeRule includeRule = IncludeRule.SKIP_NONE;
    private FileFormat defaultFormat = FileFormats.JSON;

    public IncludeRule includeRule() {
        return this.includeRule;
    }

    public FileFormat defaultFormat() {
        return this.defaultFormat;
    }

    @Override
    public SerializerConfigurer includeRule(final IncludeRule rule) {
        this.includeRule = rule;
        return this;
    }

    public DefinedSerializerConfiguration defaultFormat(final FileFormat defaultFormat) {
        this.defaultFormat = defaultFormat;
        return this;
    }

}
