package com.incubatesoft.istpl.util;

public class FleetManagementUtility {
	
	public FleetManagementUtility() {
		// TODO Auto-generated constructor stub
	}
    /**
     * This method is used to convert NMEA format to Decimal Degrees either for Latitude or Longitude
     * @param latOrLan
     * @return
     */
   public double convertToDegrees(double latOrLan) {
	   
	   String latLanString = String.valueOf(latOrLan);
	   String minLatLong = latLanString.substring(2, latLanString.length());
	   double minLatLongDble = Double.parseDouble(minLatLong);
	   double minutes = minLatLongDble/60;
	   double degrees = Double.parseDouble(latLanString.substring(0, 2)) + minutes;
	   
	   //System.out.println(" Latitude or Logitude in degrees : "+degrees);
	   return degrees;
   }
	

}
