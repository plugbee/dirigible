/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.view;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.ui.widget.extbrowser.ExtendedBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class TerminalView extends ViewPart {
	private static String TERMINAL_LOCATION;

	static {
		TERMINAL_LOCATION = ((CommonIDEParameters.getRuntimeUrl() == null) || "".equals(CommonIDEParameters.getRuntimeUrl()))
				? "/web/registry/templates/monitoring/logging/terminal.html" : "/services/web/registry/templates/monitoring/logging/terminal.html";
	}

	private ExtendedBrowser browser = null;

	public TerminalView() {
		super();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		browser = new ExtendedBrowser(parent, SWT.NONE);
		browser.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl(TERMINAL_LOCATION);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void dispose() {
		browser = null;
		super.dispose();
	}
}
