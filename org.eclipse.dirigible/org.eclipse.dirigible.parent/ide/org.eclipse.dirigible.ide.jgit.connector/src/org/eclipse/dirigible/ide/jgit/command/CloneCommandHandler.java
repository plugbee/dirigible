/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.status.DefaultProgressMonitor;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.jgit.command.ui.CloneCommandDialog;
import org.eclipse.dirigible.ide.jgit.utils.GitFileUtils;
import org.eclipse.dirigible.ide.jgit.utils.GitProjectProperties;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.commands.AbstractWorkspaceHandler;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.git.JGitConnector;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.project.ProjectMetadataDependency;
import org.eclipse.dirigible.repository.project.ProjectMetadataRepository;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Clone project(s) from a Git repository and optionally publish it
 */
public class CloneCommandHandler extends AbstractWorkspaceHandler {

	protected static final String PROJECT_S_HAS_BEEN_CLONED_SUCCESSFULLY = "Project(s) has been cloned successfully";
	private static final String PROJECT_S_HAS_BEEN_PUBLISHED = "Project %s has been published";
	private static final String DO_YOU_WANT_TO_PUBLISH_THE_PROJECT_YOU_JUST_CLONED = "Do you want to publish the project(s) you just cloned?";
	private static final String PUBLISH_CLONED_PROJECT = "Publish Cloned Project?";
	protected static final String TASK_CLONING_REPOSITORY = Messages.CloneCommandHandler_TASK_CLONING_REPOSITORY;
	// private static final String MASTER = "master"; //$NON-NLS-1$
	private static final String PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY = Messages.CloneCommandHandler_MASTER;
	private static final String NO_REMOTE_REPOSITORY_FOR = Messages.CloneCommandHandler_NO_REMOTE_REPOSITORY_FOR;
	private static final String DOT_GIT = ".git";

	private static final String PROJECT_WAS_CLONED = Messages.CloneCommandHandler_PROJECT_WAS_CLONED;
	protected static final String WHILE_CLONING_REPOSITORY_ERROR_OCCURED = Messages.CloneCommandHandler_WHILE_CLONING_REPOSITORY_ERROR_OCCURED;
	private static final String NOT_SUPPORTED_REPOSITORY_TYPE = Messages.CloneCommandHandler_NOT_SUPPORTED_REPOSITORY_TYPE;
	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(CloneCommandHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return execute(event, null);
	}

	public Object execute(ExecutionEvent event, String git) throws ExecutionException {
		DefaultProgressMonitor monitor = new DefaultProgressMonitor();
		monitor.beginTask(TASK_CLONING_REPOSITORY, IProgressMonitor.UNKNOWN);

		final Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		CloneCommandDialog dialog = new CloneCommandDialog(parent, git);

		switch (dialog.open()) {
			case Window.OK:
				try {
					File gitDirectory = createGitDirectory(dialog.getRepositoryURI());
					Set<String> clonedProjects = new HashSet<String>();
					logger.debug(String.format("Start cloning repository %s ...", dialog.getRepositoryURI()));
					cloneProject(dialog.getRepositoryURI(), dialog.getRepositoryBranch(), dialog.getUsername(), dialog.getPassword(), gitDirectory,
							clonedProjects);
					logger.debug(String.format("Cloning repository %s finished successfully.", dialog.getRepositoryURI()));
					refreshWorkspace();
					publishProjects(clonedProjects);
					StatusLineManagerUtil.setInfoMessage(PROJECT_S_HAS_BEEN_CLONED_SUCCESSFULLY);

				} catch (IOException e) {
					logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
					MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
				}
				break;
		}

		monitor.done();
		return null;
	}

	protected File createGitDirectory(String repositoryURI) throws IOException {
		String repositoryName = repositoryURI.substring(repositoryURI.lastIndexOf(SLASH) + 1, repositoryURI.lastIndexOf(DOT_GIT));
		File gitDirectory = GitFileUtils.createTempDirectory(GitFileUtils.TEMP_DIRECTORY_PREFIX + repositoryName);
		return gitDirectory;
	}

