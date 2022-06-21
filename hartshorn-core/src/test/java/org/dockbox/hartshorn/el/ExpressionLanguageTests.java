/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.el;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.el.expression.Expression;
import org.dockbox.hartshorn.el.expression.IllegalExpressionException;
import org.dockbox.hartshorn.el.parser.ExpressionParser;
import org.dockbox.hartshorn.el.parser.SimpleExpressionParser;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@HartshornTest
public class ExpressionLanguageTests {

    @Inject
    private ApplicationContext applicationContext;
    private SimpleExternalContext externalContext = new SimpleExternalContext();

    @BeforeEach
    public void setUp(){
        this.externalContext.addParameter("a", 3d);
        this.externalContext.addParameter("b", 2.50d);
        this.externalContext.addParameter("c", -4.00d);
        this.externalContext.addParameter("context", this.applicationContext);
    }

    @Test
    public void testParse() throws IllegalExpressionException {
        final String expressionToParse1 = "#{((a<14.0) or (b>2.43)) and (c==-4.00)}";
        final ExpressionParser parser1 = new SimpleExpressionParser(this.externalContext);
        final Expression<Boolean> expression1 = parser1.parse(expressionToParse1);
        Assertions.assertTrue(expression1.getEvaluationResult());

        final String expressionToParse2 = "#{(a>67.0) or (b<1.2)}";
        final ExpressionParser parser2 = new SimpleExpressionParser(this.externalContext);
        final Expression<Boolean> expression2 = parser2.parse(expressionToParse2);
        Assertions.assertFalse(expression2.getEvaluationResult());

        final String expressionToParse3 = "#{context not null}";
        final ExpressionParser parser3 = new SimpleExpressionParser(this.externalContext);
        final Expression<Boolean> expression3 = parser3.parse(expressionToParse3);
        Assertions.assertTrue(expression3.getEvaluationResult());
    }

}
