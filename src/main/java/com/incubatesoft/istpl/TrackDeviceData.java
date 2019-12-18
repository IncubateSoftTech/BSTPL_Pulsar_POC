package com.incubatesoft.istpl;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.bson.Document;

import com.incubatesoft.istpl.beans.DeviceMessage;
import com.incubatesoft.istpl.constants.GatewayAppConstants;
import com.incubatesoft.istpl.dao.ISTPLDao;
import com.incubatesoft.istpl.dao.impl.ISTPLDaoImpl;
import com.incubatesoft.istpl.pulsar.DataConsumer;
import com.incubatesoft.istpl.pulsar.DataProducer;
import com.incubatesoft.istpl.pulsar.PulsarMain;
import com.incubatesoft.istpl.util.FleetManagementUtility;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

public class TrackDeviceData {

    public DeviceMessage deviceMessage;    
    public static final long TIMEOUT = 10000L;
    public int clients = 0;
    ByteBuffer readBuffer = ByteBuffer.allocate(512);    
    private ServerSocketChannel serverChannel;
    private Selector selector;    
    BufferedWriter logDataBuffer;        
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;  
    MongoCollection<Document> liveDataColl;
    MongoCollection<Document> historyDataColl;
    ISTPLDao fleetManageDaoImpl;
    FleetManagementUtility fleetManageUtil;
    PulsarMain pulsarMain;
    PulsarClient pClient;
    DataProducer dataProducer;
    DataConsumer dataConsumer;
    Producer<DeviceMessage> jmcProducer;
    Consumer<DeviceMessage> jmcConsumer;
    
    static String packetMessage = "$10,864502037809795,A,120719,074758,1725.4942,N,07827.4867,E,0,1,222,08,30,1,0,00.0100,000.00,00#$10,864502037809795,A,120719,074803,1725.4942,N,07827.4867,E,0,1,259,08,30,1,0,00.0100,000.00,00#$10,864502037809795,A,120719,074808,1725.4942,N,07827.4867,E,0,1,343,08,30,1,0,00.0100,000.00,00#$10,864502037809795,A,120719,074813,1725.4942,N,07827.4867,E,0,1,309,08,30,1,0,00.0100,000.00,00#$10,864502037809795,A,120719,074818,1725.4942,N,07827.4867,E,0,1,350,08,30,1,0,00.0100,000.00,00#";
    static String packetMessage1 = "$10,864502037809795,A,120719,074758,1725.4942,N,07827.4867,E,0,1,222,08,30,1,0,00.0100,000.00,00#";
    
	public TrackDeviceData() {
		fleetManageDaoImpl = new ISTPLDaoImpl();
		fleetManageUtil = new FleetManagementUtility();
		pulsarMain = new PulsarMain();
		dataProducer = new DataProducer();
		dataConsumer = new DataConsumer(); 
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		startProcess();
	}
	
    private static void startProcess()
    {
       TrackDeviceData trackDeviceData = new TrackDeviceData();
       trackDeviceData.initialize();
       //trackDeviceData.accessDevice();    
       try {
    	   trackDeviceData.parsePacketData(packetMessage);   
       }catch(Exception exp) {
    	   exp.printStackTrace();
       }       
    }
    
    /*
     * This method is used to initialize application connectivity parameters
     * Eg : Mongo DB, Vehicle Tracking device connectivity
     */
    private void initialize() {
    	try {
    		/*  Connect to MongoDB instance */
    		mongoDatabase = fleetManageDaoImpl.connectToMongoInstance(GatewayAppConstants.DB_HOST,GatewayAppConstants.DB_PORT);
    
    		/* Connect and get a PulsarClient instance */
    		pClient = pulsarMain.getPulsarClient();    		
    		jmcProducer = pulsarMain.getPulsarProducer();
    		jmcConsumer = pulsarMain.getPulsarConsumer();
    		
    		/* Below statements are for Java Nio API to get Selector, ServerChannel objects */
    		selector = Selector.open();
    		serverChannel = ServerSocketChannel.open();
    		serverChannel.socket().bind(new java.net.InetSocketAddress(GatewayAppConstants.SERVER_HOST, GatewayAppConstants.SERVER_PORT),5000);
    		serverChannel.configureBlocking(false);
    		serverChannel.register(selector, SelectionKey.OP_ACCEPT);       
    	}catch(Exception exp) {
    		exp.printStackTrace();
    	}
    }
	
