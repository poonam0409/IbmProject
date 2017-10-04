package com.ibm.is.sappack.cw.app.data.bdr;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class FieldUsageStatusDeserializer extends JsonDeserializer<FieldUsageStatus> {

	@Override
	public FieldUsageStatus deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		return FieldUsageStatus.fromStatusCode(parser.getValueAsInt());
	}
}
