//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2010                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the US Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM Information Server R/3 Pack IDoc Load 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idocload.online.test
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.online.test;

import java.util.Arrays;
import java.util.List;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;
import com.ibm.is.sappack.dsstages.idocload.IDocLoadFactory;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;

/**
 * IDocTestDataGenerator
 * 
 * generates SegmentCollectors with test IDocs
 *
 */
@SuppressWarnings("nls")
public class IDocTestDataGenerator {
	
	static String copyright()
	{ return com.ibm.is.sappack.dsstages.idocload.online.test.Copyright.IBM_COPYRIGHT_SHORT; }

	DSSAPConnection connection;
	private IDocType idocTypeDEBMAS06;
	IDocSegment e1kna1m;
	IDocSegment e1kna11;
	IDocSegment e1knvvm;
	IDocSegment e1knvim;
	IDocSegment e1knvkm;
	
	public IDocTestDataGenerator(DSSAPConnection conn, IDocType idocTypeDebmas06) {
		this.connection = conn;
		this.idocTypeDEBMAS06 = idocTypeDebmas06;
		this.e1kna1m = Utilities.findIDocSegment(idocTypeDEBMAS06, "E1KNA1M"); 
		this.e1kna11 = Utilities.findIDocSegment(idocTypeDEBMAS06, "E1KNA11"); 
		this.e1knvvm = Utilities.findIDocSegment(idocTypeDEBMAS06, "E1KNVVM"); 
		this.e1knvim = Utilities.findIDocSegment(idocTypeDEBMAS06, "E1KNVIM"); 
		this.e1knvkm = Utilities.findIDocSegment(idocTypeDEBMAS06, "E1KNVKM"); 
		
	}
	
