package com.ibm.is.sappack.dsstages.idocload.segcoll.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.impl.ControlRecord;
import com.ibm.is.sappack.dsstages.common.test.TestLog;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;
import com.ibm.is.sappack.dsstages.idocload.IDocTree;
import com.ibm.is.sappack.dsstages.idocload.IDocTreeTraversal;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;
import com.ibm.is.sappack.dsstages.idocload.SegmentValidationHandler;
import com.ibm.is.sappack.dsstages.idocload.SegmentValidationResult;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.ExternalIDocSegmentReader;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.ExternalIDocSegmentWriter;

@SuppressWarnings("nls")
public abstract class SegmentCollectorTest extends TestCase {

	public SegmentCollectorTest() {
		TestLog.log("Running JUnit test: " + this.getClass().getName());
	}
	
	String IDOCNUMPFX = "IDOC";

	String SEGPFX = "SEG";

	protected void createChildren(Object[] parent, int number, int currentLevel, int levels, List<Object[]> keys, DummyIDocSegment[] segments) {
		if (currentLevel >= levels) {
			return;
		}
		for (int i = 1; i <= number; i++) {
			Object[] n = new Object[4];
			n[0] = parent[0];
			int nnum = keys.size();
			n[1] = SEGPFX + Integer.toString(nnum);
			n[2] = parent[1];
			n[3] = segments[currentLevel];
			keys.add(n);
			createChildren(n, number, currentLevel + 1, levels, keys, segments);
		}
	}

	protected List<Object[]> createArtificialSegmentList(int idocNums, int levels, int nodePerLevel, IDocType[] outIDocType) {

		TestLog.log("=======================================");
		TestLog.log("Creating keys");

		TestLog.log("IDoc number: " + idocNums);
		TestLog.log("Levels: " + levels);
		TestLog.log("Nodes per level: " + nodePerLevel);

		int overallNodes = 0;
		for (int i = 0; i <= levels; i++) {
			double d = Math.pow(nodePerLevel, i);
			overallNodes += ((int) d);
		}
		TestLog.log("Overall nodes/number of segments per IDoc: " + overallNodes);
		overallNodes = overallNodes * idocNums;

		TestLog.log("Overall nodes/number of segments: " + overallNodes);
		TestLog.log("---------------------------------------");
		TestLog.log("Building artificial IDoc segments...");

		// create a simple hierarchical segmetn structure
		DummyIDocType idocType = new DummyIDocType();
		outIDocType[0] = idocType;
		idocType.name = "IDOCTYPE";
		DummyIDocSegment[] segments = new DummyIDocSegment[levels];
		DummyIDocSegment root = new DummyIDocSegment("SEGTYPE0", null, idocType);
		idocType.root.add(root);
		root.type = idocType;
		segments[0] = root;
		for (int i = 1; i < levels; i++) {
			segments[i] = new DummyIDocSegment("SEGTYPE" + i, segments[i - 1], idocType);
		}

		List<Object[]> keys = new ArrayList<Object[]>();

		for (int idocCount = 1; idocCount <= idocNums; idocCount++) {
			// root seg
			Object[] roottuple = new Object[4];
			roottuple[0] = IDOCNUMPFX + idocCount;
			roottuple[1] = SEGPFX + "0";
			roottuple[2] = "null";
			roottuple[3] = root;
			keys.add(roottuple);
			createChildren(roottuple, nodePerLevel, 1, levels, keys, segments);
		}
		TestLog.log("Artificial IDoc segments built");

		return keys;
	}

	static class DummySegmentData implements SegmentData {
		String[] testTuple;

		@Override
		public char[] getSegmentDataBuffer() {
			return new String(testTuple[0] + testTuple[1] + testTuple[2]).toCharArray();
		}

		@Override
		public String getSegmentDefinitionName() {
			return testTuple[0] + "," + testTuple[1] + "," + testTuple[2];
		}

	}

	static class ListSegmentValidationHandler implements SegmentValidationHandler {

