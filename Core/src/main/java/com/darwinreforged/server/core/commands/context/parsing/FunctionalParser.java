package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

/**
 The interface Functional parser.

 @param <T>
 the type parameter
 */
@FunctionalInterface
public interface FunctionalParser<T> {
    /**
     Parse optional.

     @param val
     the val

     @return the optional
     */
    Optional<T> parse(AbstractCommandValue<String> val);
}
