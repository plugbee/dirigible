/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command.ui;

import org.eclipse.jgit.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ShareCommandDialog extends PushCommandDialog {
	private static final long serialVersionUID = -5124345102495879231L;

	private static final String SHARE_TO_REMOTE_GIT_REPOSITORY = Messages.ShareCommandDialog_SHARE_TO_REMOTE_GIT_REPOSITORY;
	private static final String REPOSITORY_URI = Messages.CommandDialog_REPOSITORY_URI;
	private static final String REPOSITORY_BRANCH = Messages.CommandDialog_REPOSITORY_BRANCH;
	private static final String REPOSITORY_URI_IS_EMPTY = Messages.ShareCommandDialog_REPOSITORY_URI_IS_EMPTY;
	private static final String MASTER = "master"; //$NON-NLS-1$

	private Text textRepositoryURI;

	private Text textRepositoryBranch;

	private String repositoryURI;

	private String repositoryBranch;

	public ShareCommandDialog(Shell parentShell) {
		super(parentShell);
		this.repositoryBranch = MASTER;
	}

	@Override
	public void create() {
		super.create();
		setTitle(SHARE_TO_REMOTE_GIT_REPOSITORY);
	}

	@Override
	protected void addWidgets(Composite container) {
		createRepositoryURIField(container);
		createRepositoryBranchField(container);
		super.addWidgets(container);
	}

	private void createRepositoryURIField(Composite container) {
		Label labelRepositoryURI = new Label(container, SWT.NONE);
		labelRepositoryURI.setText(REPOSITORY_URI);

		GridData dataGitRepositoryURI = new GridData();
		dataGitRepositoryURI.grabExcessHorizontalSpace = true;
		dataGitRepositoryURI.horizontalAlignment = GridData.FILL;

		textRepositoryURI = new Text(container, SWT.BORDER);
		textRepositoryURI.setLayoutData(dataGitRepositoryURI);
	}

	private void createRepositoryBranchField(Composite container) {
		Label labelRepositoryBranch = new Label(container, SWT.NONE);
		labelRepositoryBranch.setText(REPOSITORY_BRANCH);

		GridData dataGitRepositoryBranch = new GridData();
		dataGitRepositoryBranch.grabExcessHorizontalSpace = true;
		dataGitRepositoryBranch.horizontalAlignment = GridData.FILL;

		textRepositoryBranch = new Text(container, SWT.BORDER);
		textRepositoryBranch.setLayoutData(dataGitRepositoryBranch);
		textRepositoryBranch.setText(this.repositoryBranch);

	}

	@Override
	protected boolean validateInput() {
		boolean valid = false;
		if (super.validateInput()) {
			if (StringUtils.isEmptyOrNull(textRepositoryURI.getText())) {
				errorMessage = REPOSITORY_URI_IS_EMPTY;
			} else {
				valid = true;
			}
		}
		return valid;
	}

	@Override
	protected void saveInput() {
		super.saveInput();
		repositoryURI = textRepositoryURI.getText();
		repositoryBranch = textRepositoryBranch.getText();
	}

	public String getRepositoryURI() {
		return repositoryURI;
	}

	public String getRepositoryBranch() {
		return repositoryBranch;
	}

}
