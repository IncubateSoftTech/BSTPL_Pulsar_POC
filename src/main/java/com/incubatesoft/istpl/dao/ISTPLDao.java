package com.incubatesoft.istpl.dao;

import com.incubatesoft.istpl.beans.DeviceMessage;
import com.mongodb.client.MongoDatabase;

public interface ISTPLDao {

	public MongoDatabase connectToMongoInstance(String host, int port);
	public void insDataToLiveHistory(DeviceMessage deviceMessage, MongoDatabase mongoDatabase);
	public void insDataToHistory(DeviceMessage deviceMessage, MongoDatabase mongoDatabase);
	public boolean updateDeviceData(DeviceMessage deviceMessage, MongoDatabase mongoDatabase);
}
