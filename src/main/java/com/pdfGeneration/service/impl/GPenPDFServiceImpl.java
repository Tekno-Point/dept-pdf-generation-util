package com.pdfGeneration.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.*;
import com.pdfGeneration.service.GPenPDFService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GPenPDFServiceImpl extends NomineeAddendumPDF implements GPenPDFService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;

    public GPenPDFServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;
    }


    @Override
    public byte[] downloadGPenForm(String gPenReqObj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{

            JsonObject userData = jsonUtility.getJsonObject(gPenReqObj);
            JsonObject basicDetailObj = jsonUtility.getJsonObjectByKey("basicDetails",userData);
            logger.info("Basic details object:{}", basicDetailObj);
            JsonObject bankDetailObj = jsonUtility.getJsonObjectByKey("bankDetails",userData);
            logger.info("Bank details object:{}", bankDetailObj);
            JsonObject planDetailObj = jsonUtility.getJsonObjectByKey("planDetails",userData);
            logger.info("Plan details object:{}", planDetailObj);
            JsonObject nomineeDetailObj = jsonUtility.getJsonObjectByKey("nomineeDetails",userData);
            logger.info("Nominee details object:{}", nomineeDetailObj);
            JsonObject lifestyleDetailObj = jsonUtility.getJsonObjectByKey("lifestyleQ",userData);
            logger.info("Lifestyle details object:{}", lifestyleDetailObj);
            JsonObject medicalDetailObj = jsonUtility.getJsonObjectByKey("medicalQ",userData);
            logger.info("Medical details object:{}", medicalDetailObj);
            JsonObject investmentDetailObj = jsonUtility.getJsonObjectByKey("investmentStretegic",userData);
            logger.info("Investment details object:{}", investmentDetailObj);
            JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails",userData);
            logger.info("Personal details object:{}", personalDetailObj);
            JsonObject employmentDetailObj = jsonUtility.getJsonObjectByKey("employeementData",userData);
            logger.info("Employment details object:{}", employmentDetailObj);
            JsonObject fatcaDetailObj = jsonUtility.getJsonObjectByKey("fatcaDetails",userData);
            logger.info("Fatca details object:{}", fatcaDetailObj);
            JsonObject otherDetailObj = jsonUtility.getJsonObjectByKey("otherPolicyDetails",userData);
            logger.info("Other details object:{}", otherDetailObj);
            JsonObject healthDetailObj = jsonUtility.getJsonObjectByKey("healthDetails",userData);
            logger.info("Health details object:{}", healthDetailObj);
            JsonObject eMandateObj = jsonUtility.getJsonObjectByKey("emandateDetails",userData);
            logger.info("EMandate object:{}", eMandateObj);
            JsonObject documentDetailObj = jsonUtility.getJsonObjectByKey("document",userData);
            logger.info("Document details object:{}", documentDetailObj);
            JsonObject paymentDetailObj = jsonUtility.getJsonObjectByKey("payment",userData);
            logger.info("Payment details object:{}", paymentDetailObj);
            JsonObject contentJson = jsonUtility.getJsonObjectByKey("content",userData);

            JsonObject primaryMedicalObj = jsonUtility.getJsonObjectByKey("primary", medicalDetailObj);
            logger.info("primaryMedical details object:{}", primaryMedicalObj);

            JsonObject policyHolderBasicDetailObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            JsonObject insuredPersonBasicDetailObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);
            String buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderBasicDetailObj);
            logger.info("---- flag---:{}", buyFor);

            JsonObject primaryBankObj = jsonUtility.getJsonObjectByKey("primary", bankDetailObj);
            JsonObject primaryPersonalDetailObj = jsonUtility.getJsonObjectByKey("primary", personalDetailObj);
            JsonObject secondaryPersonalDetailObj = jsonUtility.getJsonObjectByKey("secondary", personalDetailObj);
            JsonObject primaryEmploymentDetailObj = jsonUtility.getJsonObjectByKey("primary", employmentDetailObj);
            JsonObject primaryFatcaDetailObj = jsonUtility.getJsonObjectByKey("primary", fatcaDetailObj);
            JsonObject primaryOtherDetailObj = jsonUtility.getJsonObjectByKey("primary", otherDetailObj);
            JsonObject primaryDocumentDetailObj = jsonUtility.getJsonObjectByKey("primary", documentDetailObj);
            JsonObject secondaryDocumentDetailObj = jsonUtility.getJsonObjectByKey("secondary", documentDetailObj);

            String applicationNumber = jsonUtility.getJsonKeyValue("applicationNumber", userData);
            boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            ////////////////////
            JsonObject metadataObj = jsonUtility.getJsonObjectByKey("metadata",userData);
            String author = jsonUtility.getJsonKeyValue("author", metadataObj);
            String creator = jsonUtility.getJsonKeyValue("creator", metadataObj);
            String title = jsonUtility.getJsonKeyValue("title", metadataObj);
            PdfDocumentInfo pdfDocumentInfo=pdfDoc.getDocumentInfo();
            pdfDocumentInfo.setAuthor(author);
            pdfDocumentInfo.setCreator(creator);
            pdfDocumentInfo.setTitle(title);
            pdfDocumentInfo.addCreationDate();

            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images", userData);
            String logoFilename = jsonUtility.getJsonKeyValue("logo", imagesJson);
            String logoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);
            img.setWidth(200f);
            img.setHeight(120f);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);
            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            logoFilename = jsonUtility.getJsonKeyValue("rupee", imagesJson);
            String rupeeLogoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image rupeeLogo = pdfUtility.getPDFLogo(rupeeLogoBase64).setHeight(8F).setWidth(8F);

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A3).setFont(font);
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

            Table table = new Table(1);
            table.setWidthPercent(100);
            Cell headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("heading", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            float[] pointColumnWidths = new float[]{200F, 680F};
            Table firstBlockTable = new Table(pointColumnWidths);

            SolidBorder solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);
            Table firstBlockLeft = new Table(new float[]{150F,10F, 150F});
            firstBlockLeft.setMarginTop(20);
            firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
            firstBlockLeft.setWidth(150);
            byte[] photoBytes = Base64.getDecoder().decode(jsonUtility.getJsonKeyValue("proposerPhotoBase64",userData));
            ImageData dataPhoto = ImageDataFactory.create(photoBytes);
            Image photo1 = new Image(dataPhoto);
            photo1.setHeight(150);
            photo1.setWidth(130);
            firstBlockLeft.addCell(new Cell().add(photo1).setBorder(solidBorder));
            photoBytes = Base64.getDecoder().decode(jsonUtility.getJsonKeyValue("insuredPhotoBase64",userData));
            dataPhoto = ImageDataFactory.create(photoBytes);
            Image photo2 = new Image(dataPhoto);
            photo2.setHeight(150);
            photo2.setWidth(130);
            firstBlockLeft.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            firstBlockLeft.addCell(new Cell().add(photo2).setBorder(solidBorder));
            Table firstBlockRight = new Table(new float[]{300F, 300F, 300F, 300F});

            String agentCode = "0N000001";
            String branchCodeValue = jsonUtility.getJsonKeyValue("branchCode", policyHolderBasicDetailObj);
            String branchCode = branchCodeValue.isEmpty() ? "DM001" : branchCodeValue;
            String branchManagerCode = "";
            String rmCode = "ON000001";
            String channelCode = "Online";
            String dbmMobile = "";
            firstBlockRight.addCell(new Cell(1, 4).add("").setHeight(30).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell(1, 4).add(new Paragraph("For Branch Sales Use Only").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell(1, 4).add("").setHeight(15).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add("LG / Agent Code: ").setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[agentCode.length()];
            for (int i = 0; i < agentCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            Table tableCodes;
            if (!agentCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(agentCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            firstBlockRight.addCell(new Cell().add("Branch Code: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[branchCode.length()];
            for (int i = 0; i < branchCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!branchCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(branchCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            firstBlockRight.addCell(new Cell(1,4).add(new Paragraph("(LG code to be written for Banca, Agent Code to be written for Agency.)").setFontSize(6f)).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add("Branch Manager Code: ").setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[branchManagerCode.length()];
            for (int i = 0; i < branchManagerCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!branchManagerCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(branchManagerCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            firstBlockRight.addCell(new Cell().add("BDM / RM Code: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[rmCode.length()];
            for (int i = 0; i < rmCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!rmCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(rmCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            firstBlockRight.addCell(new Cell().add("Channel Code: ").setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[channelCode.length()];
            for (int i = 0; i < channelCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!channelCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(channelCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            firstBlockRight.addCell(new Cell().add("BDM Mobile No: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[dbmMobile.length()];
            for (int i = 0; i < dbmMobile.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!dbmMobile.isEmpty()) {
                tableCodes = pdfUtility.codeTable(dbmMobile, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            Cell others = new Cell(1, 4);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content1", contentJson));
            p.add(new Text("Direct Sales").setBold().setUnderline());
            others.add(p);
            others.setBorder(Border.NO_BORDER);
            firstBlockRight.addCell(others);
            firstBlockTable.addCell(new Cell().add(firstBlockLeft).setBorder(Border.NO_BORDER));
            firstBlockTable.addCell(new Cell().add(firstBlockRight).setBorder(Border.NO_BORDER));
            p=new Paragraph(new Text("Important Guidelines:").setBold());
            p.add(jsonUtility.getJsonKeyValue("content2", contentJson));
            p.add(rupeeLogo);
            p.add(jsonUtility.getJsonKeyValue("content3", contentJson));
            p.add(rupeeLogo);
            p.add(jsonUtility.getJsonKeyValue("content4", contentJson));
            firstBlockTable.addCell(new Cell(1, 4).add(p).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(firstBlockTable).setBorder(Border.NO_BORDER));

            JsonObject staffObj = jsonUtility.getJsonObjectByKey("staff", primaryPersonalDetailObj);
            String staffStatus = jsonUtility.getJsonKeyValue("status", staffObj);
            String staffEmpCodeNo = jsonUtility.getJsonKeyValue("employeeCode", staffObj);

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("content5", contentJson)).setBold());
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            Table tableContent = new Table(new float[]{1000F, 200F, 250F, 200F});
            tableContent.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (staffStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (staffStatus.equalsIgnoreCase("N")) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            p.setFontColor(Color.WHITE);
            tableContent.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            tableContent.addCell(new Cell().add("Employee code/ Ref. no :").setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            tableContent.addCell(new Cell().add(staffEmpCodeNo).setFontColor(Color.WHITE).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            headingCell.add(new Cell().add(tableContent).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            String policyNumber = "000000000000";
            String clientId = "111111111111";
            String groupPolicyHolderName = "_____________________________________";
            String employeeNumber = "222222222222";
            String pranNumber = "________________________";
            String npsOthers = "________________";
            String qROPSDetails = "___________________________________";

            Table proposerDetails = new Table(new float[]{180F, 300F, 90F, 300F});
            p = new Paragraph(jsonUtility.getJsonKeyValue("content26", contentJson));
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            proposerDetails.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            proposerDetails.addCell(new Cell().add("If Yes, please provide: Policy Number")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[policyNumber.length()];
            for (int i = 0; i < policyNumber.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!policyNumber.isEmpty()) {
                tableCodes = pdfUtility.codeTable(policyNumber, pointColumnWidths).setFontColor(Color.WHITE);
                proposerDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }else {
                proposerDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            proposerDetails.addCell(new Cell().add("Client ID").setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[clientId.length()];
            for (int i = 0; i < clientId.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!clientId.isEmpty()) {
                tableCodes = pdfUtility.codeTable(clientId, pointColumnWidths).setFontColor(Color.WHITE);
                proposerDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }else {
                proposerDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            proposerDetails.addCell(new Cell(1, 4).add("If this Annuity Plan is through Group Scheme, please provide").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add("Group Master Policyholder Name")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph(groupPolicyHolderName))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add("Employee Number").setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[employeeNumber.length()];
            for (int i = 0; i < employeeNumber.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!employeeNumber.isEmpty()) {
                tableCodes = pdfUtility.codeTable(employeeNumber, pointColumnWidths).setFontColor(Color.WHITE);
                proposerDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }else {
                proposerDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("Are you a NPS Subscriber ?       ");
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            p.add("     If Yes, please provide: PRAN Number ");
            p.add(pranNumber);
            proposerDetails.addCell(new Cell(1,4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            p = new Paragraph("If Yes, please provide NPS Subscriber Category:      ");
            p.add(imgUnchecked);
            p.add("     Govt Sector     ");
            p.add(imgUnchecked);
            p.add("     Corporate Sector     ");
            p.add(imgUnchecked);
            p.add("     NPS Lite     ");
            p.add(imgUnchecked);
            p.add("     Swavalamban     ");
            p.add(imgUnchecked);
            p.add("     Others,     ");
            p.add(new Text(npsOthers));
            proposerDetails.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            p = new Paragraph("Is this Policy sourced under QROPS ?     ");
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            p.add("     If Yes, please provide details ");
            p.add(qROPSDetails);
            proposerDetails.addCell(new Cell(1,4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(proposerDetails).setBorder(Border.NO_BORDER));

            String maritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", primaryPersonalDetailObj);
            String gender = jsonUtility.getJsonKeyValue("gender", policyHolderBasicDetailObj);
            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("1. Details of First Annuitant/Life Assured");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
            proposerDetails = new Table(new float[]{600F, 600F});
            proposerDetails.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)").setBorder(Border.NO_BORDER));
            p = new Paragraph("Mr.  ");
            if (gender.equalsIgnoreCase("male")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mrs. ");
            if (gender.equalsIgnoreCase("female") && maritalStatus.equalsIgnoreCase("married")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Ms.  ");
            if (gender.equalsIgnoreCase("female") && maritalStatus.equalsIgnoreCase("single")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mx.  ");
            if (gender.equalsIgnoreCase("other")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            proposerDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Cell proposerMergedcell = new Cell(1, 2);
            String fullName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            String policyNo = "";
            clientId = "00000000";

            pointColumnWidths = new float[fullName.length()];
            for (int i = 0; i < fullName.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!fullName.isEmpty()) {
                tableCodes = pdfUtility.codeTable(fullName, pointColumnWidths);
                proposerMergedcell.add(tableCodes);
            }else {
                proposerMergedcell.add("");
            }
            proposerMergedcell.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);

            proposerDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("content6", contentJson)).setBorder(Border.NO_BORDER));

            Table innerProposer = new Table(new float[]{40F,50F,40F,50F,200F});
            innerProposer.addCell(new Cell().add(imgChecked).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            innerProposer.addCell(new Cell().add("     Policy No:      ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[policyNo.length()];
            for (int i = 0; i < policyNo.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!policyNo.isEmpty()) {
                tableCodes = pdfUtility.codeTable(policyNo, pointColumnWidths);
                innerProposer.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }else {
                innerProposer.addCell(new Cell().add(imgUnchecked).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            innerProposer.addCell(new Cell().add("     Client ID:      ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[clientId.length()];
            for (int i = 0; i < clientId.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!clientId.isEmpty()) {
                tableCodes = pdfUtility.codeTable(clientId, pointColumnWidths).setFontColor(Color.WHITE);
                innerProposer.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }else {
                innerProposer.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            proposerDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));

            p = new Paragraph(new Text("Communication Address of the Life Assured ").setBold());
            p.add("(Address to which policy document will be dispatched)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);
            String address1 = jsonUtility.getJsonKeyValue("addressline1", primaryPersonalDetailObj);

            if (!address1.isEmpty()) {
                if (address1.length() <= 10) {
                    address1 += "                                        ";
                } else if (address1.length() > 10 && address1.length() <= 20) {
                    address1 += "                              ";
                } else {
                    address1 += "                   ";
                }
            }
            String address2 = jsonUtility.getJsonKeyValue("addressline2", primaryPersonalDetailObj);
            if (!address2.isEmpty()) {
                if (address2.length() <= 10) {
                    address2 += "                                        ";
                } else if (address2.length() > 10 && address2.length() <= 20) {
                    address2 += "                              ";
                } else if (address2.length() > 20 && address2.length()<=30){
                    address2 += "                      ";
                } else if (address2.length() > 30 && address2.length()<=40) {
                    address2 +="           ";
                } else {
                    address2 += "";
                }
            }
            String address3 = jsonUtility.getJsonKeyValue("addressline3", primaryPersonalDetailObj);
            if (!address3.isEmpty()) {
                if (address3.length() <= 10) {
                    address3 += "                                        ";
                } else if (address3.length() > 10 && address3.length() <= 20) {
                    address3 += "                              ";
                } else {
                    address3 += "                     ";
                }
            }

            String permanentaddressline1 = jsonUtility.getJsonKeyValue("permanentaddressline1", primaryPersonalDetailObj);

            if (permanentaddressline1.length()>0) {
                if (permanentaddressline1.length() <= 10) {
                    permanentaddressline1 += "                                        ";
                } else if (permanentaddressline1.length() > 10 && permanentaddressline1.length() <= 20) {
                    permanentaddressline1 += "                              ";
                } else {
                    permanentaddressline1 += "                ";
                }
            }
            String permanentaddressline2 = jsonUtility.getJsonKeyValue("permanentaddressline2", primaryPersonalDetailObj);
            if (!permanentaddressline2.isEmpty()) {
                if (permanentaddressline2.length() <= 10) {
                    permanentaddressline2 += "                                        ";
                } else if (permanentaddressline2.length() > 10 && permanentaddressline2.length() <= 20) {
                    permanentaddressline2 += "                              ";
                } else if (permanentaddressline2.length() > 20 && permanentaddressline2.length() <= 30) {
                    permanentaddressline2 += "                  ";
                } else if (permanentaddressline2.length() > 30 && permanentaddressline2.length() <= 40) {
                    permanentaddressline2 += "           ";
                } else {
                    permanentaddressline2 += "";
                }
            }
            String permanentaddressline3 = jsonUtility.getJsonKeyValue("permanentaddressline3", primaryPersonalDetailObj);
            if (!permanentaddressline3.isEmpty()) {
                if (permanentaddressline3.length() <= 10) {
                    permanentaddressline3 += "                                        ";
                } else if (permanentaddressline3.length() > 10 && permanentaddressline3.length() <= 20) {
                    permanentaddressline3 += "                              ";
                } else {
                    permanentaddressline3 += "                     ";
                }
            }
            String pincode = jsonUtility.getJsonKeyValue("pincode",primaryPersonalDetailObj );
            if (pincode.length() > 0) {
            }else {
                pincode += "      ";
            }


            String permanentcity = jsonUtility.getJsonKeyValue("permanentcity", primaryPersonalDetailObj);
            if (permanentcity.length()>0) {
                if (permanentcity.length() <= 10) {
                    permanentcity += "                                        ";
                } else if (permanentcity.length() > 10 && permanentcity.length() <= 20) {
                    permanentcity += "                              ";
                } else {
                    permanentcity += "                ";
                }
            }
            String permanentstate = jsonUtility.getJsonKeyValue("permanentstate", primaryPersonalDetailObj);
            if (permanentstate.length()>0) {
                if (permanentstate.length() <= 10) {
                    permanentstate += "                                        ";
                } else if (permanentstate.length() > 10 && permanentstate.length() <= 20) {
                    permanentstate += "                              ";
                } else {
                    permanentstate += "                ";
                }
            }

            String city = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
            if (!city.isEmpty()) {
                if (city.length() <= 10) {
                    city += "                                        ";
                } else if (city.length() > 10 && city.length() <= 20) {
                    city += "                              ";
                } else {
                    city += "                     ";
                }
            }
            String state = jsonUtility.getJsonKeyValue("state", primaryPersonalDetailObj);
            if (!state.isEmpty()) {
                if (state.length() <= 10) {
                    state += "                                        ";
                } else if (state.length() > 10 && state.length() <= 20) {
                    state += "                              ";
                } else {
                    state += "                     ";
                }
            }

            String landmark = jsonUtility.getJsonKeyValue("landmark", primaryPersonalDetailObj);
            if (landmark.length()>0) {
                if (landmark.length() <= 10) {
                    landmark += "                                        ";
                } else if (landmark.length() > 10 && landmark.length() <= 20) {
                    landmark += "                              ";
                } else {
                    landmark += "                ";
                }
            }
            String permanentlandmark = jsonUtility.getJsonKeyValue("permanentlandmark", primaryPersonalDetailObj);
            if (permanentlandmark.length()>0) {
                if (permanentlandmark.length() <= 10) {
                    permanentlandmark += "                                        ";
                } else if (permanentlandmark.length() > 10 && permanentlandmark.length() <= 20) {
                    permanentlandmark += "                              ";
                } else {
                    permanentlandmark += "                ";
                }
            }

            Table addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(landmark,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            addressBlock=new Table(new float[]{600F,100F,200F});
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"0000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(pincode,"000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            //Permanent Address Section addition started
            boolean isaddressSame = jsonUtility.getBooleanKeyValue("IsaddressSame", primaryPersonalDetailObj);
            p = new Paragraph("Permanent Address (If different from the above Address)          ");
            p.setPaddingBottom(10);
            if(isaddressSame){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }            p.add("     Same as First Annuitant");
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);


            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? address1 :permanentaddressline1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? address2 :permanentaddressline2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? address3 :permanentaddressline3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? landmark :permanentlandmark,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? city :permanentcity,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? state :permanentstate,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //Permanent Address Section addition ended

            String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderBasicDetailObj);
            String mobileNo = jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetailObj);
            String landLine = "";

            Table contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F});
            contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("+").setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[countryCode.length()];
            for (int i = 0; i < countryCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!countryCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(countryCode, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            p=new Paragraph("Mobile No*:      ");
            p.add(new Text("\n*Receive alerts througth SMS").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[mobileNo.length()];
            for (int i = 0; i < mobileNo.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!mobileNo.isEmpty()) {
                tableCodes = pdfUtility.codeTable(mobileNo, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            p=new Paragraph("Landline:");
            p.add(new Text("\nSTD/ISD").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[landLine.length()];
            for (int i = 0; i < landLine.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!landLine.isEmpty()) {
                tableCodes = pdfUtility.codeTable(landLine, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);

            String pinCode = jsonUtility.getJsonKeyValue("permanentpincode", primaryPersonalDetailObj);
            logger.info("pin code length:{}",pinCode.length());
            String emailId = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetailObj);

            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(new float[]{200F, 430F, 200F, 400F});
            p=new Paragraph("Email ID*:  ");
            p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            pointColumnWidths = new float[emailId.length()];
            for (int i = 0; i < emailId.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!emailId.isEmpty()) {
                tableCodes = pdfUtility.codeTable(emailId, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            pointColumnWidths = new float[pinCode.length()];
            for (int i = 0; i < pinCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!pinCode.isEmpty()) {
                tableCodes = pdfUtility.codeTable(pinCode, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT));
            p = new Paragraph("Male:     ");
            if (gender.equalsIgnoreCase("male")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Female:     ");
            if (gender.equalsIgnoreCase("female")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Transgender:    ");
            if (gender.equalsIgnoreCase("other")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Nationality:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Indian:      ");
            if (jsonUtility.getJsonKeyValue("nationality", primaryPersonalDetailObj).equalsIgnoreCase("Indian")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Non Indian:     ");
            if (!jsonUtility.getJsonKeyValue("nationality", primaryPersonalDetailObj).equalsIgnoreCase("Indian")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            String dob = jsonUtility.getJsonKeyValue("dateOfBirth", primaryPersonalDetailObj);
            LocalDate birthDate = LocalDate.parse(dob);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String date = dateTimeFormatter.format(birthDate);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("DOB :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
            pointColumnWidths = new float[date.length()];
            for (int i = 0; i < date.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!date.isEmpty()) {
                tableCodes = pdfUtility.codeTable(date, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            contactTable.addCell(new Cell().add("  Residential Status:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            String residentialStatus = jsonUtility.getJsonKeyValue("residentialstatus", primaryPersonalDetailObj);
            p = new Paragraph();
            p.add("Resident:    ");
            if (residentialStatus.equalsIgnoreCase("RNT")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     NRI:    ");
            if (residentialStatus.equalsIgnoreCase("nri")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Others:    ");
            if (!(residentialStatus.equalsIgnoreCase("RNT")||residentialStatus.equalsIgnoreCase("nri"))) {
                p.add(new Text(residentialStatus).setUnderline());
            }else {
                p.add(new Text("").setUnderline());
            }
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            contactTable.addCell(new Cell().add("Marital Status :   ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Unmarried:   ");
            if (maritalStatus.equalsIgnoreCase("unmarried") || maritalStatus.equalsIgnoreCase("Single")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Married:  ");
            if (maritalStatus.equalsIgnoreCase("married")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Widow(er):  ");
            if (maritalStatus.equalsIgnoreCase("widow")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Divorced:   ");
            if (maritalStatus.equalsIgnoreCase("divorced")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

            //Age Proof Map
            Map<String, String> ageProofMap = new HashMap<>();
            ageProofMap.put("AD", "Aadhaar Card");
            ageProofMap.put("PP", "Passport");
            ageProofMap.put("SG", "Govt Order/Baptism Certificate");
            ageProofMap.put("BC", "Birth Certificate");
            ageProofMap.put("ES", "Employer Certificate");
            ageProofMap.put("PA", "Pan Card");
            ageProofMap.put("MC", "Marriage Cert Issued Rc Church");
            ageProofMap.put("SC", "School Leaving Certificate");
            ageProofMap.put("SA", "Army ID/LIC( B,C,F,I,M,P,S,J )");
            ageProofMap.put("ND", "Driving License");
            ageProofMap.put("RC", "Ration Card");
            ageProofMap.put("VC", "Election Identity Card");
            ageProofMap.put("GP", "Gram Panchayat Certificate");
            ageProofMap.put("SD", "Stamp Affidavit /LIC( A,E,H,T,R)");
            ageProofMap.put("PO", "Pension Order Of Spouse");
            ageProofMap.put("NC", "ESIS Cards");
            ageProofMap.put("", "NA");

            //Address Proof Map
            Map<String, String> addressProofMap = new HashMap<>();
            addressProofMap.put("RTN CARD", "Ration Card");
            addressProofMap.put("AADHAR", "Aadhaar Card");
            addressProofMap.put("DRVR LIC", "Driving License");
            addressProofMap.put("PASSPORT", "Passport");
            addressProofMap.put("ELCT BILL", "Electric Bill");
            addressProofMap.put("VOTER IC", "Voter Id Card");
            addressProofMap.put("DMCL CRT", "Govt Issued Domicile Certificate");
            addressProofMap.put("PENSNORD", "Government Issued Pension Order");
            addressProofMap.put("PA", "Pan Card");
            addressProofMap.put("CERTINSC", "Address Cert. By InsCo Employee");
            addressProofMap.put("PAN ALLT", "Pan Allotment Letter");
            addressProofMap.put("NTRY STMP", "Notary Attested Address Proof");
            addressProofMap.put("EMP CERT", "Current Employer Cert/ID Card");
            addressProofMap.put("VHCL RC", "Vehicle Reg Certftvhcl");
            addressProofMap.put("PO PB", "Post Office Savings Pass Book");
            addressProofMap.put("TEL BILL", "Telephone Bill");
            addressProofMap.put("OTHERS", "Other Than Listed Item");
            addressProofMap.put("", "NA");

            //Identity Proof Map
            Map<String, String> idProofMap = new HashMap<>();
            idProofMap.put("AADHAR", "Aadhaar Card");
            idProofMap.put("PASSPORT", "Passport");
            idProofMap.put("MAPINCRD", "Mapin Card (Issued by NSDL)");
            idProofMap.put("PASSBOOK", "Passbook");
            idProofMap.put("RTN CARD", "Ration Card");
            idProofMap.put("PAN CARD", "Pan Card");
            idProofMap.put("ELCT BILL", "Electric Bill");
            idProofMap.put("DMCL CRT", "Govt Issued Domicile Certificate");
            idProofMap.put("SOCL SEC", "Govt Issued Social Security Card");
            idProofMap.put("DRVR LIC", "Driving License");
            idProofMap.put("PNSORDR", "PensionOrder/Book/Card-Govt");
            idProofMap.put("AGNT CRD", "Insurance Company Agents Id");
            idProofMap.put("EMPLYRID", "Employer Id Proof");
            idProofMap.put("VOTER IC", "Voter Id Card");
            idProofMap.put("PSUID", "PhotoIDbyPSU/PFU/PSB");
            idProofMap.put("", "NA");

            String primaryAgeDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof-all", primaryDocumentDetailObj));
            String primaryAddressDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("addressProof", primaryDocumentDetailObj));
            String primaryIdDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("idProof", primaryDocumentDetailObj));

            String identityProof = "";
            String addressProof = "";
            String ageProof = "";
            if(isOmniDoc && idProofMap.containsKey(primaryIdDocType)){
                identityProof=idProofMap.get(primaryIdDocType);
            }
            if(isOmniDoc && ageProofMap.containsKey(primaryAgeDocType)){
                ageProof=ageProofMap.get(primaryAgeDocType);
            }
            if(isOmniDoc && addressProofMap.containsKey(primaryAddressDocType)){
                addressProof=addressProofMap.get(primaryAddressDocType);
            }

            String annualIncome = jsonUtility.getJsonKeyValue("annualIncome", primaryEmploymentDetailObj);
            String sourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", primaryEmploymentDetailObj);
            String panCardNo = jsonUtility.getJsonKeyValue("pancard", primaryPersonalDetailObj);

            p=new Paragraph("Identity Proof: ");
            p.add(new Text("\n(Life Assured)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(identityProof).setUnderline()).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell(1,4).add("").setBorder(Border.NO_BORDER));
            p=new Paragraph("Address Proof:    ");
            p.add(new Text("\n(Life Assured)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(addressProof)));
            p=new Paragraph("Age Proof:    ");
            p.add(new Text("\n(Life Assured)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(ageProof)));

            p=new Paragraph("Source of Income: ");
            p.add(new Text("\n(Life Assured)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            contactTable.addCell(new Cell().add(new Paragraph(sourceOfIncome)));
            p=new Paragraph("Annual Income ");
            p.add(new Text("\n(Life Assured)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(new Paragraph(annualIncome)));
//            contactTable.addCell(new Cell(1,4).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell(1,4).add("").setBorder(Border.NO_BORDER));
            p=new Paragraph("PAN: ");
            p.add(new Text("\n(Life Assured)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[panCardNo.length()];
            for (int i = 0; i < panCardNo.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!panCardNo.isEmpty()) {
                tableCodes = pdfUtility.codeTable(panCardNo, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            }else {
                contactTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            String primaryAgeProofDoc = jsonUtility.getJsonKeyValue("documentType",jsonUtility.getJsonObjectByKey("ageProof", primaryDocumentDetailObj));
            String primaryPanCardName = jsonUtility.getJsonKeyValue("name",jsonUtility.getJsonObjectByKey("panCard", primaryDocumentDetailObj));
            p = new Paragraph("PAN: ");
            p.add(" (photocopy Enclosed)");
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (isOmniDoc) {
                if (primaryAgeDocType.equalsIgnoreCase("PA") || primaryIdDocType.equalsIgnoreCase("PAN CARD") ||
                        primaryAgeProofDoc.equalsIgnoreCase("PA") || !primaryPanCardName.isEmpty()) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (!(primaryAgeDocType.equalsIgnoreCase("PA") || primaryIdDocType.equalsIgnoreCase("PAN CARD") ||
                        primaryAgeProofDoc.equalsIgnoreCase("PA") || !primaryPanCardName.isEmpty())) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No     ");
            }else {
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
            }
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            String ckycNo = "";


            JsonObject medicalDisabilityObj = jsonUtility.getJsonObjectByKey("disabilityQuestions", primaryOtherDetailObj);
            JsonObject haveDisabilityObj = jsonUtility.getJsonObjectByKey("have_disability", medicalDisabilityObj);
            boolean medicalStatus = jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("y");
            p=new Paragraph("CKYC No.:    ");
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell(1,4).add(pdfUtility.createDataTable(ckycNo,"0000000000")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            String politicallyExposed = jsonUtility.getJsonKeyValue("politicallyExposed", primaryEmploymentDetailObj);
            p = new Paragraph("Are you a Politically Exposed Person (Life to be Assured)?       ");
            if (politicallyExposed.equalsIgnoreCase("Yes")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (politicallyExposed.equalsIgnoreCase("No")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            p.add("\n\n");
            p.add("Do you have any disability that restricts you from providing consent/ signature in the proposal form?  ");
            p.add(medicalStatus ? imgChecked : imgUnchecked);
            p.add("   Yes   ");
            boolean medicalStatusNo = jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("n");
            p.add(medicalStatusNo ? imgChecked : imgUnchecked);
            p.add("  No   ");
            contactTable.addCell(new Cell(1,4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.setBorder(Border.NO_BORDER);

            proposerDetails.addCell(proposerMergedcell);
            table.addCell(new Cell().add(proposerDetails).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph("Politically Exposed Persons (PEPs) are individuals who are or have been entrusted with prominent public functions in a foreign country, example, Heads of State or of Governments, " +
                    "senior politicians, senior government/judicial/military officials, senior executives of state owned corporations, important political party officials, etc., including their family members and " +
                    "close relatives.")).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setHeight(10).setBorder(Border.NO_BORDER));
            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            String proposerPlaceOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth", jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));
            String countryOfBirth = jsonUtility.getJsonKeyValue("countryOfBirthLabel", jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));
            contactTable = new Table(new float[]{400F, 150F, 220F, 100F});
            contactTable.addCell(new Cell(1,2).add("(a) Place and Country of birth ")
                    .setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (proposerPlaceOfBirth.equalsIgnoreCase("")){
                p.add(countryOfBirth).setUnderline();
            }
            if (countryOfBirth.equalsIgnoreCase("")){
                p.add(proposerPlaceOfBirth).setUnderline();
            }
            if (!(proposerPlaceOfBirth.equalsIgnoreCase("") || countryOfBirth.equalsIgnoreCase(""))) {
                p.add(proposerPlaceOfBirth + " , " + countryOfBirth).setUnderline();
            }
            contactTable.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            Table additionalDetails = new Table(new float[]{330F, 200F});
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            additionalDetails.addCell(proposerMergedcell);

            additionalDetails.addCell(new Cell().add("(b) Are you a citizen of any other country also (Dual / Multiple) ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            additionalDetails.addCell(new Cell().add("(c) Are you a resident (For tax purposes) of any other country other then India  ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            additionalDetails.addCell(new Cell().add("(d) Do you hold a green card of US or any similar card for any other country ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
            proposerMergedcell.add(p);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            additionalDetails.addCell(proposerMergedcell);
            table.addCell(new Cell().add(additionalDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("2. Plan Details");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("content7", contentJson)).setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("content9", contentJson)).setBorder(Border.NO_BORDER)));

            Table annuityDetails = new Table(new float[]{330F, 300F, 300F});
            annuityDetails.addCell(new Cell(1, 3).add(new Paragraph("Annuity Option").setBold().setTextAlignment(TextAlignment.CENTER)));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Life Annuity");
            annuityDetails.addCell(new Cell().add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Life Annuity with return of 100% of purchase price");
            annuityDetails.addCell(new Cell().add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Joint Life Last Survivor Annuity for Life");
            annuityDetails.addCell(new Cell().add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Joint Life Last Survivor Annuity for Life with return of 100% of purchase price");
            annuityDetails.addCell(new Cell(1, 3).add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));

            p=new Paragraph();
            p.add(imgUnchecked);
            p.add("     Annuity Certain for a period of :   ");
            p.add(imgUnchecked);
            p.add("     5 years     ");
            p.add(imgUnchecked);
            p.add("     10 years      ");
            p.add(imgUnchecked);
            p.add("     15 years     ");
            //p.add(imgUnchecked);
            p.add("     and Life thereafter      ");
            annuityDetails.addCell(new Cell(1,3).add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));

            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Deferred Life Annuity where deferment period is 5 to 10 years     ");
//            annuityDetails.addCell(new Cell().add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
//            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Deferred Life Annuity with Return of Purchase Price where deferment period is 5 to 10 years");
            //annuityDetails.addCell(new Cell(1, 3).add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p .add("\nDeferment period :   ");
            p.add(imgUnchecked);
            p.add("     5 years     ");
            p.add(imgUnchecked);
            p.add("     6 years      ");
            p.add(imgUnchecked);
            p.add("     7 years     ");
            p.add(imgUnchecked);
            p.add("     8 years      ");
            p.add(imgUnchecked);
            p.add("     9 years     ");
            p.add(imgUnchecked);
            p.add("     10 years      ");
            annuityDetails.addCell(new Cell(1, 3).add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Life Annuity with Return of Purchase Price on diagnosis of Critical Illness");
            annuityDetails.addCell(new Cell(1, 3).add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Annuity with Return of Purchase Price in parts      ");
            p.add(imgUnchecked);
            p.add("     Escalating Life Annuity     ");
            p.add(imgUnchecked);
            p.add("     Escalating Life Annuity with Return of Purchase price   ");
            p.add(imgUnchecked);
            p.add("     NPS – Family Income*");
            annuityDetails.addCell(new Cell(1, 3).add(p).setPaddingLeft(10).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(annuityDetails).setBorder(Border.NO_BORDER));
            p = new Paragraph("*If you have opted for NPS - Family Income Option, please fill the below details");
            table.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setHeight(5).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            String fatherName = " ";
            String fatherDOB = "";
            String fatherAddress = "";
            String fatherContactNo = "";
            String motherName = "";
            String motherDOB = "";
            String motherAddress =  "";
            String motherContact = "";
            String spouseName = "";
            String spouseDOB = "";
            String spouseAddress =  "";
            String spouseContact = "";
            String child1Name = "";
            String child1DOB = "";
            String child1Address =  "";
            String child1Contact = "";
            String child2Name = "";
            String child2DOB = "";
            String child2Address =  "";
            String child2Contact = "";


            Table familyMembersDetails = new Table(new float[]{150F, 400F, 150F, 400F, 300F});
            familyMembersDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add("Name*").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add("DOB*").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add("Address").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add("Contact number").setTextAlignment(TextAlignment.CENTER));

            familyMembersDetails.addCell(new Cell().add("Father").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(fatherName).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(fatherDOB).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(fatherAddress).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(fatherContactNo).setTextAlignment(TextAlignment.CENTER));

            familyMembersDetails.addCell(new Cell().add("Mother").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(motherName).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(motherDOB).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(motherAddress).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(motherContact).setTextAlignment(TextAlignment.CENTER));

            familyMembersDetails.addCell(new Cell().add("Spouse").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(spouseName).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(spouseDOB).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(spouseAddress).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(spouseContact).setTextAlignment(TextAlignment.CENTER));

            familyMembersDetails.addCell(new Cell().add("Child 1").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child1Name).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child1DOB).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child1Address).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child1Contact).setTextAlignment(TextAlignment.CENTER));

            familyMembersDetails.addCell(new Cell().add("Child 2").setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child2Name).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child2DOB).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child2Address).setTextAlignment(TextAlignment.CENTER));
            familyMembersDetails.addCell(new Cell().add(child2Contact).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(familyMembersDetails).setBorder(Border.NO_BORDER));

            ///New Section Added/////////
            Table planDetails = new Table(new float[]{400F, 100F, 200F, 150F, 200F});
            planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("Policy Term(years)").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("Premium Payment Term (years)").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("Installment Premium (Rs.)").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("Sum Assured").setTextAlignment(TextAlignment.CENTER));

            String planName=jsonUtility.getJsonKeyValue("content8", contentJson);
            String planTerm="";
            String premiumPlayingTerm="";
            String premiumInstallment="";
            String sumAssured="";
            planDetails.addCell(planName);
            planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));

            planName = jsonUtility.getJsonKeyValue("content9", contentJson);
            planTerm = "";
            premiumPlayingTerm = "";
            premiumInstallment = "";
            sumAssured = "";

            planDetails.addCell(planName);
            planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(planDetails).setBorder(Border.NO_BORDER));
            ////New Section ended///////

            table.addCell(new Cell().add("").setHeight(5).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph("Purchase Price / Annuity Amount (Please tick any one option)").setBold()).setBorder(Border.NO_BORDER));

            String purchasePrice = "____________";
            String annuityAmount = "____________";

            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Purchase Price      ");
            p.add(new Text(purchasePrice));
            p.add("     Or      ");
            p.add(imgUnchecked);
            p.add("     Annuity Amount      ");
            p.add(new Text(annuityAmount));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Table purchaseOptions = new Table(new float[]{100F, 100F, 150F, 180F, 100F,180F});
            p = new Paragraph("Annuity Value:   ");
            p.add(imgUnchecked);
            p.add("     Up to 60% as cash lumpsum and rest as Annuity    ");
            p.add(imgUnchecked);
            p.add("     100% of Vesting Amount as Annuity");
            purchaseOptions.addCell(new Cell(1,6).add(p)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            purchaseOptions.addCell(new Cell().add("Source of Premium:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     NPS Proceeds");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add(jsonUtility.getJsonKeyValue("content10", contentJson));
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Other Company Pension Plan");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Self-Funded");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Others,(pl specify)     ");
            p.add(new Text("_________"));
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));


            purchaseOptions.addCell(new Cell().add("Annuity Frequency:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yearly");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Half Yearly");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Quarterly");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Monthly");
            purchaseOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(purchaseOptions).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setHeight(5).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("content11", contentJson)).setBold()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph("Annuity Option (please tick annuity of your choice )")).setBorder(Border.NO_BORDER));

            String planOption = jsonUtility.getJsonKeyValue("planOption", planDetailObj);
            Table annuityOptions = new Table(new float[]{300F, 700F, 100F});
            annuityOptions.addCell(new Cell(1, 3).add(new Paragraph("Annuity Option").setFontSize(10).setBold()));
            annuityOptions.addCell(new Cell().add("Annuity Option*").setBold());
            annuityOptions.addCell(new Cell().add("Description").setBold());
            annuityOptions.addCell(new Cell().add(""));

            annuityOptions.addCell(new Cell().add("Plan Option A"));
            annuityOptions.addCell(new Cell().add("Life Annuity"));
            if (planOption.equalsIgnoreCase("Life Annuity")){
                annuityOptions.addCell(new Cell().add(imgChecked));
            }else {
                annuityOptions.addCell(new Cell().add(imgUnchecked));
            }
            annuityOptions.addCell(new Cell().add("Plan Option B"));
            annuityOptions.addCell(new Cell().add("Life increasing Annuity"));
            if (planOption.equalsIgnoreCase("Life increasing Annuity")){
                annuityOptions.addCell(new Cell().add(imgChecked));
            }else {
                annuityOptions.addCell(new Cell().add(imgUnchecked));
            }
            annuityOptions.addCell(new Cell().add("Plan Option C"));
            annuityOptions.addCell(new Cell().add("Life Annuity with Return of Purchase Price on Death"));
            if (planOption.equalsIgnoreCase("Life Annuity with Return of purchase price on Death")){
                annuityOptions.addCell(new Cell().add(imgChecked));
            }else {
                annuityOptions.addCell(new Cell().add(imgUnchecked));
            }
            annuityOptions.addCell(new Cell().add("Plan Option D"));
            annuityOptions.addCell(new Cell().add("Life Annuity with Return of Purchase Price on Death or on Critical Illness"));
            if (planOption.equalsIgnoreCase("Life Annuity with Return of Purchase Price on Death or on Critical Illness")){
                annuityOptions.addCell(new Cell().add(imgChecked));
            }else {
                annuityOptions.addCell(new Cell().add(imgUnchecked));
            }
            annuityOptions.addCell(new Cell().add("Plan Option E"));
            annuityOptions.addCell(new Cell().add("Life Annuity with Return of Purchase Price on Death on in instalment on survival"));
            if (planOption.equalsIgnoreCase("Life Annuity with Return of purchase price on Death or in instalments on survival")){
                annuityOptions.addCell(new Cell().add(imgChecked));
            }else {
                annuityOptions.addCell(new Cell().add(imgUnchecked));
            }
            table.addCell(new Cell().add(annuityOptions).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("*Plan Option A and Plan Option C are available for single life and joint life whereas other plan options will be available only for Single Life").setBorder(Border.NO_BORDER));

            logger.info("Premium paying term is before");
            String premiumPayingTerm = jsonUtility.getJsonKeyValue("premiumPayingTerm", planDetailObj);
            logger.info("Premium paying term is:{}",premiumPayingTerm);
            Table annuityPlanDetails = new Table(new float[]{150F, 700F});
            annuityPlanDetails.addCell(new Cell().add("Premium Paying Term")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (premiumPayingTerm.equalsIgnoreCase("5")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     5 years     ");
            if (premiumPayingTerm.equalsIgnoreCase("6")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     6 years     ");
            if (premiumPayingTerm.equalsIgnoreCase("7")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     7 years     ");
            if (premiumPayingTerm.equalsIgnoreCase("8")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     8 years     ");
            if (premiumPayingTerm.equalsIgnoreCase("9")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     9 years     ");
            if (premiumPayingTerm.equalsIgnoreCase("10")){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     10 years     ");
            annuityPlanDetails.addCell(new Cell().add(p)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            String investmentFrequency = jsonUtility.getJsonKeyValue("investmentFrequency", planDetailObj);
            annuityPlanDetails.addCell(new Cell().add("Premium Paying Frequency:")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph("Yearly     ");
            if (investmentFrequency.equalsIgnoreCase("yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Half Yearly     ");
            if (investmentFrequency.equalsIgnoreCase("half yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Quarterly     ");
            if (investmentFrequency.equalsIgnoreCase("quarterly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Monthly     ");
            if (investmentFrequency.equalsIgnoreCase("monthly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            annuityPlanDetails.addCell(new Cell().add(p)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(annuityPlanDetails).setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text("Limited Premium / Annuity Amount").setBold());
            p.add("     (Please tick any one option)");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            annuityPlanDetails = new Table(new float[]{150F, 700F});
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("         Limited Premium         ");
            p.add(new Text("____________"));
            p.add("         or         ");
            p.add(imgUnchecked);
            p.add("         Annuity Amount         ");
            p.add(new Text("____________"));
            annuityPlanDetails.addCell(new Cell(1, 2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            annuityPlanDetails.addCell(new Cell().add("Annuity Value:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("         Up to 60% as cash lumpsum and rest as Annuity        ");
            p.add(new Text("Please Specify").setFontSize(6));
            p.add("  _________     ");
            p.add(imgUnchecked);
            p.add("         100% of Vesting Amount as Annuity        ");
            annuityPlanDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            annuityPlanDetails.addCell(new Cell().add("Source of Premium:")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     NPS Proceeds     ");
            p.add(imgUnchecked);
            p.add(jsonUtility.getJsonKeyValue("content12", contentJson));
            p.add(imgUnchecked);
            p.add("     Other Company Pension Plan     ");
            p.add(imgUnchecked);
            p.add("     Self-Funded     ");
            p.add(imgUnchecked);
            p.add("     Others,(pl specify)     ");
            p.add(new Text("_________"));
            annuityPlanDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            String annuityFrequency = jsonUtility.getJsonKeyValue("annuityFrequency", planDetailObj);
            annuityPlanDetails.addCell(new Cell().add("Annuity Frequency:")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yearly     ");
            if (annuityFrequency.equalsIgnoreCase("yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Half Yearly     ");
            if (annuityFrequency.equalsIgnoreCase("Half Yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Quarterly     ");
            if (annuityFrequency.equalsIgnoreCase("Quarterly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Monthly     ");
            if (annuityFrequency.equalsIgnoreCase("Monthly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            annuityPlanDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            //New Section added///
            p=new Paragraph(new Text(jsonUtility.getJsonKeyValue("content12", contentJson)).setBold());
            p.add("Annuity Option (Please tick annuity of your choice)\n");
            p.add(imgUnchecked);
            p.add("   Life Annuity  \n");
            p.add(imgUnchecked);
            p.add("   Life Annuity with Return of Purchase Price  \n");
            p.add(imgUnchecked);
            p.add("   Joint Life (Two) Last Survivor (JLLS) Annuity for Life (only for Husband and Wife)  \n");
            p.add(imgUnchecked);
            p.add("   Annuity Certain for a period of     ");
            p.add(imgUnchecked);
            p.add("     5 years     ");
            p.add(imgUnchecked);
            p.add("     10 years     ");
            p.add(imgUnchecked);
            p.add("     15 years and life thereafter     ");
            annuityPlanDetails.addCell(new Cell(1,2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            p=new Paragraph(new Text("Purchase Price/Annuity amount (Please tick one option)\n").setBold());
            p.add(imgUnchecked);
            p.add("   Purchase Price or     ");
            p.add(imgUnchecked);
            p.add("     Annuity Amount     ");
            p.add(new Text("________").setUnderline());
            p.add(new Text("\nSource of Premium     ").setBold());
            p.add(imgUnchecked);
            p.add("   NPS Proceeds     ");
            p.add(imgUnchecked);
            p.add(jsonUtility.getJsonKeyValue("content13", contentJson));
            p.add(imgUnchecked);
            p.add("   Other Company Pension Plan     ");
            p.add(imgUnchecked);
            p.add("     Self Funded     ");
            p.add(imgUnchecked);
            p.add("   Others     ");

            p.add(new Text("\nAnnuity Frequency     ").setBold());
            p.add(imgUnchecked);
            p.add("   Yearly     ");
            p.add(imgUnchecked);
            p.add("     Half yearly     ");
            p.add(imgUnchecked);
            p.add("   Quarterly     ");
            p.add(imgUnchecked);
            p.add("     Monthly     ");
            annuityPlanDetails.addCell(new Cell(1,2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            ///New section ended///
            table.addCell(new Cell().add(annuityPlanDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("3. Details of Second Annuitant (if Joint Life is chosen)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            Table secondaryLifeAssuredDetails = this.getSecondaryLifeAssuredDetails(imgChecked, imgUnchecked, planDetailObj, insuredPersonBasicDetailObj, secondaryPersonalDetailObj, secondaryDocumentDetailObj, ageProofMap,idProofMap,addressProofMap, buyFor, isOmniDoc, contentJson);
            table.addCell(new Cell().add(secondaryLifeAssuredDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("4. Nominee/ Appointee Details (Appointee details required only if nominee is a minor)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            JsonArray nomineeList = nomineeDetailObj.get("nominees").getAsJsonArray();

            Table nomineeDetails = new Table(new float[]{100F,70F,70F,70F,100F,100F,140F,140F,90F});
            nomineeDetails.addCell("Nominee Name");
            nomineeDetails.addCell("Percentage Share");
            nomineeDetails.addCell("DOB of Nominee");
            nomineeDetails.addCell("Age of Nominee");
            nomineeDetails.addCell("Mobile No");
            nomineeDetails.addCell("Email-ID");
            nomineeDetails.addCell("Current Address of Nominee");
            nomineeDetails.addCell("Permanent Address of Nominee");
            nomineeDetails.addCell("Relationship of Nominee");

            for (JsonElement element : nomineeList) {
                JsonObject objNom = element.getAsJsonObject();
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("name", objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("allocation", objNom));
                String dateOfBirth = jsonUtility.getJsonKeyValue("dateOfBirth", objNom);
                try {
                    LocalDate nomineeDateOfBirth = LocalDate.parse(dateOfBirth);
                    String formatDob = nomineeDateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    nomineeDetails.addCell(formatDob);
                } catch (DateTimeParseException e) {
                    logger.info("Invalid date format for dateOfBirth: {}", dateOfBirth);
                    nomineeDetails.addCell(dateOfBirth);
                }
                birthDate = LocalDate.parse(dateOfBirth);
                LocalDate currentDate = LocalDate.now();
                int age = Period.between(birthDate, currentDate).getYears();
                nomineeDetails.addCell(String.valueOf(age));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("nomineeMobileNumber", objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("nomineeEmailID", objNom));
                String communicationAddressLines = Stream.of(
                                jsonUtility.getJsonKeyValue("communicationAddressline1", objNom),
                                jsonUtility.getJsonKeyValue("communicationAddressline2", objNom),
                                jsonUtility.getJsonKeyValue("communicationAddressline3", objNom)
                        )
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .collect(Collectors.joining(" "));
                String communicationCityDetails = Stream.of(
                                jsonUtility.getJsonKeyValue("communicationlandmark", objNom),
                                jsonUtility.getJsonKeyValue("communicationPincode", objNom),
                                jsonUtility.getJsonKeyValue("communicationState", objNom),
                                jsonUtility.getJsonKeyValue("communicationCity", objNom)
                        )
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .collect(Collectors.joining(" "));
                String communicationAddress = communicationAddressLines + "\n" + communicationCityDetails;
                nomineeDetails.addCell(new Cell().add(new Paragraph(communicationAddress)));

                String addressLines = Stream.of(
                                jsonUtility.getJsonKeyValue("addressline1", objNom),
                                jsonUtility.getJsonKeyValue("addressline2", objNom),
                                jsonUtility.getJsonKeyValue("addressline3", objNom)
                        )
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .collect(Collectors.joining(" "));

                String cityDetails = Stream.of(
                                jsonUtility.getJsonKeyValue("landmark", objNom),
                                jsonUtility.getJsonKeyValue("pincode", objNom),
                                jsonUtility.getJsonKeyValue("state", objNom),
                                jsonUtility.getJsonKeyValue("city", objNom)
                        )
                        .filter(s -> s != null && !s.trim().isEmpty())
                        .collect(Collectors.joining(" "));
                String permanentAddress = addressLines + "\n" + cityDetails;
                nomineeDetails.addCell(new Cell().add(new Paragraph(permanentAddress)));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("relation", objNom));

            }
            table.addCell(new Cell().add(nomineeDetails).setBorder(Border.NO_BORDER));

            Table appointeeDetails = new Table(new float[]{150F,150F,150F,150F,150F,150F});
            appointeeDetails.addCell("Appointee’s Name");
            appointeeDetails.addCell("DOB");
            appointeeDetails.addCell("Age");
            appointeeDetails.addCell("Gender");
            appointeeDetails.addCell("Relationship with Nominee");
            appointeeDetails.addCell("Appointee’s Address");

            for (JsonElement element : nomineeList) {
                JsonObject objNom = element.getAsJsonObject();
                if (objNom.has("appointeeName") && !jsonUtility.getJsonKeyValue("appointeeName", objNom).isEmpty()) {
                    appointeeDetails.addCell(jsonUtility.getJsonKeyValue("appointeeName", objNom));
                    String appointeeDateOfBirth = jsonUtility.getJsonKeyValue("appointeeDateOfBirth", objNom);
                    try {
                        LocalDate appointeeDob = LocalDate.parse(appointeeDateOfBirth);
                        String formatDob = appointeeDob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        appointeeDetails.addCell(formatDob);
                    } catch (DateTimeParseException e) {
                        logger.info("Invalid date format for dateOfBirth: {}", appointeeDateOfBirth);
                        appointeeDetails.addCell(appointeeDateOfBirth);
                    }
                    birthDate = LocalDate.parse(appointeeDateOfBirth);
                    int age = Period.between(birthDate, LocalDate.now()).getYears();
                    appointeeDetails.addCell(String.valueOf(age));
                    appointeeDetails.addCell(jsonUtility.getJsonKeyValue("appointeeGender", objNom));
                    appointeeDetails.addCell(jsonUtility.getJsonKeyValue("appointeeRelation", objNom));
                    String addressLines = Stream.of(
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationAddressline1", objNom),
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationAddressline2", objNom),
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationAddressline3", objNom)
                            )
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .collect(Collectors.joining(" "));
                    String cityDetails = Stream.of(
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationlandmark", objNom),
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationPincode", objNom),
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationState", objNom),
                                    jsonUtility.getJsonKeyValue("appointeeCommunicationCity", objNom)
                            )
                            .filter(s -> s != null && !s.trim().isEmpty())
                            .collect(Collectors.joining(" "));
                    String permanentAddress = addressLines + "\n" + cityDetails;
                    appointeeDetails.addCell(new Cell().add(new Paragraph(permanentAddress)));
                }
            }
            table.addCell(new Cell().add(appointeeDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("5. Payment Mode (Choose any one mode only)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            ///////////////////////////
            Table bankDetails = this.getBankDetails(imgUnchecked, imgChecked,primaryBankObj,nomineeList,contentJson);
            table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("6. Insurance Repository");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            JsonObject existingInsuranceAccountObj = jsonUtility.getJsonObjectByKey("existingInsuranceAccount",
                    primaryOtherDetailObj);
            String insuranceRepositoryName = jsonUtility.getJsonKeyValue("insuranceRepository",
                    existingInsuranceAccountObj);
            logger.info("Insurance repository name:{}", insuranceRepositoryName);
            String eIAServiceProvider = jsonUtility.getJsonKeyValue("eIAServiceProvider", existingInsuranceAccountObj);
            String eIANumber = "";
            String irName = "";
            if (jsonUtility.getJsonKeyValue("status", existingInsuranceAccountObj).equalsIgnoreCase("y")) {
                eIANumber = jsonUtility.getJsonKeyValue("eIAAccountNumber", existingInsuranceAccountObj);
                irName = eIAServiceProvider;
            }

            p = new Paragraph();
            p.add("Existing e - Insurance Account (e-IA) holder, please provide the e IA and IR name");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            Table eInsuranceDetails = new Table(new float[]{100F, 900F});
            eInsuranceDetails.addCell("E IA Number: ");
            eInsuranceDetails.addCell(eIANumber);
            eInsuranceDetails.addCell("IR Name: ");
            eInsuranceDetails.addCell(irName);
            table.addCell(new Cell().add(eInsuranceDetails).setBorder(Border.NO_BORDER));
            p = new Paragraph("Open New e - Insurance Account - Please choose the repository from the below");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            eInsuranceDetails = new Table(new float[]{100F, 200F, 700F});
            eInsuranceDetails.addCell(new Paragraph("IR Code").setBold());
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(new Paragraph("IR Name").setBold());
            eInsuranceDetails.addCell(proposerMergedcell);
            if (jsonUtility.getJsonKeyValue("status", existingInsuranceAccountObj).equalsIgnoreCase("n")) {
                irName = jsonUtility.getJsonKeyValue("insuranceRepository", existingInsuranceAccountObj);
                eInsuranceDetails.addCell("01.");
                eInsuranceDetails.addCell("NSDL Database Management Limited");
                if (irName.equalsIgnoreCase("NSDL Database Management Limited")) {
                    eInsuranceDetails.addCell(imgChecked);
                } else {
                    eInsuranceDetails.addCell(imgUnchecked);
                }

                eInsuranceDetails.addCell("02.");
                eInsuranceDetails.addCell("Central Insurance Repository Limited");
                if (irName.equalsIgnoreCase("CDSL Insurance Repository Limited")) {
                    eInsuranceDetails.addCell(imgChecked);
                } else {
                    eInsuranceDetails.addCell(imgUnchecked);
                }

                eInsuranceDetails.addCell("04.");
                eInsuranceDetails.addCell("Karvy Insurance Repository Limited");
                if (irName.equalsIgnoreCase("Karvy Insurance Repository Limited")) {
                    eInsuranceDetails.addCell(imgChecked);
                } else {
                    eInsuranceDetails.addCell(imgUnchecked);
                }

                eInsuranceDetails.addCell("05.");
                eInsuranceDetails.addCell("CAMS Repository Limited");
                if (irName.equalsIgnoreCase("CAMS Repository Limited")) {
                    eInsuranceDetails.addCell(imgChecked);
                } else {
                    eInsuranceDetails.addCell(imgUnchecked);
                }
            } else {
                eInsuranceDetails.addCell("01.");
                eInsuranceDetails.addCell("NSDL Database Management Limited");
                eInsuranceDetails.addCell(imgUnchecked);
                eInsuranceDetails.addCell("02.");
                eInsuranceDetails.addCell("Central Insurance Repository Limited");
                eInsuranceDetails.addCell(imgUnchecked);
                eInsuranceDetails.addCell("04.");
                eInsuranceDetails.addCell("Karvy Insurance Repository Limited");
                eInsuranceDetails.addCell(imgUnchecked);
                eInsuranceDetails.addCell("05.");
                eInsuranceDetails.addCell("CAMS Repository Limited");
                eInsuranceDetails.addCell(imgUnchecked);
            }
            table.addCell(new Cell().add(eInsuranceDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("7. Do you need a physical copy of Policy Document?     ");
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgChecked);
            p.add("     No      ");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
            //table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));


            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("8. Declaration by First Annuitant/ Life to be Assured");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
            p = new Paragraph(jsonUtility.getJsonKeyValue("content14", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("declarationHeading1", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("declarationContent1", contentJson));
            p.add(jsonUtility.getJsonKeyValue("declarationContent2", contentJson));
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationContent3", contentJson)).setBold());
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p=new Paragraph(new Text(jsonUtility.getJsonKeyValue("declarationContent4", contentJson)).setBold());
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p=new Paragraph(new Text(jsonUtility.getJsonKeyValue("declarationContent5", contentJson)).setBold());
            p.add("\n\n");
            if (jsonUtility.getJsonKeyValue("basbastatus",paymentDetailObj).equalsIgnoreCase("success")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("declarationContent6", contentJson)).setBold());
            }
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationContent7", contentJson)).setBold());
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("\n");
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationHeading2", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("declarationContent8", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p=new Paragraph();
            if (isOmniDoc) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationContent9", contentJson)));
            p.add("\n");
            if (isOmniDoc) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationContent10", contentJson)));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Table signature = new Table(new float[]{500F, 500F});
            signature.addCell(new Cell().add("").setHeight(100).setBorder(new SolidBorder(1)));
            signature.addCell(new Cell().add("").setHeight(100).setBorder(new SolidBorder(1)));
            if (isOmniDoc) {
                p=new Paragraph();
                p.add(imgChecked);
                if (countryCode.equalsIgnoreCase("91")) {
                    p.add("   Validated through the OTP sent to " +
                            "registered mobile no. ");
                    p.add(pdfUtility.maskMobileNumber(mobileNo)).setBold();
                } else {
                    p.add("   Validated through the OTP sent to " +
                            "registered email id. ");
                }
                signature.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
                signature.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            }

            signature.addCell(new Cell().add(new Paragraph("Life to be Assured’s Signature or Thumb Impression\n" +
                    "(Not applicable in case of minor lives)")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            signature.addCell(new Cell().add(new Paragraph("Annuitant Signature or Thumb Impression")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));

            String laName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            String laPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String laDate = currentDate.format(formatter);

            Table laSignaturedetails = new Table(new float[]{300F, 300F, 200F, 200F});
            laSignaturedetails.addCell(new Cell().add("Name: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            laSignaturedetails.addCell(new Cell().add(laName).setUnderline().setBorder(Border.NO_BORDER));
            laSignaturedetails.addCell(new Cell().add("Place: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            laSignaturedetails.addCell(new Cell().add(laPlace).setUnderline().setBorder(Border.NO_BORDER));
            laSignaturedetails.addCell(new Cell().add("Date: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            laSignaturedetails.addCell(new Cell().add(laDate).setUnderline().setBorder(Border.NO_BORDER));
            signature.addCell(new Cell().add(laSignaturedetails).setBorder(Border.NO_BORDER));


            String annuitantName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
            String annuitantPlace = jsonUtility.getJsonKeyValue("city", secondaryPersonalDetailObj);
            LocalDate currentDates = LocalDate.now();
            DateTimeFormatter formatte = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String annuitantDate = currentDates.format(formatte);

            Table annuitantSignaturedetails = new Table(new float[]{300F, 300F, 200F, 200F});
            annuitantSignaturedetails.addCell(new Cell().add("Name: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            annuitantSignaturedetails.addCell(new Cell().add(annuitantName).setUnderline().setBorder(Border.NO_BORDER));
            annuitantSignaturedetails.addCell(new Cell().add("Place: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            annuitantSignaturedetails.addCell(new Cell().add(annuitantPlace).setUnderline().setBorder(Border.NO_BORDER));
            annuitantSignaturedetails.addCell(new Cell().add("Date: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            annuitantSignaturedetails.addCell(new Cell().add(annuitantDate).setUnderline().setBorder(Border.NO_BORDER));
            signature.addCell(new Cell().add(annuitantSignaturedetails).setBorder(Border.NO_BORDER));

            String witnessName = "____________";
            String witnessPlace = "____________";
            String witnessDate = "____________";

            signature.addCell(new Cell().add(new Paragraph("")).setHeight(100).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            signature.addCell(new Cell().add(new Paragraph("")).setHeight(100).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            signature.addCell(new Cell().add(new Paragraph("Witness's Signature in English")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            signature.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            Table witnessSignaturedetails = new Table(new float[]{300F, 300F, 250F, 200F});
            witnessSignaturedetails.addCell(new Cell().add("Name: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add(witnessName).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add("Place: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add(witnessPlace).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add("Date: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add(witnessDate).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add("Address of Witness : ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            witnessSignaturedetails.addCell(new Cell().add("____________").setBorder(Border.NO_BORDER));
            signature.addCell(new Cell().add(witnessSignaturedetails).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signature).setBorder(Border.NO_BORDER));

            p = new Paragraph("OTP Verified     ");
            if(isOmniDoc){
                p.add(imgChecked).setTextAlignment(TextAlignment.LEFT);
            }else {
                p.add(imgUnchecked).setTextAlignment(TextAlignment.LEFT);
            }
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p=new Paragraph(new Text("Signature authentication(Single factor authentication):\n").setBold().setBorder(Border.NO_BORDER));

            p.add(jsonUtility.getJsonKeyValue("content15", contentJson)).setBorder(Border.NO_BORDER);
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text("Section 41 of Insurance Act 1938,as amended from time to time :  ").setBold());
            p.add(jsonUtility.getJsonKeyValue("content16", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("content17", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("content18", contentJson));
            p.add(jsonUtility.getJsonKeyValue("content19", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("9. Declaration for Signing in Vernacular or for Uneducated Persons");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
            p = new Paragraph("1. Vernacular Declaration by the person filling in the form (In case form is filled up / signed in a language different from that of the Proposal Form)\n" +
                    "I do hereby state that I have read out and explained the contents of the proposal form to the annuitant and he/she have understood the same.");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            String nameOfDeclarant = "";
            String signatureOfDeclarant = "";
            String addressOfdeclarant = "";
            String annutantRelation="___________";
            String language = "__________________";

            p = new Paragraph();
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            Table signatureDeclarants = new Table(new float[]{300F, 200F, 200F, 200F});
            signatureDeclarants.addCell(new Cell().add("Name of the Declarant:").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(nameOfDeclarant).setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Signature:     ");
            p.add(new Text(signatureOfDeclarant).setUnderline());
            signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p=new Paragraph("Or OTP verified    ");
            p.add(imgUnchecked);
            signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add("Address of the Declarant: ").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(addressOfdeclarant).setUnderline()).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add("Relation with the Annuitant:").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(annutantRelation).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));

            p = new Paragraph();
            p.add("\n");
            p.add("2. In case the Annuitant is illiterate, his/her thumb impression should be attested by a person of standing whose identity can easily be established, but unconnected with the insurer and this declaration should be made by him. \n“I hereby declare that I have fully explained the above questions and contents of the proposal form to the proposer in ");
            p.add(new Text(language).setUnderline());
            p.add("  language, and that the life assured / proposer has affixed the thumb impression above after fully understanding the contents thereof.”");
            p.add("\n");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            signatureDeclarants = new Table(new float[]{300F, 200F, 200F, 200F});
            signatureDeclarants.addCell(new Cell().add("Name of the Declarant:").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(nameOfDeclarant).setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Signature:     ");
            p.add(new Text(signatureOfDeclarant).setUnderline());
            signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p=new Paragraph("Or OTP verified    ");
            p.add(imgUnchecked);
            signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add("Address of the Declarant: ").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(addressOfdeclarant).setUnderline()).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add("Relation with Annuitant:").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(annutantRelation).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));

            //////NEW section added/////////////////
            String authorisedRepresentativeName=language;
            String authorisedPersonResident=language;
            String relationWithAuthPerson=language;
            String fatherMotherName=language;
            String locationAt=language;
            String formattedDate=language;
            String fullNames=language;
            String representativeMobileNumber="";
            if (jsonUtility.getJsonKeyValue("status",haveDisabilityObj).equalsIgnoreCase("y")){
                authorisedRepresentativeName= jsonUtility.getJsonKeyValue("authorisedRepresentativeName",haveDisabilityObj);
                authorisedPersonResident= jsonUtility.getJsonKeyValue("authorisedPersonResident",haveDisabilityObj);
                relationWithAuthPerson= jsonUtility.getJsonKeyValue("relationWhitAuthPreson",haveDisabilityObj);
                fatherMotherName= jsonUtility.getJsonKeyValue("fatherMotherName",haveDisabilityObj);
                locationAt= jsonUtility.getJsonKeyValue("locationAt",haveDisabilityObj);
                formattedDate = LocalDate.now().format(formatter);
                fullNames = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
                representativeMobileNumber= jsonUtility.getJsonKeyValue("representativeMobileNumber",haveDisabilityObj);
            }

            p = new Paragraph("\n");
            p.add("3. Declaration by Authorised Representative of Person with Disability\n");
            p.add("I ");
            p.add(new Text(authorisedRepresentativeName).setUnderline());
            p.add(" Son/Daughter of ");
            p.add(new Text(fatherMotherName).setUnderline());
            p.add(", adult and residing at ");
            p.add(new Text(authorisedPersonResident).setUnderline());
            p.add(" do hereby declare on solemn affirmation as under:\n");
            p.add("  The Proposer is a person with disability and requires assistance in completing the proposal form. I am duly authorised by the competent court / authority to fill this proposal form on behalf of the Proposer. I have read out and fully explained the contents of the proposal form to Mr./Mrs./Ms. ");
            p.add(new Text(fullNames).setUnderline());
            p.add("  and he/she has understood the significance of the proposed contract. I have truthfully and correctly recorded the replies given by the Proposer in the proposal form.");
            p.add("\n");
            p.add("Solemnly affirmed at ");
            p.add(new Text(locationAt).setUnderline());
            p.add(" on ");
            p.add(new Text(formattedDate).setUnderline());
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            boolean disclaimerStatus = jsonUtility.getBooleanKeyValue("disclamer", medicalDisabilityObj);
            boolean isOtpVerified = jsonUtility.getBooleanKeyValue("isOtpVerified", medicalDisabilityObj);

            signatureDeclarants = new Table(new float[]{400F, 100F, 300F, 100F});
            p=new Paragraph();
            p.add(isOtpVerified ? imgChecked : imgUnchecked);
            p.add(new Text("   Validated through the OTP sent to registered mobile no."));
            p.add(new Text(isOtpVerified ? pdfUtility.maskMobileNumber(representativeMobileNumber) : "").setBold());
            signatureDeclarants.addCell(new Cell(1,4).add(p).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            p=new Paragraph("Signature of the Authorised Representative");
            p.add("  Or OTP verified  ");
            if (disclaimerStatus) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }

            signatureDeclarants.addCell(new Cell(1,4).add(p).setPaddingRight(10f).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell(1,4).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Relationship of the Authorized Representative");
            p.add("    ");
            p.add(new Text(relationWithAuthPerson).setUnderline());
            signatureDeclarants.addCell(new Cell(1,4).add(p).setPaddingRight(45f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

            p=new Paragraph();
            if (disclaimerStatus) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("   I certify that the contents of the proposal form have been clearly explained to me and I have fully understood them. I further certify that the replies in the proposal form have been recorded as per the information provided by me.");
            signatureDeclarants.addCell(new Cell(1,4).add(p).setBorder(Border.NO_BORDER));

            signatureDeclarants.addCell(new Cell(1,4).add("").setHeight(20F).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add("Signature or thumb impression of Annuitant").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell(2,4).add("").setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));
            //////NEW section ended/////////////////

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("10. Intermediary details");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            Table intermediaryDetails = new Table(new float[]{200F, 200F, 200F, 200F});
            intermediaryDetails.addCell(new Cell().add("Name of the Intermediary").setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell().add(new Paragraph("ONL").setUnderline()).setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell().add("License Number :").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell(1, 4).add("(Applicable for all channels except Individual Agents)").setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell(1, 2).setHeight(50).setBorder(new SolidBorder(1)));
            intermediaryDetails.addCell(new Cell(1, 2).setHeight(50).setBorder(new SolidBorder(1)));

            intermediaryDetails.addCell(new Cell(1, 2).add("Signature of the Agent / Specified Agents").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            intermediaryDetails.addCell(new Cell(1, 2).add("Stamp of the Intermediary").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            intermediaryDetails.addCell(new Cell().add("Name of the Agent / Specified Agents:").setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell().add("Online Channel").setUnderline().setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell().add("License Code:").setBorder(Border.NO_BORDER));
            intermediaryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(intermediaryDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("11. Know Your Customer Certificate Issued by Bank");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            String customerNameBank = "          ";
            String accountno = "          ";
            String customerId = "           ";
            String authorisedSignature = "";
            String nameOfAuthorized = "";
            String nameOfBranch = "";

            p = new Paragraph();
            p.add("We hereby confirm that    ");
            p.add(new Text(customerNameBank).setUnderline());
            p.add("     holds Savings / Current / Fixed deposit loan account no.    ");
            p.add(new Text(accountno).setUnderline());
            p.add("     and bank customer ID    ");
            p.add(new Text(customerId).setUnderline());
            p.add("     with our bank. We confirm that we have obtained the necessary documentary evidence to establish the identity and address" +
                    " of the customer as mentioned by him/ her in this proposal form, as per the “Know Your Customer” (KYC) norms for banks.");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Table kycBankDetailsSignatures = new Table(new float[]{400F, 250F});
            kycBankDetailsSignatures.addCell(new Cell().add("Signature of authorised signatory from bank :").setBorder(Border.NO_BORDER));
            kycBankDetailsSignatures.addCell(new Cell().add(authorisedSignature).setBorder(Border.NO_BORDER));
            kycBankDetailsSignatures.addCell(new Cell().add("Name of authorised signatory from bank :").setBorder(Border.NO_BORDER));
            kycBankDetailsSignatures.addCell(new Cell().add(nameOfAuthorized).setBorder(Border.NO_BORDER));
            kycBankDetailsSignatures.addCell(new Cell().add("Name of the bank branch :").setBorder(Border.NO_BORDER));
            kycBankDetailsSignatures.addCell(new Cell().add(nameOfBranch).setBorder(Border.NO_BORDER));

            Table bankSeal = new Table(1);
            bankSeal.addCell(new Cell().setMinHeight(50).setBorder(new SolidBorder(1)));
            bankSeal.addCell(new Cell().add("Bank Seal").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            bankSeal.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));

            Table mergedKyc = new Table(2);
            mergedKyc.addCell(new Cell().add(kycBankDetailsSignatures).setBorder(Border.NO_BORDER));
            mergedKyc.addCell(new Cell().add(bankSeal).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(mergedKyc).setBorder(Border.NO_BORDER));

            table.addCell(new Cell().add(jsonUtility.getJsonKeyValue("content20", contentJson)).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(jsonUtility.getJsonKeyValue("content21", contentJson)).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(21, 76, 121), 100);
            p = new Paragraph("Confidential Report (To be completed by the sales personnel after receiving the completed proposal form)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            Table confidentialReposts = new Table(new float[]{600F, 200F});
            confidentialReposts.addCell(new Cell(1, 2).add("Note: If the Life to be Assured is related to the advisor, this report should be countersigned by the authorized signatory"));
            confidentialReposts.addCell(new Cell().add("1. Have you met the Proposer/ Life to be Assured?"));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            confidentialReposts.addCell(new Cell().add(p));
            confidentialReposts.addCell(new Cell().add("2. Are you related to the proposed Life to be Assured? If yes, please state your relationship with applicant"));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            confidentialReposts.addCell(new Cell().add(p));
            confidentialReposts.addCell(new Cell().add("3. Are you satisfied with the financial standing of the proposed Life to be Assured?"));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            confidentialReposts.addCell(new Cell().add(p));
            confidentialReposts.addCell(new Cell().add("4. What is the estimated annual income of the Life to be Assured?"));
            p = new Paragraph();
            p.add("");
            confidentialReposts.addCell(new Cell().add(p));
            confidentialReposts.addCell(new Cell().add("5. Does the life assured appear to be in good health without any mental disorder (or) physical disability?"));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            confidentialReposts.addCell(new Cell().add(p));
            confidentialReposts.addCell(new Cell().add("6. Does the appearance of the proposed Life to be Assured correspond with the age stated in application?"));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            confidentialReposts.addCell(new Cell().add(p));
            p = new Paragraph("7. Is the Proposer a:    ");
            p.add(imgUnchecked);
            p.add("     Judge       ");
            p.add(imgUnchecked);
            p.add("     Member of Parliament       ");
            p.add(imgUnchecked);
            p.add("     Member of state legislature       ");
            p.add(imgUnchecked);
            p.add("     National/State level office bearer of political party      ");
            p.add(new Text("(*Tick if applicable, default value No)").setFontSize(6).setTextAlignment(TextAlignment.RIGHT));
            confidentialReposts.addCell(new Cell(1, 2).add(p));
            table.addCell(new Cell().add(confidentialReposts).setBorder(Border.NO_BORDER));
            String remarks = "";
            p = new Paragraph("Other Remarks    ");
            p.add(new Text(remarks).setUnderline());
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            table.addCell(new Cell().setHeight(50).setBorder(new SolidBorder(1)));
            table.addCell(new Cell().add("Licensed Advisor’s Signature").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            String agentPlace = "";
            String agentDate = "";
            String advisorCode = "";

            Table agentDetails = new Table(new float[]{300F, 300F});
            p=new Paragraph("Name of the Intermediary              ");
            p.add(new Text("ONL").setUnderline());
            p.add(new Text("\n(Applicable for all channels except Individual Agents)").setFontSize(5F));

            agentDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            agentDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Name of the Agent / Specified Agents       ");
            p.add(new Text("Online Channel").setUnderline());
            agentDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p=new Paragraph("   Intermediary License No :  ");
            p.add("");
            agentDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            agentDetails.addCell(new Cell().add("License Code : ").setBorder(Border.NO_BORDER));
            agentDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Place: ");
            p.add(new Text(agentPlace).setUnderline());
            agentDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p=new Paragraph("   Date:  ");
            p.add(new Text(agentDate).setUnderline());
            agentDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            agentDetails.addCell(new Cell().add("Advisor Code: ").setBorder(Border.NO_BORDER));
            agentDetails.addCell(new Cell().add(new Paragraph(advisorCode).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(agentDetails).setBorder(Border.NO_BORDER));
            document.add(table);


            p = new Paragraph();
            p.add("\n");
            p.add("\n");
            document.add(p);

            if (isOmniDoc) {
                p = new Paragraph();
                p.add(imgChecked);
                String primaryMobileNo = jsonUtility.getJsonKeyValue("mobileNumber", jsonUtility.getJsonObjectByKey("primary", personalDetailObj));
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

            companyContact.add(new Text("\n-----------------------------------------------------------------------------------------------------------------------------------"));
            companyContact.add(new Text("\nE-mail: ").setBold());
            companyContact.add(new Text(jsonUtility.getJsonKeyValue("content22", contentJson)));
            companyContact.add(new Text("  Website: ").setBold());
            companyContact.add(new Text("www.india\u001Afirstlife.com"));

            grandFooterTable.addCell(companyContact);
            grandFooterTable.setWidthPercent(100).setFixedPosition(15, 20, 800F);
            document.add(grandFooterTable.setVerticalAlignment(VerticalAlignment.BOTTOM)).setTextAlignment(TextAlignment.JUSTIFIED);
            logger.info("Nominee list size is:{}",nomineeList.size());
            if(nomineeList.size() > 1){
                Table nomineeAddendum = this.generateNomineePDF(pdfUtility, jsonUtility, applicationNumber, nomineeList, imagesJson);
                document.add(new AreaBreak(AreaBreakType.NEXT_AREA));
                document.add(nomineeAddendum);
            }
            document.close();

            return baos.toByteArray();
        }catch (Exception e){
            logger.info("Exception occurs in g-pen pdf generation method:",e);
            return null;
        }
    }

    public Table getSecondaryLifeAssuredDetails(Image imgChecked,Image imgUnchecked,JsonObject planDetailObj,JsonObject insuredPersonBasicDetailObj,JsonObject secondaryPersonalDetailObj,JsonObject secondaryDocumentDetailObj,Map<String, String> ageProofMap,Map<String, String> idProofMap,Map<String, String> addressProofMap,String buyFor,boolean isOmniDoc, JsonObject contentJson){

        Table table=new Table(1);
        Table proposerDetails = new Table(new float[]{600f, 600f});
        proposerDetails.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)").setBorder(Border.NO_BORDER));
        Paragraph p;
        Table contactTable;
        if (!buyFor.equalsIgnoreCase("Myself")) {
            String insuredMaritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", secondaryPersonalDetailObj);
            String insuredGender = jsonUtility.getJsonKeyValue("gender", insuredPersonBasicDetailObj);
            p = new Paragraph("Mr.  ");
            if (insuredGender.equalsIgnoreCase("male")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mrs. ");
            if (insuredGender.equalsIgnoreCase("female") && insuredMaritalStatus.equalsIgnoreCase("married")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Ms.  ");
            if (insuredGender.equalsIgnoreCase("female") && insuredMaritalStatus.equalsIgnoreCase("single")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mx.  ");
            if (insuredGender.equalsIgnoreCase("other")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            proposerDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Cell proposerMergedcell = new Cell(1, 2);
            String lifeAssuredFullName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
            String policyNo = "";
            String clientId = "00000000";

            proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName,"00000000000000000000")).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);

            proposerDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("content23", contentJson)).setBorder(Border.NO_BORDER));
            Table innerProposer = new Table(new float[]{120F, 80F, 120F, 180F, 120F, 200F});
            innerProposer.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add("     Policy No:      ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add(pdfUtility.createDataTable(policyNo,"0000000")).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add("     Client ID:      ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add(pdfUtility.createDataTable(clientId,"0000000")).setBorder(Border.NO_BORDER));
            innerProposer.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));

            String address1 = jsonUtility.getJsonKeyValue("addressline1", secondaryPersonalDetailObj);
            String address2 = jsonUtility.getJsonKeyValue("addressline2", secondaryPersonalDetailObj);
            String address3 = jsonUtility.getJsonKeyValue("addressline3", secondaryPersonalDetailObj);
            String permanentaddresslinesec1 = jsonUtility.getJsonKeyValue("permanentaddressline1", secondaryPersonalDetailObj);
            String permanentaddresslinesec2 = jsonUtility.getJsonKeyValue("permanentaddressline2", secondaryPersonalDetailObj);
            String permanentaddresslinesec3 = jsonUtility.getJsonKeyValue("permanentaddressline3", secondaryPersonalDetailObj);
            String landmark = jsonUtility.getJsonKeyValue("landmark", secondaryPersonalDetailObj);
            String permanentlandmark = jsonUtility.getJsonKeyValue("permanentlandmark", secondaryPersonalDetailObj);

            if (permanentaddresslinesec1.length() > 0) {
                if (permanentaddresslinesec1.length() <= 10) {
                    permanentaddresslinesec1 += "                                        ";
                } else if (permanentaddresslinesec1.length() > 10 && permanentaddresslinesec1.length() <= 20) {
                    permanentaddresslinesec1 += "                           ";
                } else {
                    permanentaddresslinesec1 += "                ";
                }
            }
            if (permanentaddresslinesec2.length() > 0) {
                if (permanentaddresslinesec2.length() <= 10) {
                    permanentaddresslinesec1 += "                                        ";
                } else if (permanentaddresslinesec2.length() > 10 && permanentaddresslinesec2.length() <= 20) {
                    permanentaddresslinesec2 += "                           ";
                } else {
                    permanentaddresslinesec2 += "                ";
                }
            }
            if (permanentaddresslinesec3.length() > 0) {
                if (permanentaddresslinesec3.length() <= 10) {
                    permanentaddresslinesec3 += "                                        ";
                } else if (permanentaddresslinesec3.length() > 10 && permanentaddresslinesec3.length() <= 20) {
                    permanentaddresslinesec3 += "                           ";
                } else {
                    permanentaddresslinesec3 += "                ";
                }
            }

            if (address2.length() > 0) {
                if (address2.length() <= 10) {
                    address2 += "                                        ";
                } else if (address2.length() > 10 && address2.length() <= 20) {
                    address2 += "                              ";
                } else if (address2.length() > 20 && address2.length() <= 30) {
                    address2 += "                  ";
                } else if (address2.length() > 30 && address2.length() <= 40) {
                    address2 += "           ";
                } else {
                    address2 += "";
                }
            }
            if (address3.length() > 0) {
                if (address3.length() <= 10) {
                    address3 += "                                        ";
                } else if (address3.length() > 10 && address3.length() <= 20) {
                    address3 += "                              ";
                } else {
                    address3 += "                     ";
                }
            }

            if (landmark.length() > 0) {
                if (landmark.length() <= 10) {
                    landmark += "                                        ";
                } else if (landmark.length() > 10 && landmark.length() <= 20) {
                    landmark += "                           ";
                } else {
                    landmark += "                ";
                }
            }
            if (permanentlandmark.length() > 0) {
                if (permanentlandmark.length() <= 10) {
                    permanentlandmark += "                                        ";
                } else if (permanentlandmark.length() > 10 && permanentlandmark.length() <= 20) {
                    permanentlandmark += "                           ";
                } else {
                    permanentlandmark += "                ";
                }
            }
            String permanentcity = jsonUtility.getJsonKeyValue("permanentcity", secondaryPersonalDetailObj);
            if (permanentcity.length()>0) {
                if (permanentcity.length() <= 10) {
                    permanentcity += "                                        ";
                } else if (permanentcity.length() > 10 && permanentcity.length() <= 20) {
                    permanentcity += "                              ";
                } else {
                    permanentcity += "                ";
                }
            }
            String permanentstate = jsonUtility.getJsonKeyValue("permanentstate", secondaryPersonalDetailObj);
            if (permanentstate.length()>0) {
                if (permanentstate.length() <= 10) {
                    permanentstate += "                                        ";
                } else if (permanentstate.length() > 10 && permanentstate.length() <= 20) {
                    permanentstate += "                              ";
                } else {
                    permanentstate += "                ";
                }
            }
            String city = jsonUtility.getJsonKeyValue("city", secondaryPersonalDetailObj);
            if (city.length() > 0) {
                if (city.length() <= 10) {
                    city += "                                        ";
                } else if (city.length() > 10 && city.length() <= 20) {
                    city += "                              ";
                } else {
                    city += "                     ";
                }
            }
            String state = jsonUtility.getJsonKeyValue("state", secondaryPersonalDetailObj);
            if (state.length() > 0) {
                if (state.length() <= 10) {
                    state += "                                        ";
                } else if (state.length() > 10 && state.length() <= 20) {
                    state += "                              ";
                } else {
                    state += "                     ";
                }
            }
            String pincode = jsonUtility.getJsonKeyValue("pincode",secondaryPersonalDetailObj );
            if (pincode.length() > 0) {
            }else {
                pincode += "      ";
            }
            p = new Paragraph(new Text("Communication Address of the Second Annuitant ").setBold());
            p.add("(Address to which policy document will be dispatched)          ");
            p.setPaddingBottom(10);
           /* p.add(imgUnchecked);
            p.add("     Same as First Annuitant");*/
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);
            //proposerMergedcell.add(new Paragraph(new Text(communicationAddress.toUpperCase()).setUnderline())).setBorder(Border.NO_BORDER);
            Table addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(landmark,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            proposerMergedcell = new Cell(1,2);
            addressBlock=new Table(new float[]{600F,100F,200F});
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"0000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(pincode,"000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);


            //Permanent Address Section addition started
            boolean isaddressSame = jsonUtility.getBooleanKeyValue("IsaddressSame", secondaryPersonalDetailObj);
            p = new Paragraph("Permanent Address (If different from the above Address)          ");
            p.setPaddingBottom(10);
            if(isaddressSame){
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add("     Same as First Annuitant");
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);

            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? address1:permanentaddresslinesec1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? address2:permanentaddresslinesec2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? address3:permanentaddresslinesec3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? landmark:permanentlandmark,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? city:permanentcity,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(isaddressSame ? state:permanentstate,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //Permanent Address Section addition ended

            String pinCode = jsonUtility.getJsonKeyValue("permanentpincode", secondaryPersonalDetailObj);
            String mobileNo = jsonUtility.getJsonKeyValue("mobileNumber", secondaryPersonalDetailObj);
            String countryCode = jsonUtility.getJsonKeyValue("countryCode", insuredPersonBasicDetailObj);
            String landLine = "";

            contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F,200F});
            contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("+").setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(countryCode,"00")).setBorder(Border.NO_BORDER));
            p=new Paragraph("Mobile No*:      ");
            p.add(new Text("\n*Receive alerts througth SMS").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(mobileNo,"0000000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(pinCode,"000000")).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);

            String emailId = jsonUtility.getJsonKeyValue("emailId", secondaryPersonalDetailObj);

            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(new float[]{220F, 430F, 250F, 400F});
            p=new Paragraph("Email ID*:  ");
            p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(emailId,"000000000000")).setBorder(Border.NO_BORDER));
            p=new Paragraph("Landline:");
            p.add(new Text("\nSTD/ISD").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(landLine,"0000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            p = new Paragraph("Male:     ");
            if (insuredGender.equalsIgnoreCase("male")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Female:     ");
            if (insuredGender.equalsIgnoreCase("female")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Transgender:    ");
            if (insuredGender.equalsIgnoreCase("other")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Nationality:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Indian:      ");
            if (jsonUtility.getJsonKeyValue("nationality", secondaryPersonalDetailObj).equalsIgnoreCase("Indian")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Non Indian:     ");
            if (!jsonUtility.getJsonKeyValue("nationality", secondaryPersonalDetailObj).equalsIgnoreCase("Indian")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            String dob = jsonUtility.getJsonKeyValue("dateOfBirth", secondaryPersonalDetailObj);
//                currentDate = LocalDate.now();
            LocalDate birthDate = LocalDate.parse(dob);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String date = dateTimeFormatter.format(birthDate);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("DOB :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(dob,"00000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("  Residential Status:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            String residentialStatus = jsonUtility.getJsonKeyValue("residentialstatus", secondaryPersonalDetailObj);
            p = new Paragraph();
            p.add("Resident:    ");
            if (residentialStatus.equalsIgnoreCase("RNT")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     NRI:    ");
            if (residentialStatus.equalsIgnoreCase("nri")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Others:    ");
            if (!(residentialStatus.equalsIgnoreCase("RNT") || residentialStatus.equalsIgnoreCase("nri"))) {
                p.add(new Text(residentialStatus).setUnderline());
            } else {
                p.add(new Text("").setUnderline());
            }
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

            contactTable.addCell(new Cell().add("Marital Status :   ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Unmarried:   ");
            if (insuredMaritalStatus.equalsIgnoreCase("unmarried") || insuredMaritalStatus.equalsIgnoreCase("Single")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Married:  ");
            if (insuredMaritalStatus.equalsIgnoreCase("married")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Widow(er):  ");
            if (insuredMaritalStatus.equalsIgnoreCase("widow")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Divorced:   ");
            if (insuredMaritalStatus.equalsIgnoreCase("divorced")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

            String identityProof = "";
            String addressProof = "";
            String ageProof = "";

            String secondaryAgeDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof-all", secondaryDocumentDetailObj));
            String secondaryAddressDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("addressProof", secondaryDocumentDetailObj));
            String secondaryIdDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("idProof", secondaryDocumentDetailObj));
            if (isOmniDoc && idProofMap.containsKey(secondaryIdDocType)) {
                identityProof = idProofMap.get(secondaryIdDocType);
            }
            if (isOmniDoc && ageProofMap.containsKey(secondaryAgeDocType)) {
                ageProof = ageProofMap.get(secondaryAgeDocType);
            }
            if (isOmniDoc && addressProofMap.containsKey(secondaryAddressDocType)) {
                addressProof = addressProofMap.get(secondaryAddressDocType);
            }

            String panCardNo = jsonUtility.getJsonKeyValue("pancard", secondaryPersonalDetailObj);
            p=new Paragraph("Identity Proof");
            p.add(new Text(" (Second annuitant ): ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(identityProof).setUnderline()).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell(1,4).add("").setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell(1,4).add("").setBorder(Border.NO_BORDER));
            p=new Paragraph("Address Proof");
            p.add(new Text(" (Second annuitant ): ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(addressProof)));
            p=new Paragraph("Age Proof");
            p.add(new Text(" (Second annuitant ): ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(ageProof)));

            p=new Paragraph("PAN");
            p.add(new Text("\n(Second annuitant ): ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(panCardNo,"0000000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Relationship with First Annuitant: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("relation", planDetailObj)).setUnderline()).setBorder(Border.NO_BORDER));

            String secondaryAgeProofDoc = jsonUtility.getJsonKeyValue("documentType",jsonUtility.getJsonObjectByKey("ageProof", secondaryDocumentDetailObj));
            String secondaryPanCardName = jsonUtility.getJsonKeyValue("name",jsonUtility.getJsonObjectByKey("panCard", secondaryDocumentDetailObj));
            p = new Paragraph("PAN: ");
            p.add(new Text("\n(photocopy Enclosed)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (isOmniDoc) {
                if (secondaryAgeDocType.equalsIgnoreCase("PA") || secondaryIdDocType.equalsIgnoreCase("PAN CARD") ||
                        secondaryAgeProofDoc.equalsIgnoreCase("PA") || !secondaryPanCardName.isEmpty()) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (!(secondaryAgeDocType.equalsIgnoreCase("PA") || secondaryIdDocType.equalsIgnoreCase("PAN CARD") ||
                        secondaryAgeProofDoc.equalsIgnoreCase("PA") || !secondaryPanCardName.isEmpty())) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No  ");
            }else {
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
            }
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        } else {
            p = new Paragraph("Mr.  ");
            p.add(imgUnchecked);
            p.add("     Mrs. ");
            p.add(imgUnchecked);
            p.add("     Ms.  ");
            p.add(imgUnchecked);
            p.add("     Mx.  ");
            p.add(imgUnchecked);
            proposerDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Cell proposerMergedcell = new Cell(1, 2);
            String lifeAssuredFullName = "";
            String policyNo = "";
            String clientId = "";

            proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName,"00000000000000000000")).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);

            proposerDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("content24", contentJson)).setBorder(Border.NO_BORDER));
            Table innerProposer = new Table(new float[]{120F, 80F, 120F, 180F, 120F, 200F});
            innerProposer.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add("     Policy No:      ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add(pdfUtility.createDataTable(policyNo,"0000000")).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add("     Client ID:      ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            innerProposer.addCell(new Cell().add(pdfUtility.createDataTable(clientId,"0000000")).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));

            String address1 = "";
            String address2 = "";
            String address3 = "";
            String city = "";
            String state = "";
            p = new Paragraph(new Text("Communication Address of the Second Annuitant ").setBold());
            p.add("(Address to which policy document will be dispatched)          ");
            p.setPaddingBottom(10);
            p.add(imgUnchecked);
            p.add("     Same as First Annuitant");
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);
            //proposerMergedcell.add(new Paragraph(new Text(communicationAddress.toUpperCase()).setUnderline())).setBorder(Border.NO_BORDER);
            Table addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            //Permanent Address Section addition started

            p = new Paragraph("Permanent Address (If different from the above Address)          ");
            p.setPaddingBottom(10);
            p.add(imgUnchecked);
            p.add("     Same as First Annuitant");
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);

            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //Permanent Address Section addition ended

            String mobileNo = "";
            String countryCode = "";
            String landLine = "";
            String pinCode = "";

            contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F,200F});
            contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("+").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(countryCode,"00")).setBorder(Border.NO_BORDER));
            p=new Paragraph("Mobile No*:      ");
            p.add(new Text("\n*Receive alerts througth SMS").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(mobileNo,"000000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(pinCode,"0000000")).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell);

            String emailId = "";
            String dob = "";
            String identityProof = "";
            String addressProof = "";
            String ageProof = "";
            String panCardNo = "";

            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(new float[]{180F, 450F, 200F, 400F});
            p=new Paragraph("Email ID*:  ");
            p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(emailId,"000000000000")).setBorder(Border.NO_BORDER));
            p=new Paragraph("Landline:");
            p.add(new Text("\nSTD/ISD").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(landLine,"0000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            p = new Paragraph("Male:     ");
            p.add(imgUnchecked);
            p.add("     Female:     ");
            p.add(imgUnchecked);
            p.add("     Transgender:    ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Nationality:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Indian:      ");
            p.add(imgUnchecked);
            p.add("     Non Indian:     ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("DOB :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(dob,"00000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("  Residential Status:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add("Resident:    ");
            p.add(imgUnchecked);
            p.add("     NRI:    ");
            p.add(imgUnchecked);
            p.add("     Others:    ");
            p.add(new Text("").setUnderline());
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            contactTable.addCell(new Cell().add("Marital Status :   ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Unmarried:   ");
            p.add(imgUnchecked);
            p.add("     Married:  ");
            p.add(imgUnchecked);
            p.add("     Widow(er):  ");
            p.add(imgUnchecked);
            p.add("     Divorced:   ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            p=new Paragraph("Identity Proof");
            p.add(new Text(" (Second annuitant ) ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(identityProof).setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Address Proof");
            p.add(new Text(" (Second annuitant ) ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(addressProof).setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("Age Proof");
            p.add(new Text(" (Second annuitant ) ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(new Paragraph(ageProof).setUnderline()).setBorder(Border.NO_BORDER));
            p=new Paragraph("PAN");
            p.add(new Text("\n(Second annuitant ) ").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(panCardNo,"00000000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Relationship with First Annuitant: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            p = new Paragraph("PAN: ");
            p.add(new Text("\n(photocopy Enclosed)").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No     ");
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        }
        logger.info("outside of joint life condition");
        Cell proposerMergedcell=new Cell(1,2);
        proposerMergedcell.add(contactTable);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);
        table.addCell(new Cell().add(proposerDetails).setBorder(Border.NO_BORDER));
        return table;
    }

    public Table getBankDetails(Image imgUnchecked, Image imgChecked,JsonObject primaryBankObj,JsonArray nomineeList, JsonObject contentJson){
        String bankName = jsonUtility.getJsonKeyValue("Bank_Name", primaryBankObj);
        bankName=bankName.length()>21 ? bankName.substring(0,21) : bankName;
        String accountNo = jsonUtility.getJsonKeyValue("accountNumber", primaryBankObj);
        String micr = jsonUtility.getJsonKeyValue("MICR_Code", primaryBankObj);
        String ifscode = jsonUtility.getJsonKeyValue("ifscCode", primaryBankObj);
        String customerName = jsonUtility.getJsonKeyValue("accountHolderName", primaryBankObj);
        Table table=new Table(1);
        ////
        Paragraph p = new Paragraph("For Proposer & Life Assured").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the proposer according to the terms of the plan. Further, the Company reserves the right to use any alternative payout option including demand draft/ payable at par cheque in spite of option for Direct credit.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(new Text("Details of First Annuitant").setBold())).setBorder(Border.NO_BORDER));

        Table bankDetails = new Table(new float[] {70F ,400F, 30F, 60F, 100F, 500F});
        bankDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("  Direct Credit (Bank of Baroda) ");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        bankDetails.addCell(new Cell().add(pdfUtility.createDataTable(bankName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        Table accountDetails = new Table(new float[] {100F, 300F,10F, 80F, 180F,10F, 80F,170F});
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(accountNo,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("IFSC Code: ");
        p.add(new Text("\n(Mandatory for NEFT mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(ifscode,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("MICR: ");
        //p.add(new Text("\n(Mandatory for ECS mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(micr,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));


        Table nameDetails = new Table(new float[] {220F, 700F});
        nameDetails.addCell(new Cell().add("Customer’s Name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell().add(pdfUtility.createDataTable(customerName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ////


        table.addCell(new Cell().add(new Paragraph(new Text("Details of Second Annuitant (if Joint Life is chosen)").setBold())).setBorder(Border.NO_BORDER));
        bankDetails = new Table(new float[] {70F ,400F, 30F, 60F, 100F, 500F});
        bankDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("  Direct Credit (Bank of Baroda) ");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        bankDetails.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        accountDetails = new Table(new float[] {100F, 300F,10F, 80F, 180F,10F, 80F,170F});
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("IFSC Code: ");
        p.add(new Text("\n(Mandatory for NEFT mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("MICR: ");
        //p.add(new Text("\n(Mandatory for ECS mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));


        nameDetails = new Table(new float[] {220F, 700F});
        nameDetails.addCell(new Cell().add("Customer’s Name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell(1,2).add(new Paragraph(new Text("Please provide a cancelled copy of your cheque if any of the above option is selected").setBold())).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ////
        table.addCell(new Cell().add(new Paragraph(new Text("").setBold()).setUnderline()).setBorder(Border.NO_BORDER));

        ///Nominee details section started//
        JsonObject nomineeObj1 = nomineeList.isEmpty() ? new JsonObject() : nomineeList.get(0).getAsJsonObject();
        String modeOfPaymentNominee = jsonUtility.getJsonKeyValue("modeOfPayment", nomineeObj1);
        bankName = jsonUtility.getJsonKeyValue("Bank_Name", nomineeObj1);
        bankName=bankName.length()>21 ? bankName.substring(0,21) : bankName;
        accountNo = jsonUtility.getJsonKeyValue("accountNumber", nomineeObj1);
        ifscode = jsonUtility.getJsonKeyValue("ifscCode", nomineeObj1);
        customerName = jsonUtility.getJsonKeyValue("name", nomineeObj1);
        micr = jsonUtility.getJsonKeyValue("MICR_Code_Nominee", nomineeObj1);
        p = new Paragraph("For Nominee 1").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the nominee according to the terms of the plan. Further, the Company reserves the right to use any alternative payout option including demand draft/ payable at par cheque in spite of option for Direct credit.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        bankDetails = new Table(new float[] {70F ,400F, 30F, 60F, 100F, 500F});
        bankDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentNominee.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        p.add("  Direct Credit (Bank of Baroda) ");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentNominee.equalsIgnoreCase("NEFT") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        bankDetails.addCell(new Cell().add(pdfUtility.createDataTable(bankName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        accountDetails = new Table(new float[] {100F, 300F,10F, 80F, 180F,10F, 80F,170F});
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(accountNo,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("IFSC Code: ");
        p.add(new Text("\n(Mandatory for NEFT mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(ifscode,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("MICR: ");
        //p.add(new Text("\n(Mandatory for ECS mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(micr,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));


        nameDetails = new Table(new float[] {220F, 700F});
        nameDetails.addCell(new Cell().add("Nominee's name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell().add(pdfUtility.createDataTable(customerName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p=new Paragraph(new Text("Note: ").setBold());
        p.add("In case of multiple nominations, please add all nominees bank account details");
        nameDetails.addCell(new Cell(1,2).add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ////


        ///Appointee section started////
        String modeOfPaymentAppointee = jsonUtility.getJsonKeyValue("appointeeModeOfPayment", nomineeObj1);
        bankName = jsonUtility.getJsonKeyValue("Appointee_Bank_Name", nomineeObj1);
        bankName=bankName.length()>21 ? bankName.substring(0,21) : bankName;
        accountNo = jsonUtility.getJsonKeyValue("appointeeAccountNumber", nomineeObj1);
        ifscode = jsonUtility.getJsonKeyValue("appointeeIfscCode", nomineeObj1);
        customerName = jsonUtility.getJsonKeyValue("appointeeName", nomineeObj1);
        micr = jsonUtility.getJsonKeyValue("MICR_Code_Appointee", nomineeObj1);
        p = new Paragraph("For Appointee (In case the Nominee is minor)").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the Appointee according to the terms of the plan. If none of the below electronic payout option is chosen,the Company reserves the right to use any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        bankDetails = new Table(new float[] {70F ,400F, 30F, 60F, 100F, 500F});
        bankDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentAppointee.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        p.add("  Direct Credit (Bank of Baroda) ");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentAppointee.equalsIgnoreCase("NEFT") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        bankDetails.addCell(new Cell().add(pdfUtility.createDataTable(bankName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        accountDetails = new Table(new float[] {100F, 300F,10F, 80F, 180F,10F, 80F,170F});
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(accountNo,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("IFSC Code: ");
        p.add(new Text("\n(Mandatory for NEFT mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(ifscode,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p=new Paragraph("MICR: ");
        //p.add(new Text("\n(Mandatory for ECS mode)").setFontSize(5F));
        accountDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(micr,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));


        nameDetails = new Table(new float[] {220F, 700F});
        nameDetails.addCell(new Cell().add("Appointee's Name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell().add(pdfUtility.createDataTable(customerName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p=new Paragraph(new Text("Disclaimer: ").setBold());
        p.add(jsonUtility.getJsonKeyValue("content25", contentJson));
        nameDetails.addCell(new Cell(1,2).add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        return table;
    }


    @Override
    public Table codeTable(String codeData, float[] pointColumnWidths) {
        Table tableCodes = new Table(pointColumnWidths);
        for (char c : codeData.toCharArray()) {
            Cell cellLeft = new Cell();
            Paragraph p = new Paragraph();
            p.add(new Text(String.valueOf(c)));
            cellLeft.add(p);
            cellLeft.setTextAlignment(TextAlignment.CENTER);
            tableCodes.addCell(cellLeft);
        }
        return tableCodes;
    }
}
