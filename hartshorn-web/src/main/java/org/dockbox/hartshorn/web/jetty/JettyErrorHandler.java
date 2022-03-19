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

package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.data.annotations.Value;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.web.RequestError;
import org.dockbox.hartshorn.web.RequestErrorImpl;
import org.dockbox.hartshorn.web.servlet.ErrorServlet;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.io.ByteBufferOutputStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JettyErrorHandler extends ErrorHandler {

    @Inject
    private ApplicationContext context;
    @Inject
    private ErrorServlet errorServlet;

    @Value(value = "hartshorn.web.headers.hartshorn")
    private boolean addHeader = true;

    @Override
    protected void generateAcceptableResponse(final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response, final int code, String message, final String contentType)
            throws IOException {
        if (message == null) message = HttpStatus.getMessage(code);

        try {
            final Charset charset = this.charSet(baseRequest, contentType);
            final ByteBuffer buffer = baseRequest.getResponse().getHttpOutput().getBuffer();
            final ByteBufferOutputStream out = new ByteBufferOutputStream(buffer);
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset));

            response.setCharacterEncoding(charset.name());
            if (this.addHeader) response.addHeader("Hartshorn-Version", Hartshorn.VERSION);

            final Throwable th = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            final RequestError error = new RequestErrorImpl(this.context, request, response, code, writer, message, th);

            this.errorServlet.handle(error);
            message = error.message();

            if (error.yieldDefaults()) {
                this.writeDefaults(request, response, writer, code, message);
            }
            writer.flush();
        }
        catch (final ApplicationException e) {
            this.context.handle("Could not handle request error", e);
            throw new IOException("Server error");
        }

        baseRequest.getHttpChannel().sendResponseAndComplete();
    }

    protected void writeDefaults(final HttpServletRequest request, final HttpServletResponse response, final PrintWriter writer, final int code, final String message) {
        response.setContentType(Type.TEXT_PLAIN.asString());
        writer.write("HTTP ERROR ");
        writer.write(Integer.toString(code));
        writer.write(' ');
        writer.write(StringUtil.sanitizeXmlString(message));
        writer.write("\n");
        writer.printf("URI: %s%n", request.getRequestURI());
        writer.printf("STATUS: %s%n", code);
        writer.printf("MESSAGE: %s%n", message);
    }

    protected Charset charSet(final Request baseRequest, final String contentType) {
        Charset charset = null;
        final List<String> acceptable = baseRequest.getHttpFields().getQualityCSV(HttpHeader.ACCEPT_CHARSET);
        if (!acceptable.isEmpty()) {
            for (final String name : acceptable) {
                if ("*".equals(name)) {
                    charset = StandardCharsets.UTF_8;
                    break;
                }

                try {
                    charset = Charset.forName(name);
                }
                catch (final Exception e) {
                    this.context.handle(e);
                }
            }
            if (charset == null)
                return StandardCharsets.UTF_8;
        }

        final MimeTypes.Type type;
        switch (contentType) {
            case "text/html", "text/*", "*/*", "text/plain" -> {
                if (charset == null)
                    charset = StandardCharsets.ISO_8859_1;
            }
            case "text/json", "application/json" -> {
                if (charset == null)
                    charset = StandardCharsets.UTF_8;
            }
            default -> charset = StandardCharsets.UTF_8;
        }
        return charset;
    }
}
