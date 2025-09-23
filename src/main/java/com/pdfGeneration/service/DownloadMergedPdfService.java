package com.pdfGeneration.service;

import com.google.gson.JsonObject;

import java.util.List;

public interface DownloadMergedPdfService {

    byte[] downloadMedicalLifestylePdf(String medicalLifeStyleReq);
    byte[] downloadMergedMedicalLifestylePdf(List<String> base64String, String applicationNumber, JsonObject metadataObj);

}
