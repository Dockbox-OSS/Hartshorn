package com.darwinreforged.server.core.commands.element;

import java.util.LinkedList;
import java.util.List;
import com.darwinreforged.server.core.commands.command.CommandException;
import com.darwinreforged.server.core.commands.command.Context;
import com.darwinreforged.server.core.commands.command.Input;


public class MultiValueElement implements Element {

    private final Element element;

    public MultiValueElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "Value: [" + element + "]";
    }

    @Override
    public void parse(Input input, Context context) throws CommandException {
        if (suggest(input, context)) {
            return;
        }

        List<String> suggestions = new LinkedList<>();
        LinkedList<CommandException> exceptions = new LinkedList<>();

        int startPos = input.getPos();
        int endPos = startPos;
        suggest(input, context, suggestions);

        if (suggestions.isEmpty()) {
            input.setPos(startPos);
            suggestions.add(input.next());
        }

        for (String suggestion : suggestions) {
            try {
                input.setPos(startPos);
                Input next = input.replace(suggestion);
                element.parse(next, context);
                endPos = next.getPos();
            } catch (CommandException e) {
                exceptions.add(e);
            }
        }

        if (startPos == endPos && !exceptions.isEmpty()) {
            throw exceptions.getLast();
        }

        input.setPos(endPos);
    }

    @Override
    public void suggest(Input input, Context context, List<String> suggestions) {
        element.suggest(input, context, suggestions);
    }

    private boolean suggest(Input input, Context context) throws CommandException {
        if (context.has("#suggest")) {
            try {
                element.parse(input, context);
            } catch (CommandException e) {
                if (!input.hasNext()) {
                    throw e;
                }
            }
            return true;
        }
        return false;
    }
}
