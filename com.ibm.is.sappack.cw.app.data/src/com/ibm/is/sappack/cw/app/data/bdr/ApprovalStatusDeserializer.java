package com.ibm.is.sappack.cw.app.data.bdr;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;


public class ApprovalStatusDeserializer extends JsonDeserializer<ApprovalStatus> {

	@Override
   public ApprovalStatus deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		return ApprovalStatus.fromStatusCode(parser.getValueAsInt());
	}
}