	/**
	 * createMultiDEBMAS06
	 * 
	 * create a SegmentCollector containing
	 * the following DEBMAS06 IDocs
	 * 
	 * IDOC1:
	 * (no CONTROL_RECORD)
	 * E1KNA1M
	 * 	E1KNA11
	 * 	E1KNVVM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 	E1KNVKM
	 * 
	 * IDOC2:
	 * CONTROL_RECORD
	 * E1KNA1M
	 * 	E1KNA11
	 * 	E1KNVVM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVKM
	 * 
	 * 
	 * @return
	 */
	public SegmentCollector createMultiDEBMAS06() {
		
		String[][] debmas06 = { // DEBMAS06
				{ "IDoc1", "root", "000" }, // E1KNA1M
				{ "IDoc1", "one", "root" }, // E1KNA11
				{ "IDoc1", "two", "root" }, // E1KNVVM
				{ "IDoc1", "three", "root" }, // E1KNVVM
				{ "IDoc1", "four", "three" }, // E1KNVIM
				{ "IDoc1", "five", "three" }, // E1KNVIM
				{ "IDoc1", "six", "root" }, // E1KNVVM
				{ "IDoc1", "seven", "six" }, // E1KNVIM
				{ "IDoc1", "eight", "root" }, // E1KNVKM
				{ "IDoc2", "root", "000" }, // E1KNA1M
				{ "IDoc2", "one", "root" }, // E1KNA11
				{ "IDoc2", "two", "root" }, // E1KNVVM
				{ "IDoc2", "six", "root" }, // E1KNVVM
				{ "IDoc2", "seven", "six" }, // E1KNVIM
				{ "IDoc2", "eight", "six" }, // E1KNVIM
				{ "IDoc2", "nine", "root" }, // E1KNVKM
		};
		
		final List<String[]> tupleList = Arrays.asList(debmas06);
		
		SegmentCollector forest = IDocLoadFactory.createSegmentCollector(getEnvironment(), connection, idocTypeDEBMAS06);
		
		//////////////////////////////////////
		// IDOC1
		//////////////////////////////////////
		
		
		// E1KNA1M root
		String [] root = tupleList.get(0);
		SegmentData rootSD = new DummySegment("E1KNA1M", "0050000401258Mr.                                                                000000000000    HITE0                                              0170  US                        ALAN FAITH                                                                                                                                    BOSTON                             SUFFOLK                            1030                02110     MA             A-CRM      E                             200 200 Fremont Drive                             303-789-186                                                                                                                                                                                                         0000     0000000000                          0               2202501001                                                 00X EN                                                                           ");
		forest.insertSegment(root[0], root[1], root[2], e1kna1m, rootSD);
		
		// E1KNA11 one
		String [] one = tupleList.get(1);
		SegmentData oneSD = new DummySegment("E1KNA11", "HTTP://WWW.IBM.COM/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ");
		forest.insertSegment(one[0], one[1], one[2], e1kna11, oneSD);
		
		// E1KNVVM two
		String [] two = tupleList.get(2);
		SegmentData twoSD = new DummySegment("E1KNVVM", "00520001000        199          100FH                                9    02            02       GBP    0001                                     M                      0.0 0.0              0   ");
		forest.insertSegment(two[0], two[1], two[2], e1knvvm, twoSD);
		
		// E1KNVVM three
		String [] three = tupleList.get(3);
		SegmentData threeSD = new DummySegment("E1KNVVM", "00530201400        199          100FH                                9  X 02            02       USD    0001                                                            0.0 0.0              0   ");
		forest.insertSegment(three[0], three[1], three[2], e1knvvm, threeSD);
		
		// E1KNVIM four
		String [] four = tupleList.get(4);
		SegmentData fourSD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(four[0], four[1], four[2], e1knvim, fourSD);
		
		// E1KNVIM five
		String [] five = tupleList.get(5);
		SegmentData fiveSD = new DummySegment("E1KNVIM", "005US UTXJ1");
		forest.insertSegment(five[0], five[1], five[2], e1knvim, fiveSD);
		
		// E1KNVVM six
		String [] six = tupleList.get(6);
		SegmentData sixSD = new DummySegment("E1KNVVM", "00530203000        199          100FH                                9    02            02       USD    0001                                                            0.0 0.0 X            0   ");
		forest.insertSegment(six[0], six[1], six[2], e1knvvm, sixSD);
		
		// E1KNVIM seven
		String [] seven = tupleList.get(7);
		SegmentData sevenSD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(seven[0], seven[1], seven[2], e1knvim, sevenSD);
		
		
		// E1KNVKM ten
		String [] eight = tupleList.get(8);
		SegmentData eightSD = new DummySegment("E1KNVKM", "0050000000954ALAN                               FAITH                                              0000000000                Herr                              1E1967-12-310000000000                                                         00:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:00               A-CRM                                                   EN");
		forest.insertSegment(eight[0], eight[1], eight[2], e1knvkm, eightSD);
		
		//////////////////////////////////////
		// IDOC2
		//////////////////////////////////////
		
		// CONTROL_RECORD
		ControlRecordData crData = new DummyControlRecordData("IDoc2", "DEBMAS06", "DEBMAS", "ZLSIO42", "LS", "T90CLNT090", "LS", "");
		forest.setControlRecord("IDoc2", crData);
		
		// E1KNA1M root
		String [] root1 = tupleList.get(9);
		SegmentData root1SD = new DummySegment("E1KNA1M", "0050000401258Mr.                                                                000000000000    HITE0                                              0170  US                        ALAN FÄITH                                                                                                                                    BOSTON                             SUFFOLK                            1030                02110     MA             A-CRM      E                             200 200 Fremont Drive                             303-789-186                                                                                                                                                                                                         0000     0000000000                          0               2202501001                                                 00X EN                                                                           ");
		forest.insertSegment(root1[0], root1[1], root1[2], e1kna1m, root1SD);
		
		// E1KNA11 one
		String [] one1 = tupleList.get(10);
		SegmentData one1SD = new DummySegment("E1KNA11", "HTTP://WWW.SAP.COM/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ");
		forest.insertSegment(one1[0], one1[1], one1[2], e1kna11, one1SD);
		
		// E1KNVVM two
		String [] two1 = tupleList.get(11);
		SegmentData two1SD = new DummySegment("E1KNVVM", "00520001000        199          100FH                                9    02            02       GBP    0001                                     M                      0.0 0.0              0   ");
		forest.insertSegment(two1[0], two1[1], two1[2], e1knvvm, two1SD);
		
		
		// E1KNVVM six
		String [] six1 = tupleList.get(12);
		SegmentData six1SD = new DummySegment("E1KNVVM", "00530201400        199          100FH                                9  X 02            02       USD    0001                                                            0.0 0.0              0   ");
		forest.insertSegment(six1[0], six1[1], six1[2], e1knvvm, six1SD);
		
		
		// E1KNVIM seven
		String [] seven1 = tupleList.get(13);
		SegmentData seven1SD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(seven1[0], seven1[1], seven1[2], e1knvim, seven1SD);
		
		// E1KNVIM eight
		String [] eight1 = tupleList.get(14);
		SegmentData eight1SD = new DummySegment("E1KNVIM", "005US UTXJ1");
		forest.insertSegment(eight1[0], eight1[1], eight1[2], e1knvim, eight1SD);
		
		// E1KNVKM nine
		String [] nine1 = tupleList.get(15);
		SegmentData nine1SD = new DummySegment("E1KNVKM", "0050000000954ALAN                               FAIßT                                              0000000000                Herr                              1E1967-12-310000000000                                                         00:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:00               A-CRM                                                   EN");
		forest.insertSegment(nine1[0], nine1[1], nine1[2], e1knvkm, nine1SD);
	
		return forest;
	}
	
	
	/**
	 * createSingleDEBMAS06
	 * 
	 * create a SegmentCollector containing
	 * the following DEBMAS06 IDoc
	 * 
	 * E1KNA1M
	 * 	E1KNA11
	 * 	E1KNVVM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVKM
	 * 
	 * 
	 * @return
	 */
	public SegmentCollector createSingleDEBMAS06() {
		
		String[][] debmas06 = { // DEBMAS06
				{ "IDoc1", "root", "000" }, // E1KNA1M
				{ "IDoc1", "one", "root" }, // E1KNA11
				{ "IDoc1", "two", "root" }, // E1KNVVM
				{ "IDoc1", "three", "root" }, // E1KNVVM
				{ "IDoc1", "four", "three" }, // E1KNVIM
				{ "IDoc1", "five", "three" }, // E1KNVIM
				{ "IDoc1", "six", "root" }, // E1KNVVM
				{ "IDoc1", "seven", "six" }, // E1KNVIM
				{ "IDoc1", "eight", "six" }, // E1KNVIM
				{ "IDoc1", "nine", "root" }, // E1KNVKM
		};
		
		final List<String[]> tupleList = Arrays.asList(debmas06);
		
		SegmentCollector forest = IDocLoadFactory.createSegmentCollector(getEnvironment(), null, null);
		
		// E1KNA1M root
		String [] root = tupleList.get(0);
		SegmentData rootSD = new DummySegment("E1KNA1M", "0050000401258Mr.                                                                000000000000    HITE0                                              0170  US                        ALAN FAITH                                                                                                                                    BOSTON                             SUFFOLK                            1030                02110     MA             A-CRM      E                             200 200 Fremont Drive                             303-789-186                                                                                                                                                                                                         0000     0000000000                          0               2202501001                                                 00X EN                                                                           ");
		forest.insertSegment(root[0], root[1], root[2], e1kna1m, rootSD);
		
//		// E1KNA11 one
//		String [] one = tupleList.get(1);
//		SegmentData oneSD = new DummySegment("E1KNA11", "HTTP://WWW.IBM.COM/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ");
//		forest.insertSegment(one[0], one[1], one[2], e1kna11, oneSD);
//		
//		// E1KNVVM two
//		String [] two = tupleList.get(2);
//		SegmentData twoSD = new DummySegment("E1KNVVM", "00520001000        199          100FH                                9    02            02       GBP    0001                                     M                      0.0 0.0              0   ");
//		forest.insertSegment(two[0], two[1], two[2], e1knvvm, twoSD);
//		
//		// E1KNVVM three
//		String [] three = tupleList.get(3);
//		SegmentData threeSD = new DummySegment("E1KNVVM", "00530201400        199          100FH                                9  X 02            02       USD    0001                                                            0.0 0.0              0   ");
//		forest.insertSegment(three[0], three[1], three[2], e1knvvm, threeSD);
//		
//		// E1KNVIM four
//		String [] four = tupleList.get(4);
//		SegmentData fourSD = new DummySegment("E1KNVIM", "005DE MWST1");
//		forest.insertSegment(four[0], four[1], four[2], e1knvim, fourSD);
//		
//		// E1KNVIM five
//		String [] five = tupleList.get(5);
//		SegmentData fiveSD = new DummySegment("E1KNVIM", "005US UTXJ1");
//		forest.insertSegment(five[0], five[1], five[2], e1knvim, fiveSD);
//		
//		// E1KNVVM six
//		String [] six = tupleList.get(6);
//		SegmentData sixSD = new DummySegment("E1KNVVM", "00530203000        199          100FH                                9    02            02       USD    0001                                                            0.0 0.0 X            0   ");
//		forest.insertSegment(six[0], six[1], six[2], e1knvvm, sixSD);
//		
//		// E1KNVIM seven
//		String [] seven = tupleList.get(7);
//		SegmentData sevenSD = new DummySegment("E1KNVIM", "005DE MWST1");
//		forest.insertSegment(seven[0], seven[1], seven[2], e1knvim, sevenSD);
//		
//		// E1KNVIM eight
//		String [] eight = tupleList.get(8);
//		SegmentData eightSD = new DummySegment("E1KNVIM", "005US UTXJ1");
//		forest.insertSegment(eight[0], eight[1], eight[2], e1knvim, eightSD);
//		
//		// E1KNVKM ten
//		String [] nine = tupleList.get(9);
//		SegmentData nineSD = new DummySegment("E1KNVKM", "0050000000954ALAN                               FAITH                                              0000000000                Herr                              1E1967-12-310000000000                                                         00:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:00               A-CRM                                                   EN");
//		forest.insertSegment(nine[0], nine[1], nine[2], e1knvkm, nineSD);
		
		
		return forest;
	}
	
