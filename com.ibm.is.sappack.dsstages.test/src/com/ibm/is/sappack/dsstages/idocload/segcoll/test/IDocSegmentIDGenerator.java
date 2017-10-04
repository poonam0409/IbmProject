package com.ibm.is.sappack.dsstages.idocload.segcoll.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.dsstages.common.IDocMetadataFactory;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.util.IDocSegmentTraversal;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class IDocSegmentIDGenerator {

	File dir;
	IDocType idocType;

	static class Entry {
		int numbers;
		OutputStream file;
	}

	Map<IDocSegment, Entry> segmentEntries;

	public IDocSegmentIDGenerator(String idocType, String release, JCoDestination dest) throws JCoException, IOException {
		this.idocType = IDocMetadataFactory.createIDocType(dest, null, idocType, null, release);

	}

	void initializeSegments(final Map<String, Integer> segmentNameNumbers) {
		this.segmentEntries = new HashMap<IDocSegment, Entry>();
		IDocSegmentTraversal.traverseIDoc(idocType, new IDocSegmentTraversal.Visitor() {

			@Override
			public void visit(IDocSegment seg) {
				String name = seg.getSegmentTypeName();
				if (segmentEntries.keySet().contains(name)) {
					OutputStream os;
					try {
						os = new FileOutputStream(new File(dir, seg.getSegmentTypeName() + ".txt")); //$NON-NLS-1$
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					}
					Entry e = new Entry();
					e.numbers = segmentNameNumbers.get(name);
					e.file = os;
					segmentEntries.put(seg, e);
				}
			}

		});

	}

	public void generateIDFiles(Map<String, Integer> segmentNumbers, int numberOfIDocs) throws IOException {
		initializeSegments(segmentNumbers);

		for (int num = 0; num < numberOfIDocs; num++) {
			IDocSegmentTraversal.traverseIDoc(idocType, new IDocSegmentTraversal.Visitor() {

				@Override
				public void visit(IDocSegment segment) {
					Entry e = segmentEntries.get(segment);
					if (e != null) {
						for (int i=0; i<e.numbers; i++) {
							
						}
					}
				}

			});
		}

		for (Entry e : this.segmentEntries.values()) {
			e.file.close();
		}

	}
}
