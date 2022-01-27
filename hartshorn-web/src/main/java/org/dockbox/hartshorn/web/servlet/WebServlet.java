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

package org.dockbox.hartshorn.web.servlet;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.web.HttpAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebServlet {

    void get(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void head(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void post(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void put(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void delete(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void options(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    void trace(final HttpServletRequest req, final HttpServletResponse res, HttpAction fallback) throws ApplicationException;
    
}
