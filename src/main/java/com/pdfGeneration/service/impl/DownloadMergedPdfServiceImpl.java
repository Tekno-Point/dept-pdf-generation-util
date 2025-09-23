package com.pdfGeneration.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.pdfGeneration.service.DownloadMergedPdfService;
import com.pdfGeneration.service.GenerateHealthPDF;
import com.pdfGeneration.service.GenerateLifeStylePDF;
import com.pdfGeneration.utility.JsonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class DownloadMergedPdfServiceImpl implements DownloadMergedPdfService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GenerateHealthPDF generateHealthPDF;
    private final GenerateLifeStylePDF generateLifeStylePDF;
    private final JsonUtility jsonUtility;

    public DownloadMergedPdfServiceImpl(GenerateHealthPDF generateHealthPDF, GenerateLifeStylePDF generateLifeStylePDF, JsonUtility jsonUtility) {
        this.generateHealthPDF = generateHealthPDF;
        this.generateLifeStylePDF = generateLifeStylePDF;
        this.jsonUtility = jsonUtility;

    }


    @Override
    public byte[] downloadMedicalLifestylePdf(String medicalLifeStyleReq) {
        try {
            JsonObject userData = jsonUtility.getJsonObject(medicalLifeStyleReq);
            JsonObject basicDetailObj = jsonUtility.getJsonObjectByKey("basicDetails",userData);
            logger.info("Basic details object:{}", basicDetailObj);
            JsonObject lifeStyleDetailObj = jsonUtility.getJsonObjectByKey("lifestyleQ",userData);
            logger.info("Lifestyle details object:{}", lifeStyleDetailObj);
            JsonObject medicalDetailObj = jsonUtility.getJsonObjectByKey("medicalQ",userData);
            logger.info("Medical details object:{}", medicalDetailObj);
            JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails",userData);
            logger.info("Personal details object:{}", personalDetailObj);
            JsonObject travelDetailObj = jsonUtility.getJsonObjectByKey("travelDetails",userData);
            logger.info("Travel details object:{}", travelDetailObj);

            String applicationNo = jsonUtility.getJsonKeyValue("applicationNumber", userData);
            JsonArray pdfNames = jsonUtility.getJsonArrayByKey("pdfNameArray", userData);
            boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);
            boolean jointLife = jsonUtility.getBooleanKeyValue("jointLife", userData);
            logger.info("Pdf name list to be generated:{}", pdfNames);
            logger.info("Application number:{}", applicationNo);


            JsonObject policyHolderObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderObj);
            JsonObject insuredHolderObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);

            boolean buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderObj).equalsIgnoreCase("Myself");

            //get key
            if (jointLife) {
                buyFor = true;
            }
            logger.info("Buy for boolean value is:{} ", buyFor);
            String lifeAssureName;
            JsonObject lifeStyleObj;
            JsonObject medicalObj;
            JsonObject travelObj;
            if (buyFor) {
                lifeAssureName = jsonUtility.getJsonKeyValue("fullName", policyHolderObj).toUpperCase();
                lifeStyleObj = jsonUtility.getJsonObjectByKey("primary", lifeStyleDetailObj);
                medicalObj = jsonUtility.getJsonObjectByKey("primary", medicalDetailObj);
                travelObj = jsonUtility.getJsonObjectByKey("primary", travelDetailObj);
            } else {
                lifeAssureName = jsonUtility.getJsonKeyValue("fullName", insuredHolderObj).toUpperCase();
                lifeStyleObj = jsonUtility.getJsonObjectByKey("secondary", lifeStyleDetailObj);
                medicalObj = jsonUtility.getJsonObjectByKey("secondary", medicalDetailObj);
                travelObj = jsonUtility.getJsonObjectByKey("secondary", travelDetailObj);
            }
            JsonObject healthHistoryObj = jsonUtility.getJsonObjectByKey("healthHistory", medicalObj);
            JsonObject primaryPersonalDetailsObj = jsonUtility.getJsonObjectByKey("primary", personalDetailObj);
            String placeName = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailsObj);
            String primaryMobileNo = jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetailsObj);
            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images", userData);
            List<String> combineData = new ArrayList<>();
            for (JsonElement element : pdfNames) {
                String names = element.getAsString();
                if (names.equalsIgnoreCase("armedForces")) {
                    JsonObject armObj = jsonUtility.getJsonObjectByKey("army", lifeStyleObj);
                    String armedForcesResponse = "";
                    try {
                        armedForcesResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateArmedForcesPDF(applicationNo, lifeAssureName, armObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Armed forces pdf response:{}", armedForcesResponse);
                        if (!armedForcesResponse.isEmpty()) {
                            combineData.add(armedForcesResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in armed forces method:{}", e.getMessage());
                    }

                }
                if (names.equalsIgnoreCase("fishing")) {
                    JsonObject fishingObj = jsonUtility.getJsonObjectByKey("fishing", lifeStyleObj);
                    String fishingResponse = "";
                    try {
                        fishingResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateFishingPDF(applicationNo, lifeAssureName, fishingObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Fishing pdf response:{}", fishingResponse);
                        if (!fishingResponse.isEmpty()) {
                            combineData.add(fishingResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in fishing method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("diving")) {
                    JsonObject divingObj = jsonUtility.getJsonObjectByKey("diving", lifeStyleObj);
                    String divingResponse = "";
                    try {
                        divingResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateDivingPDF(applicationNo, lifeAssureName, divingObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Diving pdf response:{}", divingResponse);
                        if (!divingResponse.isEmpty()) {
                            combineData.add(divingResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in diving method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("driving")) {
                    JsonObject drivingObj = jsonUtility.getJsonObjectByKey("driving", lifeStyleObj);
                    String drivingResponse = "";
                    try {
                        drivingResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateDrivingPDF(applicationNo, lifeAssureName, drivingObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Driving pdf response:{}", drivingResponse);
                        if (!drivingResponse.isEmpty()) {
                            combineData.add(drivingResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in driving method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("aviation")) {
                    JsonObject aviationObj = jsonUtility.getJsonObjectByKey("aviation", lifeStyleObj);
                    String aviationResponse = "";
                    try {
                        aviationResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateAviationPDF(applicationNo, lifeAssureName, aviationObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Aviation pdf response:{}", aviationResponse);
                        if (!aviationResponse.isEmpty()) {
                            combineData.add(aviationResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in aviation method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("mining")) {
                    JsonObject miningObj = jsonUtility.getJsonObjectByKey("mining", lifeStyleObj);
                    String miningResponse = "";
                    try {
                        miningResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateMiningPDF(applicationNo, lifeAssureName, miningObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Mining pdf response:{}", miningResponse);
                        if (!miningResponse.isEmpty()) {
                            combineData.add(miningResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in mining method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("occupation")) {
                    JsonObject occupationalObj = jsonUtility.getJsonObjectByKey("occupational", lifeStyleObj);
                    String occupationResponse = "";
                    try {
                        occupationResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateOccupationPDF(applicationNo, lifeAssureName, occupationalObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Occupational pdf response:{}", occupationResponse);
                        if (!occupationResponse.isEmpty()) {
                            combineData.add(occupationResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in occupation method:{}", e.getMessage());
                    }

                }
                if (names.equalsIgnoreCase("oilRefinery")) {
                    JsonObject oilRefineryObj = jsonUtility.getJsonObjectByKey("oilrefinery", lifeStyleObj);
                    String oilRefineryResponse = "";
                    try {
                        oilRefineryResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateOilRefineryPDF(applicationNo, lifeAssureName, oilRefineryObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Oil Refinery pdf response:{}", oilRefineryResponse);
                        if (!oilRefineryResponse.isEmpty()) {
                            combineData.add(oilRefineryResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in oil refinery method:{}", e.getMessage());
                    }
                }

                if (names.equalsIgnoreCase("marine")) {
                    JsonObject marineObj = jsonUtility.getJsonObjectByKey("marine", lifeStyleObj);
                    String marineResponse = "";
                    try {
                        marineResponse = Base64.getEncoder().encodeToString(
                                generateLifeStylePDF.generateMarinePDF(applicationNo, lifeAssureName, marineObj, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Marine pdf response:{}", marineResponse);
                        if (!marineResponse.isEmpty()) {
                            combineData.add(marineResponse);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in Marine method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("thyroid")) {
                    JsonObject medical = jsonUtility.getJsonObjectByKey("diagnosedHepatitis", medicalObj);
                    String medicalResp = "";
                    try {
                        medicalResp = Base64.getEncoder().encodeToString(
                                generateHealthPDF.generateThyroidPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode, imagesJson));
                        logger.info("Thyroid pdf response:{}", medicalResp);
                        if (!medicalResp.isEmpty()) {
                            combineData.add(medicalResp);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in thyroid forces method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("respiratory")) {
                    JsonObject medical = jsonUtility.getJsonObjectByKey("respiratoryDisorder", healthHistoryObj);
                    String respiratoryResp = "";
                    try {
                        respiratoryResp = Base64.getEncoder().encodeToString(
                                generateHealthPDF.generateRespiratoryPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
                        logger.info("Respiratory pdf response:{}", respiratoryResp);
                        if (!respiratoryResp.isEmpty()) {
                            combineData.add(respiratoryResp);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in respiratory method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("hyperTension")) {
                    JsonObject medical = jsonUtility.getJsonObjectByKey("hypertension", healthHistoryObj);
                    String hyperTensionResp = "";
                    try {
                        hyperTensionResp = Base64.getEncoder().encodeToString(
                                generateHealthPDF.generateHyperTensionPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
                        logger.info("Hyper tension pdf response:{}", hyperTensionResp);
                        if (!hyperTensionResp.isEmpty()) {
                            combineData.add(hyperTensionResp);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in hypertension method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("diabetes")) {
                    JsonObject medical = jsonUtility.getJsonObjectByKey("sufferedDiabetes", healthHistoryObj);
                    String diabetesResp = "";
                    try {
                        diabetesResp = Base64.getEncoder().encodeToString(
                                generateHealthPDF.generateDiabetesPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
                        logger.info("Diabetes pdf response:{}", diabetesResp);
                        if (!diabetesResp.isEmpty()) {
                            combineData.add(diabetesResp);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in diabetes method:{}", e.getMessage());
                    }
                }
                if (names.equalsIgnoreCase("covid")) {
                    String covidResp = "";
                    try {
                        covidResp = Base64.getEncoder().encodeToString(
                                generateHealthPDF.generateCovidPDF(applicationNo, lifeAssureName, medicalObj, isOmniDoc, placeName, primaryMobileNo, travelObj, countryCode));
                        logger.info("Covid pdf response:{}", covidResp);
                        if (!covidResp.isEmpty()) {
                            combineData.add(covidResp);
                        }
                    } catch (Exception e) {
                        logger.info("Exception in generate covid pdf method:{}", e.getMessage());
                    }
                }
            }
            JsonObject metadataObj = jsonUtility.getJsonObjectByKey("metadata",userData);
            return this.downloadMergedMedicalLifestylePdf(combineData, applicationNo, metadataObj);
        } catch (Exception e) {
            JsonObject object = new JsonObject();
            logger.error("Exception occurs in medical and lifestyle merged pdf:{}", e.getMessage());
            object.addProperty("status", "error");
            object.addProperty("msg", e.getMessage());
            return object.toString().getBytes();
        }
    }

    @Override
    public byte[] downloadMergedMedicalLifestylePdf(List<String> base64String, String applicationNumber, JsonObject metadataObj) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(output);
            PdfDocument pdf = new PdfDocument(writer);
            //
            String author = jsonUtility.getJsonKeyValue("author", metadataObj);
            String creator = jsonUtility.getJsonKeyValue("creator", metadataObj);
            String title = jsonUtility.getJsonKeyValue("title", metadataObj);
            PdfDocumentInfo pdfDocumentInfo=pdf.getDocumentInfo();
            pdfDocumentInfo.setAuthor(author);
            pdfDocumentInfo.setCreator(creator);
            pdfDocumentInfo.setTitle(title);
            pdfDocumentInfo.addCreationDate();
            PdfMerger merger = new PdfMerger(pdf);
            for (String base64Pdf : base64String) {
                mergeBase64Pdf(merger, base64Pdf);
            }
            pdf.close();
            return output.toByteArray();
        } catch (IOException e) {
            logger.info("Exception in download medical and lifestyle pdf method:", e);
            return new byte[0];
        }
    }

    private static void mergeBase64Pdf(PdfMerger merger, String base64Pdf) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(base64Pdf))) {
            PdfDocument sourcePdf = new PdfDocument(new PdfReader(input));
            merger.merge(sourcePdf, 1, sourcePdf.getNumberOfPages());
            sourcePdf.close();
        }
    }
}


    //
//    @Override
//    public byte[] downloadMedicalLifestylePdf(String medicalLifeStyleReq) {
//
//        try {
//            JsonObject userData = jsonUtility.getJsonObject(medicalLifeStyleReq);
//
////            BuyOnlineUserInfo userData = buyOnlineUserInfoRepository.getUserData(applicationNo);
////            logger.info("Pdf name list to be generated:{}", pdfNames);
////            logger.info("Application number:{}", applicationNo);
//            if (userData != null) {
//                JsonObject basicDetailObj = jsonUtility.getJsonObjectByKey("basicDetails", userData);
//                logger.info("Basic details object:{}", basicDetailObj);
//                JsonObject lifestyleDetailObj = jsonUtility.getJsonObjectByKey("lifestyleQ", userData);
//                logger.info("Lifestyle details object:{}", lifestyleDetailObj);
//                JsonObject medicalDetailObj = jsonUtility.getJsonObjectByKey("medicalQ", userData);
//                logger.info("Medical details object:{}", medicalDetailObj);
//                JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails", userData);
//                logger.info("Personal details object:{}", personalDetailObj);
//                JsonObject travelDetailObj = jsonUtility.getJsonObjectByKey("travelDetails", userData);
//                logger.info("Travel detail object:{}", travelDetailObj);
//                JsonObject policyHolderObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
//                String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderObj);
//                JsonObject insuredHolderObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);
//
//                String applicationNo = jsonUtility.getJsonKeyValue("applicationNumber", userData);
//                boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);
//
//                boolean buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderObj).equalsIgnoreCase("Myself");

                //get key
//                if (jointLife) {
//                    buyFor = true;
//                }
//                logger.info("Buy for boolean value is:{} ", buyFor);
//                String lifeAssureName;
//                JsonObject lifeStyleObj;
//                JsonObject medicalObj;
//                JsonObject travelObj;
//                if (buyFor) {
//                    lifeAssureName = jsonUtility.getJsonKeyValue("fullName", policyHolderObj).toUpperCase();
//                    lifeStyleObj = jsonUtility.getJsonObjectByKey("primary", lifestyleDetailObj);
//                    medicalObj = jsonUtility.getJsonObjectByKey("primary", medicalDetailObj);
//                    travelObj = jsonUtility.getJsonObjectByKey("primary", travelDetailObj);
//                } else {
//                    lifeAssureName = jsonUtility.getJsonKeyValue("fullName", insuredHolderObj).toUpperCase();
//                    lifeStyleObj = jsonUtility.getJsonObjectByKey("secondary", lifestyleDetailObj);
//                    medicalObj = jsonUtility.getJsonObjectByKey("secondary", medicalDetailObj);
//                    travelObj = jsonUtility.getJsonObjectByKey("secondary", travelDetailObj);
//                }
//                JsonObject healthHistoryObj = jsonUtility.getJsonObjectByKey("healthHistory", medicalObj);
//                JsonObject primaryPersonalDetailsObj = jsonUtility.getJsonObjectByKey("primary", personalDetailObj);
//                String placeName = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailsObj);
//                String primaryMobileNo = jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetailsObj);
//                List<String> combineData = new ArrayList<>();
//                for (String names : pdfNames) {
//
//                    if (names.equalsIgnoreCase("armedForces")) {
//                        JsonObject armObj = jsonUtility.getJsonObjectByKey("army", lifeStyleObj);
//                        String armedForcesResponse = "";
//                        try {
//                            armedForcesResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateArmedForcesPDF(applicationNo, lifeAssureName, armObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Armed forces pdf response:{}", armedForcesResponse);
//                            if (!armedForcesResponse.isEmpty()) {
//                                combineData.add(armedForcesResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in armed forces method:{}", e.getMessage());
//                        }
//
//                    }
//                    if (names.equalsIgnoreCase("fishing")) {
//                        JsonObject fishingObj = jsonUtility.getJsonObjectByKey("fishing", lifeStyleObj);
//                        String fishingResponse = "";
//                        try {
//                            fishingResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateFishingPDF(applicationNo, lifeAssureName, fishingObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Fishing pdf response:{}", fishingResponse);
//                            if (!fishingResponse.isEmpty()) {
//                                combineData.add(fishingResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in fishing method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("diving")) {
//                        JsonObject divingObj = jsonUtility.getJsonObjectByKey("diving", lifeStyleObj);
//                        String divingResponse = "";
//                        try {
//                            divingResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateDivingPDF(applicationNo, lifeAssureName, divingObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Diving pdf response:{}", divingResponse);
//                            if (!divingResponse.isEmpty()) {
//                                combineData.add(divingResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in diving method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("driving")) {
//                        JsonObject drivingObj = jsonUtility.getJsonObjectByKey("driving", lifeStyleObj);
//                        String drivingResponse = "";
//                        try {
//                            drivingResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateDrivingPDF(applicationNo, lifeAssureName, drivingObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Driving pdf response:{}", drivingResponse);
//                            if (!drivingResponse.isEmpty()) {
//                                combineData.add(drivingResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in driving method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("aviation")) {
//                        JsonObject aviationObj = jsonUtility.getJsonObjectByKey("aviation", lifeStyleObj);
//                        String aviationResponse = "";
//                        try {
//                            aviationResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateAviationPDF(applicationNo, lifeAssureName, aviationObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Aviation pdf response:{}", aviationResponse);
//                            if (!aviationResponse.isEmpty()) {
//                                combineData.add(aviationResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in aviation method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("mining")) {
//                        JsonObject miningObj = jsonUtility.getJsonObjectByKey("mining", lifeStyleObj);
//                        String miningResponse = "";
//                        try {
//                            miningResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateMiningPDF(applicationNo, lifeAssureName, miningObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Mining pdf response:{}", miningResponse);
//                            if (!miningResponse.isEmpty()) {
//                                combineData.add(miningResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in mining method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("occupation")) {
//                        JsonObject occupationalObj = jsonUtility.getJsonObjectByKey("occupational", lifeStyleObj);
//                        String occupationResponse = "";
//                        try {
//                            occupationResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateOccupationPDF(applicationNo, lifeAssureName, occupationalObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Occupational pdf response:{}", occupationResponse);
//                            if (!occupationResponse.isEmpty()) {
//                                combineData.add(occupationResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in occupation method:{}", e.getMessage());
//                        }
//
//                    }
//                    if (names.equalsIgnoreCase("oilRefinery")) {
//                        JsonObject oilRefineryObj = jsonUtility.getJsonObjectByKey("oilrefinery", lifeStyleObj);
//                        String oilRefineryResponse = "";
//                        try {
//                            oilRefineryResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateOilRefineryPDF(applicationNo, lifeAssureName, oilRefineryObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Oil Refinery pdf response:{}", oilRefineryResponse);
//                            if (!oilRefineryResponse.isEmpty()) {
//                                combineData.add(oilRefineryResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in oil refinery method:{}", e.getMessage());
//                        }
//                    }
//
//                    if (names.equalsIgnoreCase("marine")) {
//                        JsonObject marineObj = jsonUtility.getJsonObjectByKey("marine", lifeStyleObj);
//                        String marineResponse = "";
//                        try {
//                            marineResponse = Base64.getEncoder().encodeToString(
//                                    generateLifeStylePDF.generateMarinePDF(applicationNo, lifeAssureName, marineObj, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Marine pdf response:{}", marineResponse);
//                            if (!marineResponse.isEmpty()) {
//                                combineData.add(marineResponse);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in Marine method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("thyroid")) {
//                        JsonObject medical = jsonUtility.getJsonObjectByKey("diagnosedHepatitis", medicalObj);
//                        String medicalResp = "";
//                        try {
//                            medicalResp = Base64.getEncoder().encodeToString(
//                                    generateHealthPDF.generateThyroidPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Thyroid pdf response:{}", medicalResp);
//                            if (!medicalResp.isEmpty()) {
//                                combineData.add(medicalResp);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in thyroid forces method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("respiratory")) {
//                        JsonObject medical = jsonUtility.getJsonObjectByKey("respiratoryDisorder", healthHistoryObj);
//                        String respiratoryResp = "";
//                        try {
//                            respiratoryResp = Base64.getEncoder().encodeToString(
//                                    generateHealthPDF.generateRespiratoryPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Respiratory pdf response:{}", respiratoryResp);
//                            if (!respiratoryResp.isEmpty()) {
//                                combineData.add(respiratoryResp);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in respiratory method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("hyperTension")) {
//                        JsonObject medical = jsonUtility.getJsonObjectByKey("hypertension", healthHistoryObj);
//                        String hyperTensionResp = "";
//                        try {
//                            hyperTensionResp = Base64.getEncoder().encodeToString(
//                                    generateHealthPDF.generateHyperTensionPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Hyper tension pdf response:{}", hyperTensionResp);
//                            if (!hyperTensionResp.isEmpty()) {
//                                combineData.add(hyperTensionResp);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in hypertension method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("diabetes")) {
//                        JsonObject medical = jsonUtility.getJsonObjectByKey("sufferedDiabetes", healthHistoryObj);
//                        String diabetesResp = "";
//                        try {
//                            diabetesResp = Base64.getEncoder().encodeToString(
//                                    generateHealthPDF.generateDiabetesPDF(applicationNo, lifeAssureName, medical, isOmniDoc, placeName, primaryMobileNo, countryCode));
//                            logger.info("Diabetes pdf response:{}", diabetesResp);
//                            if (!diabetesResp.isEmpty()) {
//                                combineData.add(diabetesResp);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in diabetes method:{}", e.getMessage());
//                        }
//                    }
//                    if (names.equalsIgnoreCase("covid")) {
//                        String covidResp = "";
//                        try {
//                            covidResp = Base64.getEncoder().encodeToString(
//                                    generateHealthPDF.generateCovidPDF(applicationNo, lifeAssureName, medicalObj, isOmniDoc, placeName, primaryMobileNo, travelObj, countryCode));
//                            logger.info("Covid pdf response:{}", covidResp);
//                            if (!covidResp.isEmpty()) {
//                                combineData.add(covidResp);
//                            }
//                        } catch (Exception e) {
//                            logger.info("Exception in generate covid pdf method:{}", e.getMessage());
//                        }
//                    }
//                }
//                return downloadPDFService.downloadMedicalLifestylePdf(combineData, applicationNo);
//            } else {
//                JsonObject object = new JsonObject();
//                object.addProperty(BuyOnlineConstants.COMMON_CONSTANT.STATUS, "error");
//                logger.info("Invalid application number:{}", applicationNo);
//                return object.toString().getBytes();
//            }
//        } catch (Exception e) {
//            JsonObject object = new JsonObject();
//            logger.error("Exception occurs in medical and lifestyle merged pdf:{}", e.getMessage());
//            object.addProperty(BuyOnlineConstants.COMMON_CONSTANT.STATUS, "error");
//            object.addProperty(BuyOnlineConstants.COMMON_CONSTANT.RESPONSE_MSG, e.getMessage());



