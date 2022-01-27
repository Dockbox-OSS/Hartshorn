/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum MediaType {
    ALL("*", "*"),
    APPLICATION_JSON("application", "json", FileFormats.JSON),
    APPLICATION_XML("application", "xml", FileFormats.XML),
    APPLICATION_YAML("application", "x-yaml", FileFormats.YAML),
    APPLICATION_TOML("application", "toml", FileFormats.TOML),
    TEXT_PLAIN("text", "plain"),
    TEXT_HTML("text", "html"),
    TEXT_CSS("text", "css"),
    TEXT_XML("text", "xml"),
    TEXT_YAML("text", "yaml"),
    TEXT_TOML("text", "toml"),
    TEXT_MARKDOWN("text", "markdown"),
    ;

    private final String type;
    private final String subtype;

    @Getter(AccessLevel.NONE)
    private FileFormat format;

    public boolean isSerializable() {
        return this.format != null;
    }

    public Exceptional<FileFormat> fileFormat() {
        return Exceptional.of(this.format);
    }

    public String value() {
        return "%s/%s".formatted(this.type(), this.subtype());
    }
}
