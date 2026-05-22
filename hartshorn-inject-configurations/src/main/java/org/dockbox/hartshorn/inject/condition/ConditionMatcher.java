/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject.condition;

import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentProviderObjectFactoryAdapter;
import org.dockbox.hartshorn.inject.InjectionApplicationAwareContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.ObjectFactory;
import org.dockbox.hartshorn.inject.ReflectionObjectFactory;
import org.dockbox.hartshorn.util.collections.LFUCache;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.EnclosableView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.ClassFileUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.classfile.Annotation;
import java.lang.classfile.AttributedElement;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A matcher that can be used to match {@link RequiresCondition} annotations against a given set of
 * contexts. This matcher will use the {@link InjectionCapableApplication} to resolve the
 * {@link Condition} instances that are referenced by the {@link RequiresCondition} annotations.
 *
 * <p>Condition matching can be used for a variety of purposes. For example, it can be used to
 * determine whether a component should be registered, a binding method should be invoked, or an
 * event should be dispatched.
 *
 * @see RequiresCondition
 * @see Condition
 * @see ConditionContext
 * @see ConditionResult
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public final class ConditionMatcher extends DefaultContext
    implements InjectionApplicationAwareContext {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionMatcher.class);

    private record ConditionCacheKey(ConditionContext context, ConditionDeclaration declaration) {}

    private final LFUCache<ConditionCacheKey, ConditionResult> conditionResultCache =
            new LFUCache<>(32);
    private final Supplier<InjectionCapableApplication> applicationSupplier;
    private boolean includeEnclosingConditions = true;

    public ConditionMatcher(Supplier<InjectionCapableApplication> applicationSupplier) {
        this.applicationSupplier = applicationSupplier;
    }

    /**
     * Determines whether enclosing conditions should be included when matching conditions. If this
     * method returns {@code true}, the matcher will also match any conditions that are declared on
     * enclosing elements of the given element. For example, if the given element is a method, this
     * method will also match any conditions that are declared on the class that declares the
     * method.
     *
     * @return {@code true} if enclosing conditions should be included, {@code false} otherwise
     */
    public boolean includeEnclosingConditions() {
        return this.includeEnclosingConditions;
    }

    /**
     * Sets whether enclosing conditions should be included when matching conditions. If this method
     * is called with {@code true}, the matcher will also match any conditions that are declared on
     * enclosing elements of the given element. For example, if the given element is a method, this
     * method will also match any conditions that are declared on the class that declares the
     * method.
     *
     * @param includeEnclosingConditions {@code true} if enclosing conditions should be included,
     * {@code false} otherwise
     *
     * @return this matcher
     */
    public ConditionMatcher includeEnclosingConditions(boolean includeEnclosingConditions) {
        this.includeEnclosingConditions = includeEnclosingConditions;
        return this;
    }

    /**
     * Matches the {@link RequiresCondition} and {@link RequiresReferenceCondition} annotations of
     * the given {@link AnnotatedElementView}, providing any additional {@link ContextView}
     * instances to the {@link ConditionContext} that is used to match the
     * {@link Condition condition implementations}. If any of the conditions does not match, this
     * method will return {@code false}. If all conditions match, this method will return
     * {@code true}.
     *
     * <p>If enabled, this method will also match any enclosing conditions of the given element.
     * For
     * example, if the given element is a method, this method will also match any conditions that
     * are declared on the class that declares the method.
     *
     * <p>Note that when matching enclosing conditions, the order of evaluation is from the least
     * enclosed element to the most enclosed element (the given element). This means that
     * class-level conditions are evaluated before method-level conditions.
     *
     * <p>{@link ReferenceCondition reference conditions} are also matched for any reference
     * views encountered during the matching process. Note that this may cause issues with class
     * loading if the references point to classes that are not available at runtime. In such cases,
     * {@link #match(AttributedElement, ContextView...)} should be used directly to avoid class
     * loading.
     *
     * @param annotatedElementContext the annotated element to match against
     * @param contexts the additional contexts to provide to the condition context
     *
     * @return {@code true} if all conditions match, {@code false} otherwise
     */
    public ConditionResult match(
            AnnotatedElementView annotatedElementContext,
            ContextView... contexts
    ) {
        SequencedCollection<AnnotatedElementView> views = this.includeEnclosingConditions()
            ? this.collectEnclosedViews(annotatedElementContext)
            : List.of(annotatedElementContext);

        if (views.isEmpty()) {
            throw new IllegalStateException("No views found for element "
                + annotatedElementContext);
        }

        for (AnnotatedElementView elementView : views) {
            Set<ConditionDeclaration> declarations = elementView.annotations()
                .all(RequiresCondition.class)
                .stream()
                .map(AnnotationConditionDeclaration::new)
                .collect(Collectors.toSet());

            ConditionResult result = this.matchAnnotatedElement(
                    elementView,
                    declarations,
                    contexts
            );
            if (!result.matches()) {
                return result;
            }

            result = elementView.classFileElement()
                    .ofType(AttributedElement.class)
                    .map(element -> this.match(element, contexts))
                    .orElseGet(ConditionResult::matched);
            if (!result.matches()) {
                return result;
            }
        }
        return ConditionResult.matched();
    }

    /**
     * Matches the {@link RequiresReferenceCondition} annotations of the given
     * {@link AttributedElement}, providing any additional {@link ContextView} instances to the
     * {@link ConditionContext} that is used to match the
     * {@link Condition condition implementations}. If any of the conditions do not match, this
     * method will return {@code false}. If all conditions match, this method will return
     * {@code true}.
     *
     * @param element the attributed element to match against
     * @param contexts the additional contexts to provide to the condition context
     * @return {@code true} if all conditions match, {@code false} otherwise
     */
    public ConditionResult match(AttributedElement element, ContextView... contexts) {
        List<Annotation> annotations = ClassFileUtilities.getMetaAnnotations(
                element,
                RequiresReferenceCondition.class
        );
        for (Annotation annotation : annotations) {
            var declaration = ReferenceConditionDeclaration.createFromMetaAnnotation(annotation);
            ReferenceConditionContext conditionContext =
                    new ReferenceConditionContext(
                            declaration,
                            element
                    );
            ConditionResult result = this.matchConditionContext(
                    conditionContext,
                    declaration,
                    contexts
            );
            if (!result.matches()) {
                return result;
            }
        }
        return ConditionResult.matched();
    }

    /**
     * Matches the given {@link ConditionDeclaration condition declarations} against the given
     * {@link AnnotatedElementView}, providing any additional {@link ContextView} instances to the
     * {@link ConditionContext} that is used to match the
     * {@link Condition condition implementations}. If any of the conditions does not match, this
     * method will return {@code false}. If all conditions match, this method will return
     * {@code true}.
     *
     * @param annotatedElementContext the annotated element to match against
     * @param declarationContexts the {@link ConditionDeclaration declarations of the conditions} to
     * match
     * @param contexts the additional contexts to provide to the condition context
     *
     * @return {@code true} if all conditions match, {@code false} otherwise
     */
    private ConditionResult matchAnnotatedElement(
        AnnotatedElementView annotatedElementContext,
        Set<ConditionDeclaration> declarationContexts,
        ContextView... contexts
    ) {
        for (ConditionDeclaration declarationContext : declarationContexts) {
            ConditionContext context = new IntrospectedConditionContext(
                this.application(),
                annotatedElementContext,
                declarationContext
            );
            ConditionResult result = this.matchConditionContext(
                    context,
                    declarationContext,
                    contexts
            );
            if (!result.matches()) {
                return result;
            }
        }
        return ConditionResult.matched();
    }

    /**
     * Matches the given {@link ConditionDeclaration} against the given
     * {@link AnnotatedElementView}, providing any additional {@link ContextView} instances to the
     * {@link ConditionContext} that is used to match the
     * {@link Condition condition implementations}. If the condition does not match, this method
     * will return {@code false}. If the condition matches, this method will return {@code true}.
     *
     * @param context the condition context to use for matching
     * @param declarationContext the {@link ConditionDeclaration declaration of the condition} to
     * match
     * @param contexts the additional contexts to provide to the condition context
     *
     * @return {@code true} if the condition matches, {@code false} otherwise
     */
    private ConditionResult matchConditionContext(
        ConditionContext context,
        ConditionDeclaration declarationContext,
        ContextView... contexts
    ) {
        ConditionCacheKey cacheKey = new ConditionCacheKey(context, declarationContext);
        final ConditionResult result;
        if (this.conditionResultCache.containsKey(cacheKey)) {
            result = this.conditionResultCache.get(cacheKey);
        } else {
            InjectionCapableApplication application = this.application();
            // Application may still be starting, thus fallback should be used.
            final ObjectFactory objectFactory;
            if (application == null) {
                assert context instanceof ReferenceConditionContext : """
                Expected type reference condition context when application is starting.
                Introspection-capable condition contexts require at least a partially
                initialized application.
                """;
                objectFactory = new ReflectionObjectFactory();
            }
            else {
                objectFactory = new ComponentProviderObjectFactoryAdapter(
                        application.defaultProvider()
                );
            }

            Condition condition = declarationContext.condition(objectFactory);
            for (ContextView child : contexts) {
                context.addContext(child);
            }
            result = condition.matches(context);

            if (LOG.isDebugEnabled()) {
                String location = switch (context) {
                    case ReferenceConditionContext referenceConditionContext ->
                            referenceConditionContext.element().toString();
                    case IntrospectedConditionContext introspectedConditionContext ->
                            introspectedConditionContext.annotatedElement().qualifiedName();
                    default -> "unknown context";
                };
                LOG.debug("Matched condition {} with context {}: {} ({})",
                        condition.getClass().getSimpleName(),
                        location, result.matches(), result.message());
            }
            if (declarationContext.cacheable()) {
                this.conditionResultCache.put(cacheKey, result);
            }
        }

        if (!result.matches() && declarationContext.failOnNoMatch()) {
            throw new ConditionFailedException(declarationContext, result);
        }
        return result;
    }

    /**
     * Collects all {@link AnnotatedElementView annotated element views} that are enclosed by the
     * given {@link AnnotatedElementView}. The given element is included in the result, as well as
     * all elements that are enclosed by the given element. The result is ordered from the least
     * enclosed element to the most enclosed element (the given element).
     *
     * @param element the element to collect enclosed views for
     *
     * @return a collection of enclosed views, ordered from least to most enclosed
     */
    private SequencedCollection<AnnotatedElementView> collectEnclosedViews(
        AnnotatedElementView element
    ) {
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
    public InjectionCapableApplication application() {
        return this.applicationSupplier.get();
    }
}
