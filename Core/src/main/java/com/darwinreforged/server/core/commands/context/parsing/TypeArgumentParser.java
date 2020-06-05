package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

/**
 Argument parser to parse {@link AbstractCommandValue}s to different types depending on the
 implementation of {@link #parse(AbstractCommandValue, Class)}.
 */
public abstract class TypeArgumentParser implements AbstractParser {

    /**
     The method used to parse {@link AbstractCommandValue}s into the given
     generic type.

     @param <A>
     the generic type to convert to
     @param commandValue
     the {@link AbstractCommandValue} in String format to parse.
     @param type
     the class type to use while parsing

     @return the optional type of the generic type. Should return {@link Optional#empty()} if
     null or if the value could not be parsed.
     */
    public abstract <A> Optional<A> parse(AbstractCommandValue<String> commandValue, Class<A> type);

}
