/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.bridge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Terminal WebSocket channel
 */
@ServerEndpoint("/terminal")
public class WebSocketTerminalBridgeServlet {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketTerminalBridgeServlet.class.getCanonicalName());

	private static Map<String, Session> openSessions = new ConcurrentHashMap<String, Session>();

	/**
	 * On open connection handler
	 *
	 * @param session
	 * @throws IOException
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException {
		if (Boolean.parseBoolean(InitParametersInjector.get(InitParametersInjector.INIT_PARAM_ENABLE_ROLES))) {
			Principal principal = session.getUserPrincipal();
			if (principal == null) {
				// no logged in user
				session.getBasicRemote().sendText("Login first to be able to use the Terminal websocket channel.");
				session.close();
			}
		}
		// else {
		// // assume trial instance
		// session.getBasicRemote().sendText("Terminal websocket channel is disabled on this instance");
		// }
		openSessions.put(session.getId(), session);
		callInternal("onOpen", session, null);
	}

	protected void callInternal(String methodName, Session session, String message) {

		logger.debug("Getting internal pair...");

		Object terminalInternal = DirigibleBridge.BRIDGES.get("websocket_terminal_channel_internal");

		logger.debug("Getting internal pair passed: " + (terminalInternal != null));

		if (terminalInternal == null) {
			String peerError = "Internal WebSocket peer for Terminal Service is null.";
			logger.error(peerError);
			try {
				session.getBasicRemote().sendText(peerError);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			return;
		}

		try {
			Method method = null;
			if (message == null) {
				method = terminalInternal.getClass().getMethod(methodName, Session.class);
				method.invoke(terminalInternal, session);
			} else {
				method = terminalInternal.getClass().getMethod(methodName, String.class, Session.class);
				method.invoke(terminalInternal, message, session);
			}
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(), e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * On message handler
	 *
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		callInternal("onMessage", session, message);
	}

	/**
	 * On error raised handler
	 *
	 * @param session
	 * @param t
	 */
	@OnError
	public void onError(Session session, Throwable t) {
		callInternal("onError", session, t.getMessage());
		logger.error(t.getMessage(), t);
	}

	/**
	 * On close handler
	 *
	 * @param session
	 */
	@OnClose
	public void onClose(Session session) {
		openSessions.remove(session.getId());
		callInternal("onClose", session, null);
	}

}
