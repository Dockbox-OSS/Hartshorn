package com.darwinreforged.server.core.commands.element;

import com.darwinreforged.server.core.commands.element.function.Filter;
import com.darwinreforged.server.core.commands.element.function.Options;
import com.darwinreforged.server.core.commands.element.function.ValueParser;


public interface ElementProvider {

    Element create(String id, int priority, Options options, Filter filter, ValueParser parser);
}