		List<SegmentValidationResult> validationResults = new ArrayList<SegmentValidationResult>();
		
		@Override
		public void handleSegmentValidationMessage(SegmentValidationResult segValidationResult) {
			validationResults.add(segValidationResult);
		}
		
	}
	
	void insertAndCheckSegments(IDocType idocType, List<Object[]> segments, int idocNum, int levels, int nodesPerLevel) throws Exception {
		TestLog.log("Segments created: " + segments.size());
		SegmentCollector segColl = createSegmentCollector(getEnvironment(), idocType);
		ListSegmentValidationHandler handler = new ListSegmentValidationHandler();
		segColl.setSegmentValidationHandler(handler);

		final Map<String, String[]> idocNumPlusSegNumMap = new HashMap<String, String[]>();
		for (Object[] segment : segments) {
			DummySegmentData dsg = new DummySegmentData();
			String[] t = new String[] { (String) segment[0], (String) segment[1], (String) segment[2] };
			dsg.testTuple = t;
			segColl.insertSegment((String) segment[0], (String) segment[1], (String) segment[2], (IDocSegment) segment[3], dsg);
			idocNumPlusSegNumMap.put((String) segment[0] + (String) segment[1], t);
			//	assertNotNull(node);
			//	assertTrue(node.getSegmentData() == dsg);
		}

		TestLog.log("All segments inserted");		

		int treeNum = 0;
		Iterator<IDocTree> it = segColl.getAllIDocTrees();
		List<SegmentValidationResult> validationResult = handler.validationResults;
		assertTrue(validationResult.isEmpty());

		while (it.hasNext()) {
			treeNum++;
			final IDocTree tree = it.next();
			IDocTreeTraversal.Visitor visitor = new IDocTreeTraversal.Visitor() {

				@Override
				public void visit(IDocNode node, int hierarchyLevel, int segmentNumber, int parentSegmentNumber) {
					String[] tuple = idocNumPlusSegNumMap.get(tree.getIDocNumber() + node.getSegNum());
					//String[] tuple = ((DummySegmentData) node.getSegmentData()).testTuple;
					assertEquals(tuple[0], tree.getIDocNumber());
					assertEquals(tuple[1], node.getSegNum());
					IDocNode parent = node.getParent();
					if (parent == null) {
						assertEquals("null", tuple[2]);
					} else {
						assertNotNull(tuple[2]);
					}

				}

			};

			IDocTreeTraversal.traverseIDocTree(tree, visitor);
		}

		TestLog.log("IDocTrees retrieved: " + treeNum);

		assertEquals(idocNum, treeNum);
		segColl.cleanup();
	}

	public void doSegCollTest(int idocNum, int levels, int nodesPerLevel) throws Exception {
		IDocType[] t = new IDocType[1];
		List<Object[]> segments = createArtificialSegmentList(idocNum, levels, nodesPerLevel, t);

		Random random = new Random(1);
		for (int i = 0; i < 3; i++) {
			TestLog.log("Shuffling segments: " + i);
			Collections.shuffle(segments, random);
			insertAndCheckSegments(t[0], segments, idocNum, levels, nodesPerLevel);
		}
	}

	static String NOTFOUND_STRING = "";

	static String findPSegNum(List<Object[]> tuples, String idocNum, String segNum) {
		for (Object[] t : tuples) {
			if (t[0].equals(idocNum) && t[1].equals(segNum)) {
				return (String) t[2];
			}
		}
		return NOTFOUND_STRING;
	}

	static class DummyIDocType implements IDocType {

		String name;
		String basicName;
		List<IDocSegment> root = new ArrayList<IDocSegment>();

		@Override
		public ControlRecord getControlRecord() {
			return null;
		}

		@Override
		public String getIDocTypeDescription() {
			return null;
		}

		@Override
		public String getIDocTypeName() {
			return name;
		}

		@Override
		public String getRelease() {
			return null;
		}

		@Override
		public List<IDocSegment> getRootSegments() {
			return root;
		}