	protected void cloneProject(final String repositoryURI, final String repositoryBranch, final String username, final String password,
			File gitDirectory, Set<String> clonedProjects) {
		try {
			String user = CommonIDEParameters.getUserName();

			logger.debug(String.format("Cloning repository %s, with username %s for branch %s in the directory %s ...", repositoryURI, username,
					repositoryBranch, gitDirectory.getAbsolutePath()));
			JGitConnector.cloneRepository(gitDirectory, repositoryURI, username, password, repositoryBranch);
			logger.debug(String.format("Cloning repository %s finished.", repositoryURI));

			Repository gitRepository = JGitConnector.getRepository(gitDirectory.getCanonicalPath());
			JGitConnector jgit = new JGitConnector(gitRepository);

			// final String lastSha = jgit.getLastSHAForBranch(MASTER);
			final String lastSha = jgit.getLastSHAForBranch(repositoryBranch);

			GitProjectProperties gitProperties = new GitProjectProperties(repositoryURI, lastSha);

			logger.debug(String.format("Git properties for the repository %s: %s", repositoryURI, gitProperties.toString()));

			IRepository repository = RepositoryFacade.getInstance().getRepository();
			String workspacePath = String.format(GitProjectProperties.DB_DIRIGIBLE_USERS_S_WORKSPACE, user);

			logger.debug(String.format("Start importing projects for repository directory %s ...", gitDirectory.getCanonicalPath()));
			List<String> importedProjects = GitFileUtils.importProject(gitDirectory, repository, workspacePath, user, gitProperties);
			logger.debug(String.format("Importing projects for repository directory %s finished", gitDirectory.getCanonicalPath()));

			StatusLineManagerUtil.setInfoMessage(String.format(PROJECT_WAS_CLONED, importedProjects));

			String[] projectNames = GitFileUtils.getValidProjectFolders(gitDirectory);
			for (String projectName : projectNames) {
				ProjectMetadataManager.ensureProjectMetadata(projectName, repositoryURI, repositoryBranch);
				clonedProjects.add(projectName);
			}
			logger.debug("Start cloning dependencies ...");
			for (String projectName : projectNames) {
				logger.debug(String.format("Start cloning dependencies of the project %s...", projectName));
				cloneDependencies(username, password, clonedProjects, projectName);
				logger.debug(String.format("Cloning of dependencies of the project %s finished", projectName));
			}
			logger.debug("Cloning of dependencies finished");

		} catch (InvalidRemoteException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			String causedBy = NO_REMOTE_REPOSITORY_FOR + e.getCause().getMessage();
			MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, causedBy);
		} catch (TransportException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			Throwable rootCause = e.getCause();

			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					String causedBy = PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY;
					MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, causedBy);
				} else {
					MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
				}
			} else {
				MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getMessage());
			}
		} catch (GitAPIException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
		} catch (Exception e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
		} finally {
			GitFileUtils.deleteDirectory(gitDirectory);
		}
	}

	protected void cloneDependencies(final String username, final String password, Set<String> clonedProjects, String projectName)
			throws IOException, CoreException {
		IProject selectedProject = WorkspaceLocator.getWorkspace(CommonIDEParameters.getRequest()).getRoot().getProject(projectName);
		ProjectMetadataDependency[] dependencies = ProjectMetadataManager.getDependencies(selectedProject);
		PullCommandHandler pull = new PullCommandHandler();
		if (dependencies != null) {
			for (ProjectMetadataDependency dependency : dependencies) {
				if (ProjectMetadataRepository.GIT.equalsIgnoreCase(dependency.getType())) {
					String projectGuid = dependency.getGuid();
					if (!clonedProjects.contains(projectGuid)) {
						IProject alreadyClonedProject = WorkspaceLocator.getWorkspace(CommonIDEParameters.getRequest()).getRoot()
								.getProject(projectGuid);
						if (!alreadyClonedProject.exists()) {
							String projectRepositoryURI = dependency.getUrl();
							String projectRepositoryBranch = dependency.getBranch();
							File projectGitDirectory = createGitDirectory(projectRepositoryURI);
							logger.debug(
									String.format("Start cloning of the project %s from the repository %s and branch %s into the directory %s ...",
											projectGuid, projectRepositoryURI, projectRepositoryBranch, projectGitDirectory.getCanonicalPath()));
							cloneProject(projectRepositoryURI, projectRepositoryBranch, username, password, projectGitDirectory, clonedProjects); // assume
						} else {
							logger.debug(String.format("Project %s has been already cloned, hence do pull instead.", projectGuid));
							pull.pullProjectFromGitRepository(alreadyClonedProject);
						}
						clonedProjects.add(projectGuid);

					} else {
						logger.debug(String.format("Project %s has been already cloned during this session.", projectGuid));
					}

				} else {
					String errorMessage = String.format(NOT_SUPPORTED_REPOSITORY_TYPE, dependency.getType());
					logger.error(errorMessage);
					MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, errorMessage);
				}
			}
		}
	}

	protected void publishProjects(Set<String> clonedProjects) {
		if (clonedProjects.size() > 0) {
			if (MessageDialog.openConfirm(null, PUBLISH_CLONED_PROJECT, DO_YOU_WANT_TO_PUBLISH_THE_PROJECT_YOU_JUST_CLONED)) {
				for (String projectName : clonedProjects) {
					IProject[] projects = WorkspaceLocator.getWorkspace(CommonIDEParameters.getRequest()).getRoot().getProjects();
					for (IProject project : projects) {
						if (project.getName().equals(projectName)) {
							try {
								PublishManager.publishProject(project, CommonIDEParameters.getRequest());
								logger.info(String.format(PROJECT_S_HAS_BEEN_PUBLISHED, project.getName()));
							} catch (PublishException e) {
								logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
								String causedBy = NO_REMOTE_REPOSITORY_FOR + e.getCause().getMessage();
								MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, causedBy);
							}
							break;
						}
					}
				}
			}
		}
	}

}
