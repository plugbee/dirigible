/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.dual;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;

public class WorkspaceLocator {

	public static IWorkspace getWorkspace() {
		return RemoteResourcesPlugin.getWorkspace();
	}

	public static IWorkspace getWorkspace(HttpServletRequest request) {
		return RemoteResourcesPlugin.getWorkspace(request);
	}

	public static IWorkspace getWorkspace(String user) {
		return (user == null) ? RemoteResourcesPlugin.getWorkspace() : RemoteResourcesPlugin.getWorkspace(user);
	}

	public static String getRepositoryPathForWorkspace(IWorkspace workspace) {
		Workspace workspaceRAP = (Workspace) WorkspaceLocator.getWorkspace();
		String root = workspaceRAP.getRepositoryPathForWorkspace(RemoteResourcesPlugin.getUserName());
		return root;
	}
}
