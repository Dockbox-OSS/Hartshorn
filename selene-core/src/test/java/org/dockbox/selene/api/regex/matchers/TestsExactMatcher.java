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

package org.dockbox.selene.api.regex.matchers;

import org.dockbox.selene.api.VerbalExpression;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

/** User: lanwen Date: 29.05.14 Time: 20:06 */
public final class TestsExactMatcher extends TypeSafeMatcher<VerbalExpression> {

    private final String toTest;

    private TestsExactMatcher(String toTest) {
        this.toTest = toTest;
    }

    @Factory
    public static TestsExactMatcher matchesExactly(String test) {
        return new TestsExactMatcher(test);
    }

    @Override
    public boolean matchesSafely(VerbalExpression verbalExpression) {
        return verbalExpression.testExact(this.toTest);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("regex should match exactly to ").appendValue(this.toTest);
    }
}
