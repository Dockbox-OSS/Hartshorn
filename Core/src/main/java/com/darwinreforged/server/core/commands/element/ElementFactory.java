package com.darwinreforged.server.core.commands.element;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.darwinreforged.server.core.commands.annotation.processor.Param;
import com.darwinreforged.server.core.commands.element.function.Filter;
import com.darwinreforged.server.core.commands.element.function.Options;
import com.darwinreforged.server.core.commands.element.function.ValueParser;


public class ElementFactory {

    private final Map<Class<?>, ElementProvider> providers;
    private final Map<Class<?>, ValueParser<?>> parsers;
    private final Map<Class<?>, Options> options;
    private final Map<Class<?>, Filter> filters;

    private final Options defaultOptions = Options.EMPTY;
    private final Filter defaultFilter = Filter.STARTS_WITH;
    private final ValueParser<?> defaultParser = ValueParser.EMPTY;

    protected ElementFactory(Builder builder) {
        this.providers = ImmutableMap.copyOf(builder.providers);
        this.options = ImmutableMap.copyOf(builder.options);
        this.filters = ImmutableMap.copyOf(builder.filters);
        this.parsers = ImmutableMap.<Class<?>, ValueParser<?>>builder()
                .putAll(builder.parsers)
                .putAll(ValueParser.DEFAULTS)
                .build();
    }

    public Element create(Param param, Map<String, Element> flags) {
        ValueParser<?> parser = getParser(param);
        Options options = getOptions(param);
        Filter filter = getFilter(param);
        int priority = param.getParamType().priority();

        if (parser == null) {
            throw new UnsupportedOperationException("Invalid element type " + param.getType());
        }

        if (param.getParamType() == Param.Type.ANY) {
            return createMultiValueElement(param.getId(), priority, param.getType(), options, filter, parser);
        }

        if (param.getParamType() == Param.Type.VARARG) {
            return createVarargElement(param.getId(), priority, param.getType(), options, filter, parser, flags);
        }

        if (param.getParamType() == Param.Type.FLAG) {
            return createFlagElement(param.getId(), priority, param.getType(), options, filter, parser, flags);
        }

        return createValueElement(param.getId(), priority, param.getType(), options, filter, parser);
    }

    public final boolean hasParser(Class<?> type) {
        return parsers.containsKey(type);
    }

    public final boolean hasOptions(Class<?> type) {
        return options.containsKey(type);
    }

    public final boolean hasFilter(Class<?> type) {
        return filters.containsKey(type);
    }

    public final Element createValueElement(String id, int priority, Options options, Filter filter, ValueParser parser) {
        return new ValueElement(id, priority, options, filter, parser);
    }

    public final Element createMultiValueElement(Element element) {
        return new MultiValueElement(element);
    }

    public final Element createVarargElement(Element element, Map<String, Element> flags) {
        return new VarargElement(element, flags.keySet());
    }

    public final Element createFlagElement(String id, Map<String, Element> flags) {
        return new FlagElement(id, flags);
    }

    public Element createValueElement(String id, int priority, Class<?> type, Options options, Filter filter, ValueParser parser) {
        ElementProvider provider = providers.get(type);
        if (provider == null) {
            return createValueElement(id, priority, options, filter, parser);
        }
        return provider.create(id, priority, options, filter, parser);
    }

    public Element createMultiValueElement(String id, int priority, Class<?> type, Options options, Filter filter, ValueParser parser) {
        return createMultiValueElement(createValueElement(id, priority, type, options, filter, parser));
    }

    public Element createVarargElement(String id, int priority, Class<?> type, Options options, Filter filter, ValueParser parser, Map<String, Element> flags) {
        return createVarargElement(createValueElement(id, priority, type, options, filter, parser), flags);
    }

    public Element createFlagElement(String id, int priority, Class<?> type, Options options, Filter filter, ValueParser parser, Map<String, Element> flags) {
        return createFlagElement(id, flags);
    }

    public Options getOptions(Param param) {
        return getOptions(param.getType());
    }

    public Filter getFilter(Param param) {
        return getFilter(param.getType());
    }

    public ValueParser<?> getParser(Param param) {
        if (param.getParamType() == Param.Type.JOIN) {
            return ValueParser.joinedString(" ");
        }
        return getParser(param.getType());
    }

    public Options getOptions(Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            Object[] values = type.getEnumConstants();
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            for (Object value : values) {
                builder.add(value.toString());
            }
            return Options.of(builder.build());
        }

        return get(type, options, defaultOptions);
    }

    public Filter getFilter(Class<?> type) {
        return get(type, filters, defaultFilter);
    }

    @SuppressWarnings("unchecked")
    public ValueParser<?> getParser(Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return ValueParser.enumParser((Class<? extends Enum>) type);
        }

        return get(type, parsers, defaultParser);
    }

    private <T> T get(Class<?> type, Map<Class<?>, T> map, T defaultVal) {
        while (type != null && type != Object.class) {
            T t = map.get(type);
            if (t != null) {
                return t;
            }
            type = type.getSuperclass();
        }
        return defaultVal;
    }

    public static ElementFactory create() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<Class<?>, ElementProvider> providers = new HashMap<>();
        private final Map<Class<?>, ValueParser<?>> parsers = new HashMap<>();
        private final Map<Class<?>, Filter> filters = new HashMap<>();
        private final Map<Class<?>, Options> options = new HashMap<Class<?>, Options>(){{
            put(boolean.class, Options.of("false", "true"));
            put(Boolean.class, Options.of("false", "true"));
        }};

        public <T> Builder parser(Class<T> type, ValueParser<T> parser) {
            parsers.put(type, parser);
            return this;
        }

        public Builder options(Class<?> type, Options options) {
            this.options.put(type, options);
            return this;
        }

        public Builder filter(Class<?> type, Filter filter) {
            this.filters.put(type, filter);
            return this;
        }

        public Builder provider(Class<?> type, ElementProvider provider) {
            providers.put(type, provider);
            return this;
        }

        public ElementFactory build() {
            return new ElementFactory(this);
        }

        public ElementFactory build(Function<Builder, ElementFactory> func) {
            return func.apply(this);
        }
    }
}
