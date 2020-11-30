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

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: lanwen
 * Date: 11.05.14
 * Time: 3:30
 */
public class UsageLibTest {


    @Test
    public void staticFabricsRetunSameAsConstructorExpressions() {
        VerbalExpression regexViaFactory = VerbalExpression.regex().anything().build();
        VerbalExpression regexViaConstructor = VerbalExpression.regex().anything().build();

        Assert.assertThat("Factory builder method produce not same as constructor regex",
                regexViaFactory.toString(), CoreMatchers.equalTo(regexViaConstructor.toString()));
    }

    @Test
    public void clonedBuilderEqualsOriginal() {
        VerbalExpression.Builder builder = VerbalExpression.regex().anything().addModifier('i');
        VerbalExpression.Builder clonedBuilder = VerbalExpression.regex(builder);

        Assert.assertThat("Cloned builder changed after creating new one",
                builder.build().toString(), CoreMatchers.equalTo(clonedBuilder.build().toString()));
    }

    @Test
    public void clonedBuilderCantChangeOriginal() {
        VerbalExpression.Builder builder = VerbalExpression.regex().anything().addModifier('i');
        VerbalExpression.Builder clonedBuilder = VerbalExpression.regex(builder).endOfLine();

        Assert.assertThat("Cloned builder changed after creating new one",
                builder.build().toString(), IsNot.not(clonedBuilder.build().toString()));
    }

}
