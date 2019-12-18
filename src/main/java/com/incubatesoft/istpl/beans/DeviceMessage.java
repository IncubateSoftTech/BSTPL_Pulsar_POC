package com.incubatesoft.istpl.beans;


import java.io.Serializable;
import java.sql.Timestamp;


public class DeviceMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6479024820359892378L;
	
	public String getDevicePcktHeader() {
		return devicePcktHeader;
	}
	public void setDevicePcktHeader(String devicePcktHeader) {
		this.devicePcktHeader = devicePcktHeader;
	}
	public long getDeviceImeiNumber() {
		return deviceImeiNumber;
	}
	public void setDeviceImeiNumber(long deviceImeiNumber) {
		this.deviceImeiNumber = deviceImeiNumber;
	}
	public String getDeviceGpsValidity() {
		return deviceGpsValidity;
	}
	public void setDeviceGpsValidity(String deviceGpsValidity) {
		this.deviceGpsValidity = deviceGpsValidity;
	}
	public double getDeviceLatitude() {
		return deviceLatitude;
	}
	public void setDeviceLatitude(double deviceLatitude) {
		this.deviceLatitude = deviceLatitude;
	}
	public String getDeviceDirection1() {
		return deviceDirection1;
	}
	public void setDeviceDirection1(String deviceDirection1) {
		this.deviceDirection1 = deviceDirection1;
	}
	public double getDeviceLongitude() {
		return deviceLongitude;
	}
	public void setDeviceLongitude(double deviceLongitude) {
		this.deviceLongitude = deviceLongitude;
	}
	public String getDeviceDirection2() {
		return deviceDirection2;
	}
	public void setDeviceDirection2(String deviceDirection2) {
		this.deviceDirection2 = deviceDirection2;
	}
	public int getDeviceSpeed() {
		return deviceSpeed;
	}
	public void setDeviceSpeed(int deviceSpeed) {
		this.deviceSpeed = deviceSpeed;
	}
	public int getDeviceOdometer() {
		return deviceOdometer;
	}
	public void setDeviceOdometer(int deviceOdometer) {
		this.deviceOdometer = deviceOdometer;
	}
	public int getDeviceAngle() {
		return deviceAngle;
	}
	public void setDeviceAngle(int deviceAngle) {
		this.deviceAngle = deviceAngle;
	}
	public int getNumOfSatellites() {
		return numOfSatellites;
	}
	public void setNumOfSatellites(int numOfSatellites) {
		this.numOfSatellites = numOfSatellites;
	}
	public int getDeviceGsmSignal() {
		return deviceGsmSignal;
	}
	public void setDeviceGsmSignal(int deviceGsmSignal) {
		this.deviceGsmSignal = deviceGsmSignal;
	}
	public int getDeviceMainPower() {
		return deviceMainPower;
	}
	public void setDeviceMainPower(int deviceMainPower) {
		this.deviceMainPower = deviceMainPower;
	}
	public int getDeviceDigitalInput() {
		return deviceDigitalInput;
	}
	public void setDeviceDigitalInput(int deviceDigitalInput) {
		this.deviceDigitalInput = deviceDigitalInput;
	}
	public float getDeviceAnalogVoltage() {
		return deviceAnalogVoltage;
	}
	public void setDeviceAnalogVoltage(float deviceAnalogVoltage) {
		this.deviceAnalogVoltage = deviceAnalogVoltage;
	}
	public float getDeviceFuelInLitres() {
		return deviceFuelInLitres;
	}
	public void setDeviceFuelInLitres(float deviceFuelInLitres) {
		this.deviceFuelInLitres = deviceFuelInLitres;
	}
	public float getDeviceTemparature() {
		return deviceTemparature;
	}
	public void setDeviceTemparature(float deviceTemparature) {
		this.deviceTemparature = deviceTemparature;
	}
	
	public Timestamp getDeviceMsgDateTime() {
		return deviceMsgDateTime;
	}
	public void setDeviceMsgDateTime(Timestamp deviceMsgDateTime) {
		this.deviceMsgDateTime = deviceMsgDateTime;
	}
	
	private String devicePcktHeader;
	private long deviceImeiNumber;
	private String deviceGpsValidity;// Value is either 'A' or 'V'	
	private Timestamp deviceMsgDateTime;
	private double deviceLatitude;
	private String deviceDirection1;
	private double deviceLongitude;
	private String deviceDirection2;
	private int deviceSpeed;
	private int deviceOdometer;
	private int deviceAngle;// course always
	private int numOfSatellites;
	private int deviceGsmSignal;
	private int deviceMainPower;
	private int deviceDigitalInput;
	private float deviceAnalogVoltage;
	private float deviceFuelInLitres;
	private float deviceTemparature;
	
	
	
}
