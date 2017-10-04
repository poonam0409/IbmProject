package com.ibm.iis.sappack.gen.common.ui.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class FileHelper {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	public static final String FILE_ENCODING = "UTF-8"; //$NON-NLS-1$

	public static void load(IFile f, String extension, Map<String, String> properties) throws IOException, CoreException {
		if (extension != null) {
			if (!f.getName().endsWith(extension)) {
				throw new IOException(MessageFormat.format(Messages.FileHelper_0, new Object[] { f.getName(), extension }));
			}
		}
		Properties props = new Properties();
		props.loadFromXML(f.getContents());
		for (String key : props.stringPropertyNames()) {
			properties.put(key, props.getProperty(key));
		}
	}

	public static void save(IFile f, Map<String, String> properties, boolean createIfNotExistent, IProgressMonitor monitor) throws IOException, CoreException {
		Properties props = new Properties();

		props.putAll(properties);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		props.storeToXML(baos, null, FILE_ENCODING);
		baos.close();
		byte[] b = baos.toByteArray();

		InputStream is = new ByteArrayInputStream(b);
		if (!f.exists()) {
			if (createIfNotExistent) {
				f.create(is, true, monitor);
			} else {
				String msg = Messages.FileHelper_1;
				msg = MessageFormat.format(msg, f.getLocation());
				throw new IOException(msg);
			}
		} else {
			f.setContents(is, false, true, monitor);
		}
		is.close();
	}
	
	public static String getID(IFile f, String extension) throws IOException, CoreException {
		Map<String, String> m = new HashMap<String, String>();
		load(f, extension, m);
		return m.get(PropertiesConstants.KEY_ID);
	}
	
	public static String createID(IFile f) {
		return createID(f.getFullPath().toString());
	}
	
	public static String createID(String idString) {
		return "" + System.currentTimeMillis() + Math.abs(idString.hashCode()); //$NON-NLS-1$
	}

}
