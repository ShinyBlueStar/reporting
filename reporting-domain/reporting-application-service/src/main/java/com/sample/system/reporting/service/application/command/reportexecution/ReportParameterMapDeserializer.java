package com.sample.system.reporting.service.application.command.reportexecution;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sample.system.reporting.service.application.utility.ReportParameterJsonValues;

import java.io.IOException;
import java.util.Map;

public class ReportParameterMapDeserializer extends JsonDeserializer<Map<String, Object>> {

    @Override
    public Map<String, Object> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);
        return ReportParameterJsonValues.toMap(root);
    }
}
