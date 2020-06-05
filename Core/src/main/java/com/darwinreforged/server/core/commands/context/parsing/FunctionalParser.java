package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

/**
 The functional interface type used to parse {@link AbstractCommandValue}
 values to generic types, depending on its implementation.

 @param <T>
 the type parameter to convert to
 */
@FunctionalInterface
public interface FunctionalParser<T> {

    /**
     The method used to parse {@link AbstractCommandValue}s into the given
     generic type.

     @param val
     the {@link AbstractCommandValue} in String format to parse.

     @return the optional type of the generic type. Should return {@link Optional#empty()} if
     null or if the value could not be parsed.
     */
    Optional<T> parse(AbstractCommandValue<String> val);
}
