/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.html.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.template.ui.common.GenerationException;
import org.eclipse.dirigible.ide.template.ui.common.service.AbstractGenerationServlet;

/**
 * Generation Service for WebContent artifacts
 * Sample requests:
 * POST Request for Table:
 * ====
 * {
 * "templateType":"1_index_page",
 * "fileName":"index.html",
 * "projectName":"myproject",
 * "packageName":"mypackage"
 * }
 * ====
 * Get Request returns all the available WebContent related templates
 */
public class WebContentGenerationServlet extends AbstractGenerationServlet {

	private static final long serialVersionUID = -3650506905899341103L;

	@Override
	protected String doGeneration(String parameters, HttpServletRequest request) throws GenerationException {
		try {
			return new WebContentGenerationWorker(getRepository(request), getWorkspace(request)).generate(parameters, request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

	@Override
	protected String enumerateTemplates(HttpServletRequest request) throws GenerationException {
		try {
			return new WebContentGenerationWorker(getRepository(request), getWorkspace(request)).getTemplates(request);
		} catch (ServletException e) {
			throw new GenerationException(e);
		}
	}

}
