/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

/*
 * This file is part of JavaVerbalExpressions, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package org.dockbox.selene.core;

import org.dockbox.selene.core.util.SeleneUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VerbalExpression {

    private final Pattern pattern;

    /**
     * Use builder {@link #regex()} (or {@link #regex(VerbalExpression.Builder)})
     * to create new instance of VerbalExpression
     *
     * @param pattern
     *         - {@link java.util.regex.Pattern} that constructed by builder
     */
    private VerbalExpression(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Creates new instance of VerbalExpression builder from cloned builder
     *
     * @param pBuilder
     *         - instance to clone
     *
     * @return new VerbalExpression.Builder copied from passed
     *
     * @since 1.1
     */
    public static Builder regex(final Builder pBuilder) {
        Builder builder = new Builder();

        //Using created StringBuilder
        builder.prefixes.append(pBuilder.prefixes);
        builder.source.append(pBuilder.source);
        builder.suffixes.append(pBuilder.suffixes);
        builder.modifiers = pBuilder.modifiers;

        return builder;
    }

    /**
     * Creates new instance of VerbalExpression builder
     *
     * @return new VerbalExpression.Builder
     *
     * @since 1.1
     */
    public static Builder regex() {
        return new Builder();
    }

    /**
     * Test that full string matches regular expression
     *
     * @param pToTest
     *         - string to check match
     *
     * @return true if matches exact string, false otherwise
     */
    public boolean testExact(final String pToTest) {
        boolean ret = false;
        if (null != pToTest) {
            ret = this.pattern.matcher(pToTest).matches();
        }
        return ret;
    }

    /**
     * Test that full string contains regex
     *
     * @param pToTest
     *         - string to check match
     *
     * @return true if string contains regex, false otherwise
     */
    public boolean test(final String pToTest) {
        boolean ret = false;
        if (null != pToTest) {
            ret = this.pattern.matcher(pToTest).find();
        }
        return ret;
    }

    /**
     * Extract full string that matches regex
     * Same as {@link #getText(String, int)} for 0 group
     *
     * @param toTest
     *         - string to extract from
     *
     * @return group 0, extracted from text
     */
    public String getText(final String toTest) {
        return this.getText(toTest, 0);
    }

    /**
     * Extract exact group from string
     *
     * @param toTest
     *         - string to extract from
     * @param group
     *         - group to extract
     *
     * @return extracted group
     *
     * @since 1.1
     */
    public String getText(final String toTest, final int group) {
        Matcher m = this.pattern.matcher(toTest);
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            result.append(m.group(group));
        }
        return result.toString();
    }

    /**
     * Extract exact named-group from string
     * <p>
     * Example is see to {@link Builder#capture(String)}
     *
     * @param toTest
     *         - string to extract from
     * @param group
     *         - group to extract
     *
     * @return extracted group
     *
     * @since 1.6
     */
    public String getText(final String toTest, final String group) {
        Matcher m = this.pattern.matcher(toTest);
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            result.append(m.group(group));
        }
        return result.toString();
    }

    /**
     * Extract exact group from string and add it to list
     * <p>
     * Example:
     * String text = "SampleHelloWorldString";
     * VerbalExpression regex = regex().capt().oneOf("Hello", "World").endCapt().maybe("String").build();
     * list = regex.getTextGroups(text, 0) //result: "Hello", "WorldString"
     * list = regex.getTextGroups(text, 1) //result: "Hello", "World"
     *
     * @param toTest
     *         - string to extract from
     * @param group
     *         - group to extract
     *
     * @return list of extracted groups
     */
    public List<String> getTextGroups(final String toTest, final int group) {
        List<String> groups = SeleneUtils.emptyList();
        Matcher m = this.pattern.matcher(toTest);
        while (m.find()) {
            groups.add(m.group(group));
        }
        return groups;
    }

    @Override
    public String toString() {
        return this.pattern.pattern();
    }

    public static class Builder {

        private static final Map<Character, Integer> SYMBOL_MAP = SeleneUtils.ofEntries(
                SeleneUtils.entry('d', Pattern.UNIX_LINES),
                SeleneUtils.entry('i', Pattern.CASE_INSENSITIVE),
                SeleneUtils.entry('x', Pattern.COMMENTS),
                SeleneUtils.entry('m', Pattern.MULTILINE),
                SeleneUtils.entry('s', Pattern.DOTALL),
                SeleneUtils.entry('u', Pattern.UNICODE_CASE),
                SeleneUtils.entry('U', Pattern.UNICODE_CHARACTER_CLASS)
        );
        private final StringBuilder source = new StringBuilder();
        private StringBuilder prefixes = new StringBuilder();
        private StringBuilder suffixes = new StringBuilder();
        private int modifiers = Pattern.MULTILINE;

        /**
         * Package private. Use {@link #regex()} to build a new one
         *
         * @since 1.2
         */
        Builder() {
        }

        public VerbalExpression build() {
            Pattern pattern = Pattern.compile(new StringBuilder(this.prefixes)
                    .append(this.source).append(this.suffixes).toString(), this.modifiers);
            return new VerbalExpression(pattern);
        }

        /**
         * Append a regex from builder and wrap it with unnamed group (?: ... )
         *
         * @param regex
         *         - VerbalExpression.Builder, that not changed
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder add(final Builder regex) {
            return this.group().add(regex.build().toString()).endGr();
        }

        /**
         * Mark the expression to start at the beginning of the line
         * Same as {@link #startOfLine(boolean)} with true arg
         *
         * @return this builder
         */
        public Builder startOfLine() {
            return this.startOfLine(true);
        }

        /**
         * Enable or disable the expression to start at the beginning of the line
         *
         * @param pEnable
         *         - enables or disables the line starting
         *
         * @return this builder
         */
        public Builder startOfLine(final boolean pEnable) {
            this.prefixes.append(pEnable ? "^" : "");
            if (!pEnable) {
                this.prefixes = new StringBuilder(this.prefixes.toString().replace("^", ""));
            }
            return this;
        }

        /**
         * Mark the expression to end at the last character of the line
         * Same as {@link #endOfLine(boolean)} with true arg
         *
         * @return this builder
         */
        public Builder endOfLine() {
            return this.endOfLine(true);
        }

        /**
         * Enable or disable the expression to end at the last character of the line
         *
         * @param pEnable
         *         - enables or disables the line ending
         *
         * @return this builder
         */
        public Builder endOfLine(final boolean pEnable) {
            this.suffixes.append(pEnable ? "$" : "");
            if (!pEnable) {
                this.suffixes = new StringBuilder(this.suffixes.toString().replace("$", ""));
            }
            return this;
        }

        /**
         * Add a string to the expression
         * Syntax sugar for {@link #then(String)} - use it in case:
         * regex().find("string") // when it goes first
         *
         * @param value
         *         - the string to be looked for (sanitized)
         *
         * @return this builder
         */
        public Builder find(final String value) {
            return this.then(value);
        }

        /**
         * Add a string to the expression
         *
         * @param pValue
         *         - the string to be looked for (sanitized)
         *
         * @return this builder
         */
        public Builder then(final String pValue) {
            return this.add("(?:" + this.sanitize(pValue) + ")");
        }

        /**
         * Append literal expression
         * Everything added to the expression should go trough this method
         * (keep in mind when creating your own methods).
         * All existing methods already use this, so for basic usage, you can just ignore this method.
         * <p>
         * Example:
         * regex().add("\n.*").build() // produce exact "\n.*" regexp
         *
         * @param pValue
         *         - literal expression, not sanitized
         *
         * @return this builder
         */
        public Builder add(final String pValue) {
            this.source.append(pValue);
            return this;
        }

        /**
         * Escapes any non-word char with two backslashes
         * used by any method, except {@link #add(String)}
         *
         * @param pValue
         *         - the string for char escaping
         *
         * @return sanitized string value
         */
        private String sanitize(final String pValue) {
            return pValue.replaceAll("[\\W]", "\\\\$0");
        }

        /**
         * Add a string to the expression that might appear once (or not)
         * Example:
         * The following matches all strings that contain http:// or https://
         * VerbalExpression regex = regex()
         * .find("http")
         * .maybe("s")
         * .then("://")
         * .anythingBut(" ").build();
         * regex.test("http://")    //true
         * regex.test("https://")   //true
         *
         * @param pValue
         *         - the string to be looked for
         *
         * @return this builder
         */
        public Builder maybe(final String pValue) {
            return this.then(pValue).add("?");
        }

        /**
         * Add a regex to the expression that might appear once (or not)
         * Example:
         * The following matches all names that have a prefix or not.
         * VerbalExpression.Builder namePrefix = regex().oneOf("Mr.", "Ms.");
         * VerbalExpression name = regex()
         * .maybe(namePrefix)
         * .space()
         * .zeroOrMore()
         * .word()
         * .oneOrMore()
         * .build();
         * regex.test("Mr. Bond/")    //true
         * regex.test("James")   //true
         *
         * @param regex
         *         - the string to be looked for
         *
         * @return this builder
         */
        public Builder maybe(final Builder regex) {
            return this.group().add(regex).endGr().add("?");
        }

        /**
         * Add expression that matches anything (includes empty string)
         *
         * @return this builder
         */
        public Builder anything() {
            return this.add("(?:.*)");
        }

        /**
         * Add expression that matches anything, but not passed argument
         *
         * @param pValue
         *         - the string not to match
         *
         * @return this builder
         */
        public Builder anythingBut(final String pValue) {
            return this.add("(?:[^" + this.sanitize(pValue) + "]*)");
        }

        /**
         * Add expression that matches something that might appear once (or more)
         *
         * @return this builder
         */
        public Builder something() {
            return this.add("(?:.+)");
        }

        public Builder somethingButNot(final String pValue) {
            return this.add("(?:[^" + this.sanitize(pValue) + "]+)");
        }

        /**
         * Shortcut for {@link #lineBreak()}
         *
         * @return this builder
         */
        public Builder br() {
            return this.lineBreak();
        }

        /**
         * Add universal line break expression
         *
         * @return this builder
         */
        public Builder lineBreak() {
            return this.add("(?:\\n|(?:\\r\\n)|(?:\\r\\r))");
        }

        /**
         * Add expression to match a tab character ('\u0009')
         *
         * @return this builder
         */
        public Builder tab() {
            return this.add("(?:\\t)");
        }

        /**
         * Add word, same as [a-zA-Z_0-9]+
         *
         * @return this builder
         */
        public Builder word() {
            return this.add("(?:\\w+)");
        }

        /**
         * Add word character, same as [a-zA-Z_0-9]
         *
         * @return this builder
         */
        public Builder wordChar() {
            return this.add("(?:\\w)");
        }


        /*
           --- Predefined character classes
         */

        /**
         * Add non-word character: [^\w]
         *
         * @return this builder
         */
        public Builder nonWordChar() {
            return this.add("(?:\\W)");
        }

        /**
         * Add non-digit: [^0-9]
         *
         * @return this builder
         */
        public Builder nonDigit() {
            return this.add("(?:\\D)");
        }

        /**
         * Add same as [0-9]
         *
         * @return this builder
         */
        public Builder digit() {
            return this.add("(?:\\d)");
        }

        /**
         * Add whitespace character, same as [ \t\n\x0B\f\r]
         *
         * @return this builder
         */
        public Builder space() {
            return this.add("(?:\\s)");
        }

        /**
         * Add non-whitespace character: [^\s]
         *
         * @return this builder
         */
        public Builder nonSpace() {
            return this.add("(?:\\S)");
        }

        /**
         * Add word boundary: \b
         * <p>
         * Example:
         * <pre>{@code
         * VerbalExpression regex = regex()
         * .wordBoundary().find("abc").wordBoundary()
         * .build();
         * regex.test("a abc"); // true
         * regex.test("a.abc"); // true
         * regex.test("aabc"); // false
         * }</pre>
         *
         * @return this builder
         */
        public Builder wordBoundary() {
            return this.add("(?:\\b)");
        }

        /**
         * Shortcut to {@link #anyOf(String)}
         *
         * @param value
         *         - CharSequence every char from can be matched
         *
         * @return this builder
         */
        public Builder any(final String value) {
            return this.anyOf(value);
        }


        /*
           --- / end of predefined character classes
         */

        public Builder anyOf(final String pValue) {
            this.add("[" + this.sanitize(pValue) + "]");
            return this;
        }

        /**
         * Add expression to match a range (or multiply ranges)
         * Usage: .range(from, to [, from, to ... ])
         * Example: The following matches a hexadecimal number:
         * regex().range( "0", "9", "a", "f") // produce [0-9a-f]
         *
         * @param pArgs
         *         - pairs for range
         *
         * @return this builder
         */
        public Builder range(final String... pArgs) {
            StringBuilder value = new StringBuilder("[");
            for (int firstInPairPosition = 1; firstInPairPosition < pArgs.length; firstInPairPosition += 2) {
                String from = this.sanitize(pArgs[firstInPairPosition - 1]);
                String to = this.sanitize(pArgs[firstInPairPosition]);

                value.append(from).append("-").append(to);
            }
            value.append("]");

            return this.add(value.toString());
        }

        /**
         * Turn ON matching with ignoring case
         * Example:
         * // matches "a"
         * // matches "A"
         * regex().find("a").withAnyCase()
         *
         * @return this builder
         */
        public Builder withAnyCase() {
            return this.withAnyCase(true);
        }

        public Builder withAnyCase(final boolean pEnable) {
            if (pEnable) {
                this.addModifier('i');
            } else {
                this.removeModifier('i');
            }
            return this;
        }

        public Builder addModifier(final char pModifier) {
            if (SYMBOL_MAP.containsKey(pModifier)) {
                this.modifiers |= SYMBOL_MAP.get(pModifier);
            }

            return this;
        }

        public Builder removeModifier(final char pModifier) {
            if (SYMBOL_MAP.containsKey(pModifier)) {
                this.modifiers &= ~SYMBOL_MAP.get(pModifier);
            }

            return this;
        }

        public Builder searchOneLine(final boolean pEnable) {
            if (pEnable) {
                this.removeModifier('m');
            } else {
                this.addModifier('m');
            }
            return this;
        }

        /**
         * Convenient method to show that string usage count is exact count, range count or simply one or more
         * Usage:
         * regex().multiply("abc")                                  // Produce (?:abc)+
         * regex().multiply("abc", null)                            // Produce (?:abc)+
         * regex().multiply("abc", (int)from)                       // Produce (?:abc){from}
         * regex().multiply("abc", (int)from, (int)to)              // Produce (?:abc){from, to}
         * regex().multiply("abc", (int)from, (int)to, (int)...)    // Produce (?:abc)+
         *
         * @param pValue
         *         - the string to be looked for
         * @param count
         *         - (optional) if passed one or two numbers, it used to show count or range count
         *
         * @return this builder
         *
         * @see #oneOrMore()
         * @see #then(String)
         * @see #zeroOrMore()
         */
        public Builder multiple(final String pValue, final int... count) {
            if (null == count) {
                return this.then(pValue).oneOrMore();
            }
            switch (count.length) {
                case 1:
                    return this.then(pValue).count(count[0]);
                case 2:
                    return this.then(pValue).count(count[0], count[1]);
                default:
                    return this.then(pValue).oneOrMore();
            }
        }

        /**
         * Adds "+" char to regexp
         * Same effect as {@link #atLeast(int)} with "1" argument
         * Also, used by {@link #multiple(String, int...)} when second argument is null, or have length more than 2
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder oneOrMore() {
            return this.add("+");
        }

        /**
         * Add count of previous group
         * for example:
         * .find("w").count(3) // produce - (?:w){3}
         *
         * @param count
         *         - number of occurrences of previous group in expression
         *
         * @return this Builder
         */
        public Builder count(final int count) {
            this.source.append("{").append(count).append("}");
            return this;
        }

        /**
         * Produce range count
         * for example:
         * .find("w").count(1, 3) // produce (?:w){1,3}
         *
         * @param from
         *         - minimal number of occurrences
         * @param to
         *         - max number of occurrences
         *
         * @return this Builder
         *
         * @see #count(int)
         */
        public Builder count(final int from, final int to) {
            this.source.append("{").append(from).append(",").append(to).append("}");
            return this;
        }

        /**
         * Adds "*" char to regexp, means zero or more times repeated
         * Same effect as {@link #atLeast(int)} with "0" argument
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder zeroOrMore() {
            return this.add("*");
        }

        /**
         * Produce range count with only minimal number of occurrences
         * for example:
         * .find("w").atLeast(1) // produce (?:w){1,}
         *
         * @param from
         *         - minimal number of occurrences
         *
         * @return this Builder
         *
         * @see #count(int)
         * @see #oneOrMore()
         * @see #zeroOrMore()
         * @since 1.2
         */
        public Builder atLeast(final int from) {
            return this.add("{").add(String.valueOf(from)).add(",}");
        }

        /**
         * Add a alternative expression to be matched
         * <p>
         * Issue #32
         *
         * @param pValue
         *         - the string to be looked for
         *
         * @return this builder
         */
        public Builder or(final String pValue) {
            this.prefixes.append("(?:");

            int opened = this.countOccurrencesOf(this.prefixes.toString(), "(");
            int closed = this.countOccurrencesOf(this.suffixes.toString(), ")");

            if (opened >= closed) {
                this.suffixes = new StringBuilder(")" + this.suffixes);
            }

            this.add(")|(?:");
            if (null != pValue) {
                this.then(pValue);
            }
            return this;
        }

        /**
         * Counts occurrences of some substring in whole string
         * Same as org.apache.commons.lang3.StringUtils#countMatches(String, java.lang.String)
         * by effect. Used to count braces for {@link #or(String)} method
         *
         * @param where
         *         - where to find
         * @param what
         *         - what needs to count matches
         *
         * @return 0 if nothing found, count of occurrences instead
         */
        private int countOccurrencesOf(String where, String what) {
            return (where.length() - where.replace(what, "").length()) / what.length();
        }

        /**
         * Adds an alternative expression to be matched
         * based on an array of values
         *
         * @param pValues
         *         - the strings to be looked for
         *
         * @return this builder
         *
         * @since 1.3
         */
        public Builder oneOf(final String... pValues) {
            if (null != pValues && 0 < pValues.length) {
                this.add("(?:");
                for (int i = 0; i < pValues.length; i++) {
                    String value = pValues[i];
                    this.add("(?:");
                    this.add(value);
                    this.add(")");
                    if (i < pValues.length - 1) {
                        this.add("|");
                    }
                }
                this.add(")");
            }
            return this;
        }

        /**
         * Shortcut for {@link #capture()}
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder capt() {
            return this.capture();
        }

        /**
         * Adds capture - open brace to current position and closed to suffixes
         *
         * @return this builder
         */
        public Builder capture() {
            return this.capture(null);
        }

        /**
         * Adds named-capture - open brace to current position and closed to suffixes
         * <pre>Example:{@code
         * String text = "test@example.com";
         * VerbalExpression regex = regex()
         * .find("@")
         * .capture("domain").anything().build();
         * regex.getText(text, "domain"); // => "example.com"
         * }</pre>
         *
         * @param name
         *         The name to capture
         *
         * @return this builder
         *
         * @since 1.6
         */
        public Builder capture(final String name) {
            this.suffixes.append(")");

            if (null == name || name.trim().isEmpty()) {
                return this.add("(");
            }
            return this.add("(?<" + name + ">");
        }

        /**
         * Shortcut for {@link #capture(String)}
         *
         * @param name
         *         The name to capture
         *
         * @return this builder
         *
         * @since 1.6
         */
        public Builder capt(final String name) {
            return this.capture(name);
        }

        /**
         * Same as {@link #capture()}, but don't save result
         * May be used to set count of duplicated captures, without creating a new saved capture
         * Example:
         * // Without group() - count(2) applies only to second capture
         * regex().group()
         * .capt().range("0", "1").endCapt().tab()
         * .capt().digit().count(5).endCapt()
         * .endGr().count(2);
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder group() {
            this.suffixes.append(")");
            return this.add("(?:");
        }

        /**
         * Shortcut for {@link #endCapture()}
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder endCapt() {
            return this.endCapture();
        }

        /**
         * Close brace for previous capture and remove last closed brace from suffixes
         * Can be used to continue build regex after capture or to add multiply captures
         *
         * @return this builder
         */
        public Builder endCapture() {
            if (-1 != this.suffixes.indexOf(")")) {
                this.suffixes.setLength(this.suffixes.length() - 1);
                return this.add(")");
            } else {
                throw new IllegalStateException("Can't end capture (group) when it not started");
            }
        }

        /**
         * Closes current unnamed and unmatching group
         * Shortcut for {@link #endCapture()}
         * Use it with {@link #group()} for prettify code
         * Example:
         * regex().group().maybe("word").count(2).endGr()
         *
         * @return this builder
         *
         * @since 1.2
         */
        public Builder endGr() {
            return this.endCapture();
        }
    }
}
