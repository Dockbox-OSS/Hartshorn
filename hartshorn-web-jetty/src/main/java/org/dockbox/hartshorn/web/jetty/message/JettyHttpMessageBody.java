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

package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.HttpMessageBody;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;

import java.io.IOException;

/**
 * An implementation of {@link HttpMessageBody} for Jetty server requests.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyHttpMessageBody implements HttpMessageBody {

    private final Request request;

    public JettyHttpMessageBody(Request request) {
        this.request = request;
    }

    @Override
    public byte[] bytes() throws IOException {
        return Content.Source.asByteBuffer(this.request).array();
    }

    @Override
    public String asString() throws IOException {
        return Content.Source.asString(this.request);
    }
}
