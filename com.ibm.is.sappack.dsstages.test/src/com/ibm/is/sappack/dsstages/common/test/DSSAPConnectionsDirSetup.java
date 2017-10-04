package com.ibm.is.sappack.dsstages.common.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.Utilities;

import junit.framework.Assert;
import junit.framework.TestCase;

@SuppressWarnings("nls")
/**
 * Subclass this class to setup and tear down a test DSSAPConnections directory
 * to some temp folder.
 * The DSSAPConnections directory is stored in the field dsSAPConnectionsDir after setUp() was called.
 * The field tearDownDSSAPConnectionsDir can be set to true if deleting the temp DSSAPConnections folder
 * should be done.
 * 
 */
public class DSSAPConnectionsDirSetup extends TestCase {

	private String explicitDSSAPConnectionsZipFile = null;
	protected File dsSAPConnectionsDir;
	protected boolean tearDownDSSAPConnectionsDir = false;

	protected DSSAPConnectionsDirSetup() {
		super();
	}

	protected DSSAPConnectionsDirSetup(String explicitDSSAPConnectionsZipFile) {
		super();
		this.explicitDSSAPConnectionsZipFile = explicitDSSAPConnectionsZipFile;
	}

	protected void createDSSAPConnectionsFolder() throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		long millis = System.currentTimeMillis();
		File f = new File(tempDir + File.separator + "DSIDocTest_Server" + millis);
		TestLog.log("Temp file: " + f.getAbsolutePath());

		Assert.assertFalse(f.exists());

		Assert.assertTrue(f.mkdirs());

		String dsSAPConnectionsZipFile = null;
		if (explicitDSSAPConnectionsZipFile != null) {
			dsSAPConnectionsZipFile = explicitDSSAPConnectionsZipFile;
		} else {
			String osname = System.getProperty("os.name");
			if (osname.toLowerCase().startsWith("win")) {
				dsSAPConnectionsZipFile = "com/ibm/is/sappack/dsstages/common/test/DSSAPConnections_folder_windows.zip";
			}
		}

		ZipInputStream zis = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream(dsSAPConnectionsZipFile));

		ZipEntry ze = null;

		while ((ze = zis.getNextEntry()) != null) {
			TestLog.log("Creating zip entry: " + ze.getName());
			File zipEntryFile = new File(f, ze.getName());
			if (ze.isDirectory()) {
				Assert.assertTrue(zipEntryFile.mkdirs());
			} else {
				// assume that directories are listed before normal files				
				byte[] fileContents = Utilities.readInputStream(zis);
				FileOutputStream fos = new FileOutputStream(zipEntryFile);
				fos.write(fileContents);
				fos.close();
			}
		}

		this.dsSAPConnectionsDir = f;

	}

	protected static void deleteRecursively(File f) {
		if (!f.isDirectory()) {
			boolean deleted = f.delete();
			Assert.assertTrue(deleted);
		} else {
			File[] files = f.listFiles();
			for (File file : files) {
				deleteRecursively(file);
			}
			Assert.assertTrue(f.delete());
		}
	}

	protected void deleteDSSAPConnectionsFolder() {
		deleteRecursively(dsSAPConnectionsDir);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.createDSSAPConnectionsFolder();
		RuntimeConfiguration.overwrittenDSSAPHome = this.dsSAPConnectionsDir.getAbsolutePath();
	}

	@Override
	protected void tearDown() throws Exception {
		if (tearDownDSSAPConnectionsDir) {
			TestLog.log("Deleting directory: " + this.dsSAPConnectionsDir.getAbsolutePath());
			this.deleteDSSAPConnectionsFolder();
		} else {
			TestLog.log("Don't delete directory: " + this.dsSAPConnectionsDir.getAbsolutePath());
		}
		super.tearDown();
	}
public static void main(String ...poo)
{File f = new File("C:\\poonam\\tmp.txt");

	}
}
