package com.ibm.is.sappack.cw.app.data.rdm;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;


public class TableStatusSerializer extends JsonSerializer<TableStatus> {

	@Override
	public void serialize(TableStatus tableStatus, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {

		switch(tableStatus) {
		case LOADED:
			generator.writeNumber(0);
			break;
		case NOT_LOADED:
			generator.writeNumber(1);
			break;
		case MISSING_IN_CW:
			generator.writeNumber(2);
			break;
		case TEXT_TABLE_MISSING_IN_CW:
			generator.writeNumber(3);
			break;
		default:
			generator.writeNumber(-1);
			break;
		}
	}
}
