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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.ibm.datatools.core.ui.plugin.DMPlugin;
import com.ibm.db.models.logical.Entity;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.SapIDocTypeBrowser;
import com.sap.conn.jco.JCoException;


public class Utilities {

	public static class ForceNumericInput implements VerifyListener {
		public void verifyText(VerifyEvent verifyEv) {
			Text srcText = (Text) verifyEv.getSource();

			// no numeric check if it's a job parameter
			// ==> neither the 'text to verify' nor the entry field text 
			//     may contain a Job Parameter character
			if (verifyEv.text.indexOf(com.ibm.is.sappack.gen.tools.sap.constants.Constants.JOB_PARAM_SEPARATOR)     < 0    && 
				 srcText.getText().indexOf(com.ibm.is.sappack.gen.tools.sap.constants.Constants.JOB_PARAM_SEPARATOR) < 0) {
				Matcher patMatcher = com.ibm.is.sappack.gen.common.Constants.NUMERIC_PATTERN.matcher(verifyEv.text);
				if (!patMatcher.matches()) {
					verifyEv.doit = false;
				}
			}
		}
	}
	
	public static class ForceUppercaseLetters implements VerifyListener {
		public void verifyText(VerifyEvent verifyEv) {
			Text srcText = (Text) verifyEv.getSource();

			// no uppercase if it's a job parameter
			if (srcText.getText().indexOf(com.ibm.is.sappack.gen.tools.sap.constants.Constants.JOB_PARAM_SEPARATOR) < 0) {
				verifyEv.text = verifyEv.text.toUpperCase();
			}
		}
	}
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	public static String getTextFieldValue(Text text) {
		String textValue = null;
		if (text != null) {
			String s = text.getText().trim();

			if (!s.isEmpty()) {
				textValue = s;
			}
		}
		return textValue;
	}

	/*
	 * Return the text field value as an integer. Return Integer.MAX_VALUE if value is invalid.
	 */
	public static int getTextFieldValueAsInt(Text text) {
		String s = getTextFieldValue(text);
		if (s == null) {
			return Integer.MAX_VALUE;
		}
		int result = Integer.MAX_VALUE;
		try {
			result = Integer.parseInt(s);
		} catch(NumberFormatException exc) {
			return Integer.MAX_VALUE;
		}
		return result;
	}

	public static final boolean isEmpty(String text) {
		return ((text == null) || text.trim().equalsIgnoreCase(Constants.EMPTY_STRING));
	}

   public static final boolean containsAllowedChars(String text) {
      boolean hasAllowedChars = false;
      
      if (!isEmpty(text)) {
         hasAllowedChars = Pattern.matches(".*[\\w+].*", text);   //$NON-NLS-1$
      }

      return(hasAllowedChars);
   }

