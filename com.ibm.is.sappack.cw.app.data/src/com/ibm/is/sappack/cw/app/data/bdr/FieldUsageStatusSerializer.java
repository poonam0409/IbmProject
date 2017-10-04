package com.ibm.is.sappack.cw.app.data.bdr;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class FieldUsageStatusSerializer extends JsonSerializer<FieldUsageStatus> {

	@Override
	public void serialize(FieldUsageStatus status, JsonGenerator generator, SerializerProvider provider) throws IOException,
	      JsonProcessingException {
		generator.writeNumber(FieldUsageStatus.toStatusCode(status));
	}
}
