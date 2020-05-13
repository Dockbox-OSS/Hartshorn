package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

@FunctionalInterface
public interface FunctionalParser<T> {
    Optional<T> parse(AbstractCommandValue<String> val);
}
