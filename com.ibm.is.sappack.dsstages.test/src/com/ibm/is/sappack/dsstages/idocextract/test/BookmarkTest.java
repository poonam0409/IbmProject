package com.ibm.is.sappack.dsstages.idocextract.test;

import java.io.File;

import com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmark;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class BookmarkTest extends TestCase {

	static String JOBNAME = "testjob" + System.currentTimeMillis();
	static String IDOCTYPE = "TESTIDOC";
	static String IDOCEXTRACTDIR = System.getProperty("java.io.tmpdir");
	
	static void log(String s) {
		System.out.println(s + "\n");
	}
	
	public void testBookmark1() throws Exception {
		IDocBookmark bm = new IDocBookmark();
		bm.setup(JOBNAME, IDOCTYPE, IDOCEXTRACTDIR);
		
		bm.initialize(JOBNAME, IDOCTYPE, IDOCEXTRACTDIR);
		
		
		log("Bookmark file name: " + bm.getFileName());
		assertTrue(bm.isInitialized());
		
		Long p1 = bm.getPrevious();
		log("p1: " + p1);
		Long c1 = bm.getCurrent();		
		log("c1: " + c1);
		
		bm.update();
		Long p2 = bm.getPrevious();
		log("p2: " + p2);
		Long c2 = bm.getCurrent();		
		log("c2: " + c2);

		assertEquals(p2, c1);
		
		Thread.sleep(200);
		bm.update();
		Long p3 = bm.getPrevious();
		log("p3: " + p3);
		Long c3 = bm.getCurrent();		
		log("c3: " + c3);
		
		assertEquals(c2, p3);
		
//		bm.update();
		boolean reset = bm.reset();
		assertTrue(reset);
		Long p4 = bm.getPrevious();
		log("p4: " + p4);
		Long c4 = bm.getCurrent();		
		log("c4: " + c4);
		
		assertEquals(c4, c2);
		assertEquals(p4, c4);
		
		
		File f = new File(bm.getFileName());
		boolean deleted = f.delete();
		assertTrue(deleted);
		
	}
}
