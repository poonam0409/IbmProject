package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.is.sappack.cw.app.data.SAPField;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class SAPTableFieldsExtractor {
	
	private static final String GETTER_PREFIX = "get";

	private static class ResultImpl implements IResult {
		
		private final JCoTable data;
		private int currentRowPosition = 0;
		private Map<String, String> currentValue;
		private boolean done = false;
		private HashMap<String, String> sapFieldAnnotationsMap = null;
		
		public ResultImpl(JCoTable data, Class<Field> fieldClass) {
			this.data = data;
			this.done = data.getNumRows() == 0;
			this.sapFieldAnnotationsMap = new HashMap<String, String>();
			
			try {
				Method[] objectMethods = fieldClass.getDeclaredMethods();
				
				for (int i = 0; i < objectMethods.length; i++) {
					Method m = objectMethods[i];
					
					// we only check methods with the prefix 'get' 
					if (m.getName().startsWith(GETTER_PREFIX)) {
						
						// check if our custom annotation 'SAPField' is present
						if (m.isAnnotationPresent(SAPField.class)) {
							
							// if it is present we extract the annotation values
							// and put them in a hash map like this:
							// key == sapName
							// value == objectName
							Annotation annot = m.getAnnotation(SAPField.class);
							String sapName = ((SAPField) annot).sapName();
							String objectName = ((SAPField) annot).objectName();
							sapFieldAnnotationsMap.put(sapName, objectName);
						}						
					}
				}
			}
			catch (SecurityException se){
				se.printStackTrace();
			}
		}

		@Override
		public int getCurrentRowPosition() {
			return currentRowPosition;
		}

		@Override
		public int getNumberOfRows() {
			return data.getNumRows();
		}

		@Override
		public boolean nextRow() {
			if (done) {
				return false;
			}

			currentValue = new HashMap<String, String>();
			
			data.setRow(currentRowPosition);
			Iterator<JCoField> it = data.iterator();
			
			while (it.hasNext()) {
				String fieldName = it.next().getName();
				
				// we only extract the required columns
				for (Entry<String, String> entry : this.sapFieldAnnotationsMap.entrySet()) {
					if (entry.getKey().equalsIgnoreCase(fieldName)) {
						String value = data.getString(fieldName);
						currentValue.put(entry.getValue(), value);
					}
				}
			}
			
			if (!data.nextRow()) {
				done = true;
			}
			
			currentRowPosition++;
			
			return true;
		}

		@Override
		public String getValue(String columnName) {
			return this.currentValue.get(columnName);
		}

		@Override
		public Map<String, String> getRow() {
			return currentValue;
		}
	}

	JCoDestination destination;
	JCoFunction function_DDIF_FIELDINFO_GET;
	String tableName;
	String descrLanguage;

	public SAPTableFieldsExtractor(JCoDestination destination, String tableName, String loadLanguage) throws JCoException {
		this.destination = destination;
		this.function_DDIF_FIELDINFO_GET = destination.getRepository().getFunction("DDIF_FIELDINFO_GET"); //$NON-NLS-1$
		this.tableName = tableName;
		this.descrLanguage = loadLanguage;
	}
	
	public IResult performQuery() throws JCoException {
		JCoParameterList imports = this.function_DDIF_FIELDINFO_GET.getImportParameterList();
		imports.setValue("TABNAME", this.tableName);
		imports.setValue("LANGU", this.descrLanguage);
		
		this.function_DDIF_FIELDINFO_GET.execute(this.destination);

		JCoTable data = this.function_DDIF_FIELDINFO_GET.getTableParameterList().getTable("DFIES_TAB");
		
		IResult res = new ResultImpl(data, Field.class);
		
		return res;
	}
}
