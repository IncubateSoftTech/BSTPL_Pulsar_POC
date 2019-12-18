package com.incubatesoft.istpl.pulsar;


import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import com.incubatesoft.istpl.beans.DeviceMessage;

public class DataProducer {	
	
	public DataProducer() {
		// TODO Auto-generated constructor stub
	}
	
	public void produceMessages(Producer<DeviceMessage> jmcProducer, DeviceMessage deviceMessage) {
		try {
			
			jmcProducer.send(deviceMessage);			
			
		}catch(PulsarClientException pse) {
			pse.printStackTrace();
		}
	}
}
