package com.pdfGeneration.service.impl;

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
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.pdfGeneration.service.FatcaPDFService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FatcaPDFServiceImpl implements FatcaPDFService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;

    public FatcaPDFServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;

    }

    @Override
    public byte[] downloadFatcaForm(String fatcaReq) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        JsonObject userData = jsonUtility.getJsonObject(fatcaReq);
        JsonObject basicDetailObj = jsonUtility.getJsonObjectByKey("basicDetails",userData);
        logger.info("Basic details object:{}", basicDetailObj);
        JsonObject nomineeDetailObj = jsonUtility.getJsonObjectByKey("nomineeDetails",userData);
        logger.info("Nominee details object:{}", nomineeDetailObj);
        JsonObject employmentDetailObj = jsonUtility.getJsonObjectByKey("employeementData",userData);
        logger.info("Employment details object:{}", employmentDetailObj);
        JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails",userData);
        logger.info("Personal details object:{}", personalDetailObj);
        JsonObject fatcaDetailObj = jsonUtility.getJsonObjectByKey("fatcaDetails",userData);
        logger.info("Fatca details object:{}", fatcaDetailObj);
        JsonObject contentJson = jsonUtility.getJsonObjectByKey("content",userData);

        String applicationNumber = jsonUtility.getJsonKeyValue("applicationNumber", userData);
        boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);

        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            //
            JsonObject metadataObj = jsonUtility.getJsonObjectByKey("metadata",userData);
            String author = jsonUtility.getJsonKeyValue("author", metadataObj);
            String creator = jsonUtility.getJsonKeyValue("creator", metadataObj);
            String title = jsonUtility.getJsonKeyValue("title", metadataObj);
            PdfDocumentInfo pdfDocumentInfo=pdfDoc.getDocumentInfo();
            pdfDocumentInfo.setAuthor(author);
            pdfDocumentInfo.setCreator(creator);
            pdfDocumentInfo.setTitle(title);
            pdfDocumentInfo.addCreationDate();
            pdfDoc.addNewPage();

            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images",userData);

            String logoFilename = jsonUtility.getJsonKeyValue("logo", imagesJson);
            String logoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);

            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image img = pdfUtility.getPDFLogo(logoBase64);
            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);
            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font);
            document.setFontSize(9);
            document.setFontKerning(FontKerning.YES);
            document.setMargins(5f, 20f, 10f, 20f);
            document.add(img);

            Table table = new Table(1);
            table.setWidthPercent(100);
            Cell headingCell = new Cell();
            headingCell.setBackgroundColor(Color.ORANGE, 100);
            Paragraph p = new Paragraph("INSURANCE FATCA/CRS DECLARATION");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p).setBorder(Border.NO_BORDER);
            table.addCell(headingCell);
            p = new Paragraph(
                    new Text(jsonUtility.getJsonKeyValue("content1", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("content2", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content3", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content4", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content5", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.ORANGE, 100);
            p = new Paragraph("ALL THE FIELDS GIVEN BELOW ARE MANDATORY. PLEASE DO NOT LEAVE THEM BLANK");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p).setBorder(Border.NO_BORDER);
            table.addCell(headingCell);

            JsonObject policyHolderObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            String proposerName = jsonUtility.getJsonKeyValue("fullName", policyHolderObj);
            String lifeInsuredName = jsonUtility.getJsonKeyValue("fullName",
                    jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj));
            String nomineeName = jsonUtility.getJsonKeyValue("name",
                    nomineeDetailObj.get("nominees").getAsJsonArray().get(0).getAsJsonObject());

            JsonObject primaryFatcaObj = jsonUtility.getJsonObjectByKey("primary", fatcaDetailObj);
            JsonObject secondaryFatcaObj = jsonUtility.getJsonObjectByKey("secondary", fatcaDetailObj);
            JsonObject primaryBornInIndiaObj = jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaObj);
            JsonObject secondaryBornInIndiaObj = jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaObj);
            String proposerFatherName;
            if (jsonUtility.getJsonKeyValue("status", primaryBornInIndiaObj).equalsIgnoreCase("y")) {
                proposerFatherName = jsonUtility.getJsonKeyValue("fatherName", primaryBornInIndiaObj);
            } else {
                proposerFatherName = "";
            }
            String lifeInsuredFatherName;
            if (jsonUtility.getJsonKeyValue("status", secondaryBornInIndiaObj).equalsIgnoreCase("y")) {
                lifeInsuredFatherName = jsonUtility.getJsonKeyValue("fatherName", secondaryBornInIndiaObj);
            } else {
                lifeInsuredFatherName = "";
            }
            String nomineeFatherName = "";

            JsonObject primaryResidentOtherThanUsObj = jsonUtility.getJsonObjectByKey("residentOtherThanUS",
                    primaryFatcaObj);
            JsonObject secResidentOtherThanUsObj = jsonUtility.getJsonObjectByKey("residentOtherThanUS",
                    secondaryFatcaObj);

            String proposerCountryOfResidence = jsonUtility.getJsonKeyValue("countryOfResidencyLabel",
                    primaryResidentOtherThanUsObj);
            String lifeInsuredCountryOfResidence = jsonUtility.getJsonKeyValue("countryOfResidencyLabel",
                    secResidentOtherThanUsObj);
            String nomineeCountryOfResidence = "";

            String proposerTelephoneNo = jsonUtility.getJsonKeyValue("telephoneNumber", primaryResidentOtherThanUsObj);
            String lifeInsuredTelephoneNo = jsonUtility.getJsonKeyValue("telephoneNumber", secResidentOtherThanUsObj);
            String nomineeTelephoneNo = " ";

            String proposerCitizenShip = jsonUtility.getJsonKeyValue("countryOfCitizenshipLabel",
                    primaryResidentOtherThanUsObj);
            String lifeInsuredCitizenShip = jsonUtility.getJsonKeyValue("countryOfCitizenshipLabel",
                    secResidentOtherThanUsObj);
            String nomineeCitizenShip = " ";

            JsonObject primaryPermanentAddOutIndia = jsonUtility.getJsonObjectByKey("permanentAddressOutsideIndia",
                    primaryFatcaObj);
            JsonObject secondaryPermanentAddOutIndia = jsonUtility.getJsonObjectByKey("permanentAddressOutsideIndia",
                    secondaryFatcaObj);

            String proposerAddress = jsonUtility.getJsonKeyValue("addressLine1", primaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("addressLine2", primaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine3", primaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("pincode", primaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("city", primaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("state", primaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("countryLabel", primaryPermanentAddOutIndia);

            String lifeInsuredAddress = jsonUtility.getJsonKeyValue("addressLine1", secondaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine2", secondaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine3", secondaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("pincode", secondaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("city", secondaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("state", secondaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("countryLabel", secondaryPermanentAddOutIndia);
            String nomineeAddress = " ";
            String proposerSourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome",
                    jsonUtility.getJsonObjectByKey("primary", employmentDetailObj));
            String lifeInsuredSourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome",
                    jsonUtility.getJsonObjectByKey("secondary", employmentDetailObj));
            String nomineeSourceOfIncome = "";

            Table fatcaDetails = new Table(new float[]{350F, 350F, 350F, 350F});
            fatcaDetails.addCell(new Cell().add("CLIENT ID"));
            Cell proposerMergedcell = new Cell(1, 3);
            fatcaDetails.addCell(proposerMergedcell);
            fatcaDetails.addCell(new Cell().add("Parameters").setBackgroundColor(Color.LIGHT_GRAY));
            fatcaDetails.addCell(new Cell().add("Proposer").setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(Color.LIGHT_GRAY));
            fatcaDetails.addCell(new Cell().add("Life Insured").setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(Color.LIGHT_GRAY));
            fatcaDetails.addCell(new Cell().add("Nominee").setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(Color.LIGHT_GRAY));

            fatcaDetails.addCell(new Cell().add("Name "));
            fatcaDetails.addCell(new Cell().add(proposerName).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredName).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeName).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Father's name  "));
            fatcaDetails.addCell(new Cell().add(proposerFatherName).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredFatherName).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeFatherName).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("US Person  "));
            p = new Paragraph();
            if (jsonUtility
                    .getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", primaryFatcaObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility
                    .getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", primaryFatcaObj))
                    .equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (jsonUtility
                    .getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", secondaryFatcaObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility
                    .getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", secondaryFatcaObj))
                    .equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Resident of any other country other than US   "));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", primaryResidentOtherThanUsObj).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", primaryResidentOtherThanUsObj).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", secResidentOtherThanUsObj).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", secResidentOtherThanUsObj).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Country of Residence- Please specify the country   "));
            fatcaDetails.addCell(new Cell().add(proposerCountryOfResidence).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredCountryOfResidence).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeCountryOfResidence).setTextAlignment(TextAlignment.CENTER));

            String primaryPermanentAddressOutsideIndiaStatus = jsonUtility.getJsonKeyValue("status",
                    primaryPermanentAddOutIndia);
            String secondaryPermanentAddressOutsideIndiaStatus = jsonUtility.getJsonKeyValue("status",
                    secondaryPermanentAddOutIndia);

            String primaryCitizenStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaObj));
            String secondaryCitizenStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaObj));

            String primaryResidentStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaObj));
            String secondaryResidentStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaObj));

            String primaryGreenCardStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaObj));
            String secondaryGreenCardStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaObj));
            String proposerTinNumber = "";
            String insuredTinNumber = "";
            if (primaryCitizenStatus.equalsIgnoreCase("y")||primaryResidentStatus.equalsIgnoreCase("y")
                    ||primaryGreenCardStatus.equalsIgnoreCase("y")) {
                proposerTinNumber = jsonUtility.getJsonKeyValue("tinNumber", primaryResidentOtherThanUsObj);
            }
            if (secondaryCitizenStatus.equalsIgnoreCase("y")||secondaryResidentStatus.equalsIgnoreCase("y")
                    ||secondaryGreenCardStatus.equalsIgnoreCase("y")) {
                insuredTinNumber = jsonUtility.getJsonKeyValue("tinNumber", secResidentOtherThanUsObj);
            }
            fatcaDetails.addCell(new Cell().add("Taxpayer Identification Number (TIN)" +
                    "(Mention complete number and Submit a\n" +
                    "copy - Mandatory)"));
            fatcaDetails.addCell(new Cell().add(proposerTinNumber).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(insuredTinNumber).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Exemption claimed, if any (to be supported by necessary documents) "));
            fatcaDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));

            proposerMergedcell = new Cell(1, 4);
            proposerMergedcell
                    .add(new Cell().add("COUNTRY OUTSIDE INDIA Indicia").setBackgroundColor(Color.LIGHT_GRAY));
            fatcaDetails.addCell(proposerMergedcell);

            fatcaDetails.addCell(new Cell().add("Country issuing the  \"Identity Proof\" "));
            fatcaDetails.addCell(
                    new Cell().add(jsonUtility.getJsonKeyValue("countryOfIssuingIdLabel", primaryResidentOtherThanUsObj))
                            .setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(
                    new Cell().add(jsonUtility.getJsonKeyValue("countryOfIssuingIdLabel", secResidentOtherThanUsObj))
                            .setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Telephone No. Outside India?  "));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", primaryResidentOtherThanUsObj).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", primaryResidentOtherThanUsObj).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", secResidentOtherThanUsObj).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", secResidentOtherThanUsObj).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("If Yes, provide Telephone no."));
            fatcaDetails.addCell(new Cell().add(proposerTelephoneNo).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredTelephoneNo).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeTelephoneNo).setTextAlignment(TextAlignment.CENTER));


            fatcaDetails.addCell(new Cell().add("Citizenship Outside India.  "));
            p = new Paragraph();
            if (primaryCitizenStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (primaryCitizenStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (secondaryCitizenStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (secondaryCitizenStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("If Yes, provide country of citizenship"));
            fatcaDetails.addCell(new Cell().add(proposerCitizenShip).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredCitizenShip).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeCitizenShip).setTextAlignment(TextAlignment.CENTER));

            String primaryOutIndiaStatus = jsonUtility.getJsonKeyValue("status", primaryPermanentAddOutIndia);
            String secondaryOutIndiaStatus = jsonUtility.getJsonKeyValue("status", secondaryPermanentAddOutIndia);

            fatcaDetails.addCell(new Cell().add("Communication / Permanent Address Outside India?  "));
            p = new Paragraph();
            if (primaryOutIndiaStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (primaryOutIndiaStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (secondaryOutIndiaStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (secondaryOutIndiaStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No    ");
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("If Yes, provide the address "));
            fatcaDetails.addCell(new Cell().add(proposerAddress).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredAddress).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeAddress).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Country/ies of Residence for Tax purpose is outside India?"));
            p = new Paragraph();
            boolean primaryTaxStatus = jsonUtility.getBooleanKeyValue("tax", primaryPermanentAddOutIndia);
            boolean secondaryTaxStatus = jsonUtility.getBooleanKeyValue("tax", secondaryPermanentAddOutIndia);

            if (primaryPermanentAddressOutsideIndiaStatus.equalsIgnoreCase("y")) {
                if (!primaryTaxStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (primaryTaxStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No    ");
            } else {
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
            }
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (secondaryPermanentAddressOutsideIndiaStatus.equalsIgnoreCase("y")) {
                if (!secondaryTaxStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (secondaryTaxStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No    ");
            } else {
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
            }
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            boolean primaryAttorneyStatus = jsonUtility.getBooleanKeyValue("attorney", primaryPermanentAddOutIndia);
            boolean secondaryAttorneyStatus = jsonUtility.getBooleanKeyValue("attorney", secondaryPermanentAddOutIndia);
            fatcaDetails.addCell(new Cell().add("Power of Attorney (POA) of a person outside India?"));
            p = new Paragraph();
            if (primaryPermanentAddressOutsideIndiaStatus.equalsIgnoreCase("y")) {
                if (!primaryAttorneyStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (primaryAttorneyStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No    ");
            } else {
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
            }
            // Proposer
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            if (secondaryPermanentAddressOutsideIndiaStatus.equalsIgnoreCase("y")) {
                if (!secondaryAttorneyStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (secondaryAttorneyStatus) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No    ");
            } else {
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
            }
            // Life Insured
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No    ");
            // Nominee
            fatcaDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add("Source of Income "));
            fatcaDetails.addCell(new Cell().add(proposerSourceOfIncome).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(lifeInsuredSourceOfIncome).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(nomineeSourceOfIncome).setTextAlignment(TextAlignment.CENTER));

            table.addCell(fatcaDetails);

            headingCell = new Cell();
            headingCell.setHeight(25);
            headingCell.setBorder(Border.NO_BORDER);
            table.addCell(headingCell);

            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.ORANGE, 100);
            headingCell.setHeight(15);
            headingCell.setBorder(Border.NO_BORDER);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content6", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.BLACK);
            headingCell.add(p).setBorder(Border.NO_BORDER);
            table.addCell(headingCell);

            p = new Paragraph(jsonUtility.getJsonKeyValue("content7", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content8", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content9", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content10", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content11", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content12", contentJson));

            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            String proposerSignature = "";
            String lifeInsuredSignature = "";
            String nomineeSignature = "";

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String currentDate = sdf.format(date);

            Table authorizeTable = new Table(new float[]{300F, 400F, 400F, 400F});
            authorizeTable.setTextAlignment(TextAlignment.CENTER);

            authorizeTable.addCell(new Cell().add("").setBackgroundColor(Color.LIGHT_GRAY));
            authorizeTable.addCell(new Cell().add("PROPOSER").setBackgroundColor(Color.LIGHT_GRAY));
            authorizeTable.addCell(new Cell().add("LIFE INSURED").setBackgroundColor(Color.LIGHT_GRAY));
            authorizeTable.addCell(new Cell().add("NOMINEE").setBackgroundColor(Color.LIGHT_GRAY));

            authorizeTable.addCell(new Cell().add("Name").setBackgroundColor(Color.ORANGE));
            authorizeTable.addCell(new Cell().add(proposerName));
            authorizeTable.addCell(new Cell().add(lifeInsuredName));
            authorizeTable.addCell(new Cell().add(nomineeName));

            authorizeTable.addCell(new Cell().add("Signature").setBackgroundColor(Color.ORANGE));
            authorizeTable.addCell(new Cell().add(proposerSignature));
            authorizeTable.addCell(new Cell().add(lifeInsuredSignature));
            authorizeTable.addCell(new Cell().add(nomineeSignature));

            authorizeTable.addCell(new Cell().add("Date").setBackgroundColor(Color.ORANGE));
            authorizeTable.addCell(new Cell().add(currentDate));
            if (!lifeInsuredName.isEmpty()) {
                authorizeTable.addCell(new Cell().add(currentDate));
            }else {
                authorizeTable.addCell(new Cell().add(""));
            }
            authorizeTable.addCell(new Cell().add(currentDate));

            table.addCell(new Cell().add(authorizeTable).setBorder(Border.NO_BORDER));

            document.add(table);
            if (isOmniDoc) {
                p = new Paragraph();
                p.add(imgChecked);
                String primaryMobileNo = jsonUtility.getJsonKeyValue("mobileNumber", jsonUtility.getJsonObjectByKey("primary", personalDetailObj));
                String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderObj);
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
            document.add(new Paragraph("\n\n\n\n"));
            Table grandFooterTable = new Table(2);
            grandFooterTable.setFontSize(8F);
            grandFooterTable.setTextAlignment(TextAlignment.CENTER);
            Paragraph companyText = new Paragraph(
                    new Text(jsonUtility.getJsonKeyValue("signature", contentJson)).setFontColor(Color.BLUE));
            companyText.add(new Text(jsonUtility.getJsonKeyValue("addr", contentJson)));
            grandFooterTable.addCell(companyText).setTextAlignment(TextAlignment.LEFT);
            Paragraph companyContact = new Paragraph(new Text("Tel: ").setBold().setFontColor(Color.ORANGE));
            companyContact.add(new Text("+91 22 6165 8700"));
            companyContact.add(new Text("  Fax: ").setBold().setFontColor(Color.ORANGE));
            companyContact.add(new Text("+91 22 6857 0600"));
            companyContact.add(new Text("  Toll Free: ").setBold().setFontColor(Color.ORANGE));
            companyContact.add(new Text("1800-209-8700"));

            companyContact.add(new Text(
                    "\n----------------------------------------------------------------------------------------"));
            companyContact.add(new Text("\nE-mail: ").setBold().setFontColor(Color.ORANGE));
            companyContact.add(new Text("customer.\u001Afirst@india\u001Arstlife.com"));
            companyContact.add(new Text("  Website: ").setBold().setFontColor(Color.ORANGE));
            companyContact.add(new Text("www.india\u001Afirstlife.com"));

            grandFooterTable.addCell(companyContact);
            grandFooterTable.setWidth(UnitValue.createPercentValue(100));
            PageSize ps = pdfDoc.getDefaultPageSize();

            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(),
                    ps.getWidth() - document.getLeftMargin() - document.getRightMargin());
            document.add(grandFooterTable);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Exception occurs in generate fatca pdf:{}", e.getMessage());
            return baos.toByteArray();
        }
    }

}