/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class RequestUtils {

	private static final Logger logger = Logger.getLogger(RequestUtils.class);

	public static String getUser(HttpServletRequest request) {
		String user = ICommonConstants.GUEST;
		try {
			if (request != null) {
				if (request.getUserPrincipal() != null) {
					user = request.getUserPrincipal().getName();
				} else {
					if (!isRolesEnabled()) {
						String fromCookie = getCookieValue(request, ICommonConstants.COOKIE_ANONYMOUS_USER);
						if (fromCookie != null) {
							user = fromCookie;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return user;
	}

	public static String getUser(Session session) {
		String user = ICommonConstants.GUEST;
		try {
			if (session != null) {
				if (session.getUserPrincipal() != null) {
					user = session.getUserPrincipal().getName();
				} else {
					if (!isRolesEnabled()) {
						String fromProps = (String) session.getUserProperties().get("user");
						if (fromProps != null) {
							user = fromProps;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return user;
	}

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static String get(String name) {
		String parameter = EnvUtils.getEnv(name);
		// (String) request.getSession().getAttribute(name);
		return parameter;
	}

	public static Boolean isRolesEnabled() {
		Boolean rolesEnabled = Boolean.parseBoolean(get(ICommonConstants.INIT_PARAM_ENABLE_ROLES));
		return rolesEnabled;
	}
}