   public static final boolean containsOnlyWildcardChar(String searchExpression) {
      int     idx;
      boolean containsWildcard;

      containsWildcard = true;
      idx              = 0; 
      while(idx < searchExpression.length() && containsWildcard) {
         if (searchExpression.charAt(idx) != '*' && searchExpression.charAt(idx) != '%') {
            containsWildcard = false;   
         }
         idx ++;         
      }
      
      return(containsWildcard);
   }
	
	
   /**
    * getSelectButtonIndex
    * 
    * returns the index of the (first) selected button in the passed button array.
    * 
    * @param buttonArr array of buttons to be checked
    * 
    * @return index or -1 if no button is selected
    */
   public static int getSelectButtonIndex(Button buttonArr[]) {
      int arrIdx;
      int retSelectedIdx = -1;
      
      arrIdx = 0;
      while(arrIdx < buttonArr.length && retSelectedIdx < 0) {
         if (buttonArr[arrIdx].getSelection()) {
            retSelectedIdx = arrIdx;
         }
         arrIdx ++;
      }
      
      return(retSelectedIdx);
   }

   
	/**
	 * isJobParameter
	 * 
	 * checks if the given String starts and ends with a '#' character
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isJobParameter(String text) {

		if (text != null) {
			if (text.startsWith(Constants.HASH) && text.endsWith(Constants.HASH)) {
				return true;
			}
		}
		return false;
	}

	public static byte[] readInputStream(InputStream is) throws IOException {
		byte[] result = new byte[0];
		final int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		int i;
		while ((i = is.read(tmp)) != -1) {
			byte[] old = result;
			result = new byte[old.length + i];
			System.arraycopy(old, 0, result, 0, old.length);
			System.arraycopy(tmp, 0, result, old.length, i);
		}
		return result;
	}

	public static void setEnabledComposite(Composite comp, boolean enable) {
      comp.setEnabled(enable);
      
      for (Control childCtrl: comp.getChildren()) {
         if (childCtrl instanceof Composite) {
            setEnabledComposite((Composite) childCtrl, enable);
         }
         else {
            childCtrl.setEnabled(enable);
         }
      }
   }
   
   
   public static void disableCompositeWithExceptions(Composite comp) {
      comp.setEnabled(false);
      
      disableCompositeChildren(comp);
   }


   private static void disableCompositeChildren(Composite comp) {
      for (Control childCtrl: comp.getChildren()) {
         
         // Check and Radio Buttons only 
         if (childCtrl instanceof Button) {
            Button tmpButton = (Button) childCtrl;

            if ((tmpButton.getStyle() & SWT.RADIO) == SWT.RADIO || 
                (tmpButton.getStyle() & SWT.CHECK) == SWT.CHECK)
            {
               childCtrl.setEnabled(true);
               
               if (!tmpButton.getSelection()) {
                  childCtrl.setEnabled(false);
               }
            }
         }
         
         if (childCtrl instanceof Composite) {
            disableCompositeChildren((Composite) childCtrl);
         }
      }
   }


	public static boolean isValidIDocTypeName(String s) {
		if (s == null) {
			return false;
		}
		if (s.trim().isEmpty()) {
			return false;
		}
		boolean result = Pattern.matches("[\\w/]*", s); //$NON-NLS-1$
		return result;
	}

	public static boolean containsIgnoreCase(Collection<String> coll, String s) {
		if (coll == null) {
			return false;
		}
		for (String it : coll) {
			if (it.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		try {
			String s = "SDFKLSFA367*, sfj" + "";
			boolean b = Pattern.matches("\\w*", s);
			System.out.println("Matches: " + b);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String[] getExtractedIDocTypes(LdmAccessor acc) {
		Set<String> result = new HashSet<String>();
		for (Entity e : acc.getAllEntities()) {
			Map<String, String> annots = LdmAccessor.getAnnotations(e);
			String idocType = annots.get(com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_TYPE);
			if (idocType != null) {
				result.add(idocType);
			}
		}
		return result.toArray(new String[0]);
	}

	public static IDocType getIDocType(String idocTypeName, boolean isExtension, String version, SapSystem sapSystem) throws JCoException {
		String idocType = idocTypeName;
		if (idocType.trim().isEmpty()) {
			return null;
		}

		SapIDocTypeBrowser idocBrowser;
		idocBrowser = new SapIDocTypeBrowser(sapSystem, isExtension, version);
		List<IDocType> idocTypes = idocBrowser.listIDoctypes(idocType);

		if (Utilities.isEmpty(sapSystem.getPassword())) {
			return null;
		}

		if (idocTypes.size() == 0) {
			return null;
		}
		return idocTypes.get(0);
	}

	public static void createEmptyLDM(IFile newFile) throws IOException, CoreException {
		DMPlugin dmp = DMPlugin.getDefault();
		IPath templatePath = new Path("templates/LogicalModel/BlankLogicalDataModel.template"); //$NON-NLS-1$
		InputStream is = FileLocator.openStream(dmp.getBundle(), templatePath, false);

		Map<String, Comparable<?>> options = new HashMap<String, Comparable<?>>();
		options.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
		options.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$

		Resource resource = com.ibm.datatools.internal.core.resource.ResourceUtil.importFromTemplate(URI.createURI(newFile.getName()), is, options);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		resource.save(baos, options);
		byte[] b = baos.toByteArray();

		newFile.create(new ByteArrayInputStream(b), false, null);
	}
	
	public static boolean endsWith(IFile f, String fileExtension) {
		return f.getName().endsWith(fileExtension);
	}

	
}
