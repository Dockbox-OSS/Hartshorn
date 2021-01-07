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

package org.dockbox.selene.core.regex.matchers;

import org.dockbox.selene.core.VerbalExpression;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

//import org.hamcrest.TypeSafeMatcher;

/**
 User: lanwen
 Date: 29.05.14
 Time: 20:06
 */
public final class TestMatchMatcher extends TypeSafeMatcher<VerbalExpression> {

    private final String toTest;

    private TestMatchMatcher(String toTest) {
        this.toTest = toTest;
    }

    @Override
    public boolean matchesSafely(VerbalExpression verbalExpression) {
        return verbalExpression.test(this.toTest);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("regex should match to ").appendValue(this.toTest);
    }

    @Factory
    public static TestMatchMatcher matchesTo(String test) {
        return new TestMatchMatcher(test);
    }
}
