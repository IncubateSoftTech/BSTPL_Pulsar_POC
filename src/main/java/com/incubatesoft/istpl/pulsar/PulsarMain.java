package com.incubatesoft.istpl.pulsar;


import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;

import com.incubatesoft.istpl.beans.DeviceMessage;

public class PulsarMain {

	private PulsarClient pulsarClient = null;
	
	public PulsarMain() {
		// TODO Auto-generated constructor stub
	}
	
	public PulsarClient getPulsarClient() {
		try {
			// Get the PulsarClient instance
			pulsarClient = PulsarClient.builder()
					.serviceUrl("pulsar://localhost:6650")
					.build();
						
		}catch(PulsarClientException pse) {
			pse.printStackTrace();
		}
		
		return pulsarClient;
	}
	
	public Producer<DeviceMessage> getPulsarProducer(){
		Producer<DeviceMessage> jmcProducer = null;
		
		try {
			jmcProducer = pulsarClient.newProducer(JSONSchema.of(DeviceMessage.class))
			        .topic("jmc-topic")
			        .create();						
			
		}catch(PulsarClientException pse) {
			pse.printStackTrace();
		}
		return jmcProducer;
	}
	
	public Consumer<DeviceMessage> getPulsarConsumer(){
		Consumer<DeviceMessage> jmcConsumer = null;
		
		try {
			
			jmcConsumer = pulsarClient.newConsumer(JSONSchema.of(DeviceMessage.class))
					.topic("jmc-topic")
					.subscriptionName("jmc-subscription")
					.subscribe();

		}catch(PulsarClientException pse) {
			pse.printStackTrace();
		}
		return jmcConsumer;
	}
}
