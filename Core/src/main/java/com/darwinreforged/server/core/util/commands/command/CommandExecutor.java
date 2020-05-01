package com.darwinreforged.server.core.util.commands.command;

import com.darwinreforged.server.core.util.commands.annotation.Description;
import com.darwinreforged.server.core.util.commands.annotation.Permission;
import com.darwinreforged.server.core.util.commands.annotation.Usage;
import com.darwinreforged.server.core.util.commands.annotation.processor.Param;
import com.darwinreforged.server.core.util.commands.element.Element;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;


public class CommandExecutor {

    public static final Comparator<CommandExecutor> EXECUTION_ORDER = (e1, e2) -> {
        for (int i = 0, j = 0; i < e1.elements.size() && j < e2.elements.size(); i++, j++) {
            Element el1 = e1.elements.get(i);
            Element el2 = e2.elements.get(j);

            int comp = Integer.compare(el1.getPriority(), el2.getPriority());
            if (comp != 0) {
                return comp;
            }
        }

        return Integer.compare(e1.elements.size(), e2.elements.size());
    };

    public static final Comparator<CommandExecutor> ALPHABETICAL_ORDER = Comparator.comparing(e -> e.getUsage().value());

    private final Object object;
    private final Method method;
    private final List<Param> params;
    private final List<Element> elements;

    private final String args;
    private final Usage usage;
    private final Permission permission;
    private final Description description;

    private CommandExecutor(Builder builder) {
        object = builder.object;
        method = builder.method;
        params = builder.params;
        elements = builder.elements;
        usage = builder.usage;
        permission = builder.permission;
        description = builder.description;

        String usage = getUsage().value();
        int index = usage.indexOf(' ');
        args = index > -1 && index < usage.length() ? usage.substring(index) : usage;
    }

    public Context parse(Object source, Input input) throws CommandException {
        input.reset();

        Context context = new Context(source);
        for (Param param : params) {
            if (param.getParamType() == Param.Type.SOURCE) {
                if (!param.getType().isInstance(source)) {
                    throw new CommandException("Command source must be of type %s", param.getType().getSimpleName());
                }
                context.add(param.getId(), source);
            }
        }

        if (input.isEmpty() && elements.isEmpty()) {
            return context;
        }

        int priority = 0;
        for (Element element : elements) {
            try {
                element.parse(input, context);
            } catch (CommandException e) {
                throw e.priority(priority).args(args);
            }

            priority++;
        }

        if (input.hasNext()) {
            throw new CommandException("Too many args provided: '%s'", input.getRawInput()).priority(priority).args(args);
        }

        return context;
    }

    public void getSuggestions(Object source, Input input, List<String> suggestions) {
        if (elements.isEmpty()) {
            return;
        }

        input.reset();
        Context context = new Context(source);
        context.add("#suggest", true);

        for (Element element : elements) {
            if (!input.hasNext()) {
                break;
            }

            int pos = input.getPos();

            try {
                element.parse(input, context);
            } catch (CommandException e) {
                input.setPos(pos);
                element.suggest(input, context, suggestions);
                break;
            }
        }
    }

    public void getFirstSuggestion(Object source, Input input, List<String> suggestions) {
        if (elements.isEmpty()) {
            return;
        }
        input.reset();
        Context context = new Context(source);
        context.add("#suggest", true);
        Element first = elements.get(0);
        first.suggest(input, context, suggestions);
    }

    public void invoke(Context context) throws CommandException {
        Object[] args = new Object[params.size()];

        for (int i = 0; i < params.size(); i++) {
            Param param = params.get(i);
            Object val = context.get(param, param.getType());

            if (val == null) {
                throw new CommandException("Parameter %s missing from Context", param.getId()).args(this.args);
            }

            args[i] = val;
        }

        try {
            method.invoke(object, args);
        } catch (Throwable t) {
            throw new CommandException(t.getMessage());
        }
    }

    public Usage getUsage() {
        return usage;
    }

    public Permission getPermission() {
        return permission;
    }

    public Description getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getUsage().value();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Object object;
        private Method method;
        private List<Param> params;
        private List<Element> elements;
        private Usage usage;
        private Permission permission;
        private Description description;

        private Builder() {}

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder params(List<Param> params) {
            this.params = params;
            return this;
        }

        public Builder elements(List<Element> elements) {
            this.elements = elements;
            return this;
        }

        public Builder usage(Usage usage) {
            this.usage = usage;
            return this;
        }

        public Builder permission(Permission permission) {
            this.permission = permission;
            return this;
        }

        public Builder description(Description description) {
            this.description = description;
            return this;
        }

        public CommandExecutor build() {
            return new CommandExecutor(this);
        }
    }
}