		@Override
      public String getBasicTypeName() {
	      return basicName;
      }
	}

	static class DummyIDocSegment implements IDocSegment {

		String segType;
		DummyIDocSegment parent;
		DummyIDocType type;
		List<IDocSegment> kids = new ArrayList<IDocSegment>();

		public DummyIDocSegment(String segTypeName, DummyIDocSegment parent, DummyIDocType idocType) {
			this.segType = segTypeName;
			this.parent = parent;
			if (this.parent != null) {
				this.parent.kids.add(this);
			}
			type = idocType;
		}

		@Override
		public List<IDocSegment> getChildSegments() {
			return kids;
		}

		@Override
		public List<IDocField> getFields() {
			return null;
		}

		@Override
		public long getHierarchyLevel() {
			return 0;
		}

		@Override
		public IDocType getIDocType() {
			return type;
		}

		@Override
		public long getMaxOccurrence() {
			return 0;
		}

		@Override
		public long getMinOccurrence() {
			return 0;
		}

		@Override
		public IDocSegment getParent() {
			return parent;
		}

		@Override
		public String getSegmentDefinitionName() {
			return segType + "DEF";
		}

		@Override
		public String getSegmentDescription() {
			return null;
		}

		@Override
		public String getSegmentTypeName() {
			return segType;
		}

		@Override
      public long getSegmentNr() {
	      return 0;
      }

		@Override
      public boolean isMandatory() {
	      return false;
      }

		@Override
      public boolean isParentFlag() {
	      return false;
      }
	}

	public void testSimple1() throws Exception {
		/*
		 * Test this tree:
		 * (1, 1, null)   S1
		 *     (1, 2, 1)  S2
		 *        (1, 3, 2)  S3
		 *        (1, 4, 2)  S3
		 *     (1, 5, 1)  S2
		 * 
		 * (2, 1, null)   T1 
		 *     (2, 2, 1)  T2
		 *         (2, 4, 2)  T3
		 *     (2, 3, 1)  T4
		 *         (2, 5, 3)  T5
		 *         
		 */

		DummyIDocType i1 = new DummyIDocType();
		i1.name = "I1";
		DummyIDocSegment s1 = new DummyIDocSegment("S1", null, i1);
		DummyIDocSegment s2 = new DummyIDocSegment("S2", s1, i1);
		DummyIDocSegment s3 = new DummyIDocSegment("S3", s2, i1);
		i1.root.add(s1);

		//		DummyIDocType i2 = new DummyIDocType();
		//		i2.name = "I2";
		DummyIDocType i2 = i1;
		DummyIDocSegment t1 = new DummyIDocSegment("T1", null, i2);
		DummyIDocSegment t2 = new DummyIDocSegment("T2", t1, i2);
		DummyIDocSegment t3 = new DummyIDocSegment("T3", t2, i2);
		DummyIDocSegment t4 = new DummyIDocSegment("T4", t1, i2);
		DummyIDocSegment t5 = new DummyIDocSegment("T5", t4, i2);
		i2.root.add(t1);

		Object[][] tuples = { //
		{ "1", "1", "null", s1 }, // 
				{ "1", "2", "1", s2 }, //
				{ "1", "3", "2", s3 },//
				{ "1", "4", "2", s3 }, //
				{ "1", "5", "1", s2 }, //
				{ "2", "1", "null", t1 }, //
				{ "2", "2", "1", t2 }, //
				{ "2", "3", "1", t4 },//
				{ "2", "4", "2", t3 }, //
				{ "2", "5", "3", t5 } //
		};
		final List<Object[]> tupleList = Arrays.asList(tuples);

		Random rnd = new Random(1);
		for (int i = 0; i < 5; i++) {
			TestLog.log(i + "th shuffle");
			SegmentCollector segColl = createSegmentCollector(getEnvironment(), i1);
			ListSegmentValidationHandler handler = new ListSegmentValidationHandler();
			segColl.setSegmentValidationHandler(handler);

			for (Object[] to : tupleList) {
				String[] t = new String[] { (String) to[0], (String) to[1], (String) to[2] };
				DummySegmentData ds = new DummySegmentData();
				ds.testTuple = t;
				segColl.insertSegment(t[0], t[1], t[2], (DummyIDocSegment) to[3], ds);
			}


			int checkedTrees = 0;
			Iterator<IDocTree> it = segColl.getAllIDocTrees();
			List<SegmentValidationResult> validationResult = handler.validationResults;
			TestLog.log(validationResult.toString());
			assertTrue(validationResult.isEmpty());

			while (it.hasNext()) {
				final IDocTree tree = it.next();
				checkedTrees++;
				final int[] checkedNodes = new int[1];
				checkedNodes[0] = 0;
				IDocTreeTraversal.traverseIDocTree(tree, new IDocTreeTraversal.Visitor() {

					@Override
					public void visit(IDocNode node, int hierarchyLevel, int segmentNumber, int parentSegmentNumber) {
						String idocNum = tree.getIDocNumber();
						String segNum = node.getSegNum();
						String psegNum = findPSegNum(tupleList, idocNum, segNum);
						assertFalse(NOTFOUND_STRING.equals(psegNum));
						assertNotNull(node.getSegmentData());
						IDocNode parent = node.getParent();
						if (parent == null) {
							//	assertNull(psegNum);
						} else {
							assertEquals(psegNum, parent.getSegNum());
						}
						checkedNodes[0]++;
					}

				});
				assertEquals(5, checkedNodes[0]);

			}

			assertEquals(2, checkedTrees);

			Collections.shuffle(tupleList, rnd);
			segColl.cleanup();
		}

	}

