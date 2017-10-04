//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
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


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class CommandLineExec {
	static String copyright() {
		return com.ibm.is.sappack.dsstages.prereq.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static class ExecutionResult {
		public String stdoutput;
		public String stderr;

		public int returnCode;
	}

	private static class ProcessStreamBuffer {
		StringBuffer result = new StringBuffer();
		Thread th;

		public ProcessStreamBuffer(InputStream is) {
			final InputStream bis = new BufferedInputStream(is);
			th = new Thread(new Runnable() {

				public void run() {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];

					while (true) {
						int size;
						try {
							size = bis.read(buffer);
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}

						if (size == -1) {
							break;
						}

						byteArrayOutputStream.write(buffer, 0, size);
					}

					result.append(new String(byteArrayOutputStream.toByteArray()));
				}
			});

			th.start();
		}
		public void waitFor() {
			try {
				th.join(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public String getContent() {
			return result.toString();
		}
	}

	public ExecutionResult runCommandLine(String[] command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		return runProcess(p);
	}

	public ExecutionResult runCommandLine(String command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		return runProcess(p);
	}

	public ExecutionResult runCommandLineNoInput(String command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);

		// close the OutputStream first
		try {
			p.getOutputStream().close();
		}
		catch(IOException ioExcpt) {
			ioExcpt.printStackTrace();
		}

		return runProcess(p);
	}

	private ExecutionResult runProcess(Process p) {
		ProcessStreamBuffer stdout = new ProcessStreamBuffer(p.getInputStream());
		ProcessStreamBuffer stderr = new ProcessStreamBuffer(p.getErrorStream());
		int exitValue = -1;

		while (true) {
			/*
			 * try { Thread.sleep(200); } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 */

			try {
				exitValue = p.exitValue();
			} catch (IllegalThreadStateException itse) {
				// process not yet terminated -> continue to listen
				continue;
			}

			break;
		}

		stdout.waitFor();
		stderr.waitFor();

		ExecutionResult result = new ExecutionResult();
		result.returnCode = exitValue;
		result.stdoutput = stdout.getContent();
		result.stderr = stderr.getContent();

		return result;
	}

}
