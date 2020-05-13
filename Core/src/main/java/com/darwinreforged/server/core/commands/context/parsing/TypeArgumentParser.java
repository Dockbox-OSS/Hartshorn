package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

/**
 The type Type argument parser.
 */
public abstract class TypeArgumentParser implements AbstractParser {

    /**
     Parse optional.

     @param <A>
     the type parameter
     @param commandValue
     the command value
     @param type
     the type

     @return the optional
     */
    public abstract <A> Optional<A> parse(AbstractCommandValue<String> commandValue, Class<A> type);

}
