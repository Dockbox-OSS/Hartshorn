/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.component.condition;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.EnclosableView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A matcher that can be used to match {@link RequiresCondition} annotations against a given set of contexts.
 * This matcher will use the {@link ApplicationContext} to resolve the {@link Condition} instances that are
 * referenced by the {@link RequiresCondition} annotations.
 *
 * <p>Condition matching can be used for a variety of purposes. For example, it can be used to determine whether
 * a component should be registered, a binding method should be invoked, or an event should be dispatched.
 *
 * @author Guus Lieben
 * @see RequiresCondition
 * @see Condition
 * @see ConditionContext
 * @see ConditionResult
 * @since 0.5.0
 */
public final class ConditionMatcher implements ContextCarrier {

    private final ApplicationContext applicationContext;
    private boolean includeEnclosingConditions = true;

    /**
     * @param applicationContext the application context, used to resolve {@link Condition} instances
     */
    public ConditionMatcher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean includeEnclosingConditions() {
        return this.includeEnclosingConditions;
    }

    public ConditionMatcher includeEnclosingConditions(boolean includeEnclosingConditions) {
        this.includeEnclosingConditions = includeEnclosingConditions;
        return this;
    }

    /**
     * Matches the {@link RequiresCondition} annotations of the given {@link AnnotatedElementView}, providing
     * any additional {@link ContextView} instances to the {@link ConditionContext} that is used to match the
     * {@link Condition condition implementations}. If any of the conditions does not match, this method will
     * return {@code false}. If all conditions match, this method will return {@code true}.
     *
     * <p>If enabled, this method will also match any enclosing conditions of the given element. For example,
     * if the given element is a method, this method will also match any conditions that are declared on the
     * class that declares the method.
     *
     * @param annotatedElementContext the annotated element to match against
     * @param contexts                the additional contexts to provide to the condition context
     * @return {@code true} if all conditions match, {@code false} otherwise
     */
    public boolean match(AnnotatedElementView annotatedElementContext, ContextView... contexts) {
        SequencedCollection<AnnotatedElementView> views = this.includeEnclosingConditions()
                ? this.collectEnclosedViews(annotatedElementContext)
                : List.of(annotatedElementContext);

        if(views.isEmpty()) {
            throw new IllegalStateException("No views found for element " + annotatedElementContext);
        }

        for(AnnotatedElementView elementView : views) {
            Set<ConditionDeclaration> declarations = elementView.annotations()
                    .all(RequiresCondition.class)
                    .stream()
                    .map(AnnotationConditionDeclaration::new)
                    .collect(Collectors.toSet());

            if (!match(elementView, declarations, contexts)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Matches the given {@link ConditionDeclaration condition declarations} against the given
     * {@link AnnotatedElementView}, providing any additional {@link ContextView} instances to the
     * {@link ConditionContext} that is used to match the {@link Condition condition implementations}.
     * If any of the conditions does not match, this method will return {@code false}. If all
     * conditions match, this method will return {@code true}.
     *
     * @param annotatedElementContext the annotated element to match against
     * @param declarationContexts     the {@link ConditionDeclaration declarations of the conditions} to match
     * @param contexts                the additional contexts to provide to the condition context
     * @return {@code true} if all conditions match, {@code false} otherwise
     */
    public boolean match(AnnotatedElementView annotatedElementContext, Set<ConditionDeclaration> declarationContexts, ContextView... contexts) {
        for(ConditionDeclaration declarationContext : declarationContexts) {
            if(!this.match(annotatedElementContext, declarationContext, contexts)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Matches the given {@link ConditionDeclaration} against the given {@link AnnotatedElementView}, providing any
     * additional {@link ContextView} instances to the {@link ConditionContext} that is used to match the {@link Condition
     * condition implementations}. If the condition does not match, this method will return {@code false}. If the
     * condition matches, this method will return {@code true}.
     *
     * @param element            the annotated element to match against
     * @param declarationContext the {@link ConditionDeclaration declaration of the condition} to match
     * @param contexts           the additional contexts to provide to the condition context
     * @return {@code true} if the condition matches, {@code false} otherwise
     */
    public boolean match(AnnotatedElementView element, ConditionDeclaration declarationContext, ContextView... contexts) {
        Condition condition = declarationContext.condition(this.applicationContext());
        ConditionContext context = new ConditionContext(this.applicationContext, element, declarationContext);
            context.add(child);
        for(ContextView child : contexts) {
        }
        ConditionResult result = condition.matches(context);
        if(!result.matches() && declarationContext.failOnNoMatch()) {
            throw new ConditionFailedException(condition, result);
        }
        return result.matches();
    }

    /**
     * Collects all {@link AnnotatedElementView annotated element views} that are enclosed by the given
     * {@link AnnotatedElementView}. The given element is included in the result, as well as all elements
     * that are enclosed by the given element. The result is ordered from the least enclosed element to
     * the most enclosed element (the given element).
     *
     * @param element the element to collect enclosed views for
     * @return a collection of enclosed views, ordered from least to most enclosed
     */
    private SequencedCollection<AnnotatedElementView> collectEnclosedViews(AnnotatedElementView element) {
        List<AnnotatedElementView> elements = new LinkedList<>();
        elements.add(element);

        Queue<EnclosableView> enclosableViews = new ArrayDeque<>();
        enclosableViews.add(element);

        while (!enclosableViews.isEmpty()) {
            EnclosableView view = enclosableViews.poll();
            Option<EnclosableView> enclosingView = view.enclosingView();

            if (enclosingView.present()) {
                EnclosableView enclosableView = enclosingView.get();
                enclosableViews.add(enclosableView);

                if (enclosableView instanceof AnnotatedElementView annotatedElementView) {
                    elements.addFirst(annotatedElementView);
                }
            }
        }

        return elements;
    }

    @Override
    public ApplicationContext applicationContext() {
        return applicationContext;
    }
}
