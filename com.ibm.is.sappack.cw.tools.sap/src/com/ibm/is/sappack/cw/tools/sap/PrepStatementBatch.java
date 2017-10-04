package com.ibm.is.sappack.cw.tools.sap;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;

public class PrepStatementBatch {
	static String copyright() {
		return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT;
	}

	int pageSize;
	PreparedStatement statement;
	int counter = 0;

	public PrepStatementBatch(PreparedStatement statement) {
		this(statement, 50);
	}

	public PrepStatementBatch(PreparedStatement statement, int pageSize) {
		this.statement = statement;
		this.pageSize = AdvancedSettingsPreferencePage.getSettingAsInt("RM_PUBLISH_DB_PAGE_SIZE", pageSize); //$NON-NLS-1$
	}

	public void execute() throws SQLException {
		if (this.pageSize == 1) {
			this.statement.execute();
		} else {
			this.statement.addBatch();
			counter++;
			if (counter == pageSize) {
				finish();
				counter = 0;
			}
		}
	}

	public void finish() throws SQLException {
		if (this.pageSize == 1) {
			// do nothing
		} else {
			try {
				int[] results = this.statement.executeBatch();
				for (int result : results) {
					if (result == Statement.EXECUTE_FAILED) {
						throw new SQLException(Messages.PrepStatementBatch_0);
					}
				}
			} catch (BatchUpdateException bue) {
				Activator.logException(bue);
				bue.printStackTrace();
				SQLException nextExc = bue.getNextException();
				if (nextExc != null) {
					Activator.logException(nextExc);
					throw nextExc;
				}
			}
		}
	}

}