	public void testSimpleErrors1() throws Exception {
		/*
		 * Test this tree which contains orphan segments
		 * (1, 1, null)  S1
		 *     (1, 2, 1)  S2
		 *        (1, 8, 9)  S3 (WRONG , segment 9 does not exist)
		 *        (1, 4, 2)  S3
		 *     (1, 5, 1)  S3  (WRONG, wrong segment type)
		 * 
		 * (2, 1, null)  T1
		 *     (2, 2, 1)  T2
		 *         (3, 4, 2) T3  (WRONG, IDoc 3 does not exist)
		 *     (2, 3, 1)  T4
		 *         (2, 9, 5)  T5  (WRONG, segment 5 does not exist)
		 *         
		 */

		DummyIDocType i1 = new DummyIDocType();
		i1.name = "I1";

		DummyIDocSegment s1 = new DummyIDocSegment("S1", null, i1);
		DummyIDocSegment s2 = new DummyIDocSegment("S2", s1, i1);
		DummyIDocSegment s3 = new DummyIDocSegment("S3", s2, i1);
		DummyIDocSegment t1 = new DummyIDocSegment("T1", null, i1);
		DummyIDocSegment t2 = new DummyIDocSegment("T2", t1, i1);
		DummyIDocSegment t3 = new DummyIDocSegment("T3", t2, i1);
		DummyIDocSegment t4 = new DummyIDocSegment("T4", t1, i1);
		DummyIDocSegment t5 = new DummyIDocSegment("T5", t4, i1);
		i1.root.add(s1);
		i1.root.add(t1);

		Object[][] tuples = { //
		{ "1", "1", "null", s1 }, //
				{ "1", "2", "1", s2 }, //
				{ "1", "8", "9", s3 }, // wrong 
				{ "1", "4", "2", s3 }, //
				{ "1", "5", "1", s3 }, // wrong s3
				{ "2", "1", "null", t1 }, //
				{ "2", "2", "1", t2 }, //
				{ "2", "3", "1", t4 }, //
				{ "3", "4", "2", t3 }, // wrong 
				{ "2", "9", "10", t5 } // wrong
		};

		final List<Object[]> tupleList = Arrays.asList(tuples);

		Random rnd = new Random(1);
		for (int i = 0; i < 5; i++) {
			TestLog.log(i + "th shuffle");
			SegmentCollector segColl = createSegmentCollector(this.getEnvironment(), i1);
			ListSegmentValidationHandler handler = new ListSegmentValidationHandler();
			segColl.setSegmentValidationHandler(handler);

			try {

				for (Object[] to : tupleList) {
					String[] t = new String[] { (String) to[0], (String) to[1], (String) to[2] };
					DummySegmentData ds = new DummySegmentData();
					ds.testTuple = t;
					segColl.insertSegment(t[0], t[1], t[2], (IDocSegment) to[3], ds);
				}

				int idocs = 0;
				// trigger validation
				Iterator<IDocTree> it = segColl.getAllIDocTrees();
				while (it.hasNext()) {
					idocs++;
					it.next();
				}
				
			//	assertEquals(2, idocs);
				
				List<SegmentValidationResult> validationResult = handler.validationResults;
				for (SegmentValidationResult svr : validationResult) {
					TestLog.log(svr.getMessage());
				}

				assertTrue(!validationResult.isEmpty());

				assertEquals(4, validationResult.size());

				Collections.shuffle(tupleList, rnd);
			} finally {
				TestLog.log("Cleaning up segment collector");
				segColl.cleanup();
			}
		}

	}

