package com.pdfGeneration.service;

import com.google.gson.JsonObject;

public interface GenerateHealthPDF {

    byte[] generateHyperTensionPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medical, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode);


    byte[] generateThyroidPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medical, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode, JsonObject imagesJson);

    byte[] generateRespiratoryPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medical, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode);

    byte[] generateDiabetesPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medical, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode);

    byte[] generateCovidPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medical, boolean isOmniDoc, String placeName, String primaryMobileNo, JsonObject travelObj, String countryCode);
}
