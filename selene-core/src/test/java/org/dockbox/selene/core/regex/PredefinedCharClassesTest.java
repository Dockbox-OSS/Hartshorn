/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.regex;

import org.dockbox.selene.core.VerbalExpression;
import org.dockbox.selene.core.regex.matchers.TestMatchMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * User: lanwen
 * Date: 13.05.14
 * Time: 16:26
 */
public class PredefinedCharClassesTest {

    public static final String LETTERS_NO_DIGITS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_";
    public static final String DIGITS = "0123456789";
    public static final String NON_LETTERS = ";'[]{}|?/";
    public static final String SPACE = " \t\n\f\r";

    @Test
    public void testWordChar() {
        VerbalExpression regex = VerbalExpression.regex().wordChar().build();

        MatcherAssert.assertThat("Not matches on letters", regex, TestMatchMatcher.matchesTo(LETTERS_NO_DIGITS + DIGITS));
        MatcherAssert.assertThat("matches on non letters", regex, CoreMatchers.not(TestMatchMatcher.matchesTo((NON_LETTERS + SPACE))));
        MatcherAssert.assertThat("Extracts wrong word chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.equalTo(LETTERS_NO_DIGITS + DIGITS));

    }

    @Test
    public void testNonWordChar() {
        VerbalExpression regex = VerbalExpression.regex().nonWordChar().build();

        MatcherAssert.assertThat("matches on letters", regex, CoreMatchers.not(TestMatchMatcher.matchesTo((LETTERS_NO_DIGITS + DIGITS))));
        MatcherAssert.assertThat("Not matches on non letters", regex, TestMatchMatcher.matchesTo(NON_LETTERS + SPACE));
        MatcherAssert.assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.equalTo(NON_LETTERS + SPACE));

    }

    @Test
    public void testSpace() {
        VerbalExpression regex = VerbalExpression.regex().space().build();

        MatcherAssert.assertThat("matches on letters", regex, CoreMatchers.not(TestMatchMatcher.matchesTo((LETTERS_NO_DIGITS + DIGITS + NON_LETTERS))));
        MatcherAssert.assertThat("Not matches on space", regex, TestMatchMatcher.matchesTo(SPACE));
        MatcherAssert.assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.equalTo(SPACE));

    }

    @Test
    public void testNonSpace() {
        VerbalExpression regex = VerbalExpression.regex().nonSpace().build();

        MatcherAssert.assertThat("Not matches on non space", regex, TestMatchMatcher.matchesTo(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS));
        MatcherAssert.assertThat("matches on space", regex, CoreMatchers.not(TestMatchMatcher.matchesTo((SPACE))));
        MatcherAssert.assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.not(SPACE));

    }

    @Test
    public void testDigit() {
        VerbalExpression regex = VerbalExpression.regex().digit().build();

        MatcherAssert.assertThat("matches on letters", regex, CoreMatchers.not(TestMatchMatcher.matchesTo((LETTERS_NO_DIGITS + SPACE + NON_LETTERS))));
        MatcherAssert.assertThat("Not matches on digits", regex, TestMatchMatcher.matchesTo(DIGITS));
        MatcherAssert.assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.is(DIGITS));

    }

    @Test
    public void testNonDigit() {
        VerbalExpression regex = VerbalExpression.regex().nonDigit().build();

        MatcherAssert.assertThat("Not matches on letters", regex, TestMatchMatcher.matchesTo(LETTERS_NO_DIGITS + SPACE + NON_LETTERS));
        MatcherAssert.assertThat("matches on digits", regex, CoreMatchers.not(TestMatchMatcher.matchesTo((DIGITS))));
        MatcherAssert.assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.not(DIGITS));

    }

    @Test
    public void testWord() {
        VerbalExpression regex = VerbalExpression.regex().word().build();

        MatcherAssert.assertThat("not matches on word", regex, TestMatchMatcher.matchesTo(LETTERS_NO_DIGITS + DIGITS));
        MatcherAssert.assertThat("matches on space and non letters", regex, CoreMatchers.not(TestMatchMatcher.matchesTo(SPACE + NON_LETTERS)));
        MatcherAssert.assertThat("extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), CoreMatchers.is(LETTERS_NO_DIGITS + DIGITS));

    }
}
