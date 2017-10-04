package com.ibm.is.sappack.cw.app.data.bdr;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ApprovalStatusSerializer extends JsonSerializer<ApprovalStatus> {

	@Override
	public void serialize(ApprovalStatus approvalStatus, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
		generator.writeNumber(ApprovalStatus.toStatusCode(approvalStatus));
	}
}
