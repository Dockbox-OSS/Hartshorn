/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.resources;

import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link ResourceLookup} implementation that allows for multiple {@link ResourceLookupStrategy} implementations to be
 * used. The strategy to use is determined by the prefix of the source string. If no prefix is provided, the fallback
 * strategy is used.
 *
 * <p>Source strings are expected to be in the format {@code strategy:source}, where {@code strategy} is the {@link
 * ResourceLookupStrategy#name() name} of the strategy to use, and {@code source} is the source string to use for the
 * strategy. Strategy names are case-sensitive.
 *
 * <p>For example, the source string {@code classpath:data.json} will be resolved using the {@link
 * ClassPathResourceLookupStrategy}, while the source string {@code fs:data.json} will be resolved using the {@link
 * FileSystemLookupStrategy} (assuming both strategies are registered with this {@link ResourceLookup} instance).
 *
 * <p>If no strategies are registered, the fallback strategy is used for all source strings.
 *
 * @see ResourceLookup
 * @see ResourceLookupStrategy
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class FallbackResourceLookup implements ResourceLookup {

    private static final Pattern STRATEGY_PATTERN = Pattern.compile("(.+):(.+)");

    private final Map<String, ResourceLookupStrategy> strategies = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;
    private final ResourceLookupStrategy fallbackStrategy;

    public FallbackResourceLookup(ApplicationContext applicationContext, ResourceLookupStrategy fallbackStrategy) {
        this.applicationContext = applicationContext;
        this.fallbackStrategy = fallbackStrategy;
    }

    @Override
    public Set<URI> lookup(String path) {
        String matchedSource = path;
        Matcher matcher = STRATEGY_PATTERN.matcher(matchedSource);

        ResourceLookupStrategy strategy = this.fallbackStrategy;
        if (matcher.find()) {
            strategy = this.strategies.getOrDefault(matcher.group(1), strategy);
            matchedSource = matcher.group(2);
        }

        return strategy.lookup(this.applicationContext, matchedSource);
    }

    /**
     * Adds a new strategy to this {@link ResourceLookup}. If a strategy with the same name already exists, an {@link
     * IllegalArgumentException} is thrown.
     *
     * @param strategy the strategy to add
     * @throws IllegalArgumentException if a strategy with the same name already exists
     */
    public void addLookupStrategy(ResourceLookupStrategy strategy) {
        if (this.strategies.containsKey(strategy.name())) {
            throw new IllegalArgumentException("A strategy for source " + strategy.name() + " already exists");
        }
        this.strategies.put(strategy.name(), strategy);
    }

    /**
     * Removes a strategy from this {@link ResourceLookup}. If no strategy with the given name exists, no action is
     * taken.
     *
     * @param strategy the strategy to remove
     */
    public void removeLookupStrategy(ResourceLookupStrategy strategy) {
        this.strategies.remove(strategy.name());
    }

    /**
     * Returns a set of all strategies that are registered with this {@link ResourceLookup}.
     *
     * @return a set of all strategies that are registered with this {@link ResourceLookup}
     */
    public Set<ResourceLookupStrategy> strategies() {
        return Set.copyOf(this.strategies.values());
    }
}