    private void accessDevice() {
    	try {         
        while (true) {            
        	selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();    
            
            while (iter.hasNext()) {
            	
                SelectionKey key = iter.next();
 
                if (key.isAcceptable()) {
                    accept(selector, serverChannel);
                }
 
                if (key.isReadable()) {
                    read(readBuffer, key);
                }
                iter.remove();
            }//end inner while
          }//end outer while
        }catch(IOException ioe) {
        	ioe.printStackTrace();
        }catch(Exception e) {
        	e.printStackTrace();
        }
    	    	
    }
 
    private void read(ByteBuffer buffer, SelectionKey key)
    {
  
    	System.out.println(" read() called ... ");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int bytesRead = 0;
        try {
        	bytesRead = socketChannel.read(buffer); 
        	// System.out.println(" bytesRead : "+bytesRead);
            String result = new String(buffer.array()).trim();     
            
            System.out.println(" Message Received is ----- "+ result);
            buffer.flip();
            parsePacketData(result);            
            buffer.clear();        	
        }catch(IOException ioe) {
        	key.cancel();
        	try{
        		socketChannel.close();	
        	}catch(IOException ie) {
        		ie.printStackTrace();
        	}    
        	return;
        }catch(Exception exp) {
        	exp.printStackTrace();
        }
        
        if(bytesRead == -1) {
        	try {
        		key.channel().close();
        		key.cancel();
        	}catch(IOException ioe) {
        		ioe.printStackTrace();
        	}
        	return;
        }
    }
 
    private void accept(Selector selector, ServerSocketChannel serverSocket)
      throws IOException {
  
    	System.out.println(" accept() called ... ");
        SocketChannel socketChannel = serverSocket.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }	           

