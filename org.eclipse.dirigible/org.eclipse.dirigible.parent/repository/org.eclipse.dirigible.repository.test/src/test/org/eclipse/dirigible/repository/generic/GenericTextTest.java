/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.repository.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.Test;

public class GenericTextTest {

	protected IRepository repository;

	@Test
	public void testText() {
		if (repository == null) {
			return;
		}

		IResource resource = null;
		try {
			String content = "test1";

			resource = repository.createResource("/testCollection/toBeRemovedText1.txt", content.getBytes(), false, //$NON-NLS-1$
					"text/plain"); //$NON-NLS-1$
			assertNotNull(resource);
			assertTrue(resource.exists());
			assertFalse(resource.isBinary());

			IResource resourceBack = repository.getResource("/testCollection/toBeRemovedText1.txt"); //$NON-NLS-1$
			String contentback = new String(resourceBack.getContent());

			assertEquals(content, contentback);

			IResource resource2 = repository.getResource("/testCollection/toBeRemovedText1.txt"); //$NON-NLS-1$
			resource2.setContent("test2".getBytes());

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				if ((resource != null) && resource.exists()) {
					repository.removeResource("/testCollection/toBeRemovedText1.txt"); //$NON-NLS-1$
					resource = repository.getResource("/testCollection/toBeRemovedText1.txt"); //$NON-NLS-1$
					assertNotNull(resource);
					assertFalse(resource.exists());
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

}
