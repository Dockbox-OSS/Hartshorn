package com.darwinreforged.server.core.util.commands.element;

import com.darwinreforged.server.core.util.commands.element.function.Filter;
import com.darwinreforged.server.core.util.commands.element.function.Options;
import com.darwinreforged.server.core.util.commands.element.function.ValueParser;


public interface ElementProvider {

    Element create(String id, int priority, Options options, Filter filter, ValueParser parser);
}
