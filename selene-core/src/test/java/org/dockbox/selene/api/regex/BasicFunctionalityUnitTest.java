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

package org.dockbox.selene.api.regex;

import org.dockbox.selene.api.VerbalExpression;
import org.dockbox.selene.api.regex.matchers.TestMatchMatcher;
import org.dockbox.selene.api.regex.matchers.TestsExactMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BasicFunctionalityUnitTest {

    @Test
    public void testSomething() {
        VerbalExpression testRegex = VerbalExpression.regex().something().build();

        MatcherAssert.assertThat(
                "Null object doesn't have something",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo(null)));
        MatcherAssert.assertThat(
                "empty string doesn't have something",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("")));
        MatcherAssert.assertThat("a", testRegex, TestMatchMatcher.matchesTo("a"));
    }

    @Test
    public void testAnything() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().anything().build();

        MatcherAssert.assertThat(testRegex, TestMatchMatcher.matchesTo("what"));
        MatcherAssert.assertThat(testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("")));
        MatcherAssert.assertThat(testRegex, TestMatchMatcher.matchesTo(" "));
    }

    @Test
    public void testAnythingBut() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().anythingBut("w").build();

        Assertions.assertFalse(testRegex.testExact("what"), "starts with w");
        Assertions.assertTrue(testRegex.testExact("that"), "Not contain w");
        Assertions.assertTrue(testRegex.testExact(" "), "Not contain w");
        Assertions.assertFalse(testRegex.testExact(null), "Null object");
    }

    @Test
    public void testSomethingBut() {
        VerbalExpression testRegex = VerbalExpression.regex().somethingButNot("a").build();

        Assertions.assertFalse(testRegex.testExact(null), "Null string");
        Assertions.assertFalse(testRegex.testExact(""), "empty string doesn't have something");
        Assertions.assertTrue(testRegex.testExact("b"), "doesn't contain a");
        Assertions.assertFalse(testRegex.testExact("a"), "Contain a");
    }

    @Test
    public void testStartOfLine() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("a").build();

        Assertions.assertFalse(testRegex.testExact(null), "Null string");
        Assertions.assertFalse(testRegex.testExact(""), "empty string doesn't have something");
        MatcherAssert.assertThat("Starts with a", testRegex, TestMatchMatcher.matchesTo("a"));
        MatcherAssert.assertThat("Starts with a", testRegex, TestMatchMatcher.matchesTo("ab"));
        MatcherAssert.assertThat(
                "Doesn't start with a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("ba")));
    }

    @Test
    public void testStartOfLineFalse() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine(false).then("a").build();
        MatcherAssert.assertThat(testRegex, TestMatchMatcher.matchesTo("ba"));
        MatcherAssert.assertThat(testRegex, TestMatchMatcher.matchesTo("ab"));
    }

    @Test
    public void testRangeWithMultiplyRanges() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().range("a", "z", "A", "Z").build();

        MatcherAssert.assertThat(
                "Regex with multi-range differs from expected",
                regex.toString(),
                CoreMatchers.equalTo("[a-zA-Z]"));
        MatcherAssert.assertThat("Regex don't matches letter", regex, TestMatchMatcher.matchesTo("b"));
        MatcherAssert.assertThat(
                "Regex matches digit, but should match only letter",
                regex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("1")));
    }

    @Test
    public void testEndOfLine() {
        VerbalExpression testRegex = VerbalExpression.regex().find("a").endOfLine().build();

        MatcherAssert.assertThat("Ends with a", testRegex, TestMatchMatcher.matchesTo("bba"));
        MatcherAssert.assertThat("Ends with a", testRegex, TestMatchMatcher.matchesTo("a"));
        MatcherAssert.assertThat(
                "Ends with a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo(null)));
        MatcherAssert.assertThat(
                "Doesn't end with a", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("ab")));
    }

    @Test
    public void testEndOfLineIsFalse() {
        VerbalExpression testRegex = VerbalExpression.regex().find("a").endOfLine(false).build();
        MatcherAssert.assertThat(testRegex, TestMatchMatcher.matchesTo("ba"));
        MatcherAssert.assertThat(testRegex, TestMatchMatcher.matchesTo("ab"));
    }

    @Test
    public void testMaybe() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("a").maybe("b").build();

        MatcherAssert.assertThat(
                "Regex isn't correct", testRegex.toString(), CoreMatchers.equalTo("^(?:a)(?:b)?"));

        MatcherAssert.assertThat(
                "Maybe has a 'b' after an 'a'", testRegex, TestMatchMatcher.matchesTo("acb"));
        MatcherAssert.assertThat(
                "Maybe has a 'b' after an 'a'", testRegex, TestMatchMatcher.matchesTo("abc"));
        MatcherAssert.assertThat(
                "Maybe has a 'b' after an 'a'",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("cab")));
    }

    @Test
    public void testAnyOf() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("a").anyOf("xyz").build();

        MatcherAssert.assertThat(
                "Has an x, y, or z after a", testRegex, TestMatchMatcher.matchesTo("ay"));
        MatcherAssert.assertThat(
                "Doesn't have an x, y, or z after a",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
    }

    @Test
    public void testAnySameAsAnyOf() {
        VerbalExpression any = VerbalExpression.regex().any("abc").build();
        VerbalExpression anyOf = VerbalExpression.regex().anyOf("abc").build();

        MatcherAssert.assertThat(
                "any differs from anyOf", any.toString(), CoreMatchers.equalTo(anyOf.toString()));
    }

    @Test
    public void testOr() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("abc").or("def").build();

        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("xyzabc")));
    }

    @Test
    public void testLineBreak() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("abc").lineBreak().then("def").build();

        MatcherAssert.assertThat(
                "abc then line break then def", testRegex, TestMatchMatcher.matchesTo("abc\r\ndef"));
        MatcherAssert.assertThat(
                "abc then line break then def", testRegex, TestMatchMatcher.matchesTo("abc\ndef"));
        MatcherAssert.assertThat(
                "abc then line break then space then def",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("abc\r\n def")));
    }

    @Test
    public void testMacintoshLineBreak() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("abc").lineBreak().then("def").build();

        MatcherAssert.assertThat(
                "abc then line break then def", testRegex, TestMatchMatcher.matchesTo("abc\r\rdef"));
    }

    @Test
    public void testBr() {
        VerbalExpression testRegexBr =
                VerbalExpression.regex().startOfLine().then("abc").br().then("def").build();

        VerbalExpression testRegexLineBr =
                VerbalExpression.regex().startOfLine().then("abc").lineBreak().then("def").build();

        MatcherAssert.assertThat(
                ".br() differs from .lineBreak()",
                testRegexBr.toString(),
                CoreMatchers.equalTo(testRegexLineBr.toString()));
    }

    @Test
    public void testTab() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().tab().then("abc").build();

        MatcherAssert.assertThat("tab then abc", testRegex, TestMatchMatcher.matchesTo("\tabc"));
        MatcherAssert.assertThat(
                "no tab then abc", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
    }

    @Test
    public void testWord() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().word().build();

        MatcherAssert.assertThat("word", testRegex, TestMatchMatcher.matchesTo("abc123"));
        MatcherAssert.assertThat(
                "non-word", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("@#")));
    }

    @Test
    public void testMultipleNoRange() {
        VerbalExpression testRegexStringOnly =
                VerbalExpression.regex().startOfLine().multiple("abc").build();
        VerbalExpression testRegexStringAndNull =
                VerbalExpression.regex().startOfLine().multiple("abc", null).build();
        VerbalExpression testRegexMoreThan2Ints =
                VerbalExpression.regex().startOfLine().multiple("abc", 2, 4, 8).build();
        VerbalExpression[] testRegexesSameBehavior = {
                testRegexStringOnly, testRegexStringAndNull, testRegexMoreThan2Ints
        };
        for (VerbalExpression testRegex : testRegexesSameBehavior) {
            MatcherAssert.assertThat("abc once", testRegex, TestMatchMatcher.matchesTo("abc"));
            MatcherAssert.assertThat(
                    "abc more than once", testRegex, TestMatchMatcher.matchesTo("abcabcabc"));
            MatcherAssert.assertThat(
                    "no abc", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("xyz")));
        }
    }

    @Test
    public void testMultipleFrom() {
        VerbalExpression testRegexFrom =
                VerbalExpression.regex().startOfLine().multiple("abc", 2).build();
        MatcherAssert.assertThat(
                "no abc", testRegexFrom, CoreMatchers.not(TestMatchMatcher.matchesTo("xyz")));
        MatcherAssert.assertThat(
                "abc less than 2 times",
                testRegexFrom,
                CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
        MatcherAssert.assertThat(
                "abc exactly 2 times", testRegexFrom, TestMatchMatcher.matchesTo("abcabc"));
        MatcherAssert.assertThat(
                "abc more than 2 times", testRegexFrom, TestMatchMatcher.matchesTo("abcabcabc"));
    }

    @Test
    public void testMultipleFromTo() {
        VerbalExpression testRegexFromTo =
                VerbalExpression.regex().startOfLine().multiple("abc", 2, 4).build();
        MatcherAssert.assertThat(
                "no abc", testRegexFromTo, CoreMatchers.not(TestMatchMatcher.matchesTo("xyz")));
        MatcherAssert.assertThat(
                "abc less than 2 times",
                testRegexFromTo,
                CoreMatchers.not(TestMatchMatcher.matchesTo("abc")));
        MatcherAssert.assertThat(
                "abc exactly 2 times", testRegexFromTo, TestMatchMatcher.matchesTo("abcabc"));
        MatcherAssert.assertThat(
                "abc between 2 and 4 times", testRegexFromTo, TestMatchMatcher.matchesTo("abcabcabc"));
        MatcherAssert.assertThat(
                "abc exactly 4 times", testRegexFromTo, TestMatchMatcher.matchesTo("abcabcabcabc"));
        MatcherAssert.assertThat(
                "abc more than 4 times",
                testRegexFromTo,
                IsNot.not(TestsExactMatcher.matchesExactly("abcabcabcabcabc")));
    }

    @Test
    public void testWithAnyCase() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().then("a").build();

        MatcherAssert.assertThat(
                "not case insensitive", testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("A")));
        testRegex = VerbalExpression.regex().startOfLine().then("a").withAnyCase().build();

        MatcherAssert.assertThat("case insensitive", testRegex, TestMatchMatcher.matchesTo("A"));
        MatcherAssert.assertThat("case insensitive", testRegex, TestMatchMatcher.matchesTo("a"));
    }

    @Test
    public void testWithAnyCaseTurnOnThenTurnOff() {
        VerbalExpression testRegex =
                VerbalExpression.regex().withAnyCase().startOfLine().then("a").withAnyCase(false).build();

        MatcherAssert.assertThat(testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("A")));
    }

    @Test
    public void testWithAnyCaseIsFalse() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("a").withAnyCase(false).build();

        MatcherAssert.assertThat(testRegex, CoreMatchers.not(TestMatchMatcher.matchesTo("A")));
    }

    @Test
    public void testSearchOneLine() {
        VerbalExpression testRegex =
                VerbalExpression.regex().startOfLine().then("a").br().then("b").endOfLine().build();

        MatcherAssert.assertThat(
                "b is on the second line", testRegex, TestMatchMatcher.matchesTo("a\nb"));

        testRegex =
                VerbalExpression.regex()
                        .startOfLine()
                        .then("a")
                        .br()
                        .then("b")
                        .endOfLine()
                        .searchOneLine(true)
                        .build();

        MatcherAssert.assertThat(
                "b is on the second line but we are only searching the first",
                testRegex,
                TestMatchMatcher.matchesTo("a\nb"));
    }

    @Test
    public void testGetText() {
        String testString = "123 https://www.google.com 456";
        VerbalExpression testRegex =
                VerbalExpression.regex()
                        .add("http")
                        .maybe("s")
                        .then("://")
                        .then("www.")
                        .anythingBut(" ")
                        .add("com")
                        .build();
        Assertions.assertEquals(testRegex.getText(testString), "https://www.google.com");
    }

    @Test
    public void testStartCapture() {
        String text = "aaabcd";
        VerbalExpression regex =
                VerbalExpression.regex().find("a").count(3).capture().find("b").anything().build();

        MatcherAssert.assertThat(
                "regex don't match string", regex.getText(text), CoreMatchers.equalTo(text));
        MatcherAssert.assertThat(
                "can't get first captured group", regex.getText(text, 1), CoreMatchers.equalTo("bcd"));
    }

    @Test
    public void testStartNamedCapture() {
        String text = "test@example.com";
        String captureName = "domain";
        VerbalExpression regex =
                VerbalExpression.regex().find("@").capture(captureName).anything().build();

        MatcherAssert.assertThat(
                "can't get captured group named " + captureName,
                regex.getText(text, captureName),
                CoreMatchers.equalTo("example.com"));
    }

    @Test
    public void captIsSameAsCapture() {
        MatcherAssert.assertThat(
                "Capt produce different than capture regex",
                VerbalExpression.regex().capt().build().toString(),
                CoreMatchers.equalTo(VerbalExpression.regex().capture().build().toString()));
    }

    @Test
    public void namedCaptIsSameAsNamedCapture() {
        String name = "test";
        MatcherAssert.assertThat(
                "Named-capt produce different than named-capture regex",
                VerbalExpression.regex().capt(name).build().toString(),
                CoreMatchers.equalTo(VerbalExpression.regex().capture(name).build().toString()));
    }

    @Test
    public void shouldReturnEmptyStringWhenNoGroupFound() {
        String text = "abc";
        VerbalExpression regex = VerbalExpression.regex().find("d").capture().find("e").build();

        MatcherAssert.assertThat(
                "regex don't match string", regex.getText(text), CoreMatchers.equalTo(""));
        MatcherAssert.assertThat(
                "first captured group not empty string", regex.getText(text, 1), CoreMatchers.equalTo(""));
        MatcherAssert.assertThat(
                "second captured group not empty string", regex.getText(text, 2), CoreMatchers.equalTo(""));
    }

    @Test
    public void testCountWithRange() {
        String text4c = "abcccce";
        String text2c = "abcce";
        String text1c = "abce";

        VerbalExpression regex = VerbalExpression.regex().find("c").count(2, 3).build();

        MatcherAssert.assertThat(
                "regex don't match string", regex.getText(text4c), CoreMatchers.equalTo("ccc"));
        MatcherAssert.assertThat(
                "regex don't match string", regex.getText(text2c), CoreMatchers.equalTo("cc"));
        MatcherAssert.assertThat(
                "regex don't match string", regex, CoreMatchers.not(TestMatchMatcher.matchesTo(text1c)));
    }

    @Test
    public void testEndCapture() {
        String text = "aaabcd";
        VerbalExpression regex =
                VerbalExpression.regex()
                        .find("a")
                        .capture()
                        .find("b")
                        .anything()
                        .endCapture()
                        .then("cd")
                        .build();

        MatcherAssert.assertThat(regex.getText(text), CoreMatchers.equalTo("abcd"));
        MatcherAssert.assertThat(
                "can't get first captured group", regex.getText(text, 1), CoreMatchers.equalTo("b"));
    }

    @Test
    public void testEndNamedCapture() {
        String text = "aaabcd";
        String captureName = "str";
        VerbalExpression regex =
                VerbalExpression.regex()
                        .find("a")
                        .capture(captureName)
                        .find("b")
                        .anything()
                        .endCapture()
                        .then("cd")
                        .build();

        MatcherAssert.assertThat(regex.getText(text), CoreMatchers.equalTo("abcd"));
        MatcherAssert.assertThat(
                "can't get captured group named " + captureName,
                regex.getText(text, captureName),
                CoreMatchers.equalTo("b"));
    }

    @Test
    public void testMultiplyCapture() {
        String text = "aaabcd";
        VerbalExpression regex =
                VerbalExpression.regex()
                        .find("a")
                        .count(1)
                        .capture()
                        .find("b")
                        .endCapture()
                        .anything()
                        .capture()
                        .find("d")
                        .build();

        MatcherAssert.assertThat(
                "can't get first captured group", regex.getText(text, 1), CoreMatchers.equalTo("b"));
        MatcherAssert.assertThat(
                "can't get second captured group", regex.getText(text, 2), CoreMatchers.equalTo("d"));
    }

    @Test
    public void testMultiplyNamedCapture() {
        String text = "aaabcd";
        String captureName1 = "str1";
        String captureName2 = "str2";
        VerbalExpression regex =
                VerbalExpression.regex()
                        .find("a")
                        .count(1)
                        .capture(captureName1)
                        .find("b")
                        .endCapture()
                        .anything()
                        .capture(captureName2)
                        .find("d")
                        .build();

        MatcherAssert.assertThat(
                "can't get captured group named " + captureName1,
                regex.getText(text, captureName1),
                CoreMatchers.equalTo("b"));
        MatcherAssert.assertThat(
                "can't get captured group named " + captureName2,
                regex.getText(text, captureName2),
                CoreMatchers.equalTo("d"));
    }

    @Test
    public void testOrWithCapture() {
        VerbalExpression testRegex = VerbalExpression.regex().capture().find("abc").or("def").build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
        MatcherAssert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("null"));
        MatcherAssert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void testOrWithNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex =
                VerbalExpression.regex().capture(captureName).find("abc").or("def").build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(
                testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
        MatcherAssert.assertThat(
                testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("null"));
        MatcherAssert.assertThat(
                testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void testOrWithClosedCapture() {
        VerbalExpression testRegex =
                VerbalExpression.regex().capture().find("abc").endCapt().or("def").build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
        MatcherAssert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("null"));
        MatcherAssert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void testOrWithClosedNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex =
                VerbalExpression.regex().capture(captureName).find("abc").endCapt().or("def").build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(
                testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
        MatcherAssert.assertThat(
                testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("null"));
        MatcherAssert.assertThat(
                testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcnull"));
    }

    @Test
    public void addRegexBuilderWrapsItWithUnsavedGroup() throws Exception {
        VerbalExpression regex =
                VerbalExpression.regex()
                        .add(VerbalExpression.regex().capt().find("string").count(2).endCapt().count(1).digit())
                        .count(2)
                        .build();

        String example = "stringstring1";
        String example2digit = "stringstring11";

        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(example + example));
        MatcherAssert.assertThat(regex, IsNot.not(TestsExactMatcher.matchesExactly(example2digit)));
    }

    @Test
    public void atLeast1HaveSameEffectAsOneOrMore() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().find("a").atLeast(1).build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(matched));
        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        MatcherAssert.assertThat(regex, IsNot.not(TestsExactMatcher.matchesExactly(oneMatched)));
        MatcherAssert.assertThat(regex, TestMatchMatcher.matchesTo(oneMatched));
        MatcherAssert.assertThat(regex, CoreMatchers.not(TestMatchMatcher.matchesTo(empty)));
    }

    @Test
    public void oneOreMoreSameAsAtLeast1() throws Exception {
        VerbalExpression regexWithOneOrMore = VerbalExpression.regex().find("a").oneOrMore().build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        MatcherAssert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(matched));
        MatcherAssert.assertThat(
                regexWithOneOrMore, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        MatcherAssert.assertThat(
                regexWithOneOrMore, IsNot.not(TestsExactMatcher.matchesExactly(oneMatched)));
        MatcherAssert.assertThat(regexWithOneOrMore, TestMatchMatcher.matchesTo(oneMatched));
        MatcherAssert.assertThat(
                regexWithOneOrMore, CoreMatchers.not(TestMatchMatcher.matchesTo(empty)));
    }

    @Test
    public void atLeast0HaveSameEffectAsZeroOrMore() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().find("a").atLeast(0).build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(matched));
        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        MatcherAssert.assertThat(regex, IsNot.not(TestsExactMatcher.matchesExactly(oneMatched)));
        MatcherAssert.assertThat(regex, TestMatchMatcher.matchesTo(empty));
        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(empty));
    }

    @Test
    public void zeroOreMoreSameAsAtLeast0() throws Exception {
        VerbalExpression regexWithOneOrMore = VerbalExpression.regex().find("a").zeroOrMore().build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        MatcherAssert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(matched));
        MatcherAssert.assertThat(
                regexWithOneOrMore, TestsExactMatcher.matchesExactly(oneMatchedExactly));
        MatcherAssert.assertThat(
                regexWithOneOrMore, IsNot.not(TestsExactMatcher.matchesExactly(oneMatched)));
        MatcherAssert.assertThat(regexWithOneOrMore, TestMatchMatcher.matchesTo(oneMatched));
        MatcherAssert.assertThat(regexWithOneOrMore, TestMatchMatcher.matchesTo(empty));
        MatcherAssert.assertThat(regexWithOneOrMore, TestsExactMatcher.matchesExactly(empty));
    }

    @Test
    public void testOneOf() {
        VerbalExpression testRegex = VerbalExpression.regex().startOfLine().oneOf("abc", "def").build();

        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc nor def",
                testRegex,
                CoreMatchers.not(TestMatchMatcher.matchesTo("xyzabc")));
    }

    @Test
    public void testOneOfWithCapture() {
        VerbalExpression testRegex = VerbalExpression.regex().capture().oneOf("abc", "def").build();

        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcdef"));
        MatcherAssert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("def"));
    }

    @Test
    public void testOneOfWithNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex =
                VerbalExpression.regex().capture(captureName).oneOf("abc", "def").build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(
                testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcdef"));
        MatcherAssert.assertThat(
                testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("def"));
    }

    @Test
    public void testOneOfWithClosedCapture() {
        VerbalExpression testRegex =
                VerbalExpression.regex().capture().oneOf("abc", "def").endCapt().build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(testRegex.getText("xxxabcdefzzz", 1), CoreMatchers.equalTo("abcdef"));
        MatcherAssert.assertThat(testRegex.getText("xxxdefzzz", 1), CoreMatchers.equalTo("def"));
    }

    @Test
    public void testOneOfWithClosedNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex =
                VerbalExpression.regex().capture(captureName).oneOf("abc", "def").endCapt().build();
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("defzzz"));
        MatcherAssert.assertThat(
                "Starts with abc or def", testRegex, TestMatchMatcher.matchesTo("abczzz"));
        MatcherAssert.assertThat(
                "Doesn't start with abc or def",
                testRegex,
                IsNot.not(TestsExactMatcher.matchesExactly("xyzabcefg")));

        MatcherAssert.assertThat(
                testRegex.getText("xxxabcdefzzz", captureName), CoreMatchers.equalTo("abcdef"));
        MatcherAssert.assertThat(
                testRegex.getText("xxxdefzzz", captureName), CoreMatchers.equalTo("def"));
    }

    @Test
    public void shouldAddMaybeWithOneOfFromAnotherBuilder() {
        VerbalExpression.Builder namePrefix = VerbalExpression.regex().oneOf("Mr.", "Ms.");
        VerbalExpression name =
                VerbalExpression.regex().maybe(namePrefix).space().zeroOrMore().word().oneOrMore().build();

        MatcherAssert.assertThat("Is a name with prefix", name, TestMatchMatcher.matchesTo("Mr. Bond"));
        MatcherAssert.assertThat("Is a name without prefix", name, TestMatchMatcher.matchesTo("James"));
    }

    @Test
    public void testListOfTextGroups() {
        String text = "SampleHelloWorldString";
        VerbalExpression regex =
                VerbalExpression.regex().capt().oneOf("Hello", "World").endCapt().maybe("String").build();

        List<String> groups0 = regex.getTextGroups(text, 0);

        MatcherAssert.assertThat(groups0.get(0), CoreMatchers.equalTo("Hello"));
        MatcherAssert.assertThat(groups0.get(1), CoreMatchers.equalTo("WorldString"));

        List<String> groups1 = regex.getTextGroups(text, 1);

        MatcherAssert.assertThat(groups1.get(0), CoreMatchers.equalTo("Hello"));
        MatcherAssert.assertThat(groups1.get(1), CoreMatchers.equalTo("World"));
    }

    @Test
    public void testWordBoundary() {
        VerbalExpression regex =
                VerbalExpression.regex()
                        .capture()
                        .wordBoundary()
                        .then("o")
                        .word()
                        .oneOrMore()
                        .wordBoundary()
                        .endCapture()
                        .build();

        MatcherAssert.assertThat(regex.getText("apple orange grape", 1), CoreMatchers.is("orange"));
        MatcherAssert.assertThat(regex.test("appleorange grape"), CoreMatchers.is(false));
        MatcherAssert.assertThat(regex.test("apple3orange grape"), CoreMatchers.is(false));
    }
}