    private void parsePacketData(String result) throws Exception{
    	String dataRcvd = result;
		String [] msgArray = dataRcvd.split("#");
		
		System.out.println(" msgArray.length "+ msgArray.length);
		deviceMessage = new DeviceMessage();
		
		for(String eachMsg: msgArray) {
			
			String msgDateTime = new String("");
			String modMsgDateTime = new String("");
			
			System.out.println(" eachMessage : "+ eachMsg);
			//System.out.println(" eachMessage.length : "+ eachMsg.length());
			
			if(eachMsg.contains(GatewayAppConstants.MAINBATTERY_DISCONNECTED) || 
			   eachMsg.contains(GatewayAppConstants.MAINBATTERY_CONNECTED) )
			{
				System.out.println(" Main battery is either disconnected or reconnected. So skipping the read process ...");
				continue;
			}
			
			else if(eachMsg.length() < GatewayAppConstants.PACKET_DATA_LENGTH)
			{
				System.out.println( " Packet recieved is incomplete. Therefore skipping such packets.. ");
				continue;
			}
			
			else 
			{
				msgDateTime = eachMsg.substring(GatewayAppConstants.DATE_TIME_START_INDEX, GatewayAppConstants.DATE_TIME_END_INDEX);
				//System.out.println(" msgDateTime ---> "+ msgDateTime);
				modMsgDateTime = msgDateTime.replace(GatewayAppConstants.COMMA_AS_DELIMITER, GatewayAppConstants.SPACE_AS_DELIMITER);
				//System.out.println(" modMsgDateTime ---> "+ modMsgDateTime);				
			}

			String [] packetData = eachMsg.split(",");
			
			for(int index=0; index<packetData.length; index++) {										
				
				if(index == 0) {
					deviceMessage.setDevicePcktHeader(packetData[0]);
				}
				else if(index == 1) {
					deviceMessage.setDeviceImeiNumber((Long.parseLong(packetData[1])));
				}
				else if(index == 2) {
					deviceMessage.setDeviceGpsValidity((packetData[2]));
				}
				else if(index == 3) {
					continue;
				}
				else if(index == 4) {
					continue;
				}
				else if(index == 5) {
					double devLatitude = fleetManageUtil.convertToDegrees(Double.parseDouble(packetData[5]));
					deviceMessage.setDeviceLatitude(devLatitude);
				}
				else if(index == 6) {
					deviceMessage.setDeviceDirection1(packetData[6]);
				}
				else if(index == 7) {
					double devLongitude = fleetManageUtil.convertToDegrees(Double.parseDouble(packetData[7]));
					deviceMessage.setDeviceLongitude(devLongitude);
				}
				else if(index == 8) {
					deviceMessage.setDeviceDirection2(packetData[8]);
				}
				else if(index == 9) {
					int devSpeed = Integer.parseInt(packetData[9]);
					deviceMessage.setDeviceSpeed(devSpeed);
				}
				else if(index == 10) {
					int devOdometer = Integer.parseInt(packetData[10]);
					deviceMessage.setDeviceOdometer(devOdometer);
				}
				else if(index == 11) {
					int devAngle = Integer.parseInt(packetData[11]);
					deviceMessage.setDeviceAngle(devAngle);
				}
				else if(index == 12) {
					int numSatellites = Integer.parseInt(packetData[12]);
					deviceMessage.setNumOfSatellites(numSatellites);
				}
				else if(index == 13) {
					int devGsmSignal = Integer.parseInt(packetData[13]);
					deviceMessage.setDeviceGsmSignal(devGsmSignal);
				}
				else if(index == 14) {
					int devMainPower = Integer.parseInt(packetData[14]);
					deviceMessage.setDeviceMainPower(devMainPower);
				}
				else if(index == 15) {
					int devDigitalInput = Integer.parseInt(packetData[15]);
					deviceMessage.setDeviceDigitalInput(devDigitalInput);
				}
				else if(index == 16) {
					float devAnalogVoltage = Float.parseFloat(packetData[16]);
					deviceMessage.setDeviceAnalogVoltage(devAnalogVoltage);
				}
				else if(index == 17) {
					float devFuelInLitres = Float.parseFloat(packetData[17]);
					deviceMessage.setDeviceFuelInLitres(devFuelInLitres);
				}								
			}	
			
		    Date date1 = new SimpleDateFormat("ddMMyy HHmmss").parse(modMsgDateTime);				    				    
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		  
		    		    	     
		    dateFormat.setTimeZone(TimeZone.getTimeZone(GatewayAppConstants.IST_TIME_ZONE));
		    
		    String devMsgDateTime = dateFormat.format(date1);		    
		    Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(devMsgDateTime);		    
		    
		    Timestamp dateTimeStamp = new java.sql.Timestamp(date2.getTime());
		    		    
		    deviceMessage.setDeviceMsgDateTime(dateTimeStamp);		    
		    		    
			//processDeviceData(deviceMessage);
			dataProducer.produceMessages(jmcProducer, deviceMessage);
			dataConsumer.consumeMessages(jmcConsumer);
		}
    }

    /**
     * This method is used to insert the received packet or message to 'live_data' table
     */
    private void processDeviceData(DeviceMessage deviceMsg) {
    	
    	liveDataColl = mongoDatabase.getCollection("live_data");    
    	historyDataColl = mongoDatabase.getCollection("history_data");    
    	              
    	boolean chkImeiresent = fleetManageDaoImpl.updateDeviceData(deviceMsg, mongoDatabase);       
    	
    	System.out.println(" chkImeiresent : "+chkImeiresent);
    	   
    	if(!chkImeiresent){     	   
    		fleetManageDaoImpl.insDataToLiveHistory(deviceMsg, mongoDatabase);    	   
    	}     
    	else {
    		fleetManageDaoImpl.insDataToHistory(deviceMsg, mongoDatabase);
    	}       
     }
    
}
