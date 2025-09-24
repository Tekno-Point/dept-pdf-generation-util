package com.pdfGeneration.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.TextAlignment;
import com.pdfGeneration.service.ExistingPolicyPDFService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.MasterUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExistingPolicyPDFServiceImpl implements ExistingPolicyPDFService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;
    private final MasterUtility masterUtility;

    public ExistingPolicyPDFServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility, MasterUtility masterUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;
        this.masterUtility = masterUtility;
    }

    @Override
    public byte[] downloadExistingPolicy(String existingPolicyReqObj) {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos)) {

            JsonObject userData = jsonUtility.getJsonObject(existingPolicyReqObj);
            JsonObject otherPolicyDetailObj = jsonUtility.getJsonObjectByKey("basicDetails",userData);
            logger.info("Other Policy Object: {}", otherPolicyDetailObj);

            JsonObject basicDetailObj = jsonUtility.getJsonObjectByKey("basicDetails",userData);
            logger.info("Basic Details Object: {}", basicDetailObj);
            JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails",userData);
            String placeName = jsonUtility.getJsonKeyValue("city",
                    jsonUtility.getJsonObjectByKey("primary", personalDetailObj));
            JsonObject policyHolderBasicDetailObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            JsonObject insuredBasicDetailsObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);

            JsonObject primaryOtherDetailObj = jsonUtility.getJsonObjectByKey("primary", otherPolicyDetailObj);
            JsonObject primaryAppliedInsurance = jsonUtility.getJsonObjectByKey("appliedInsurance",
                    primaryOtherDetailObj);
            String existingInsuranceAccountPrimaryStatus = jsonUtility.getJsonKeyValue("status",
                    primaryAppliedInsurance);

            JsonObject secondaryDetailObj = jsonUtility.getJsonObjectByKey("secondary", otherPolicyDetailObj);
            JsonObject secondaryAppliedInsurance = jsonUtility.getJsonObjectByKey("appliedInsurance",
                    secondaryDetailObj);
            String existingInsuranceAccountSecondaryStatus = jsonUtility.getJsonKeyValue("status",
                    secondaryAppliedInsurance);
            if (existingInsuranceAccountSecondaryStatus.isEmpty()) {
                existingInsuranceAccountSecondaryStatus = "n";
            }

            JsonArray secondaryExistingPolicyDetails = new JsonArray();
            JsonArray primaryExistingPolicyDetails = new JsonArray();

            JsonArray primaryPolicyDetails = new JsonArray();
            JsonArray secondaryPolicyDetails = new JsonArray();
            if (primaryAppliedInsurance.has("existingPolicyDetails")) {
                primaryExistingPolicyDetails = primaryAppliedInsurance.get("existingPolicyDetails").getAsJsonArray();
                primaryPolicyDetails = primaryAppliedInsurance.get("policyDetails").getAsJsonArray();
            }
            if (secondaryAppliedInsurance.has("existingPolicyDetails")) {
                secondaryExistingPolicyDetails = secondaryAppliedInsurance.get("existingPolicyDetails")
                        .getAsJsonArray();
                secondaryPolicyDetails = secondaryAppliedInsurance.get("policyDetails").getAsJsonArray();
            }

            String applicationNumber = jsonUtility.getJsonKeyValue("applicationNumber", userData);
            boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);
            JsonObject contentJson = jsonUtility.getJsonObjectByKey("content", userData);

            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images", userData);
            String logoFilename = jsonUtility.getJsonKeyValue("risipLogo", imagesJson);
            String logoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image img = pdfUtility.getPDFLogo(logoBase64);
            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);
            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            PdfDocument pdf = new PdfDocument(writer);
            ////////////////////
            JsonObject metadataObj = jsonUtility.getJsonObjectByKey("metadata",userData);
            String author = jsonUtility.getJsonKeyValue("author", metadataObj);
            String creator = jsonUtility.getJsonKeyValue("creator", metadataObj);
            String title = jsonUtility.getJsonKeyValue("title", metadataObj);

            PdfDocumentInfo pdfDocumentInfo=pdf.getDocumentInfo();
            pdfDocumentInfo.setAuthor(author);
            pdfDocumentInfo.setCreator(creator);
            pdfDocumentInfo.setTitle(title);
            pdfDocumentInfo.addCreationDate();

            Document document = new Document(pdf, PageSize.A3).setFont(font);
            document.setFontSize(9);
            document.setFontKerning(FontKerning.YES);
            document.setMargins(10f, 10f, 10f, 10f);

            Paragraph p = new Paragraph();
            p.setTextAlignment(TextAlignment.RIGHT);
            p.add(img);
            document.add(p);
            p = new Paragraph();
            p.setTextAlignment(TextAlignment.RIGHT);
            p.add(new Text("Application No. : " + applicationNumber).setBold());
            document.add(p);

            Map<String, String> optionsMap = new HashMap<>();

            optionsMap.put("EN", "Endowment");
            optionsMap.put("TA", "Pure Term Assurance");
            optionsMap.put("TO", "Term Assurance");
            optionsMap.put("SH", "Savings linked Health Cover");
            optionsMap.put("AN", "Annuity");
            optionsMap.put("PS", "Pension");
            optionsMap.put("OT", "Any Other Product");
            optionsMap.put("", "");
            optionsMap.put(" ", " ");

            Table table = new Table(1);
            table.setWidthPercent(100);
            Cell headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content1", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            JsonArray primaryInsuranceHeld = new JsonArray();
            String buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderBasicDetailObj);
            boolean myself = buyFor.equalsIgnoreCase("Myself");

            if (existingInsuranceAccountPrimaryStatus.equalsIgnoreCase("Y")) {
                String fullName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
                for (JsonElement element : primaryExistingPolicyDetails) {
                    JsonObject obj = element.getAsJsonObject();
                    obj.addProperty("name", fullName);
                    obj.addProperty("companyName", masterUtility
                            .getCompanyNameByCode(jsonUtility.getJsonKeyValue("Quest_Company_Number", obj)));
                    obj.addProperty("policyNo", jsonUtility.getJsonKeyValue("QuestDBNo", obj));
                    obj.addProperty("annualPremium", jsonUtility.getJsonKeyValue("Premium", obj));
                    String policyStatus = masterUtility.getPolicyStatusByCode(jsonUtility.getJsonKeyValue("Quest_Policy_Status", obj));
                    obj.addProperty("policyStatus", policyStatus);
                    obj.addProperty("termsOfAcceptance", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                    obj.addProperty("sumAssured",
                            pdfUtility.removeTrailingZeros(jsonUtility.getJsonKeyValue("Quest_Sum_Assured", obj)));
                    obj.addProperty("include", jsonUtility.getJsonKeyValue("SelectedPolicy", obj));
                    obj.addProperty("source", "IIB");
                    obj.addProperty("productType", jsonUtility.getJsonKeyValue("Product_Type", obj));
                    obj.addProperty("terms", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                    obj.addProperty("comment", jsonUtility.getJsonKeyValue("comment", obj));
                    obj.addProperty("decline", jsonUtility.getJsonKeyValue("Reason_for_Decline", obj));
                    if (jsonUtility.getJsonKeyValue("policyStatus", obj).isEmpty()) {
                        obj.addProperty("reasonForDecline", jsonUtility.getJsonKeyValue("Reason_for_Decline", obj));
                    }
                    obj.addProperty("yearOfCommencement", jsonUtility.getJsonKeyValue("Quest_DoP_DoC", obj));
                    primaryInsuranceHeld.add(obj);
                }
                for (JsonElement element : primaryPolicyDetails) {
                    JsonObject obj = element.getAsJsonObject();
                    obj.addProperty("name", fullName);
                    obj.addProperty("companyName", masterUtility
                            .getCompanyNameByCode(jsonUtility.getJsonKeyValue("Quest_Company_Number", obj)));
                    obj.addProperty("policyNo", jsonUtility.getJsonKeyValue("QuestDBNo", obj));
                    obj.addProperty("annualPremium", jsonUtility.getJsonKeyValue("Premium", obj));
                    obj.addProperty("policyStatus", jsonUtility.getJsonKeyValue("policyStatus", obj));
                    obj.addProperty("termsOfAcceptance", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                    obj.addProperty("sumAssured",
                            pdfUtility.removeTrailingZeros(jsonUtility.getJsonKeyValue("Quest_Sum_Assured", obj)));
                    obj.addProperty("yearOfCommencement", jsonUtility.getJsonKeyValue("Quest_DoP_DoC", obj));
                    obj.addProperty("include", jsonUtility.getJsonKeyValue("SelectedPolicy", obj));
                    obj.addProperty("source", "SF");
                    obj.addProperty("productType", jsonUtility.getJsonKeyValue("Product_Type", obj));
                    obj.addProperty("terms", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                    obj.addProperty("comment", jsonUtility.getJsonKeyValue("comment", obj));
                    obj.addProperty("decline", jsonUtility.getJsonKeyValue("Reason_for_Decline", obj));
                    obj.addProperty("Quest_Policy_Description",jsonUtility.getJsonKeyValue("reason",obj));
                    primaryInsuranceHeld.add(obj);
                }
            }
            if (!myself) {
                headingCell = new Cell();
                headingCell.setBackgroundColor(Color.GRAY, 100);
                p = new Paragraph("Proposer");
                p.setBold();
                p.setFontKerning(FontKerning.YES);
                p.setFontColor(Color.WHITE);
                headingCell.add(p);
                table.addCell(headingCell);
            }
            p = new Paragraph(jsonUtility.getJsonKeyValue("content1", contentJson));
            logger.info("Myself for health Obj -- primarySecondaryPersonalDetail: ");
            if (existingInsuranceAccountPrimaryStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (existingInsuranceAccountPrimaryStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            table.addCell(p);

            Table insurancePolicies = new Table(new float[]{70,70,130,130,100,100,100,100,100,100,100,100,100});
            insurancePolicies.addCell(new Cell().add("Include")).setTextAlignment(TextAlignment.CENTER); //
            insurancePolicies.addCell(new Cell().add("Source")).setTextAlignment(TextAlignment.CENTER); //
            insurancePolicies.addCell(new Cell().add("Name of Life to be Assured/ Proposer"))
                    .setTextAlignment(TextAlignment.CENTER);
            insurancePolicies.addCell(new Cell().add("Name of the Company")).setTextAlignment(TextAlignment.CENTER);
            insurancePolicies.addCell(new Cell().add("Policy/Proposal No.")).setTextAlignment(TextAlignment.CENTER);
            insurancePolicies.addCell(new Cell().add("Policy Type")).setTextAlignment(TextAlignment.CENTER); //
            insurancePolicies.addCell(new Cell().add("Annual Premium")).setTextAlignment(TextAlignment.CENTER);
            insurancePolicies.addCell(new Cell().add("Sum Assured including riders"))
                    .setTextAlignment(TextAlignment.CENTER);
            insurancePolicies.addCell(new Cell().add("Year of Commencement")).setTextAlignment(TextAlignment.CENTER);
            insurancePolicies.addCell(new Cell().add("Present Status")).setTextAlignment(TextAlignment.CENTER); //
            insurancePolicies.addCell(new Cell().add("Terms of Acceptance")).setTextAlignment(TextAlignment.CENTER); //
            insurancePolicies.addCell(new Cell().add("Details/Reason")).setTextAlignment(TextAlignment.CENTER); //
            insurancePolicies.addCell(new Cell().add("Reason for not to include"))
                    .setTextAlignment(TextAlignment.CENTER); //

            if (!primaryInsuranceHeld.isEmpty()) {
                for (JsonElement element : primaryInsuranceHeld) {
                    JsonObject objNom = element.getAsJsonObject();
                    p = new Paragraph();
                    if (jsonUtility.getJsonKeyValue("include", objNom).equalsIgnoreCase("true")
                            || jsonUtility.getJsonKeyValue("source", objNom).equalsIgnoreCase("SF")) {
                        p.add(imgChecked);
                    } else {
                        p.add(imgUnchecked);
                    }
                    insurancePolicies.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("source", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("name", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("companyName", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("policyNo", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies
                            .addCell(new Cell().add(optionsMap.get(jsonUtility.getJsonKeyValue("productType", objNom)))
                                    .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("annualPremium", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("sumAssured", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("yearOfCommencement", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("policyStatus", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("terms", objNom))
                            .setTextAlignment(TextAlignment.CENTER)); // termsOfAcceptance
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("Quest_Policy_Description", objNom))
                            .setTextAlignment(TextAlignment.CENTER)); // comment
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("decline", objNom))
                            .setTextAlignment(TextAlignment.CENTER)); // Reason_for_Decline
                }
            }
            table.addCell(insurancePolicies);

            if (!myself) {
                JsonArray secondaryInsuranceHeld = new JsonArray();
                if (existingInsuranceAccountSecondaryStatus.equalsIgnoreCase("Y")) {
                    String fullName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
                    for (JsonElement element : secondaryExistingPolicyDetails) {
                        JsonObject obj = element.getAsJsonObject();
                        obj.addProperty("name", fullName);
                        obj.addProperty("companyName", masterUtility
                                .getCompanyNameByCode(jsonUtility.getJsonKeyValue("Quest_Company_Number", obj)));
                        obj.addProperty("policyNo", jsonUtility.getJsonKeyValue("QuestDBNo", obj));
                        obj.addProperty("annualPremium", jsonUtility.getJsonKeyValue("Premium", obj));
                        String policyStatus = masterUtility.getPolicyStatusByCode(jsonUtility.getJsonKeyValue("Quest_Policy_Status", obj));
                        obj.addProperty("policyStatus", policyStatus);
                        obj.addProperty("termsOfAcceptance", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                        obj.addProperty("sumAssured",
                                pdfUtility.removeTrailingZeros(jsonUtility.getJsonKeyValue("Quest_Sum_Assured", obj)));
                        obj.addProperty("include", jsonUtility.getJsonKeyValue("SelectedPolicy", obj));
                        obj.addProperty("source", "IIB");
                        obj.addProperty("productType", jsonUtility.getJsonKeyValue("Product_Type", obj));
                        obj.addProperty("terms", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                        obj.addProperty("comment", jsonUtility.getJsonKeyValue("Quest_Policy_Description", obj));
                        obj.addProperty("decline", jsonUtility.getJsonKeyValue("Reason_for_Decline", obj));
                        obj.addProperty("yearOfCommencement", jsonUtility.getJsonKeyValue("Quest_DoP_DoC", obj));
                        secondaryInsuranceHeld.add(obj);
                    }
                    for (JsonElement element : secondaryPolicyDetails) {
                        JsonObject obj = element.getAsJsonObject();
                        obj.addProperty("name", fullName);
                        obj.addProperty("companyName", masterUtility
                                .getCompanyNameByCode(jsonUtility.getJsonKeyValue("Quest_Company_Number", obj)));
                        obj.addProperty("policyNo", jsonUtility.getJsonKeyValue("QuestDBNo", obj));
                        obj.addProperty("annualPremium", jsonUtility.getJsonKeyValue("Premium", obj));
                        obj.addProperty("policyStatus", jsonUtility.getJsonKeyValue("policyStatus", obj));
                        obj.addProperty("termsOfAcceptance", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                        obj.addProperty("sumAssured",
                                pdfUtility.removeTrailingZeros(jsonUtility.getJsonKeyValue("Quest_Sum_Assured", obj)));
                        obj.addProperty("yearOfCommencement", jsonUtility.getJsonKeyValue("Quest_DoP_DoC", obj));
                        obj.addProperty("include", jsonUtility.getJsonKeyValue("SelectedPolicy", obj));
                        obj.addProperty("source", "SF");
                        obj.addProperty("productType", jsonUtility.getJsonKeyValue("Product_Type", obj));
                        obj.addProperty("terms", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                        obj.addProperty("comment", jsonUtility.getJsonKeyValue("Quest_Policy_Description", obj));
                        obj.addProperty("decline", jsonUtility.getJsonKeyValue("Reason_for_Decline", obj));
                        obj.addProperty("Quest_Policy_Description", jsonUtility.getJsonKeyValue("reason", obj));
                        primaryInsuranceHeld.add(obj);
                    }
                }
                headingCell = new Cell();
                headingCell.setBackgroundColor(Color.GRAY, 100);
                p = new Paragraph("Life Assured");
                p.setBold();
                p.setFontKerning(FontKerning.YES);
                p.setFontColor(Color.WHITE);
                headingCell.add(p);
                table.addCell(headingCell);
                p = new Paragraph(jsonUtility.getJsonKeyValue("content1", contentJson));
                if (existingInsuranceAccountSecondaryStatus.equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (existingInsuranceAccountSecondaryStatus.equalsIgnoreCase("n")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No      ");
                table.addCell(p);
                insurancePolicies = new Table(new float[]{70,70,130,130,100,100,100,100,100,100,100,100,100});
                insurancePolicies.addCell(new Cell().add("Include")).setTextAlignment(TextAlignment.CENTER); //
                insurancePolicies.addCell(new Cell().add("Source")).setTextAlignment(TextAlignment.CENTER); //
                insurancePolicies.addCell(new Cell().add("Name of Life to be Assured/ Proposer"))
                        .setTextAlignment(TextAlignment.CENTER);
                insurancePolicies.addCell(new Cell().add("Name of the Company")).setTextAlignment(TextAlignment.CENTER);
                insurancePolicies.addCell(new Cell().add("Policy/Proposal No.")).setTextAlignment(TextAlignment.CENTER);
                insurancePolicies.addCell(new Cell().add("Policy Type")).setTextAlignment(TextAlignment.CENTER); //
                insurancePolicies.addCell(new Cell().add("Annual Premium")).setTextAlignment(TextAlignment.CENTER);
                insurancePolicies.addCell(new Cell().add("Sum Assured including riders"))
                        .setTextAlignment(TextAlignment.CENTER);
                insurancePolicies.addCell(new Cell().add("Year of Commencement"))
                        .setTextAlignment(TextAlignment.CENTER);
                insurancePolicies.addCell(new Cell().add("Present Status")).setTextAlignment(TextAlignment.CENTER); //
                insurancePolicies.addCell(new Cell().add("Terms of Acceptance")).setTextAlignment(TextAlignment.CENTER); //
                insurancePolicies.addCell(new Cell().add("Details/Reason")).setTextAlignment(TextAlignment.CENTER); //
                insurancePolicies.addCell(new Cell().add("Reason for not to include"))
                        .setTextAlignment(TextAlignment.CENTER); //
                for (JsonElement element : secondaryPolicyDetails) {
                    JsonObject obj = element.getAsJsonObject();
                    String lifeInsuredName = jsonUtility.getJsonKeyValue("fullName",
                            jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj));
                    obj.addProperty("name", lifeInsuredName);

                    obj.addProperty("companyName", masterUtility
                            .getCompanyNameByCode(jsonUtility.getJsonKeyValue("Quest_Company_Number", obj)));
                    obj.addProperty("policyNo", jsonUtility.getJsonKeyValue("QuestDBNo", obj));
                    obj.addProperty("annualPremium", jsonUtility.getJsonKeyValue("Premium", obj));
                    obj.addProperty("policyStatus", jsonUtility.getJsonKeyValue("policyStatus", obj));
                    obj.addProperty("termsOfAcceptance", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                    obj.addProperty("sumAssured",
                            pdfUtility.removeTrailingZeros(jsonUtility.getJsonKeyValue("Quest_Sum_Assured", obj)));
                    obj.addProperty("yearOfCommencement", jsonUtility.getJsonKeyValue("Quest_DoP_DoC", obj));
                    obj.addProperty("include", jsonUtility.getJsonKeyValue("SelectedPolicy", obj));
                    obj.addProperty("source", "SF");
                    obj.addProperty("productType", jsonUtility.getJsonKeyValue("Product_Type", obj));
                    obj.addProperty("terms", jsonUtility.getJsonKeyValue("termsOfAcceptance", obj));
                    obj.addProperty("comment", jsonUtility.getJsonKeyValue("Quest_Policy_Description", obj));
                    obj.addProperty("decline", jsonUtility.getJsonKeyValue("Reason_for_Decline", obj));
                    secondaryInsuranceHeld.add(obj);
                }
                for (JsonElement element : secondaryInsuranceHeld) {
                    JsonObject objNom = element.getAsJsonObject();
                    p = new Paragraph();
                    if (jsonUtility.getJsonKeyValue("include", objNom).equalsIgnoreCase("true")
                            || jsonUtility.getJsonKeyValue("source", objNom).equalsIgnoreCase("SF")) {
                        p.add(imgChecked);
                    } else {
                        p.add(imgUnchecked);
                    }
                    insurancePolicies.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("source", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("name", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("companyName", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("policyNo", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(
                            new Cell().add(optionsMap.get(jsonUtility.getJsonKeyValue("productType", objNom)))
                                    .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("annualPremium", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("sumAssured", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies
                            .addCell(new Cell().add(jsonUtility.getJsonKeyValue("yearOfCommencement", objNom))
                                    .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("policyStatus", objNom))
                            .setTextAlignment(TextAlignment.CENTER));
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("terms", objNom))
                            .setTextAlignment(TextAlignment.CENTER)); // termsOfAcceptance
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("comment", objNom))
                            .setTextAlignment(TextAlignment.CENTER)); // comment
                    insurancePolicies.addCell(new Cell().add(jsonUtility.getJsonKeyValue("decline", objNom))
                            .setTextAlignment(TextAlignment.CENTER)); // Reason_for_Decline
                }
                table.addCell(insurancePolicies);
            }

            document.add(table);

            SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MMMM/yyyy");
            String str = simpleFormat.format(new Date());
            String[] splitDate = str.split("/");
            p = new Paragraph();
            p.add(new Text("Declaration: \n").setBold());
            p.add(jsonUtility.getJsonKeyValue("declarationContent1", contentJson));
            p.add("\n\nSigned at  ");
            p.add(new Text(placeName).setBold().setUnderline());
            p.add("  On This Day  ");
            p.add(new Text(splitDate[0]).setBold().setUnderline());
            p.add("  of  ");
            p.add(new Text(splitDate[1]).setBold().setUnderline());
            p.add("  ,  ");
            p.add(new Text(splitDate[2]).setBold().setUnderline());
            document.add(p);
            document.add(new Paragraph(""));
            if (isOmniDoc) {

                p = new Paragraph();
                p.add(imgChecked);
                String primaryMobileNo = jsonUtility.getJsonKeyValue("mobileNumber", jsonUtility.getJsonObjectByKey("primary", personalDetailObj));
                String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderBasicDetailObj);
                if (countryCode.equalsIgnoreCase("91")) {
                    p.add(new Text("   Validated through the OTP sent to " +
                            "registered mobile no."));
                    p.add(new Text(pdfUtility.maskMobileNumber(primaryMobileNo)).setBold());
                }else {
                    p.add(new Text("   Validated through the OTP sent to " +
                            "registered email id."));
                }
                document.add(p.setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
            }

            Table grandFooterTable = new Table(new float[]{500F, 500F});
            grandFooterTable.setTextAlignment(TextAlignment.CENTER);
            Paragraph companyText = new Paragraph(new Text(jsonUtility.getJsonKeyValue("signature", contentJson)));
            companyText.add(new Text(jsonUtility.getJsonKeyValue("addr", contentJson)));
            grandFooterTable.addCell(companyText).setTextAlignment(TextAlignment.LEFT);
            Paragraph companyContact = new Paragraph(new Text("Tel: ").setBold());
            companyContact.add(new Text("+91 22 6165 8700"));
            companyContact.add(new Text("  Fax: ").setBold());
            companyContact.add(new Text("+91 22 6857 0600"));
            companyContact.add(new Text("  Toll Free: ").setBold());
            companyContact.add(new Text("1800-209-8700"));

            companyContact.add(new Text(
                    "\n----------------------------------------------------------------------------------------"));
            companyContact.add(new Text("\nE-mail: ").setBold());
            companyContact.add(new Text("customer.\u001Afirst@india\u001Arstlife.com"));
            companyContact.add(new Text("  Website: ").setBold());
            companyContact.add(new Text("www.india\u001Afirstlife.com"));

            grandFooterTable.addCell(companyContact);
            grandFooterTable.setWidthPercent(100);
            document.add(grandFooterTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Exception occurs in downloadExistingPolicy pdf:{}", e.getMessage());
            return baos.toByteArray();
        }
    }
}
