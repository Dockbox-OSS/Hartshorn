/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

/*
 * This file is part of Neutrino, licensed under the MIT License (MIT).
 */
package org.dockbox.selene.integrated.data;

import org.dockbox.selene.core.impl.files.mapping.NeutrinoObjectMapperFactory;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import ninja.leaping.configurate.loader.CommentHandlers;

/**
 * Largely taken from https://github.com/zml2008/configurate/blob/master/configurate-core/src/test/java/ninja/leaping/configurate/loader/TestConfigurationLoader.java
 */
public class TestConfigurationLoader extends AbstractConfigurationLoader<ConfigurationNode> {

    private ConfigurationNode result = SimpleConfigurationNode.root();

    public static final class Builder extends AbstractConfigurationLoader.Builder<Builder> {

        @Override
        public TestConfigurationLoader build() {
            this.setDefaultOptions(this.getDefaultOptions().setObjectMapperFactory(NeutrinoObjectMapperFactory.getInstance()))
                    .setSource(() -> Mockito.mock(BufferedReader.class))
                    .setSink(() -> Mockito.mock(BufferedWriter.class));
            return new TestConfigurationLoader(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected TestConfigurationLoader(Builder builder) {
        super(builder, CommentHandlers.values());
    }

    @Override
    protected void loadInternal(ConfigurationNode node, BufferedReader reader) throws IOException {
        node.setValue(this.result);
    }

    @Override
    protected void saveInternal(ConfigurationNode node, Writer writer) throws IOException {
        this.result.setValue(node);
    }

    public ConfigurationNode getNode() {
        return this.result;
    }

    public void setNode(ConfigurationNode node) {
        this.result = node;
    }

    /**
     * Return an empty node of the most appropriate type for this loader
     *
     * @param options The options to use with this node. Must not be null (take a look at {@link ConfigurationOptions#defaults()})
     * @return The appropriate node type
     */
    @Override
    public @NotNull ConfigurationNode createEmptyNode(@NotNull ConfigurationOptions options) {
        return SimpleConfigurationNode.root(options);
    }
}
