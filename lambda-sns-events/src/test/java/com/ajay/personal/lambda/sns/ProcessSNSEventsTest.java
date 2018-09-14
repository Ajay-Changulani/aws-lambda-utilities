package com.ajay.personal.lambda.sns;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessSNSEventsTest {

	private static ProcessSNSEvents handler;
	
	private static SNSEvent mockSNSEvent = null;
	private static SNSRecord mockSNSRecord = null;
	private static SNS mockSNS = null;

	
	private String getSampleSNSRecord() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = ProcessSNSEventsTest.class.getResourceAsStream("/sample-sns-record.json");
		JSONObject s = mapper.readValue(is, JSONObject.class);
		return s.toJSONString();
	}
	
	
	@BeforeClass
	public static void setup() {
		handler = new ProcessSNSEvents();
		mockSNSEvent = Mockito.mock(SNSEvent.class);
		mockSNSRecord = Mockito.mock(SNSRecord.class);
		mockSNS = Mockito.mock(SNS.class);
	}
	
	@Test
	public void testParseSNSEvent() throws Exception {
		String sampleSNSMessage = getSampleSNSRecord();
		
		List<SNSRecord> snsRecords = new ArrayList<SNSRecord>();
		snsRecords.add(mockSNSRecord);

		Mockito.doReturn(snsRecords).when(mockSNSEvent).getRecords();
		Mockito.doReturn(mockSNS).when(mockSNSRecord).getSNS();
		Mockito.doReturn(sampleSNSMessage).when(mockSNS).getMessage();
		
		List<Pair<String, String>> pairs = handler.parseSNSEvent(mockSNSEvent);
		
		assertEquals("test-bucket", pairs.get(0).getKey());
		assertEquals("test/2015/01/19/18/S3-test-KEY", pairs.get(0).getValue());
		
	}
}
