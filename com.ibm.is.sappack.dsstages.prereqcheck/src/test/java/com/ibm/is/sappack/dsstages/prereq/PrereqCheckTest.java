//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.prereq
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.prereq;


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.Permission;
import java.util.Scanner;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author dsh
 *
 */
public class PrereqCheckTest extends TestCase {
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.prereq.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	private PrereqCheck check = null;
	private SecurityManager securityManager = null;
	
	private Scanner stdin = null;
	private PrintStream stdout = null;
	
	
	private Scanner stdinWrongTier = null;
	private PrintStream stdoutWrongTier = null;
	
	private static final String IN_PREFIX = "PrereqCheckTest";
	private static final String WRONG_TIER_PREFIX = "PrereqCheckTest-WRONG-TIER";
	private static final String STDIN = IN_PREFIX + ".in.txt";
	private static final String STDOUT = IN_PREFIX + ".out.txt";
	private static final String WRONG_TIER_STDIN = WRONG_TIER_PREFIX + ".in.txt";
	private static final String WRONG_TIER_STDOUT = WRONG_TIER_PREFIX + ".out.txt";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.check = new PrereqCheck();
		this.stdinWrongTier = new Scanner(PrereqCheckTest.class.getClassLoader().getResourceAsStream(PrereqCheckTest.WRONG_TIER_STDIN));
		this.stdoutWrongTier = new PrintStream(new FileOutputStream(PrereqCheckTest.WRONG_TIER_STDOUT));
		this.stdin = new Scanner(PrereqCheckTest.class.getClassLoader().getResourceAsStream(PrereqCheckTest.STDIN));
		this.stdout = new PrintStream(new FileOutputStream(PrereqCheckTest.STDOUT));
	    this.securityManager = System.getSecurityManager();
	    System.setSecurityManager(new NoExitSecurityManager());
	}
	
	public void testHasMessages() {
		this.check.argumentsToMembers(stdin);
		assertFalse(this.check.hasMessage());
	}
	
	public void testHasNoErrorMessage() {
		this.check.argumentsToMembers(stdin);
		assertFalse(this.check.hasErrorMessage());
	}
	
	public void testOutputFileExists() {
		this.check.argumentsToMembers(stdin);
		assertTrue(null != this.check.getOutputFile() && this.check.getOutputFile().length() > 0);
	}
	
	public void testTitleStringExists() {
		this.check.argumentsToMembers(stdin);
		assertTrue(null != this.check.getTitleString() && this.check.getTitleString().length() > 0);		
	}
	
	public void testTierExists() {
		this.check.argumentsToMembers(stdin);
		assertTrue(null != this.check.getTier() && this.check.getTier().length() > 0);
	}
	
	public void testTierIsNotValid() {
		// negative testing
		this.check.argumentsToMembers(stdinWrongTier);
		assertFalse(this.check.tierIsValid());
	}
	
	public void testTierIsValid() {
		// positive testing
		this.check.argumentsToMembers(stdin);
		assertTrue(this.check.tierIsValid());
	}
	
	public void testIISHomeStringIsValid() {
		this.check.argumentsToMembers(stdin);
		assertTrue(null != this.check.getIishome() && this.check.getIishome().length() > 0);
	}
	
	public void testIISHomeDirExists() {
		this.check.argumentsToMembers(stdin);
		assertTrue(null != this.check.getIisHomeDir() && this.check.getIisHomeDir().exists());
	}
	
	public void testIISHomeDirIsDirectory() {
		this.check.argumentsToMembers(stdin);
		assertTrue(null != this.check.getIisHomeDir() && this.check.getIisHomeDir().exists() && this.check.getIisHomeDir().isDirectory());
	}
	
	public void testMainMethod() {
		PrereqCheck.mainWrapper(stdin, stdout);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.check = null;
		this.stdin.close();
		this.stdout.close();
		this.stdinWrongTier.close();
		this.stdoutWrongTier.close();
		System.setSecurityManager(this.securityManager);
	}

	protected static class ExitException extends SecurityException {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 8289108979925965627L;
		public final int status;

	    public ExitException(int status) {
	        super("There is no escape!");
	        this.status = status;
	    }
	}

	private static class NoExitSecurityManager extends SecurityManager {
	    @Override
	    public void checkPermission(Permission perm) {
	        // allow anything.
	    }

	    @Override
	    public void checkPermission(Permission perm, Object context) {
	        // allow anything.
	    }

	    @Override
	    public void checkExit(int status) {
	        super.checkExit(status);
	        throw new ExitException(status);
	    }
	}

}
