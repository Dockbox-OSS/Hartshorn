package com.darwinreforged.server.core.commands.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import java.util.List;
import java.util.Optional;
import com.darwinreforged.server.core.commands.annotation.processor.Param;
import com.darwinreforged.server.core.commands.utils.ClassUtils;


public class Context {

    private final Object source;
    private final LinkedListMultimap<String, Object> data = LinkedListMultimap.create();

    Context(Object source) {
        this.source = source;
    }

    public <T> Optional<T> getSource(Class<T> type) {
        if (type.isInstance(source)) {
            return Optional.of(type.cast(source));
        }
        return Optional.empty();
    }

    public boolean add(String key, Object value) {
        boolean result = data.put(key, value);
        data.put(value.getClass().getCanonicalName(), value);
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> get(String key) {
        return (List<T>) data.get(key);
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    public <T> T getOr(String key, T def) {
        T t = getOne(key);
        return t == null ? def : t;
    }

    public <T> List<T> getAll(Param param) {
        return ImmutableList.copyOf(get(param.getId()));
    }

    public <T> T getOne(Param param) {
        return getOne(param.getId());
    }

    public <T> T getOne(String key) {
        List<T> list = get(key);

        if (list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public <T> T getLast(String key) {
        List<T> list = get(key);

        if (list.size() > 0) {
            return list.get(list.size() - 1);
        }

        return null;
    }

    public <T> Object get(Param param, Class<T> type) throws CommandException {
        List<T> list = get(param.getId());

        if (list.isEmpty()) {
            return null;
        }

        if (param.getParamType() == Param.Type.ANY) {
            return list;
        }

        if (param.getParamType() == Param.Type.VARARG) {
            return Iterables.toArray(list, type);
        }

        if (list.size() > 1) {
            throw new CommandException("Found multiple values for %s but was expecting only one", param.getId());
        }

        return ClassUtils.cast(list.get(0), param.getType());
    }
}