	public void testComplexTest() throws Exception {
		Logger logger = StageLogger.getLogger();
		Level l = logger.getLevel();
		logger.setLevel(Level.INFO);
		logger.log(Level.WARNING, "Complex tests are only run with log level INFO");
		
		doSegCollTest(50000, 3, 4);
		logger.setLevel(l);
	}

	public void testExternalIDocSegmentWriter() throws Exception {
		IDocType[] it = new IDocType[1];
		List<Object[]> segments = createArtificialSegmentList(10000, 3, 4, it);

		File f = File.createTempFile("extSegmentTest", ".tmp");
		TestLog.log("Writing IDoc segments to external file: " + f.getAbsolutePath());
		ExternalIDocSegmentWriter extWriter = new ExternalIDocSegmentWriter(f);

		for (Object[] segment : segments) {
			IDocSegment seg = (IDocSegment) segment[3];
			DummySegmentData dsg = new DummySegmentData();
			String[] t = new String[] { (String) segment[0], (String) segment[1], (String) segment[2] };
			dsg.testTuple = t;
			extWriter.addIDocData(t[0], t[1], t[2], seg.getSegmentTypeName(), dsg.getSegmentDataBuffer());

		}
		extWriter.close();
		ExternalIDocSegmentReader extReader = new ExternalIDocSegmentReader(f);
		Iterator<Object[]> expectedIt = segments.iterator();
		Object[] readSegment = null;
		while ((readSegment = extReader.getNextIDocData()) != null) {
			assertTrue(expectedIt.hasNext());
			Object[] expectedSegment = expectedIt.next();
			assertEquals("IDoc numbers don't match", (String) expectedSegment[0], (String) readSegment[0]);
			assertEquals("Segment numbers don't match", (String) expectedSegment[1], (String) readSegment[1]);
			assertEquals("Parent segment numbers don't match", (String) expectedSegment[2], (String) readSegment[2]);
			DummySegmentData dsg = new DummySegmentData();
			dsg.testTuple = new String[] { (String) expectedSegment[0], (String) expectedSegment[1], (String) expectedSegment[2], };
			assertEquals("IDoc data doesn't match", new String(dsg.getSegmentDataBuffer()), new String((char[]) readSegment[4]));
		}
		extReader.close();
		f.delete();
		TestLog.log("All IDocs checked");
	}

	protected abstract SegmentCollector createSegmentCollector(DSEnvironment env, IDocType idocType) throws IOException;

	protected DSEnvironment getEnvironment() {
		DSEnvironment env = new DSEnvironment();
		env.setJobName("DUMMYJOB");
		env.setInvocationID("DUMMYINVOCATIONID");
		env.setProjectName("DUMMYPROJECT");
		return env;
	}
	
}
