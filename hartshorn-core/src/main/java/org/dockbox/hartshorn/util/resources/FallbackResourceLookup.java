/*
 * Copyright 2019-2023 the original author or authors.
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

public class FallbackResourceLookup implements ResourceLookup {

    private static final Pattern STRATEGY_PATTERN = Pattern.compile("(.+):(.+)");

    private final Map<String, ResourceLookupStrategy> strategies = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;
    private final ResourceLookupStrategy fallbackStrategy;

    public FallbackResourceLookup(final ApplicationContext applicationContext, final ResourceLookupStrategy fallbackStrategy) {
        this.applicationContext = applicationContext;
        this.fallbackStrategy = fallbackStrategy;

        applicationContext.first(ResourceLookupStrategyContext.class)
                .peek(strategyContext -> strategyContext.strategies().forEach(this::addLookupStrategy));
    }

    @Override
    public Set<URI> lookup(final String path) {
        String matchedSource = path;
        final Matcher matcher = STRATEGY_PATTERN.matcher(matchedSource);

        ResourceLookupStrategy strategy = this.fallbackStrategy;
        if (matcher.find()) {
            strategy = this.strategies.getOrDefault(matcher.group(1), strategy);
            matchedSource = matcher.group(2);
        }

        return strategy.lookup(this.applicationContext, matchedSource);
    }

    @Override
    public void addLookupStrategy(final ResourceLookupStrategy strategy) {
        if (this.strategies.containsKey(strategy.name())) {
            throw new IllegalArgumentException("A strategy for source " + strategy.name() + " already exists");
        }
        this.strategies.put(strategy.name(), strategy);
    }

    @Override
    public void removeLookupStrategy(final ResourceLookupStrategy strategy) {
        this.strategies.remove(strategy.name());
    }

    @Override
    public Set<ResourceLookupStrategy> strategies() {
        return Set.copyOf(this.strategies.values());
    }
}
