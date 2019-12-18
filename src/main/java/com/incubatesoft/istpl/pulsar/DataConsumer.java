package com.incubatesoft.istpl.pulsar;


import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import com.incubatesoft.istpl.beans.DeviceMessage;

public class DataConsumer {
	
	public DataConsumer() {
		// TODO Auto-generated constructor stub
	}
	
	public void consumeMessages(Consumer<DeviceMessage> jmcConsumer) {
		try {
			
			/* while(true)
			{ */
				// Receive the message
				
				Message<DeviceMessage> deviceMessage = jmcConsumer.receive();				
			
				System.out.println(" Message Received : " + deviceMessage.getProducerName() 
								+ " : " + deviceMessage.getTopicName()
								+ " : " + deviceMessage.getKey()
								+ " : " + deviceMessage.getValue());
				
				System.out.println(" Whether any key available : " + deviceMessage.hasKey());
			
				// Acknowledge the message
				jmcConsumer.acknowledge(deviceMessage);
			/* } */
		}catch(PulsarClientException pse) {
			pse.printStackTrace();
		}
		
	}
}
