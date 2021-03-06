/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.ws;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.ide.bridge.DirigibleBridge;
import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.logging.Logger;

public class WebSocketServiceServlet extends HttpServlet {

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(WebSocketServiceServlet.class);

	private static WebSocketServiceBridgeServletInternal webSocketServiceBridgeServletInternal;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			setupDebugChannel();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void setupDebugChannel() throws IOException {

		logger.debug("Setting Service channel internal ...");

		webSocketServiceBridgeServletInternal = new WebSocketServiceBridgeServletInternal();

		DirigibleBridge.BRIDGES.put("websocket_service_channel_internal", webSocketServiceBridgeServletInternal);

		logger.debug("Service channel internal has been set.");

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String json = WebSocketRequestParser.gson.toJsonTree(WebSocketServiceBridgeServletInternal.openSessions).getAsString();
		resp.setContentType(ContentTypeHelper.APPLICATION_JSON);
		resp.getWriter().print(json);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

}
