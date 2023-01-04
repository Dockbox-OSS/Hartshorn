/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.web.mvc.freemarker;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.web.mvc.ClassPathViewTemplate;
import org.dockbox.hartshorn.web.mvc.FileViewTemplate;
import org.dockbox.hartshorn.web.mvc.MVCInitializer;
import org.dockbox.hartshorn.web.mvc.StringViewTemplate;
import org.dockbox.hartshorn.web.mvc.ViewModel;
import org.dockbox.hartshorn.web.mvc.ViewTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import jakarta.inject.Singleton;

@Singleton
public class FreeMarkerMVCInitializer implements MVCInitializer {

    private Configuration configuration;

    @Override
    public final void initialize(final ApplicationContext applicationContext) throws ApplicationException {
        if (this.configuration == null) {
            this.configuration = this.configuration(applicationContext);
        }
        else {
            throw new ApplicationException("FreeMarker configuration was already initialized!");
        }
    }

    @Override
    public String transform(final ViewTemplate template, final ViewModel model) throws ApplicationException {
        final String name = template.getClass().getSimpleName() + System.nanoTime();
        final Template freeMarkerTemplate = this.transformTemplate(name, template);
        try {
            final Writer writer = new StringWriter();
            freeMarkerTemplate.process(model.attributes(), writer);
            return writer.toString();
        }
        catch (final TemplateException | IOException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public Set<ViewTemplate> templates() {
        return null;
    }

    protected Template transformTemplate(final String name, final ViewTemplate template) throws ApplicationException {
        try {
            if (template instanceof StringViewTemplate stringView) {
                return new Template(name, stringView.template(), this.configuration);
            }
            else if (template instanceof FileViewTemplate fileView) {
                final String content = this.contentOrEmpty(fileView.path());
                return new Template(name, content, this.configuration);
            }
            else if (template instanceof ClassPathViewTemplate classPathView) {
                return this.configuration.getTemplate(classPathView.location());
            }
            else {
                throw new ApplicationException("Unsupported view template type: " + template.getClass().getName());
            }
        } catch (final IOException e) {
            throw new ApplicationException(e);
        }
    }

    private String contentOrEmpty(final Path path) {
        try {
            return Files.readString(path);
        }
        catch (final IOException ignored) {
            return "";
        }
    }

    protected Configuration configuration(final ApplicationContext applicationContext) throws ApplicationException {
        try {
            final Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setClassForTemplateLoading(Hartshorn.class, this.webRoot());
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setTemplateExceptionHandler(this.exceptionHandler(applicationContext));
            configuration.setLogTemplateExceptions(this.logExceptions());
            configuration.setWrapUncheckedExceptions(false);
            configuration.setFallbackOnNullLoopVariable(false);
            configuration.setSharedVariable("applicationContext", applicationContext);

            return configuration;
        }
        catch (final TemplateModelException e) {
            throw new ApplicationException(e);
        }
    }

    protected String webRoot() {
        return MVCInitializer.TEMPLATE_ROOT;
    }

    protected boolean logExceptions() {
        return true;
    }

    protected TemplateExceptionHandler exceptionHandler(final ApplicationContext applicationContext) {
        if (applicationContext.environment().isCI()) return TemplateExceptionHandler.RETHROW_HANDLER;
        else return TemplateExceptionHandler.HTML_DEBUG_HANDLER;
    }
}
