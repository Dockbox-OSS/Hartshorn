package com.darwinreforged.server.core.commands.context.parsing;

import com.darwinreforged.server.core.commands.context.AbstractCommandValue;

import java.util.Optional;

public abstract class ArgumentParser implements AbstractParser {

    public abstract <A> Optional<A> parse(AbstractCommandValue<String> commandValue);

}
