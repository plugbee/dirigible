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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.Test;

public class GenericExportZipTest {

	protected IRepository repository;

	@Test
	public void testExportZip() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/root1/export/folder3/text3.txt", //$NON-NLS-1$
					"text3".getBytes()); //$NON-NLS-1$
			repository.createResource("/root1/export/folder4/text4.txt", //$NON-NLS-1$
					"text4".getBytes()); //$NON-NLS-1$
			List<String> list = new ArrayList<String>();
			list.add("/root1/export"); //$NON-NLS-1$
			byte[] bytes = repository.exportZip(list);
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));

			try {
				byte[] buffer = new byte[2048];
				ZipEntry entry;
				int entriesCount = 0;
				while ((entry = zipInputStream.getNextEntry()) != null) {
					String outpath = entry.getName();
					ByteArrayOutputStream output = null;
					try {
						output = new ByteArrayOutputStream();
						int len = 0;
						while ((len = zipInputStream.read(buffer)) > 0) {
							output.write(buffer, 0, len);
						}
						outpath = outpath.replace(File.separator, IRepository.SEPARATOR);
						if (outpath.equals("export/folder3/text3.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text3", read); //$NON-NLS-1$
							entriesCount++;
						}
						if (outpath.equals("export/folder4/text4.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text4", read); //$NON-NLS-1$
							entriesCount++;
						}

					} finally {
						// we must always close the output file
						if (output != null) {
							output.close();
						}
					}
				}
				assertEquals(2, entriesCount);

			} finally {
				// we must always close the zip file.
				zipInputStream.close();
			}
			repository.removeCollection("/root1/export/"); //$NON-NLS-1$

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void testExportZipMultipleRoots() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/root1/export/folder3/text3.txt", //$NON-NLS-1$
					"text3".getBytes()); //$NON-NLS-1$
			repository.createResource("/root1/export/folder4/text4.txt", //$NON-NLS-1$
					"text4".getBytes()); //$NON-NLS-1$
			repository.createResource("/root1/export/folder5/folder6/text7.txt"); //$NON-NLS-1$
			List<String> list = new ArrayList<String>();
			list.add("/root1/export/folder3/text3.txt"); //$NON-NLS-1$
			list.add("/root1/export/folder4/text4.txt"); //$NON-NLS-1$
			list.add("/root1/export/folder5/folder6"); //$NON-NLS-1$
			byte[] bytes = repository.exportZip(list);
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));

			try {
				byte[] buffer = new byte[2048];
				ZipEntry entry;
				int entriesCount = 0;
				while ((entry = zipInputStream.getNextEntry()) != null) {
					String outpath = entry.getName();
					ByteArrayOutputStream output = null;
					try {
						output = new ByteArrayOutputStream();
						int len = 0;
						while ((len = zipInputStream.read(buffer)) > 0) {
							output.write(buffer, 0, len);
						}
						outpath = outpath.replace(File.separator, IRepository.SEPARATOR);
						if (outpath.equals("text3.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text3", read); //$NON-NLS-1$
							entriesCount++;
						}
						if (outpath.equals("text4.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text4", read); //$NON-NLS-1$
							entriesCount++;
						}
						if (outpath.equals("folder6/")) { //$NON-NLS-1$
							entriesCount++;
						}

					} finally {
						// we must always close the output file
						if (output != null) {
							output.close();
						}
					}
				}
				assertEquals(3, entriesCount);

			} finally {
				// we must always close the zip file.
				zipInputStream.close();
			}
			repository.removeCollection("/root1/export/"); //$NON-NLS-1$

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void testExportZipExclusiveRoot() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/root1/export/folder3/text3.txt", //$NON-NLS-1$
					"text3".getBytes()); //$NON-NLS-1$
			repository.createResource("/root1/export/folder4/text4.txt", //$NON-NLS-1$
					"text4".getBytes()); //$NON-NLS-1$
			byte[] bytes = repository.exportZip("/root1/export", false); //$NON-NLS-1$
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));

			try {
				byte[] buffer = new byte[2048];
				ZipEntry entry;
				int entriesCount = 0;
				while ((entry = zipInputStream.getNextEntry()) != null) {
					String outpath = entry.getName();
					ByteArrayOutputStream output = null;
					try {
						output = new ByteArrayOutputStream();
						int len = 0;
						while ((len = zipInputStream.read(buffer)) > 0) {
							output.write(buffer, 0, len);
						}
						outpath = outpath.replace(File.separator, IRepository.SEPARATOR);
						if (outpath.equals("folder3/text3.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text3", read); //$NON-NLS-1$
							entriesCount++;
						}
						if (outpath.equals("folder4/text4.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text4", read); //$NON-NLS-1$
							entriesCount++;
						}

					} finally {
						// we must always close the output file
						if (output != null) {
							output.close();
						}
					}
				}
				assertEquals(2, entriesCount);

			} finally {
				// we must always close the zip file.
				zipInputStream.close();
			}
			repository.removeCollection("/root1/export/"); //$NON-NLS-1$

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void testExportZipResourceOnly() {
		if (repository == null) {
			return;
		}

		try {
			repository.createResource("/root1/export/folder3/text3.txt", //$NON-NLS-1$
					"text3".getBytes()); //$NON-NLS-1$
			byte[] bytes = repository.exportZip("/root1/export/folder3/text3.txt", false); //$NON-NLS-1$
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));

			try {
				byte[] buffer = new byte[2048];
				ZipEntry entry;
				int entriesCount = 0;
				while ((entry = zipInputStream.getNextEntry()) != null) {
					String outpath = entry.getName();
					ByteArrayOutputStream output = null;
					try {
						output = new ByteArrayOutputStream();
						int len = 0;
						while ((len = zipInputStream.read(buffer)) > 0) {
							output.write(buffer, 0, len);
						}

						if (outpath.equals("text3.txt")) { //$NON-NLS-1$
							String read = new String(output.toByteArray());
							assertEquals("text3", read); //$NON-NLS-1$
							entriesCount++;
						}

					} finally {
						// we must always close the output file
						if (output != null) {
							output.close();
						}
					}
				}
				assertEquals(1, entriesCount);

			} finally {
				// we must always close the zip file.
				zipInputStream.close();
			}
			repository.removeCollection("/root1/export/"); //$NON-NLS-1$

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
