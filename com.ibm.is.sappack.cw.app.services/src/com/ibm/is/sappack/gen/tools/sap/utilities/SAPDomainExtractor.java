package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class SAPDomainExtractor {
	
	private static class ResultImpl implements IResult {

		private JCoTable data;
		private int currentRowPosition = 0;
		private Map<String, String> currentValue;
		private boolean done = false;
		
		public ResultImpl(JCoTable data) {
			this.data = data;
			this.done = data.getNumRows() == 0;
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
				String value = data.getString(fieldName);
				currentValue.put(fieldName, value);
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
	JCoFunction function_DD_DOMA_GET;
	String domainName;
	String tableLayout;
	String descrLanguage;
	
	public SAPDomainExtractor(JCoDestination destination, String domainName, String loadLanguage) throws JCoException {
		this.destination = destination;
		this.function_DD_DOMA_GET = destination.getRepository().getFunction("DD_DOMA_GET"); //$NON-NLS-1$
		this.domainName = domainName;
		this.descrLanguage = loadLanguage;
	}
	
	public IResult performQuery() throws JCoException {
		JCoParameterList imports = this.function_DD_DOMA_GET.getImportParameterList();
		imports.setValue("DOMAIN_NAME", this.domainName);
		imports.setValue("LANGU", this.descrLanguage);
		JCoStructure stateStruct = imports.getStructure("GET_STATE");
		stateStruct.setValue("DOMA", "A");
		
		this.function_DD_DOMA_GET.execute(this.destination);

		JCoTable data = this.function_DD_DOMA_GET.getTableParameterList().getTable("DD07V_TAB_A");
		
		IResult res = new ResultImpl(data);
		
		return res;
	}
}
