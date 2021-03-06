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

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

/**
 * Class holding the discrimination parameters for the Web Content for Entity templates
 */
public class WebContentEntityTemplateTypeDiscriminator {

	/**
	 * Category of the template
	 *
	 * @return the category
	 */
	public static String getCategory() {
		return ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
	}

	/**
	 * Templates path within the Repository
	 *
	 * @return the templates path
	 */
	public static String getTemplatesPath() {
		return IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES_WEB_CONTENT_FOR_ENTITY;
	}

}