	/**
	 * create1000DEBMAS06
	 * 
	 * create a SegmentCollector containing
	 * the following DEBMAS06 IDoc 1000 times
	 * 
	 * E1KNA1M
	 * 	E1KNA11
	 * 	E1KNVVM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVKM
	 * 
	 * 
	 * @return
	 */
	public SegmentCollector create1000DEBMAS06() {
		
		SegmentCollector forest = IDocLoadFactory.createSegmentCollector(getEnvironment(), connection, idocTypeDEBMAS06);
		
		for(int i=0; i<1000; i++) {
		
		String docNum = String.valueOf(i);
		
		String[][] debmas06 = { // DEBMAS06
				{ docNum, "root", "000" }, // E1KNA1M
				{ docNum, "one", "root" }, // E1KNA11
				{ docNum, "two", "root" }, // E1KNVVM
				{ docNum, "three", "root" }, // E1KNVVM
				{ docNum, "four", "three" }, // E1KNVIM
				{ docNum, "five", "three" }, // E1KNVIM
				{ docNum, "six", "root" }, // E1KNVVM
				{ docNum, "seven", "six" }, // E1KNVIM
				{ docNum, "eight", "six" }, // E1KNVIM
				{ docNum, "nine", "root" }, // E1KNVKM
		};
		
		final List<String[]> tupleList = Arrays.asList(debmas06);
		
	
		
		// E1KNA1M root
		String [] root = tupleList.get(0);
		SegmentData rootSD = new DummySegment("E1KNA1M", "0050000401258Mr.                                                                000000000000    HITE0                                              0170  US                        ALAN FAITH                                                                                                                                    BOSTON                             SUFFOLK                            1030                02110     MA             A-CRM      E                             200 200 Fremont Drive                             303-789-186                                                                                                                                                                                                         0000     0000000000                          0               2202501001                                                 00X EN                                                                           ");
		forest.insertSegment(root[0], root[1], root[2], e1kna1m, rootSD);
		
		// E1KNA11 one
		String [] one = tupleList.get(1);
		SegmentData oneSD = new DummySegment("E1KNA11", "HTTP://WWW.IBM.COM/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ");
		forest.insertSegment(one[0], one[1], one[2], e1kna11, oneSD);
		
		// E1KNVVM two
		String [] two = tupleList.get(2);
		SegmentData twoSD = new DummySegment("E1KNVVM", "00520001000        199          100FH                                9    02            02       GBP    0001                                     M                      0.0 0.0              0   ");
		forest.insertSegment(two[0], two[1], two[2], e1knvvm, twoSD);
		
		// E1KNVVM three
		String [] three = tupleList.get(3);
		SegmentData threeSD = new DummySegment("E1KNVVM", "00530201400        199          100FH                                9  X 02            02       USD    0001                                                            0.0 0.0              0   ");
		forest.insertSegment(three[0], three[1], three[2], e1knvvm, threeSD);
		
		// E1KNVIM four
		String [] four = tupleList.get(4);
		SegmentData fourSD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(four[0], four[1], four[2], e1knvim, fourSD);
		
		// E1KNVIM five
		String [] five = tupleList.get(5);
		SegmentData fiveSD = new DummySegment("E1KNVIM", "005US UTXJ1");
		forest.insertSegment(five[0], five[1], five[2], e1knvim, fiveSD);
		
		// E1KNVVM six
		String [] six = tupleList.get(6);
		SegmentData sixSD = new DummySegment("E1KNVVM", "00530203000        199          100FH                                9    02            02       USD    0001                                                            0.0 0.0 X            0   ");
		forest.insertSegment(six[0], six[1], six[2], e1knvvm, sixSD);
		
		// E1KNVIM seven
		String [] seven = tupleList.get(7);
		SegmentData sevenSD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(seven[0], seven[1], seven[2], e1knvim, sevenSD);
		
		// E1KNVIM eight
		String [] eight = tupleList.get(8);
		SegmentData eightSD = new DummySegment("E1KNVIM", "005US UTXJ1");
		forest.insertSegment(eight[0], eight[1], eight[2], e1knvim, eightSD);
		
		// E1KNVKM ten
		String [] nine = tupleList.get(9);
		SegmentData nineSD = new DummySegment("E1KNVKM", "0050000000954ALAN                               FAITH                                              0000000000                Herr                              1E1967-12-310000000000                                                         00:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:00               A-CRM                                                   EN");
		forest.insertSegment(nine[0], nine[1], nine[2], e1knvkm, nineSD);
		
		}
		
		return forest;
	}
	
	
	
	
	/**
	 * createSingleDEBMAS06WithCR
	 * 
	 * create a SegmentCollector containing
	 * the following DEBMAS06 IDoc
	 * 
	 * CONTROL_RECORD
	 * E1KNA1M
	 * 	E1KNA11
	 * 	E1KNVVM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVVM
	 * 		E1KNVIM
	 * 		E1KNVIM
	 * 	E1KNVKM
	 * 
	 * 
	 * @return
	 */
	public SegmentCollector createSingleDEBMAS06WithCR() {
		
		String[][] debmas06 = { // DEBMAS06
				{ "IDoc1", "root", "000" }, // E1KNA1M
				{ "IDoc1", "one", "root" }, // E1KNA11
				{ "IDoc1", "two", "root" }, // E1KNVVM
				{ "IDoc1", "three", "root" }, // E1KNVVM
				{ "IDoc1", "four", "three" }, // E1KNVIM
				{ "IDoc1", "five", "three" }, // E1KNVIM
				{ "IDoc1", "six", "root" }, // E1KNVVM
				{ "IDoc1", "seven", "six" }, // E1KNVIM
				{ "IDoc1", "eight", "six" }, // E1KNVIM
				{ "IDoc1", "nine", "root" }, // E1KNVKM
		};
		
		final List<String[]> tupleList = Arrays.asList(debmas06);
		
		SegmentCollector forest = IDocLoadFactory.createSegmentCollector(getEnvironment(), connection, idocTypeDEBMAS06);
		
		// CONTROL_RECORD
		ControlRecordData crData = new DummyControlRecordData("IDoc1", "DEBMAS06", "DEBMAS", "ZLSIO42", "LS", "T90CLNT090", "LS", "");
		forest.setControlRecord("IDoc1", crData);
		
		// E1KNA1M root
		String [] root = tupleList.get(0);
		SegmentData rootSD = new DummySegment("E1KNA1M", "0050000401258Mr.                                                                000000000000    HITE0                                              0170  US                        ALAN FAITH                                                                                                                                    BOSTON                             SUFFOLK                            1030                02110     MA             A-CRM      E                             200 200 Fremont Drive                             303-789-186                                                                                                                                                                                                         0000     0000000000                          0               2202501001                                                 00X EN                                                                           ");
		forest.insertSegment(root[0], root[1], root[2], e1kna1m, rootSD);
		
		// E1KNA11 one
		String [] one = tupleList.get(1);
		SegmentData oneSD = new DummySegment("E1KNA11", "HTTP://WWW.IBM.COM/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ");
		forest.insertSegment(one[0], one[1], one[2], e1kna11, oneSD);
		
		// E1KNVVM two
		String [] two = tupleList.get(2);
		SegmentData twoSD = new DummySegment("E1KNVVM", "00520001000        199          100FH                                9    02            02       GBP    0001                                     M                      0.0 0.0              0   ");
		forest.insertSegment(two[0], two[1], two[2], e1knvvm, twoSD);
		
		// E1KNVVM three
		String [] three = tupleList.get(3);
		SegmentData threeSD = new DummySegment("E1KNVVM", "00530201400        199          100FH                                9  X 02            02       USD    0001                                                            0.0 0.0              0   ");
		forest.insertSegment(three[0], three[1], three[2], e1knvvm, threeSD);
		
		// E1KNVIM four
		String [] four = tupleList.get(4);
		SegmentData fourSD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(four[0], four[1], four[2], e1knvim, fourSD);
		
		// E1KNVIM five
		String [] five = tupleList.get(5);
		SegmentData fiveSD = new DummySegment("E1KNVIM", "005US UTXJ1");
		forest.insertSegment(five[0], five[1], five[2], e1knvim, fiveSD);
		
		// E1KNVVM six
		String [] six = tupleList.get(6);
		SegmentData sixSD = new DummySegment("E1KNVVM", "00530203000        199          100FH                                9    02            02       USD    0001                                                            0.0 0.0 X            0   ");
		forest.insertSegment(six[0], six[1], six[2], e1knvvm, sixSD);
		
		// E1KNVIM seven
		String [] seven = tupleList.get(7);
		SegmentData sevenSD = new DummySegment("E1KNVIM", "005DE MWST1");
		forest.insertSegment(seven[0], seven[1], seven[2], e1knvim, sevenSD);
		
		// E1KNVIM eight
		String [] eight = tupleList.get(8);
		SegmentData eightSD = new DummySegment("E1KNVIM", "005US UTXJ1");
		forest.insertSegment(eight[0], eight[1], eight[2], e1knvim, eightSD);
		
		// E1KNVKM ten
		String [] nine = tupleList.get(9);
		SegmentData nineSD = new DummySegment("E1KNVKM", "0050000000954ALAN                               FAITH                                              0000000000                Herr                              1E1967-12-310000000000                                                         00:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:0000:00:00               A-CRM                                                   EN");
		forest.insertSegment(nine[0], nine[1], nine[2], e1knvkm, nineSD);
		
		
		
		return forest;
	}
	
	protected DSEnvironment getEnvironment() {
		DSEnvironment env = new DSEnvironment();
		env.setJobName("DUMMYJOB");
		env.setInvocationID("DUMMYINVOCATIONID");
		env.setProjectName("DUMMYPROJECT");
		return env;
	}
}
