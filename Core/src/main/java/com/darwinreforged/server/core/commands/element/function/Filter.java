package com.darwinreforged.server.core.commands.element.function;

import com.google.common.base.Preconditions;
import java.util.function.BiPredicate;


@FunctionalInterface
public interface Filter extends BiPredicate<String, String> {

    Filter STARTS_WITH = (content, match) -> match(content, match, 0, match.length());

    Filter ENDS_WITH = (content, match) -> match(content, match, content.length() - match.length(), content.length());

    Filter CONTAINS = (content, match) -> match(content, match, 0, content.length());

    Filter EQUALS = String::equals;

    Filter EQUALS_IGNORE_CASE = String::equalsIgnoreCase;

    static boolean match(String content, String match, int start, int end) {
        Preconditions.checkNotNull(content, "Content string is null");
        Preconditions.checkNotNull(match, "Match string is null");

        if (match.isEmpty()) {
            return true;
        }

        if (match.length() > content.length() || end > content.length()) {
            return false;
        }

        outer:
        while (start < end) {
            if (start + match.length() > end) {
                return false;
            }

            for (int t = 0, s = start; t < match.length() && s < end; t++, s++) {
                char sc = content.charAt(s);
                char tc = match.charAt(t);

                if (Character.toUpperCase(sc) != Character.toUpperCase(tc)) {
                    start++;
                    continue outer;
                }
            }

            return true;
        }

        return false;
    }
}
