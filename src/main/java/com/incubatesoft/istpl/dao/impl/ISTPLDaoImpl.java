package com.incubatesoft.istpl.dao.impl;


import java.util.Date;
import org.bson.Document;

import com.incubatesoft.istpl.beans.DeviceMessage;
import com.incubatesoft.istpl.constants.GatewayAppConstants;
import com.incubatesoft.istpl.dao.ISTPLDao;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class ISTPLDaoImpl implements ISTPLDao {

	@Override
	public MongoDatabase connectToMongoInstance(String host, int port) {
		// TODO Auto-generated method stub
		MongoDatabase mongoDatabase = null;
		MongoClient mongoClient = null;
		
    	try
    	{
    		mongoClient = MongoClients.create("mongodb://"+host+":"+port);
    		mongoDatabase = mongoClient.getDatabase(GatewayAppConstants.DATABASE_NAME);
    	}catch(IllegalArgumentException iaExp) {
    		iaExp.printStackTrace();
    	}catch(Exception exp) {
    		exp.printStackTrace();
    	}
    	return mongoDatabase;
	}

	@Override
	public void insDataToLiveHistory(DeviceMessage deviceMsg, MongoDatabase mongoDatabase) {
		// TODO Auto-generated method stub
		
	    MongoCollection<Document> liveDataColl = null;
	    MongoCollection<Document> historyDataColl = null;		

    	liveDataColl = mongoDatabase.getCollection("live_data");    
    	historyDataColl = mongoDatabase.getCollection("history_data");    

    	Document document = new Document("devicePcktHeader", deviceMsg.getDevicePcktHeader())
				   .append("deviceImeiNumber", deviceMsg.getDeviceImeiNumber())
				   .append("deviceGpsValidity", deviceMsg.getDeviceGpsValidity())
				   .append("deviceMsgDateTime", deviceMsg.getDeviceMsgDateTime())
				   .append("deviceLatitude", deviceMsg.getDeviceLatitude())
				   .append("deviceDirection1", deviceMsg.getDeviceDirection1())
				   .append("deviceLongitude", deviceMsg.getDeviceLongitude())
				   .append("deviceDirection2", deviceMsg.getDeviceDirection2())
				   .append("deviceSpeed", deviceMsg.getDeviceSpeed())
				   .append("deviceOdometer", deviceMsg.getDeviceOdometer())
				   .append("deviceAngle", deviceMsg.getDeviceAngle())
				   .append("numOfSatellites", deviceMsg.getNumOfSatellites())
				   .append("deviceGsmSignal", deviceMsg.getDeviceGsmSignal())
				   .append("deviceMainPower", deviceMsg.getDeviceMainPower())
				   .append("deviceDigitalInput", deviceMsg.getDeviceDigitalInput())
				   .append("deviceAnalogVoltage", deviceMsg.getDeviceAnalogVoltage())
				   .append("deviceFuelInLitres", deviceMsg.getDeviceFuelInLitres());

 	   System.out.println(" Inserting data into live_data table ... ");
 	   liveDataColl.insertOne(document);

 	   System.out.println(" Inserting data into history_data table ... ");
 	   historyDataColl.insertOne(document);  

	}

	@Override
	public void insDataToHistory(DeviceMessage deviceMsg, MongoDatabase mongoDatabase) {
		// TODO Auto-generated method stub
        MongoCollection<Document> historyDataColl = null;

    	historyDataColl = mongoDatabase.getCollection("history_data");    

    	
    	Document document = new Document("devicePcktHeader", deviceMsg.getDevicePcktHeader())
    			.append("deviceImeiNumber", deviceMsg.getDeviceImeiNumber())
    			.append("deviceGpsValidity", deviceMsg.getDeviceGpsValidity())
    			.append("deviceMsgDateTime", deviceMsg.getDeviceMsgDateTime())
    			.append("deviceLatitude", deviceMsg.getDeviceLatitude())
    			.append("deviceDirection1", deviceMsg.getDeviceDirection1())
    			.append("deviceLongitude", deviceMsg.getDeviceLongitude())
    			.append("deviceDirection2", deviceMsg.getDeviceDirection2())
    			.append("deviceSpeed", deviceMsg.getDeviceSpeed())
    			.append("deviceOdometer", deviceMsg.getDeviceOdometer())
    			.append("deviceAngle", deviceMsg.getDeviceAngle())
    			.append("numOfSatellites", deviceMsg.getNumOfSatellites())
    			.append("deviceGsmSignal", deviceMsg.getDeviceGsmSignal())
    			.append("deviceMainPower", deviceMsg.getDeviceMainPower())
    			.append("deviceDigitalInput", deviceMsg.getDeviceDigitalInput())
    			.append("deviceAnalogVoltage", deviceMsg.getDeviceAnalogVoltage())
    			.append("deviceFuelInLitres", deviceMsg.getDeviceFuelInLitres());

    	System.out.println(" Inserting data into history_data table ... ");
    	historyDataColl.insertOne(document);     	   
		
	}
		
	@Override
	public boolean updateDeviceData(DeviceMessage deviceMessage, MongoDatabase mongoDatabase) {
		
        boolean chkImeiPresent = false;
        Date dateRcvdFrmDevice = null;
        Date dateStoredInDB = null;
        MongoCollection<Document> collection = null;
        BasicDBObject whereQuery = null;
        MongoCursor<Document> mCursor = null;
        
        try {
        collection = mongoDatabase.getCollection("live_data");    
    	
        whereQuery = new BasicDBObject();        
        whereQuery.put(GatewayAppConstants.DEVICE_IMEI_NUMBER, deviceMessage.getDeviceImeiNumber());
        
        mCursor = collection.find(whereQuery).iterator();
        
        if(mCursor.hasNext()) 
        {
        	Document doc = mCursor.next();
                	
        	for(String key : doc.keySet()) {        		
        		if(key.equalsIgnoreCase(GatewayAppConstants.DEVICE_MESSAGE_DATE_TIME)) {        			
            		//System.out.println(" key : "+ key + " value : "+doc.get(key));
            		dateStoredInDB = (Date)doc.get(key);
            		break;
        		}
        	}
        	
 		   long timeInMillis = deviceMessage.getDeviceMsgDateTime().getTime();
 		   dateRcvdFrmDevice = new Date(timeInMillis);
 		   
 		   //System.out.println(" dateRcvdFrmDevice : "+dateRcvdFrmDevice);
		   //System.out.println(" dateStoredInDB : "+dateStoredInDB); 		   
		   		   
		   /*
		    *  Sometimes packets received from BSTPL has jumbled time stamps.
		    *  So in order to update the correct time stamp, check it with previous time stamp.
		    *  If current is greater then store current, otherwise skip 
		    */
		   
		   if(dateStoredInDB != null) {
			   if(dateStoredInDB.before(dateRcvdFrmDevice))
			   {
				   chkImeiPresent = true;
				   collection.deleteOne(whereQuery);
				   
				   Document document = new Document("devicePcktHeader", deviceMessage.getDevicePcktHeader())
						   .append("deviceImeiNumber", deviceMessage.getDeviceImeiNumber())
	   					   .append("deviceGpsValidity", deviceMessage.getDeviceGpsValidity())
		   				   .append("deviceMsgDateTime", deviceMessage.getDeviceMsgDateTime())
	   					   .append("deviceLatitude", deviceMessage.getDeviceLatitude())
		   				   .append("deviceDirection1", deviceMessage.getDeviceDirection1())
	   					   .append("deviceLongitude", deviceMessage.getDeviceLongitude())
		   				   .append("deviceDirection2", deviceMessage.getDeviceDirection2())
	   					   .append("deviceSpeed", deviceMessage.getDeviceSpeed())
		   				   .append("deviceOdometer", deviceMessage.getDeviceOdometer())
	   					   .append("deviceAngle", deviceMessage.getDeviceAngle())
		   				   .append("numOfSatellites", deviceMessage.getNumOfSatellites())
	   					   .append("deviceGsmSignal", deviceMessage.getDeviceGsmSignal())
	   					   .append("deviceMainPower", deviceMessage.getDeviceMainPower())
	   					   .append("deviceDigitalInput", deviceMessage.getDeviceDigitalInput())
	   					   .append("deviceAnalogVoltage", deviceMessage.getDeviceAnalogVoltage())
	   					   .append("deviceFuelInLitres", deviceMessage.getDeviceFuelInLitres());
				
		    	   System.out.println(" Updating live_data table ... ");				   
				   collection.insertOne(document);				   
			   }			   
		   	  }
		   	 }
        }// end try
        catch(Exception exp) {
        	exp.printStackTrace();
        }
        finally {
			mCursor.close();
		}
        return chkImeiPresent;
		
	}

}
