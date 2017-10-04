//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.iisclient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.is.sappack.gen.common.ui.Activator;

public class CommandLineExec {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static class ExecutionResult {
		String stdoutput;
		String stderr;

		int returnCode;
	}

	private static class ProcessStreamBuffer {
		StringBuffer result = new StringBuffer();
		Thread th;

		public ProcessStreamBuffer(InputStream is) {
			final InputStream bis = new BufferedInputStream(is);
			th = new Thread(new Runnable() {

				@Override
				public void run() {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];

					while (true) {
						int size;
						try {
							size = bis.read(buffer);
						} catch (IOException e) {
							Activator.logException(e);
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

	public ExecutionResult runCommandLine(String[] command, IProgressMonitor monitor) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		return runProcess(p, monitor);
	}

	public ExecutionResult runCommandLine(String command, IProgressMonitor monitor) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		return runProcess(p, monitor);
	}

	private ExecutionResult runProcess(Process p, IProgressMonitor monitor) {
		ProcessStreamBuffer stdout = new ProcessStreamBuffer(p.getInputStream());
		ProcessStreamBuffer stderr = new ProcessStreamBuffer(p.getErrorStream());
		int exitValue = -1;

		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (monitor != null) {
				monitor.worked(1);
			}

			if (monitor != null && monitor.isCanceled()) {
				p.destroy();
				break;
			}

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

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		try {
			CommandLineExec e = new CommandLineExec();
			String[] command = { "ls", "-l", "C:\\IBM\\InformationServer\\Clients\\Classic" };
			ExecutionResult r = e.runCommandLine(command, null);

			System.out.println("Result: " + r.returnCode);
			System.out.println("--------------------------------\nstdout: " + r.stdoutput);
			System.out.println("--------------------------------\nstderr: " + r.stderr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
