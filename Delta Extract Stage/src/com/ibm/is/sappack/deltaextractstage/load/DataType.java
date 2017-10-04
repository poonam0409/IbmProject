package com.ibm.is.sappack.deltaextractstage.load;

import java.text.*;

public class DataType {
	SimpleDateFormat sdf = null;
	SimpleDateFormat tdf = null;
	
	public void ishierrarchy(boolean ishier)
	{
		if(ishier)
		{
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			tdf = new SimpleDateFormat("HH-mm-ss");
		}
		else
		{
			sdf = new SimpleDateFormat("yyyyMMdd");
			tdf = new SimpleDateFormat("HHmmss");
		}
	}
	
	public Object convertDataType(int datatype, String dataValue)
	{
		Object dataValActualType = null;
		switch (datatype) {
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_DECIMAL:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_DOUBLE:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_FLOAT:
			if(!(dataValue == null || dataValue.isEmpty()))
			{
				dataValue = dataValue.replaceAll(" ", "");
				dataValue = dataValue.contains("-") ? (new StringBuilder().append("-").append(dataValue.replaceAll("-", "")).toString()) : dataValue;
			dataValActualType = java.math.BigDecimal.valueOf(Double.parseDouble(dataValue));
		//	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"+++++dataValActualType+++"+dataValActualType});
			}
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_INTEGER:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_TINYINT:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_BIGINT:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_NUMERIC:
			if(!(dataValue == null || dataValue.isEmpty()))
			{
				dataValue = dataValue.replaceAll(" ", "");
				dataValue = dataValue.contains("-") ? (new StringBuilder().append("-").append(dataValue.replaceAll("-", "")).toString()) : dataValue;
				dataValActualType = Long.valueOf(dataValue);
		//		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"+++++dataValActualType+++"+dataValActualType});
			}
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_BINARY:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_BIT:
			dataValActualType = Integer.parseInt(dataValue);
	//		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"+++++dataValActualType+++"+dataValActualType});
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_CHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_WCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_VARCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_LONGVARCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_WVARCHAR:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_WLONGVARCHAR:
			dataValActualType = dataValue;
		//	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"+++++dataValActualType+++"+dataValActualType});
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_TIME:
			SimpleDateFormat tdf = new SimpleDateFormat("HHmmss");
			if (!(dataValue == null || dataValue.isEmpty()))
			try{
				dataValActualType = new java.sql.Time(tdf.parse(dataValue).getTime());
		//		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[] {"+++++dataValActualType+++"+dataValActualType});
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}
			break;
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_TIMESTAMP:
		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_DATE:
			if (!(dataValue == null || dataValue.isEmpty() || dataValue.trim().equals("00000000") || dataValue.trim().equals("0000-00-00"))) {
				try {
					dataValActualType = new java.sql.Date(sdf.parse(dataValue).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			break;

		case com.ibm.is.cc.javastage.api.ColumnMetadata.SQL_TYPE_UNKNOWN:
			dataValActualType = dataValue;
			break;
		}
		return dataValActualType;
	}

}
