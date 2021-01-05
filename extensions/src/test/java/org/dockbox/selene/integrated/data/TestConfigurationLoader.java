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

import org.dockbox.selene.core.server.Selene;
import org.mockito.Mockito;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.loader.CommentHandlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Largely taken from https://github.com/zml2008/configurate/blob/master/configurate-core/src/test/java/ninja/leaping/configurate/loader/TestConfigurationLoader.java
 */
public class TestConfigurationLoader extends AbstractConfigurationLoader<BasicConfigurationNode> {

    private ConfigurationNode result = BasicConfigurationNode.root();

    public static final class Builder extends AbstractConfigurationLoader.Builder<Builder, TestConfigurationLoader> {

        @Override
        public TestConfigurationLoader build() {
            this.defaultOptions(this.defaultOptions())
                    .source(() -> Mockito.mock(BufferedReader.class))
                    .sink(() -> Mockito.mock(BufferedWriter.class));
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
    protected void loadInternal(BasicConfigurationNode node, BufferedReader reader) {
        try {
            node.set(this.result);
        } catch (IOException e) {
            Selene.handle(e);
        }
    }

    @Override
    protected void saveInternal(ConfigurationNode node, Writer writer) {
        try {
            this.result.set(node);
        } catch (IOException e) {
            Selene.handle(e);
        }
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
    public BasicConfigurationNode createNode(ConfigurationOptions options) {
        return BasicConfigurationNode.root(options);
    }
}
