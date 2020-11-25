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

package org.dockbox.selene.core.util.regex.matchers;

import org.hamcrest.CoreMatchers;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.dockbox.selene.core.util.regex.VerbalExpression;

/**
 * User: lanwen
 * Date: 29.05.14
 * Time: 22:59
 */
public final class EqualToRegexMatcher {
    private EqualToRegexMatcher() {
    }

    public static Matcher<VerbalExpression> equalToRegex(final VerbalExpression.Builder builder) {
        return new FeatureMatcher<VerbalExpression, String>(CoreMatchers.equalTo(builder.build().toString()), "regex", "") {
            @Override
            protected String featureValueOf(VerbalExpression verbalExpression) {
                return verbalExpression.toString();
            }
        };
    }
}
