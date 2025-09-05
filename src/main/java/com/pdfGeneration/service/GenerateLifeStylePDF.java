package com.pdfGeneration.service;

import com.google.gson.JsonObject;

public interface GenerateLifeStylePDF {

    byte[] generateDivingPDF(String applicationNumber, String nameOfLifeAssured, JsonObject divingObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode);

    byte[] generateDrivingPDF(String applicationNumber, String nameOfLifeAssured, JsonObject drivingObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);

    byte[] generateAviationPDF(String applicationNumber, String nameOfLifeAssured, JsonObject aviationObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);

    byte[] generateFishingPDF(String applicationNumber, String nameOfLifeAssured, JsonObject fishingObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);

    byte[] generateOccupationPDF(String applicationNumber, String nameOfLifeAssured, JsonObject occupationalObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);

    byte[] generateMiningPDF(String applicationNumber, String nameOfLifeAssured, JsonObject miningObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);

    byte[] generateOilRefineryPDF(String applicationNumber, String nameOfLifeAssured, JsonObject oilRefineryObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);

    byte[] generateArmedForcesPDF(String applicationNumber, String nameOfLifeAssured, JsonObject armObj,boolean isOmniDoc,String placeName,String primaryMobileNo,String countryCode);
    byte[] generateMarinePDF(String applicationNumber, String nameOfLifeAssured, JsonObject marineObj, boolean isOmniDoc, String placeName,String primaryMobileNo,String countryCode);


}
