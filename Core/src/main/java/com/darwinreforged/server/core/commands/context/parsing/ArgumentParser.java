package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

/**
 The type Argument parser.
 */
public abstract class ArgumentParser implements AbstractParser {

    /**
     Parse optional.

     @param <A>
     the type parameter
     @param commandValue
     the command value

     @return the optional
     */
    public abstract <A> Optional<A> parse(AbstractCommandValue<String> commandValue);

}
