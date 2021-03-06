/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.DatabaseUpdater;
import org.eclipse.dirigible.repository.ext.db.DsvUpdater;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionUpdater;
import org.eclipse.dirigible.repository.ext.security.SecurityUpdater;
import org.eclipse.dirigible.runtime.job.JobsUpdater;
import org.eclipse.dirigible.runtime.js.test.TestExecutionUpdater;
import org.eclipse.dirigible.runtime.listener.ListenersUpdater;

public class ContentPostImportUpdater {

	private IRepository repository;

	public ContentPostImportUpdater(IRepository repository) {
		this.repository = repository;
	}

	public IRepository getRepository() {
		return repository;
	}

	public void update(HttpServletRequest request) throws IOException, Exception {
		// 1. Execute the real database "create or update"
		DatabaseUpdater databaseUpdater = new DatabaseUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				DatabaseUpdater.REGISTRY_DATA_STRUCTURES_DEFAULT);
		databaseUpdater.applyUpdates();

		// 2. Execute the real security "create or update"
		SecurityUpdater securityUpdater = new SecurityUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				SecurityUpdater.REGISTRY_SECURITY_CONSTRAINTS_DEFAULT);
		securityUpdater.applyUpdates();

		// 3. Execute the real import from DSV files
		DsvUpdater dsvUpdater = new DsvUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				DatabaseUpdater.REGISTRY_DATA_STRUCTURES_DEFAULT);
		dsvUpdater.applyUpdates();

		// 4. Extensions
		ExtensionUpdater extensionUpdater = new ExtensionUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				ExtensionUpdater.REGISTRY_EXTENSION_DEFINITIONS_DEFAULT, request);
		extensionUpdater.applyUpdates();

		// 5. Jobs
		JobsUpdater jobsUpdater = new JobsUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				JobsUpdater.REGISTRY_INTEGRATION_DEFAULT);
		jobsUpdater.applyUpdates();

		// 6. Listener
		ListenersUpdater listenersUpdater = new ListenersUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				ListenersUpdater.REGISTRY_INTEGRATION_DEFAULT);
		listenersUpdater.applyUpdates();

		// 7. Tests Execution
		TestExecutionUpdater testExecutionUpdater = new TestExecutionUpdater(getRepository(), DataSourceFacade.getInstance().getDataSource(request),
				TestExecutionUpdater.REGISTRY_TEST_DEFAULT);
		testExecutionUpdater.applyUpdates();
	}

}
