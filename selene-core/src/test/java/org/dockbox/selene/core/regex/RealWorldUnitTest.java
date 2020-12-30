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
import org.dockbox.selene.core.regex.matchers.TestsExactMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class RealWorldUnitTest {

    @Test
    public void testUrl() {
        VerbalExpression testRegex = VerbalExpression.regex()
                .startOfLine()
                .then("http")
                .maybe("s")
                .then("://")
                .maybe("www.")
                .anythingBut(" ")
                .endOfLine()
                .build();

        // Create an example URL
        String testUrl = "https://www.google.com";
        MatcherAssert.assertThat("Matches Google's url", testRegex, TestMatchMatcher.matchesTo(testUrl)); //True

        MatcherAssert.assertThat("Regex doesn't match same regex as in example",
                testRegex.toString(),
                CoreMatchers.equalTo("^(?:http)(?:s)?(?:\\:\\/\\/)(?:www\\.)?(?:[^\\ ]*)$"));
    }

    @Test
    public void testTelephoneNumber() {
        VerbalExpression regex = VerbalExpression.regex()
                .startOfLine()
                .then("+")
                .capture().range("0", "9").count(3).maybe("-").maybe(" ").endCapture()
                .count(3)
                .endOfLine().build();

        String phoneWithSpace = "+097 234 243";
        String phoneWithoutSpace = "+097234243";
        String phoneWithDash = "+097-234-243";

        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(phoneWithSpace));
        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(phoneWithoutSpace));
        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(phoneWithDash));

    }

    @Test
    public void complexPatternWithMultiplyCaptures() {
        String logLine = "3\t4\t1\thttp://localhost:20001\t1\t63528800\t0\t63528800\t1000000000\t0\t63528800\tSTR1";

        VerbalExpression regex = VerbalExpression.regex()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().find("http://localhost:20").digit().count(3).endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().find("STR").range("0", "2").count(1).endCapture().build();

        MatcherAssert.assertThat(regex, TestsExactMatcher.matchesExactly(logLine));

        VerbalExpression.Builder digits = VerbalExpression.regex().capt().digit().oneOrMore().endCapt().tab();
        VerbalExpression.Builder range = VerbalExpression.regex().capt().range("0", "1").count(1).endCapt().tab();
        VerbalExpression.Builder host = VerbalExpression.regex().capt().find("http://localhost:20").digit().count(3).endCapt().tab();
        VerbalExpression.Builder fake = VerbalExpression.regex().capt().find("STR").range("0", "2").count(1);

        VerbalExpression regex2 = VerbalExpression.regex()
                .add(digits).add(digits)
                .add(range).add(host).add(range).add(digits).add(range)
                .add(digits).add(digits)
                .add(range).add(digits).add(fake).build();

        MatcherAssert.assertThat(regex2, TestsExactMatcher.matchesExactly(logLine));

        //(\\d+)\\t(\\d+)\\t([0-1]{1})\\t(http://localhost:20\\d{3})\\t([0-1]{1})
        // \\t(\\d+)\\t([0-1]{1})\\t(\\d+)\\t(\\d+)\\t([0-1]{1})\\t(\\d+)\\t(FAKE[1-2]{1})
        /*
        3    4    1    http://localhost:20001    1    28800    0    528800    1000000000    0    528800    STR1
        3    5    1    http://localhost:20002    1    28800    0    528800    1000020002    0    528800    STR2
        4    6    0    http://localhost:20002    1    48800    0    528800    1000000000    0    528800    STR1
        4    7    0    http://localhost:20003    1    48800    0    528800    1000020003    0    528800    STR2
        5    8    1    http://localhost:20003    1    68800    0    528800    1000000000    0    528800    STR1
        5    9    1    http://localhost:20004    1    28800    0    528800    1000020004    0    528800    STR2
         */
    }

    @Test
    public void unusualRegex() {
        MatcherAssert.assertThat(VerbalExpression.regex().add("[A-Z0-1!-|]").build().toString(), CoreMatchers.equalTo("[A-Z0-1!-|]"));

    }

    @Test
    public void oneOfShouldFindEpisodeTitleOfStarWarsMovies() {
        VerbalExpression regex = VerbalExpression.regex()
                .find("Star Wars: ")
                .oneOf("The Phantom Menace", "Attack of the Clones", "Revenge of the Sith",
                        "The Force Awakens", "A New Hope", "The Empire Strikes Back", "Return of the Jedi")
                .build();
        MatcherAssert.assertThat(regex, TestMatchMatcher.matchesTo("Star Wars: The Empire Strikes Back"));
        MatcherAssert.assertThat(regex, TestMatchMatcher.matchesTo("Star Wars: Return of the Jedi"));
    }

    @Test
    public void captureAfterNewLineHasGroupNumberOne() {

        final String lineBreak = "\n";
        final String some = "some";
        final String text = " text";
        final VerbalExpression expression = VerbalExpression.regex().
                        lineBreak()
                        .capture().find(some).endCapture().then(text)
                        .build();

        MatcherAssert.assertThat(some, CoreMatchers.equalTo(expression.getText(lineBreak + some + text, 1)));
    }

    @Test
    public void captureAfterNewLineHasANamedGroup() {

        final String lineBreak = "\n";
        final String some = "some";
        final String text = " text";
        final String captureName = "name";
        final VerbalExpression expression = VerbalExpression.regex().
                lineBreak()
                .capture(captureName).find(some).endCapture().then(text)
                .build();

        MatcherAssert.assertThat(some,
                CoreMatchers.equalTo(expression.getText(lineBreak + some + text, captureName)));
    }
}
