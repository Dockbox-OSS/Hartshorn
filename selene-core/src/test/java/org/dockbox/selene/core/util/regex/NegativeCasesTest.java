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

package org.dockbox.selene.core.util.regex;

import org.dockbox.selene.core.util.regex.matchers.TestMatchMatcher;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.PatternSyntaxException;

/**
 * User: lanwen
 * Date: 11.05.14
 * Time: 3:37
 */
public class NegativeCasesTest {

    @Test(expected = IllegalStateException.class)
    public void testEndCaptureOnEmptyRegex() {
        VerbalExpression.regex().endCapture().build();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldExceptionWhenTryGetMoreThanCapturedGroup() {
        String text = "abc";
        VerbalExpression regex = VerbalExpression.regex().find("b").capture().find("c").build();

        regex.getText(text, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionWhenTryGetByNonExistentCaptureName() {
        String text = "abc";
        VerbalExpression regex = VerbalExpression.regex().find("b")
                .capture("test1").find("c").build();

        regex.getText(text, "test2");
    }

    @Test(expected = PatternSyntaxException.class)
    public void testRangeWithoutArgs() {
        VerbalExpression.regex().startOfLine().range().build();
    }

    @Test(expected = PatternSyntaxException.class)
    public void testRangeWithOneArg() {
        VerbalExpression.regex().startOfLine().range("a").build();
    }

    @Test
    public void rangeWithThreeArgsUsesOnlyFirstTwo() {
        VerbalExpression regex = VerbalExpression.regex().startOfLine().range("a", "z", "A").build();

        Assert.assertThat("Range with three args differs from expected", regex.toString(), CoreMatchers.equalTo("^[a-z]"));
    }

    @Test
    public void orWithNullMatchesAny() {
        VerbalExpression regex = VerbalExpression.regex().startOfLine().then("a").or(null).build();
        Assert.assertThat("regex don't matches writed letter", regex, TestMatchMatcher.matchesTo("a"));
        Assert.assertThat("or(null) should match any", regex, TestMatchMatcher.matchesTo("bcd"));

        Assert.assertThat("or(null) extract only first", regex.getText("abcd"), CoreMatchers.equalTo("a"));
    }

    @Test
    public void orAfterCaptureProduceEmptyGroup() {
        VerbalExpression regex = VerbalExpression.regex().startOfLine().then("a").capture().or("b").build();

        Assert.assertThat(regex.toString(), CoreMatchers.containsString("()|"));

        Assert.assertThat("regex dont matches string abcd", regex.getText("abcd", 0), CoreMatchers.equalTo("a"));
        Assert.assertThat("regex dont extract a by first group", regex.getText("abcd", 1), CoreMatchers.equalTo(""));
    }

    @Test
    public void orAfterNamedCaptureProduceEmptyGroup() {
        String captureName = "test";
        VerbalExpression regex = VerbalExpression.regex().startOfLine().then("a")
                .capture(captureName).or("b").build();

        Assert.assertThat(regex.toString(), CoreMatchers.containsString("(?<test>)|"));

        Assert.assertThat("regex don't matches string abcd",
                regex.getText("abcd", 0), CoreMatchers.equalTo("a"));
        Assert.assertThat("regex don't extract a by group named " + captureName,
                regex.getText("abcd", captureName), CoreMatchers.equalTo(""));
    }

    @Test
    public void multiplyWithNullOnCountEqualToWithOneAndMore() {
        VerbalExpression regex = VerbalExpression.regex().multiple("some", null).build();

        Assert.assertThat("Multiply with null should be equal to oneOrMore",
                regex.toString(), CoreMatchers.equalTo(VerbalExpression.regex().find("some").oneOrMore().build().toString()));
    }

    @Test
    public void multiplyWithMoreThan3ParamsOnCountEqualToWithOneAndMore() {
        VerbalExpression regex = VerbalExpression.regex().multiple("some", 1, 2, 3).build();

        Assert.assertThat("Multiply with 3 args should be equal to oneOrMore",
                regex.toString(), CoreMatchers.equalTo(VerbalExpression.regex().find("some").oneOrMore().build().toString()));
    }

    @Test(expected = java.util.regex.PatternSyntaxException.class)
    public void twoOpenCaptsWithOrThrowSyntaxException() {
        VerbalExpression regex = VerbalExpression.regex().capt().capt().or("0").build();
        String ignored = regex.toString();
    }
}
