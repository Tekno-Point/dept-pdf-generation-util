package com.pdfGeneration.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
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
import com.pdfGeneration.service.DownloadApplicationFormService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.OnlineUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DownloadApplicationFormServiceImpl extends NomineeAddendumPDF implements DownloadApplicationFormService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;
    private final OnlineUtility onlineUtility;

    public DownloadApplicationFormServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility, OnlineUtility onlineUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;
        this.onlineUtility = onlineUtility;
    }

    @Override
    public byte[] downloadApplicationForm(String request) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JsonObject userData = jsonUtility.getJsonObject(request);
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
            JsonObject contentJson = jsonUtility.getJsonObjectByKey("content", userData);

            JsonObject policyHolderBasicDetailObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            JsonObject insuredPersonBasicDetailObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);
            String buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderBasicDetailObj);// global flag check for
            boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);
            boolean myself = buyFor.equalsIgnoreCase("Myself");
            logger.info("---- flag---:{}", buyFor);

            JsonObject responsePlanObj = jsonUtility.getJsonObjectByKey("response", planDetailObj);
            JsonObject quotePlanSectionDetails = jsonUtility.getJsonObjectByKey("quotePlanSectionDetails",
                    responsePlanObj);
            JsonObject primaryBankObj = jsonUtility.getJsonObjectByKey("primary", bankDetailObj);

            JsonObject primarysecodaryHealthDetailObj;
            JsonObject primarySecondaryLifestyleObj;
            JsonObject primarySecondaryMedicalObj;

            JsonObject primaryPersonalDetailObj = jsonUtility.getJsonObjectByKey("primary", personalDetailObj);
            JsonObject secondaryPersonalDetailObj = jsonUtility.getJsonObjectByKey("secondary", personalDetailObj);
            JsonObject primaryEmploymentDetailObj = jsonUtility.getJsonObjectByKey("primary", employmentDetailObj);
            JsonObject secondaryEmploymentDetailObj = jsonUtility.getJsonObjectByKey("secondary", employmentDetailObj);
            JsonObject primaryFatcaDetailObj = jsonUtility.getJsonObjectByKey("primary", fatcaDetailObj);
            JsonObject secondaryFatcaDetailObj = jsonUtility.getJsonObjectByKey("secondary", fatcaDetailObj);
            JsonObject primaryOtherDetailObj = jsonUtility.getJsonObjectByKey("primary", otherDetailObj);
            JsonObject secondaryOtherDetailObj = jsonUtility.getJsonObjectByKey("secondary", otherDetailObj);
            JsonObject primaryDocumentDetailObj = jsonUtility.getJsonObjectByKey("primary", documentDetailObj);
            JsonObject secondaryDocumentDetailObj = jsonUtility.getJsonObjectByKey("secondary", documentDetailObj);

            if (myself) {
                primarysecodaryHealthDetailObj = jsonUtility.getJsonObjectByKey("primary", healthDetailObj);
                primarySecondaryLifestyleObj = jsonUtility.getJsonObjectByKey("primary", lifestyleDetailObj);
                primarySecondaryMedicalObj = jsonUtility.getJsonObjectByKey("primary", medicalDetailObj);
            } else {
                primarysecodaryHealthDetailObj = jsonUtility.getJsonObjectByKey("secondary", healthDetailObj);
                primarySecondaryLifestyleObj = jsonUtility.getJsonObjectByKey("secondary", lifestyleDetailObj);
                primarySecondaryMedicalObj = jsonUtility.getJsonObjectByKey("secondary", medicalDetailObj);
            }

            JsonObject metadataObj = jsonUtility.getJsonObjectByKey("metadata",userData);
            String author = jsonUtility.getJsonKeyValue("author", metadataObj);
            String creator = jsonUtility.getJsonKeyValue("creator", metadataObj);
            String title = jsonUtility.getJsonKeyValue("title", metadataObj);
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            ////////////////////
            PdfDocumentInfo pdfDocumentInfo=pdfDoc.getDocumentInfo();
            pdfDocumentInfo.setAuthor(author);
            pdfDocumentInfo.setCreator(creator);
            pdfDocumentInfo.setTitle(title);
            pdfDocumentInfo.addCreationDate();
            //pdfDoc.addNewPage();

            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images",userData);
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

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            //PdfDocument pdf = new PdfDocument(writer);
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
            p.add(new Text("Application No. : " + jsonUtility.getJsonKeyValue("applicationNumber", userData)).setBold());
            document.add(p);

            Table table = new Table(1);
            table.setWidthPercent(100);
            Cell headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("heading", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);
            headingCell = new Cell();
            p = new Paragraph(jsonUtility.getJsonKeyValue("disclaimer", contentJson));
            headingCell.add(new Cell().setPaddingTop(10).add(p).setPaddingBottom(10));
            table.addCell(headingCell);

            float[] pointColumnWidths = new float[]{200F, 680F};
            Table firstBlockTable = new Table(pointColumnWidths);

            Table firstBlockLeft = new Table(1);
            firstBlockLeft.setMarginTop(20);
            firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
            firstBlockLeft.setWidth(150);
             SolidBorder solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);
            try {

                byte[] photoBytes = Base64.getDecoder().decode(jsonUtility.getJsonKeyValue("proposerPhotoBase64",userData));
                ImageData dataPhoto = ImageDataFactory.create(photoBytes);
                Image photoImg = new Image(dataPhoto);
                photoImg.setHeight(150);
                photoImg.setWidth(150);
                firstBlockLeft.addCell(new Cell().add(photoImg).setBorder(solidBorder));
            } catch (Exception e) {
                logger.info("Base64 error in application form:{}", e);
                firstBlockLeft.addCell(new Cell().add("").setBorder(solidBorder));
            }
            firstBlockTable.addCell(firstBlockLeft);
            Table firstBlockRight = new Table(4);

            String agentCode = "ON000001";
            String branchCodeValue = jsonUtility.getJsonKeyValue("branchCode", policyHolderBasicDetailObj);
            String branchCode = branchCodeValue.isEmpty() ? "DM001" : branchCodeValue;
            String branchManagerCode = "";
            String rmCode = "ON000001";
            String channelCode = "Online";
            String dbmMobile = "";

            Cell salesBranch = new Cell(1, 4);
            salesBranch.add("For Branch Sales Use Only");
            firstBlockRight.addCell(salesBranch);
            firstBlockRight.addCell(new Cell().add("LG / Agent Code: "));
            pointColumnWidths = new float[agentCode.length()];
            for (int i = 0; i < agentCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            Table tableCodes = null;
            if (agentCode.length() > 0) {
                tableCodes = codeTable(agentCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes));
            } else {
                firstBlockRight.addCell(new Cell().add(new Paragraph("")));
            }

            firstBlockRight.addCell(new Cell().add("Branch Code: "));
            pointColumnWidths = new float[branchCode.length()];
            for (int i = 0; i < branchCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (branchCode.length() > 0) {
                tableCodes = codeTable(branchCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes));
            } else {
                firstBlockRight.addCell(new Cell().add(new Paragraph("")));
            }
            firstBlockRight.addCell(new Cell().add("Branch Manager Code: "));
            pointColumnWidths = new float[branchManagerCode.length()];
            for (int i = 0; i < branchManagerCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (branchManagerCode.length() > 0) {
                tableCodes = codeTable(branchManagerCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes));
            } else {
                firstBlockRight.addCell(new Cell().add(new Paragraph("")));
            }

            firstBlockRight.addCell(new Cell().add("BDM / RM Code: "));
            pointColumnWidths = new float[rmCode.length()];
            for (int i = 0; i < rmCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (rmCode.length() > 0) {
                tableCodes = codeTable(rmCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes));
            } else {
                firstBlockRight.addCell(new Cell().add(new Paragraph("")));
            }

            firstBlockRight.addCell(new Cell().add("Channel Code: "));
            pointColumnWidths = new float[channelCode.length()];
            for (int i = 0; i < channelCode.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (channelCode.length() > 0) {
                tableCodes = codeTable(channelCode, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes));
            } else {
                firstBlockRight.addCell(new Cell().add(new Paragraph("")));
            }

            firstBlockRight.addCell(new Cell().add("BDM Mobile No: "));
            pointColumnWidths = new float[dbmMobile.length()];
            for (int i = 0; i < dbmMobile.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (dbmMobile.length() > 0) {
                tableCodes = codeTable(dbmMobile, pointColumnWidths);
                firstBlockRight.addCell(new Cell().add(tableCodes));
            } else {
                firstBlockRight.addCell(new Cell().add(new Paragraph("")));
            }

            Cell others = new Cell(1, 4);
            p = new Paragraph(jsonUtility.getJsonKeyValue("desc1", contentJson));
            p.add(new Text("Direct Sales").setBold().setUnderline());
            others.add(p);
            firstBlockRight.addCell(others);

            others = new Cell(1, 4);
            others.setMarginTop(10);
            p = new Paragraph(jsonUtility.getJsonKeyValue("guidelines", contentJson));
            others.add(p);
            others.setMarginBottom(10);
            firstBlockRight.addCell(others);
            firstBlockTable.addCell(firstBlockRight);
            table.addCell(firstBlockTable);

            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("desc2", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            String maritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", primaryPersonalDetailObj);
            String gender = jsonUtility.getJsonKeyValue("gender", policyHolderBasicDetailObj);

            LocalDate currentDate = LocalDate.now();
            Cell proposerMergedcell = new Cell(1, 2);
            Table contactTable = new Table(6);

            String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderBasicDetailObj);
            String mobileNo = jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetailObj);
            Table proposerDetails = new Table(2);
            proposerDetails.setBorder(Border.NO_BORDER);
            proposerDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)"));
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
            proposerDetails.addCell(new Cell().add(p));

            String fullName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            String policyNo = "";
            String clientId = "";

            proposerMergedcell.add(pdfUtility.createDataTable(fullName,"00000000000000000000")).setBorder(Border.NO_BORDER);

            proposerDetails.addCell(proposerMergedcell);

            proposerDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("desc8", contentJson)));
            // Is Existing Policy Owner
            Table innerProposer = new Table(5);
            innerProposer.addCell(imgUnchecked);
            for (int i = 0; i < policyNo.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            innerProposer.addCell("     Policy No:      ");
            if (policyNo.length() > 0) {
                tableCodes = codeTable(policyNo, pointColumnWidths);
                innerProposer.addCell(tableCodes);
            } else {
                innerProposer.addCell(new Paragraph(""));
            }
            innerProposer.addCell("     Client ID:      ");
            for (int i = 0; i < clientId.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (clientId.length() > 0) {
                tableCodes = codeTable(clientId, pointColumnWidths);
                innerProposer.addCell(tableCodes);
            } else {
                innerProposer.addCell(new Paragraph(""));
            }
            proposerDetails.addCell(innerProposer);
            p=new Paragraph();
            p .add("Communication Address of the Proposer (Address to which policy document will be dispatched)");
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
                    address1 += "                ";
                }
            }
            String address2 = jsonUtility.getJsonKeyValue("addressline2", primaryPersonalDetailObj);
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
            String address3 = jsonUtility.getJsonKeyValue("addressline3", primaryPersonalDetailObj);
            if (address3.length() > 0) {
                if (address3.length() <= 10) {
                    address3 += "                                        ";
                } else if (address3.length() > 10 && address3.length() <= 20) {
                    address3 += "                              ";
                } else {
                    address3 += "                     ";
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

            String pincode = jsonUtility.getJsonKeyValue("pincode", primaryPersonalDetailObj);
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
            if (city.length() > 0) {
                if (city.length() <= 10) {
                    city += "                                        ";
                } else if (city.length() > 10 && city.length() <= 20) {
                    city += "                              ";
                } else {
                    city += "                     ";
                }
            }
            String state = jsonUtility.getJsonKeyValue("state", primaryPersonalDetailObj);
            if (state.length() > 0) {
                if (state.length() <= 10) {
                    state += "                                        ";
                } else if (state.length() > 10 && state.length() <= 20) {
                    state += "                              ";
                } else {
                    state += "                     ";
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

            p = new Paragraph("Permanent Address (If different from the above Address)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);
            // proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);
            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressline1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressline2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressline3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentlandmark,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentcity,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentstate,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //Permanent Address Section addition ended
            ////New section for address ended//////
            String landLine = "";

            contactTable.addCell("Country Code");
            contactTable.addCell(pdfUtility.createDataTable(countryCode,"00000000000000"));
            contactTable.addCell("Mobile No:      ");
            contactTable.addCell(pdfUtility.createDataTable(mobileNo,"00000000000000"));

            String pinCode = jsonUtility.getJsonKeyValue("permanentpincode", primaryPersonalDetailObj);
            contactTable.addCell("Pin Code:  ");
            contactTable.addCell(pdfUtility.createDataTable(pinCode,"000000"));
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerDetails.addCell(proposerMergedcell);

            String emailId = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetailObj);
            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(new float[]{100F,350F,100F,200F});
            contactTable.addCell("Email ID:  ");
            contactTable.addCell(pdfUtility.createDataTable(emailId,"00000000000000"));

            contactTable.addCell("Landline:       ");
            contactTable.addCell(pdfUtility.createDataTable(landLine,"000000000"));

            proposerMergedcell.add(contactTable);
            proposerDetails.addCell(proposerMergedcell);

            proposerMergedcell = new Cell(1, 2);
            pointColumnWidths = new float[]{50F, 350F, 50F, 350F};
            contactTable = new Table(pointColumnWidths);
            contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Male: ");
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
            contactTable.addCell(p);
            contactTable.addCell(new Cell().add("Nationality:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph("Indian:      ");
            if (jsonUtility.getJsonKeyValue("nationality", primaryPersonalDetailObj).equalsIgnoreCase("Indian")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Non Indian:     ");
            if (!jsonUtility.getJsonKeyValue("nationality", primaryPersonalDetailObj)
                    .equalsIgnoreCase("Indian")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(p);
            proposerMergedcell.add(contactTable);
            proposerDetails.addCell(proposerMergedcell);

            String dob = jsonUtility.getJsonKeyValue("dateOfBirth", primaryPersonalDetailObj);

            LocalDate birthDate = LocalDate.parse(dob);

            int age = Period.between(birthDate, currentDate).getYears();
            proposerDetails.addCell("DOB :   ");
            proposerDetails.addCell(pdfUtility.createDataTable(dob,"00000000"));
            proposerDetails.addCell("Age:  ");
            proposerDetails.addCell(age + " Years");

            proposerMergedcell = new Cell(1, 2);
            pointColumnWidths = new float[]{110F, 500F, 120F, 320F};
            contactTable = new Table(pointColumnWidths);
            contactTable.addCell(new Cell(1, 4).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Marital Status :   ").setVerticalAlignment(VerticalAlignment.MIDDLE));
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

            String residentialstatus = jsonUtility.getJsonKeyValue("residentialstatus", primaryPersonalDetailObj);
            contactTable.addCell(p);
            contactTable
                    .addCell(new Cell().add("  Residental Status:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
            p = new Paragraph();
            p.add("Resident:    ");
            if (residentialstatus.equalsIgnoreCase("RNT")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     NRI:    ");
            if (residentialstatus.equalsIgnoreCase("nri")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     PIO:    ");
            if (residentialstatus.equalsIgnoreCase("pio")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(p);
            proposerMergedcell.add(contactTable);
            proposerDetails.addCell(proposerMergedcell);

            String educationType = jsonUtility.getJsonKeyValue("educationType", primaryEmploymentDetailObj);
            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("Education: ");
            p.add("     Post Grad:   ");
            if (educationType.equalsIgnoreCase("PG") || educationType.equalsIgnoreCase("PA")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Graduate:    ");
            if (educationType.equalsIgnoreCase("GR")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Diploma:     ");
            if (educationType.equalsIgnoreCase("DP")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     12th pass:   ");
            if (educationType.equalsIgnoreCase("12")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     10th pass:   ");
            if (educationType.equalsIgnoreCase("SC")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Below 10th:  ");
            if (educationType.equalsIgnoreCase("10")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Illiterate:  ");
            if (educationType.equalsIgnoreCase("IL")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            proposerMergedcell.add(p);
            proposerDetails.addCell(proposerMergedcell);

            String occupation = jsonUtility.getJsonKeyValue("occupation", primaryEmploymentDetailObj);
            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("Occupation: ");
            p.add("     Salaried:   ");
            if (occupation.equalsIgnoreCase("SALR")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Professional:   ");
            if (occupation.equalsIgnoreCase("PROF")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Self Employed:   ");
            if (occupation.equalsIgnoreCase("SELF")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Student:   ");
            if (occupation.equalsIgnoreCase("STUD")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Housewife:   ");
            if (occupation.equalsIgnoreCase("HSWF")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Retired:   ");
            if (occupation.equalsIgnoreCase("RETD")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Agriculturist:   ");
            if (occupation.equalsIgnoreCase("AGRI")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            Set<String> otherSetOccupation = new HashSet<>();
            otherSetOccupation.add("SALR");
            otherSetOccupation.add("PROF");
            otherSetOccupation.add("SELF");
            otherSetOccupation.add("STUD");
            otherSetOccupation.add("HSWF");
            otherSetOccupation.add("RETD");
            otherSetOccupation.add("AGRI");
            p.add("     Others:   ");
            if (!otherSetOccupation.contains(occupation)) {
                p.add(imgChecked);
                p.add("    ");
                p.add(new Text(occupation).setUnderline());
            } else {
                p.add(imgUnchecked);
            }
            proposerMergedcell.add(p);
            proposerDetails.addCell(proposerMergedcell);

            String industryType = jsonUtility.getJsonKeyValue("industryType", primaryEmploymentDetailObj);
            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("Industry Type:   ");
            p.add("     Jewellery:      ");
            if (industryType.equalsIgnoreCase("Jewellery")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Import/ Export:     ");
            if (industryType.equalsIgnoreCase("Import/ Export")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mining:     ");
            if (industryType.equalsIgnoreCase("Mining worker")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Shipping:   ");
            if (industryType.equalsIgnoreCase("Shipping")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Scrap Dealing:     ");
            if (industryType.equalsIgnoreCase("Scrap Dealing")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Real Estate:    ");
            if (industryType.equalsIgnoreCase("Real Estate")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("    Agriculture:    ");
            if (industryType.equalsIgnoreCase("Agriculture")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Stock Broking:  ");
            if (industryType.equalsIgnoreCase("Stock Broking")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            Set<String> otherSet = new HashSet<>();
            otherSet.add("Stock Broking");
            otherSet.add("Agriculture");
            otherSet.add("Real Estate");
            otherSet.add("Scrap Dealing");
            otherSet.add("Shipping");
            otherSet.add("Mining worker");
            otherSet.add("Import/ Export");
            otherSet.add("Jewellery");

            p.add("     Others:     ");
            if (!otherSet.contains(industryType)) {
                if (industryType.length() > 0) {
                    p.add(imgChecked);
                    p.add("     ");
                    p.add(new Text(industryType).setUnderline());
                } else {
                    p.add(imgUnchecked);
                }
            } else {
                p.add(imgUnchecked);
            }
            proposerMergedcell.add(p);
            proposerDetails.addCell(proposerMergedcell);

            String organizationType = jsonUtility.getJsonKeyValue("organizationType", primaryEmploymentDetailObj);
            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("Organisation Type:   ");
            p.add("     Govt:   ");
            if (organizationType.equalsIgnoreCase("Govt.")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Pvt. Ltd.:      ");
            if (organizationType.equalsIgnoreCase("Pvt. Ltd.")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Public Ltd.:    ");
            if (organizationType.equalsIgnoreCase("Public Ltd.")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Partner/ Proprietor:       ");
            if (organizationType.equalsIgnoreCase("Partner/ Proprietor")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Trust:      ");
            if (organizationType.equalsIgnoreCase("Trust")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     HUF:    ");
            if (organizationType.equalsIgnoreCase("HUF")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Society:    ");
            if (organizationType.equalsIgnoreCase("Society")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            proposerMergedcell.add(p);
            proposerDetails.addCell(proposerMergedcell);

            proposerDetails.addCell("Name of the Org./Business :");
            proposerDetails.addCell(
                    new Paragraph(jsonUtility.getJsonKeyValue("nameOfOrganisation", primaryEmploymentDetailObj))
                            .setUnderline());
            proposerDetails.addCell("Total Years in Service/ Business");
            proposerDetails.addCell(jsonUtility.getJsonKeyValue("experience", primaryEmploymentDetailObj));

            proposerDetails.addCell("Income (Annual): ");
            proposerDetails
                    .addCell(new Paragraph(jsonUtility.getJsonKeyValue("annualIncome", primaryEmploymentDetailObj))
                            .setUnderline());

            proposerDetails.addCell("Source of Income: ");
            proposerDetails.addCell(jsonUtility.getJsonKeyValue("sourceOfIncome", primaryEmploymentDetailObj));

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

            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(6);
            contactTable.addCell("Identity Proof: ");
            contactTable.addCell(identityProof);
            contactTable.addCell("Address Proof:    ");
            contactTable.addCell(addressProof);
            contactTable.addCell("Age Proof:    ");
            contactTable.addCell(ageProof);
            proposerMergedcell.add(contactTable);
            proposerDetails.addCell(proposerMergedcell);

            String isPanEnclosed = " ";

            p = new Paragraph("PAN: ");
            p.add("\n(Please provide Form 60, if PAN is not available)");
            proposerDetails.addCell(p);
            proposerDetails.addCell(jsonUtility.getJsonKeyValue("pancard", primaryPersonalDetailObj));

            String primaryAgeProofDoc = jsonUtility.getJsonKeyValue("documentType",jsonUtility.getJsonObjectByKey("ageProof", primaryDocumentDetailObj));
            String primaryPanCardName = jsonUtility.getJsonKeyValue("name",jsonUtility.getJsonObjectByKey("panCard", primaryDocumentDetailObj));
            Table innerTable=new Table(new float[]{60F,200F});
            String ckycNo = "";
//            p=new Paragraph("CKYC No.:    ");
            innerTable.addCell("CKYC No.:    ");
            innerTable.addCell(pdfUtility.createDataTable(ckycNo,"0000000000"));
            proposerDetails.addCell(new Cell().add(innerTable)).setBorder(Border.NO_BORDER);
            innerTable=new Table(new float[]{90F,300F});
            p = new Paragraph("PAN: ");
            p.add("\n(photocopy Enclosed)");
            innerTable.addCell(p);
            p=new Paragraph();
            if (isOmniDoc) {
                if (primaryAgeDocType.equalsIgnoreCase("PA") || primaryIdDocType.equalsIgnoreCase("PAN CARD") ||
                        primaryAgeProofDoc.equalsIgnoreCase("PA") || !primaryPanCardName.isEmpty()) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
            }else {
                p.add(imgUnchecked);
            }

            innerTable.addCell(p);
            proposerDetails.addCell(new Cell().add(innerTable).setBorder(Border.NO_BORDER));
            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(4);
            contactTable.addCell("Is this policy self proposed?");
            if (myself) {
                contactTable.addCell(imgChecked);
            } else {
                contactTable.addCell(imgUnchecked);
            }
            contactTable.addCell("Relationship with Life to be Assured: ");
            if (myself) {
                contactTable.addCell(new Paragraph("Self").setUnderline());
            } else {
                contactTable.addCell(new Paragraph(buyFor).setUnderline());
            }
            contactTable.addCell("Are you a Politically Exposed Person? 1) Proposer: ");
            if (jsonUtility.getJsonKeyValue("politicallyExposed", primaryEmploymentDetailObj).equalsIgnoreCase("Yes")) {
                contactTable.addCell(imgChecked);
            } else {
                contactTable.addCell(imgUnchecked);
            }
            contactTable.addCell("2) Life to be Assured: ");
            if (jsonUtility.getJsonKeyValue("politicallyExposed", secondaryEmploymentDetailObj)
                    .equalsIgnoreCase("Yes")) {
                contactTable.addCell(imgChecked);
            } else {
                contactTable.addCell(imgUnchecked);
            }
            JsonObject medicalDisabilityObj = jsonUtility.getJsonObjectByKey("disabilityQuestions", primaryOtherDetailObj);
            JsonObject haveDisabilityObj = jsonUtility.getJsonObjectByKey("have_disability", medicalDisabilityObj);
            boolean medicalStatus = jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("y");
            p=new Paragraph("Do you have any disability that restricts you from providing consent/ signature in the proposal form?  ");
            p.add(medicalStatus ? imgChecked : imgUnchecked);
            p.add("   Yes   ");
            boolean medicalStatusNo = jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("n");
            p.add(medicalStatusNo ? imgChecked : imgUnchecked);
            p.add("  No   ");
            contactTable.addCell(new Cell(1,4).add(p).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(contactTable);
            proposerDetails.addCell(proposerMergedcell);
            table.addCell(proposerDetails);
            document.add(table);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph(
                    "Politically Exposed Persons (PEPs) are individuals who are or have been entrusted with prominent public functions in a foreign country, example, Heads of State or of Governments, "
                            +
                            "senior politicians, senior government/judicial/military officials, senior executives of state owned corporations, important political party officials, etc., including their family members and "
                            +
                            "close relatives."));

            String placeOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth",
                    jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            String proposerPlaceOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth",
                    jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));
            contactTable = new Table(4);
            contactTable.addCell("(a) Place of birth: ");
            p = new Paragraph();
            p.add(proposerPlaceOfBirth).setUnderline();
            contactTable.addCell(p);
            contactTable.addCell("   Country of birth: ");
            p = new Paragraph();
            p.add(jsonUtility.getJsonKeyValue("countryOfBirthLabel",
                    jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj))).setUnderline();
            contactTable.addCell(p);

            Table additionalDetails = new Table(2);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            additionalDetails.addCell(proposerMergedcell);
            additionalDetails.addCell("(b) Are you a citizen of any other country also (Dual / Multiple): ");
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            additionalDetails.addCell(p);

            additionalDetails
                    .addCell("(c) Are you a resident (For tax purposes) of any other country other than India.:  ");
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            additionalDetails.addCell(p);

            additionalDetails.addCell("(d) Do you hold a green card of US or any similar card for any other country: ");
            p = new Paragraph();
            if (jsonUtility
                    .getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            additionalDetails.addCell(p);

            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
            proposerMergedcell.add(p);
            p = new Paragraph();
            p.setTextAlignment(TextAlignment.RIGHT);
            p.add(new Text("Application No. : " + userData.get("applicationNumber").getAsString()).setBold());
            proposerMergedcell.add(p);
            additionalDetails.addCell(proposerMergedcell);
            table.addCell(additionalDetails);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("2. Details of the Life to be Assured (Please fill section 2 only if Life to be Assured is different from Proposer)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            Table lifeAssuredDetails = new Table(2);
            lifeAssuredDetails.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)"));

            if (!(myself && jsonUtility.getJsonKeyValue("proposalType", policyHolderBasicDetailObj).equalsIgnoreCase("individual"))) {

                String insuredMaritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", secondaryPersonalDetailObj);
                String insuredGender = jsonUtility.getJsonKeyValue("gender", insuredPersonBasicDetailObj);
                p = new Paragraph();
                p.add("     Mr. ");
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
                lifeAssuredDetails.addCell(new Cell().add(p));

                proposerMergedcell = new Cell(1, 2);
                String lifeAssuredFullName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
                proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName,"00000000000000000000"));
                lifeAssuredDetails.addCell(proposerMergedcell);

                lifeAssuredDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("desc3", contentJson)));
                lifeAssuredDetails.addCell(new Cell().add(innerProposer));

                p=new Paragraph();
                //lifeAssuredDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p .add("\n\nCommunication Address of the Life Assured (Address to which policy document will be dispatched)");
                p.setPaddingBottom(10);
                proposerMergedcell = new Cell(1, 2);

                proposerMergedcell.add(p);
                // proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);

                address1 = jsonUtility.getJsonKeyValue("addressline1", secondaryPersonalDetailObj);

                if (!address1.isEmpty()) {
                    if (address1.length() <= 10) {
                        address1 += "                                        ";
                    } else if (address1.length() > 10 && address1.length() <= 20) {
                        address1 += "                              ";
                    } else {
                        address1 += "                ";
                    }
                }
                address2 = jsonUtility.getJsonKeyValue("addressline2", secondaryPersonalDetailObj);
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
                address3 = jsonUtility.getJsonKeyValue("addressline3", secondaryPersonalDetailObj);
                if (address3.length() > 0) {
                    if (address3.length() <= 10) {
                        address3 += "                                        ";
                    } else if (address3.length() > 10 && address3.length() <= 20) {
                        address3 += "                              ";
                    } else {
                        address3 += "                     ";
                    }
                }
                String landmarkSecondary = jsonUtility.getJsonKeyValue("landmark", secondaryPersonalDetailObj);
                if (landmarkSecondary.length()>0) {
                    if (landmarkSecondary.length() <= 10) {
                        landmarkSecondary += "                                        ";
                    } else if (landmarkSecondary.length() > 10 && landmarkSecondary.length() <= 20) {
                        landmarkSecondary += "                              ";
                    } else {
                        landmarkSecondary += "                ";
                    }
                }
                String permanentlandmarkSec = jsonUtility.getJsonKeyValue("permanentlandmark", secondaryPersonalDetailObj);            if (landmark.length()>0) {
                    if (permanentlandmarkSec.length() <= 10) {
                        permanentlandmarkSec += "                                        ";
                    } else if (permanentlandmarkSec.length() > 10 && permanentlandmarkSec.length() <= 20) {
                        permanentlandmarkSec += "                              ";
                    } else {
                        permanentlandmarkSec += "                ";
                    }
                }

                String secondaryPermanentaddressline1 = jsonUtility.getJsonKeyValue("permanentaddressline1", secondaryPersonalDetailObj);
                if (!secondaryPermanentaddressline1.isEmpty()) {
                    if (secondaryPermanentaddressline1.length() <= 10) {
                        secondaryPermanentaddressline1 += "                                        ";
                    } else if (secondaryPermanentaddressline1.length() > 10 && secondaryPermanentaddressline1.length() <= 20) {
                        secondaryPermanentaddressline1 += "                              ";
                    } else {
                        secondaryPermanentaddressline1 += "                ";
                    }
                }
                String secondaryPermanentaddressline2 = jsonUtility.getJsonKeyValue("permanentaddressline2", secondaryPersonalDetailObj);
                if (secondaryPermanentaddressline2.length() > 0) {
                    if (secondaryPermanentaddressline2.length() <= 10) {
                        secondaryPermanentaddressline2 += "                                        ";
                    } else if (secondaryPermanentaddressline2.length() > 10 && secondaryPermanentaddressline2.length() <= 20) {
                        secondaryPermanentaddressline2 += "                              ";
                    } else if (secondaryPermanentaddressline2.length() > 20 && secondaryPermanentaddressline2.length() <= 30) {
                        secondaryPermanentaddressline2 += "                  ";
                    } else if (secondaryPermanentaddressline2.length() > 30 && secondaryPermanentaddressline2.length() <= 40) {
                        secondaryPermanentaddressline2 += "           ";
                    } else {
                        secondaryPermanentaddressline2 += "";
                    }
                }
                String secondaryPermanentaddressline3 = jsonUtility.getJsonKeyValue("permanentaddressline3", secondaryPersonalDetailObj);
                if (secondaryPermanentaddressline3.length() > 0) {
                    if (secondaryPermanentaddressline3.length() <= 10) {
                        secondaryPermanentaddressline3 += "                                        ";
                    } else if (secondaryPermanentaddressline3.length() > 10 && secondaryPermanentaddressline3.length() <= 20) {
                        secondaryPermanentaddressline3 += "                              ";
                    } else {
                        secondaryPermanentaddressline3 += "                     ";
                    }
                }

                String pincodeSec = jsonUtility.getJsonKeyValue("pincode",secondaryPersonalDetailObj );
                if (pincodeSec.length() > 0) {
                }else {
                    pincodeSec += "      ";
                }


                String permanentcitySecondary = jsonUtility.getJsonKeyValue("permanentcity", secondaryPersonalDetailObj);
                if (permanentcitySecondary.length()>0) {
                    if (permanentcitySecondary.length() <= 10) {
                        permanentcitySecondary += "                                        ";
                    } else if (permanentcitySecondary.length() > 10 && permanentcitySecondary.length() <= 20) {
                        permanentcitySecondary += "                              ";
                    } else {
                        permanentcitySecondary += "                ";
                    }
                }
                String permanentstateSecondary = jsonUtility.getJsonKeyValue("permanentstate", secondaryPersonalDetailObj);
                if (permanentstateSecondary.length()>0) {
                    if (permanentstateSecondary.length() <= 10) {
                        permanentstateSecondary += "                                        ";
                    } else if (permanentstateSecondary.length() > 10 && permanentstateSecondary.length() <= 20) {
                        permanentstateSecondary += "                              ";
                    } else {
                        permanentstateSecondary += "                ";
                    }
                }


                city = jsonUtility.getJsonKeyValue("city", secondaryPersonalDetailObj);
                if (city.length() > 0) {
                    if (city.length() <= 10) {
                        city += "                                        ";
                    } else if (city.length() > 10 && city.length() <= 20) {
                        city += "                              ";
                    } else {
                        city += "                     ";
                    }
                }
                state = jsonUtility.getJsonKeyValue("state", secondaryPersonalDetailObj);
                if (state.length() > 0) {
                    if (state.length() <= 10) {
                        state += "                                        ";
                    } else if (state.length() > 10 && state.length() <= 20) {
                        state += "                              ";
                    } else {
                        state += "                     ";
                    }
                }
                addressBlock = new Table(1);

                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(landmarkSecondary,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
                lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
                addressBlock=new Table(new float[]{600F,100F,200F});
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add("Pin Code:  ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(pincodeSec, "000000")).setBorder(Border.NO_BORDER));
                proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
                lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
                //Permanent Address Section addition started

                p = new Paragraph("Permanent Address (If different from the above Address)");
                p.setPaddingBottom(10);
                proposerMergedcell = new Cell(1, 2);
                proposerMergedcell.add(p);
                // proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);
                addressBlock = new Table(1);
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(secondaryPermanentaddressline1,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(secondaryPermanentaddressline2,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(secondaryPermanentaddressline3,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentlandmarkSec,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentcitySecondary,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentstateSecondary,"00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
                lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
                //Permanent Address Section addition ended

                landLine = "";

                contactTable = new Table(6);
                contactTable.addCell("Country Code");
                contactTable.addCell(pdfUtility.createDataTable(countryCode,"00000000000000"));
                contactTable.addCell("Mobile No:      ");
                contactTable.addCell(pdfUtility.createDataTable(mobileNo,"00000000000000"));

                pinCode = jsonUtility.getJsonKeyValue("permanentpincode", secondaryPersonalDetailObj);
                contactTable.addCell("Pin Code:  ");
                contactTable.addCell(pdfUtility.createDataTable(pinCode,"000000"));
                proposerMergedcell = new Cell(1, 2);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                emailId = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetailObj);
                proposerMergedcell = new Cell(1, 2);
                contactTable = new Table(new float[]{100F,350F,100F,200F});
                contactTable.addCell("Email ID:  ");
                contactTable.addCell(pdfUtility.createDataTable(emailId,"00000000000000"));

                contactTable.addCell("Landline:       ");
                contactTable.addCell(pdfUtility.createDataTable(landLine,"000000000"));

                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);
                ////New section for address ended//////

                proposerMergedcell = new Cell(1, 2);
                contactTable = new Table(new float[]{50F, 350F, 50F, 350F});
                contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph("Male: ");
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
                contactTable.addCell(p);
                contactTable.addCell(new Cell().add("Nationality:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph("Indian:      ");
                if (jsonUtility.getJsonKeyValue("nationality", secondaryPersonalDetailObj).equalsIgnoreCase("Indian")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Non Indian:     ");
                if (!jsonUtility.getJsonKeyValue("nationality", secondaryPersonalDetailObj)
                        .equalsIgnoreCase("Indian")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                contactTable.addCell(p);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String lifeAssuredDoB = jsonUtility.getJsonKeyValue("dateOfBirth", secondaryPersonalDetailObj);
                birthDate = LocalDate.parse(lifeAssuredDoB);
                age = Period.between(birthDate, currentDate).getYears();

                pointColumnWidths = new float[lifeAssuredDoB.length()];
                lifeAssuredDetails.addCell("DOB :   ");
                for (int i = 0; i < lifeAssuredDoB.length(); i++) {
                    pointColumnWidths[i] = 20F;
                }
                if (lifeAssuredDoB.length() > 0) {
                    tableCodes = codeTable(lifeAssuredDoB, pointColumnWidths);
                    lifeAssuredDetails.addCell(tableCodes);
                } else {
                    lifeAssuredDetails.addCell(new Paragraph(""));
                }
                lifeAssuredDetails.addCell("Age:  ");
                lifeAssuredDetails.addCell(age + " Years");

                proposerMergedcell = new Cell(1, 2);
                pointColumnWidths = new float[]{110F, 500F, 120F, 320F};
                contactTable = new Table(pointColumnWidths);
                contactTable
                        .addCell(new Cell().add("Marital Status :   ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph("Unmarried:   ");
                if (insuredMaritalStatus.equalsIgnoreCase("unmarried")
                        || insuredMaritalStatus.equalsIgnoreCase("Single")) {
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
                contactTable.addCell(p);
                contactTable.addCell(
                        new Cell().add("  Residental Status:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
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
                p.add("     PIO:    ");
                if (residentialStatus.equalsIgnoreCase("pio")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                contactTable.addCell(p);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String empSecEducationType = jsonUtility.getJsonKeyValue("educationType", secondaryEmploymentDetailObj);
                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("    Post Grad:    ");
                if (empSecEducationType.equalsIgnoreCase("PG") || empSecEducationType.equalsIgnoreCase("PA")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Graduate:    ");
                if (empSecEducationType.equalsIgnoreCase("GR")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Diploma:     ");
                if (empSecEducationType.equalsIgnoreCase("DP")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     12th pass:   ");
                if (empSecEducationType.equalsIgnoreCase("12")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     10th pass:   ");
                if (empSecEducationType.equalsIgnoreCase("SC")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Below 10th:  ");
                if (empSecEducationType.equalsIgnoreCase("10")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Illiterate:  ");
                if (empSecEducationType.equalsIgnoreCase("IL")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String empSecOccupation = jsonUtility.getJsonKeyValue("occupation", secondaryEmploymentDetailObj);
                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Occupation: ");
                p.add("     Salaried:   ");
                if (empSecOccupation.equalsIgnoreCase("SALR")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Professional:   ");
                if (empSecOccupation.equalsIgnoreCase("PROF")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Self Employed:   ");
                if (empSecOccupation.equalsIgnoreCase("SELF")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Student:   ");
                if (empSecOccupation.equalsIgnoreCase("STUD")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Housewife:   ");
                if (empSecOccupation.equalsIgnoreCase("HSWF")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Retired:   ");
                if (empSecOccupation.equalsIgnoreCase("RETD")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Agriculturist:   ");
                if (empSecOccupation.equalsIgnoreCase("AGRI")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Others:   ");
                if (!otherSetOccupation.contains(empSecOccupation)) {
                    p.add(imgChecked);
                    p.add("    ");
                    p.add(new Text(empSecOccupation).setUnderline());
                } else {
                    p.add(imgUnchecked);
                }
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String empSecIndustryType = jsonUtility.getJsonKeyValue("industryType", secondaryEmploymentDetailObj);
                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Industry Type:   ");
                p.add("     Jewellery:      ");
                if (empSecIndustryType.equalsIgnoreCase("Jewellery")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Import/ Export:     ");
                if (empSecIndustryType.equalsIgnoreCase("Import/ Export")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Mining:     ");
                if (empSecIndustryType.equalsIgnoreCase("Mining worker")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Shipping:   ");
                if (empSecIndustryType.equalsIgnoreCase("Shipping")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Scrap Dealing:     ");
                if (empSecIndustryType.equalsIgnoreCase("Scrap Dealing")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Real Estate:    ");
                if (empSecIndustryType.equalsIgnoreCase("Real Estate")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Agriculture:    ");
                if (empSecIndustryType.equalsIgnoreCase("Agriculture")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Stock Broking:  ");
                if (empSecIndustryType.equalsIgnoreCase("Stock Broking")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                // Set<String> otherSet=new HashSet<>();
                // otherSet.add("Stock Broking");
                // otherSet.add("Agriculture");
                // otherSet.add("Real Estate");
                // otherSet.add("Scrap Dealing");
                // otherSet.add("Shipping");
                // otherSet.add("Mining worker");
                // otherSet.add("Import/ Export");
                // otherSet.add("Jewellery");

                p.add("     Others:     ");
                if (!otherSet.contains(empSecIndustryType)) {
                    if (empSecIndustryType.length() > 0) {
                        p.add(imgChecked);
                        p.add("     ");
                        p.add(new Text(empSecIndustryType).setUnderline());
                    } else {
                        p.add(imgUnchecked);
                    }
                } else {
                    p.add(imgUnchecked);
                }
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String empSecOrganizationType = jsonUtility.getJsonKeyValue("organizationType",
                        secondaryEmploymentDetailObj);
                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Organisation Type:   ");
                p.add("     Govt:   ");
                if (empSecOrganizationType.equalsIgnoreCase("Govt.")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Pvt. Ltd.:      ");
                if (empSecOrganizationType.equalsIgnoreCase("Pvt. Ltd.")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Public Ltd.:    ");
                if (empSecOrganizationType.equalsIgnoreCase("Public Ltd.")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Partner/ Proprietor:       ");
                if (empSecOrganizationType.equalsIgnoreCase("Partner/ Proprietor")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Trust:      ");
                if (empSecOrganizationType.equalsIgnoreCase("Trust")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     HUF:    ");
                if (empSecOrganizationType.equalsIgnoreCase("HUF")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Society:    ");
                if (empSecOrganizationType.equalsIgnoreCase("Society")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                lifeAssuredDetails.addCell("Name of the Org./Business :");
                lifeAssuredDetails.addCell(new Paragraph(
                        jsonUtility.getJsonKeyValue("nameOfOrganisation", secondaryEmploymentDetailObj))
                        .setUnderline());
                lifeAssuredDetails.addCell("Total Years in Service/ Business");
                lifeAssuredDetails.addCell(jsonUtility.getJsonKeyValue("experience", secondaryEmploymentDetailObj));

                lifeAssuredDetails.addCell("Income (Annual): ");
                lifeAssuredDetails.addCell(
                        new Paragraph(jsonUtility.getJsonKeyValue("annualIncome", secondaryEmploymentDetailObj))
                                .setUnderline());

                lifeAssuredDetails.addCell("Source of Income: ");
                lifeAssuredDetails
                        .addCell(jsonUtility.getJsonKeyValue("sourceOfIncome", secondaryEmploymentDetailObj));

                String secondaryAgeDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof-all", secondaryDocumentDetailObj));
                String secondaryAddressDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("addressProof", secondaryDocumentDetailObj));
                String secondaryIdDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("idProof", secondaryDocumentDetailObj));

                String lifeAssuredIdentityProof = "";
                String lifeAssuredAddressProof = "";
                String lifeAssuredAgeProof = "";

                if(isOmniDoc && idProofMap.containsKey(secondaryIdDocType)){
                    lifeAssuredIdentityProof=idProofMap.get(secondaryIdDocType);
                }
                if(isOmniDoc && ageProofMap.containsKey(secondaryAgeDocType)){
                    lifeAssuredAgeProof=ageProofMap.get(secondaryAgeDocType);
                }
                if(isOmniDoc && addressProofMap.containsKey(secondaryAddressDocType)){
                    lifeAssuredAddressProof=addressProofMap.get(secondaryAddressDocType);
                }

                proposerMergedcell = new Cell(1, 2);
                contactTable = new Table(6);
                contactTable.addCell("Identity Proof: ");
                contactTable.addCell(lifeAssuredIdentityProof);
                contactTable.addCell("Address Proof:    ");
                contactTable.addCell(lifeAssuredAddressProof);
                contactTable.addCell("Age Proof:    ");
                contactTable.addCell(lifeAssuredAgeProof);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String lifeAssuredIsPanEnclosed = "";

                p = new Paragraph("PAN: ");
                p.add("\n(Please provide Form 60, if PAN is not available)");
                lifeAssuredDetails.addCell(p);
                lifeAssuredDetails.addCell(jsonUtility.getJsonKeyValue("pancard", secondaryPersonalDetailObj));

                String secondaryAgeProofDoc = jsonUtility.getJsonKeyValue("documentType",jsonUtility.getJsonObjectByKey("ageProof", secondaryDocumentDetailObj));
                String secondaryPanCardName = jsonUtility.getJsonKeyValue("name",jsonUtility.getJsonObjectByKey("panCard", secondaryDocumentDetailObj));
                p = new Paragraph("PAN: ");
                p.add("\n(photocopy Enclosed)");
                lifeAssuredDetails.addCell(p);
                p=new Paragraph();
                if (isOmniDoc) {
                    if (secondaryAgeDocType.equalsIgnoreCase("PA") || secondaryIdDocType.equalsIgnoreCase("PAN CARD") ||
                            secondaryAgeProofDoc.equalsIgnoreCase("PA") || !secondaryPanCardName.isEmpty()) {
                        p.add(imgChecked);
                    } else {
                        p.add(imgUnchecked);
                    }
                }else {
                    p.add(imgUnchecked);
                }
                lifeAssuredDetails.addCell(p);
                table.addCell(lifeAssuredDetails);
                document.add(table);

                table = new Table(1);
                table.setWidthPercent(100);
                headingCell = new Cell();
                headingCell.setBackgroundColor(Color.GRAY, 100);
                p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
                p.setBold();
                p.setFontKerning(FontKerning.YES);
                p.setFontColor(Color.WHITE);
                headingCell.add(p);
                table.addCell(headingCell);

                String lifeAssuredPlaceOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth",
                        jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaDetailObj));
                contactTable = new Table(4);
                contactTable.addCell("(a) Place of birth: ");
                p = new Paragraph();
                p.add(lifeAssuredPlaceOfBirth).setUnderline();
                contactTable.addCell(p);
                contactTable.addCell("   Country of birth: ");
                p = new Paragraph();
                p.add(jsonUtility.getJsonKeyValue("countryOfBirthLabel",
                        jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaDetailObj))).setUnderline();
                contactTable.addCell(p);

                Table lifeAdditionalDetails = new Table(2);
                proposerMergedcell = new Cell(1, 2);
                proposerMergedcell.add(contactTable);
                lifeAdditionalDetails.addCell(proposerMergedcell);
                lifeAdditionalDetails.addCell("(b) Are you a citizen of any other country also (Dual / Multiple): ");
                p = new Paragraph();
                if (jsonUtility
                        .getJsonKeyValue("status",
                                jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaDetailObj))
                        .equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (!jsonUtility
                        .getJsonKeyValue("status",
                                jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaDetailObj))
                        .equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No      ");
                lifeAdditionalDetails.addCell(p);

                lifeAdditionalDetails
                        .addCell("(c) Are you a resident (For tax purposes) of any other country other than India.:  ");
                p = new Paragraph();
                if (jsonUtility.getJsonKeyValue("status",
                                jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaDetailObj))
                        .equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (!jsonUtility.getJsonKeyValue("status",
                                jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaDetailObj))
                        .equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No      ");
                lifeAdditionalDetails.addCell(p);

                lifeAdditionalDetails
                        .addCell("(d) Do you hold a green card of US or any similar card for any other country: ");
                p = new Paragraph();
                if (jsonUtility
                        .getJsonKeyValue("status",
                                jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaDetailObj))
                        .equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     Yes     ");
                if (!jsonUtility
                        .getJsonKeyValue("status",
                                jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaDetailObj))
                        .equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No      ");
                lifeAdditionalDetails.addCell(p);

                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
                proposerMergedcell.add(p);
                lifeAdditionalDetails.addCell(proposerMergedcell);
                table.addCell(lifeAdditionalDetails);
                document.add(table);

            } else {
                p = new Paragraph("Mr.  ");
                p.add(imgUnchecked);
                p.add("     Mrs. ");
                p.add(imgUnchecked);
                p.add("     Ms.  ");
                p.add(imgUnchecked);
                p.add("     Mx.  ");
                p.add(imgUnchecked);
                lifeAssuredDetails.addCell(new Cell().add(p));

                proposerMergedcell = new Cell(1, 2);
                String lifeAssuredFullName = "";

                proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName,"00000000000000000000"));
                lifeAssuredDetails.addCell(proposerMergedcell);

                lifeAssuredDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("desc4", contentJson)));
                lifeAssuredDetails.addCell(new Cell().add(innerProposer));

                p=new Paragraph();
                p .add("Communication Address of the Proposer (Address to which policy document will be dispatched)");
                p.setPaddingBottom(10);
                proposerMergedcell = new Cell(1, 2);

                proposerMergedcell.add(p);
                addressBlock = new Table(1);

                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
                lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

                //Permanent Address Section addition started

                p = new Paragraph("Permanent Address (If different from the above Address)");
                p.setPaddingBottom(10);
                proposerMergedcell = new Cell(1, 2);
                proposerMergedcell.add(p);
                // proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);
                addressBlock = new Table(1);
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","00000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
                proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
                lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
                //Permanent Address Section addition ended

                contactTable = new Table(6);
                contactTable.addCell("Country Code");
                contactTable.addCell(pdfUtility.createDataTable("","00"));
                contactTable.addCell("Mobile No:      ");
                contactTable.addCell(pdfUtility.createDataTable("","00000000000000"));

                contactTable.addCell("Pin Code:  ");
                contactTable.addCell(pdfUtility.createDataTable("","000000"));
                proposerMergedcell = new Cell(1, 2);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                proposerMergedcell = new Cell(1, 2);
                contactTable = new Table(new float[]{100F,350F,100F,200F});
                contactTable.addCell("Email ID:  ");
                contactTable.addCell(pdfUtility.createDataTable("","000000000000"));

                contactTable.addCell("Landline:       ");
                contactTable.addCell(pdfUtility.createDataTable("","000000000"));

                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);
                ////New section for address ended//////

                proposerMergedcell = new Cell(1, 2);
                pointColumnWidths = new float[]{50F, 350F, 50F, 350F};
                contactTable = new Table(pointColumnWidths);
                contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph("Male: ");
                p.add(imgUnchecked);
                p.add("     Female:     ");
                p.add(imgUnchecked);
                p.add("     Transgender:    ");
                p.add(imgUnchecked);
                contactTable.addCell(p);
                contactTable.addCell(new Cell().add("Nationality:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph("Indian:      ");
                p.add(imgUnchecked);
                p.add("     Non Indian:     ");
                p.add(imgUnchecked);
                contactTable.addCell(p);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String lifeaAssuredDoB = "";
                String lifeAssuredAge = "";

                pointColumnWidths = new float[lifeaAssuredDoB.length()];
                lifeAssuredDetails.addCell("DOB :   ");
                if (lifeaAssuredDoB.length() > 0) {
                    tableCodes = codeTable(lifeaAssuredDoB, pointColumnWidths);
                    lifeAssuredDetails.addCell(tableCodes);
                } else {
                    lifeAssuredDetails.addCell(new Paragraph(""));
                }
                lifeAssuredDetails.addCell("Age:  ");
                lifeAssuredDetails.addCell(lifeAssuredAge + " Years");

                proposerMergedcell = new Cell(1, 2);
                pointColumnWidths = new float[]{110F, 500F, 120F, 320F};
                contactTable = new Table(pointColumnWidths);
                contactTable
                        .addCell(new Cell().add("Marital Status :   ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph("Unmarried:   ");
                p.add(imgUnchecked);
                p.add("     Married:  ");
                p.add(imgUnchecked);
                p.add("     Widow(er):  ");
                p.add(imgUnchecked);
                p.add("     Divorced:   ");
                p.add(imgUnchecked);
                contactTable.addCell(p);
                contactTable.addCell(
                        new Cell().add("  Residental Status:    ").setVerticalAlignment(VerticalAlignment.MIDDLE));
                p = new Paragraph();
                p.add("Resident:    ");
                p.add(imgUnchecked);
                p.add("     NRI:    ");
                p.add(imgUnchecked);
                p.add("     PIO:    ");
                p.add(imgUnchecked);
                contactTable.addCell(p);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Education: ");
                p.add("     Post Grad:   ");
                p.add(imgUnchecked);
                p.add("     Graduate:    ");
                p.add(imgUnchecked);
                p.add("     Diploma:     ");
                p.add(imgUnchecked);
                p.add("     12th pass:   ");
                p.add(imgUnchecked);
                p.add("     10th pass:   ");
                p.add(imgUnchecked);
                p.add("     Below 10th:  ");
                p.add(imgUnchecked);
                p.add("     Illiterate:  ");
                p.add(imgUnchecked);
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Occupation: ");
                p.add("     Salaried:   ");
                p.add(imgUnchecked);
                p.add("     Professional:   ");
                p.add(imgUnchecked);
                p.add("     Self Employed:   ");
                p.add(imgUnchecked);
                p.add("     Student:   ");
                p.add(imgUnchecked);
                p.add("     Housewife:   ");
                p.add(imgUnchecked);
                p.add("     Retired:   ");
                p.add(imgUnchecked);
                p.add("     Agriculturist:   ");
                p.add(imgUnchecked);
                p.add("     Others:   ");
                p.add(imgUnchecked);
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Industry Type:   ");
                p.add("     Jewellery:      ");
                p.add(imgUnchecked);
                p.add("     Import/ Export:     ");
                p.add(imgUnchecked);
                p.add("     Mining:     ");
                p.add(imgUnchecked);
                p.add("     Shipping:   ");
                p.add(imgUnchecked);
                p.add("     Scrap Dealing:     ");
                p.add(imgUnchecked);
                p.add("     Real Estate:    ");
                p.add(imgUnchecked);
                p.add("     \nAgriculture:    ");
                p.add(imgUnchecked);
                p.add("     Stock Broking:  ");
                p.add(imgUnchecked);
                p.add("     Others:     ");
                p.add(imgUnchecked);
                p.add("     ");
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("Organisation Type:   ");
                p.add("     Govt:   ");
                p.add(imgUnchecked);
                p.add("     Pvt. Ltd.:      ");
                p.add(imgUnchecked);
                p.add("     Public Ltd.:    ");
                p.add(imgUnchecked);
                p.add("     Partner/ Proprietor:       ");
                p.add(imgUnchecked);
                p.add("     Trust:      ");
                p.add(imgUnchecked);
                p.add("     HUF:    ");
                p.add(imgUnchecked);
                p.add("     Society:    ");
                p.add(imgUnchecked);
                proposerMergedcell.add(p);
                lifeAssuredDetails.addCell(proposerMergedcell);

                String lifeAssuredNameOfOrg = "";
                String lifeAssuredYearsInService = "";
                String lifeAssuredIncome = "";
                String lifeAssuredSourceOfIncome = "";

                lifeAssuredDetails.addCell("Name of the Org./Business :");
                lifeAssuredDetails.addCell(new Paragraph(lifeAssuredNameOfOrg).setUnderline());
                lifeAssuredDetails.addCell("Total Years in Service/ Business");
                lifeAssuredDetails.addCell(lifeAssuredYearsInService);

                lifeAssuredDetails.addCell("Income (Annual): ");
                lifeAssuredDetails.addCell(new Paragraph(lifeAssuredIncome).setUnderline());

                lifeAssuredDetails.addCell("Source of Income: ");
                lifeAssuredDetails.addCell(lifeAssuredSourceOfIncome);

                String lifeAssuredIdentityProof = "";
                String lifeAssuredAddressProof = "";
                String lifeAssuredAgeProof = "";

                proposerMergedcell = new Cell(1, 2);
                contactTable = new Table(6);
                contactTable.addCell("Identity Proof: ");
                contactTable.addCell(lifeAssuredIdentityProof);
                contactTable.addCell("Address Proof:    ");
                contactTable.addCell(lifeAssuredAddressProof);
                contactTable.addCell("Age Proof:    ");
                contactTable.addCell(lifeAssuredAgeProof);
                proposerMergedcell.add(contactTable);
                lifeAssuredDetails.addCell(proposerMergedcell);

                p = new Paragraph("PAN: ");
                p.add("\n(Please provide Form 60, if PAN is not available)");
                lifeAssuredDetails.addCell(p);
                lifeAssuredDetails.addCell("");

                p = new Paragraph("PAN: ");
                p.add("\n(photocopy Enclosed)");
                lifeAssuredDetails.addCell(p);
                lifeAssuredDetails.addCell(imgUnchecked);
                table.addCell(lifeAssuredDetails);
                document.add(table);

                table = new Table(1);
                table.setWidthPercent(100);
                headingCell = new Cell();
                headingCell.setBackgroundColor(Color.GRAY, 100);
                p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
                p.setBold();
                p.setFontKerning(FontKerning.YES);
                p.setFontColor(Color.WHITE);
                headingCell.add(p);
                table.addCell(headingCell);

                String lifeAssuredPlaceOfBirth = "";
                String lifeAssuredCountryOfBirth = "";

                contactTable = new Table(4);
                contactTable.addCell("(a) Place of birth: ");
                p = new Paragraph();
                p.add(lifeAssuredPlaceOfBirth).setUnderline();
                contactTable.addCell(p);
                contactTable.addCell("   Country of birth: ");
                p = new Paragraph();
                p.add(lifeAssuredCountryOfBirth).setUnderline();
                contactTable.addCell(p);

                Table lifeAdditionalDetails = new Table(2);
                proposerMergedcell = new Cell(1, 2);
                proposerMergedcell.add(contactTable);
                lifeAdditionalDetails.addCell(proposerMergedcell);
                lifeAdditionalDetails.addCell("(b) Are you a citizen of any other country also (Dual / Multiple): ");
                p = new Paragraph();
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No      ");
                lifeAdditionalDetails.addCell(p);

                lifeAdditionalDetails
                        .addCell("(c) Are you a resident (For tax purposes) of any other country other than India.:  ");
                p = new Paragraph();
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No      ");
                lifeAdditionalDetails.addCell(p);

                lifeAdditionalDetails
                        .addCell("(d) Do you hold a green card of US or any similar card for any other country: ");
                p = new Paragraph();
                p.add(imgUnchecked);
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No      ");
                lifeAdditionalDetails.addCell(p);

                proposerMergedcell = new Cell(1, 2);
                p = new Paragraph();
                p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
                proposerMergedcell.add(p);
                lifeAdditionalDetails.addCell(proposerMergedcell);
                table.addCell(lifeAdditionalDetails);
                document.add(table);
            }

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("3. Nominee/ Appointee Details (To be filled in case life to be assured and proposer are same. Appointee details required only if nominee is a minor)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

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
                age = Period.between(birthDate, currentDate).getYears();
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
            table.addCell(nomineeDetails);

            //New section started///
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
                    age = Period.between(birthDate, LocalDate.now()).getYears();
                    appointeeDetails.addCell(String.valueOf(age));
                    appointeeDetails.addCell(jsonUtility.getJsonKeyValue("appointeegender", objNom));
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
            table.addCell(new Cell().add(appointeeDetails));
            //New section ended///
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("4. Plan Details");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            Table planDetails = new Table(new float[]{330F, 230F, 150F, 150F, 150F,150F});
            Cell planNameCell = new Cell();
            planNameCell.add(new Paragraph("Plan Name"));
            planDetails.addCell(planNameCell);

            planDetails.addCell("Plan Option");
            planDetails.addCell("Policy Term");
            planDetails.addCell("Premium Paying Term");
            planDetails.addCell("Installment Premium");
            planDetails.addCell("Sum Assured");

            planDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("productName", quotePlanSectionDetails)));
            planDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("planOption", planDetailObj)).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("planTerm", quotePlanSectionDetails)).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("premiumPayingTerm", quotePlanSectionDetails)).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("installmentPremium", quotePlanSectionDetails)).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("sumAssured", quotePlanSectionDetails)).setTextAlignment(TextAlignment.CENTER));

            String planName=jsonUtility.getJsonKeyValue("desc5", contentJson);
            String planOption="";
            String planTerm="";
            String premiumPlayingTerm="";
            String premiumInstallment="";
            String sumAssured="";
            planDetails.addCell(planName);
            planDetails.addCell(new Cell().add(planOption).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));

            planName = jsonUtility.getJsonKeyValue("desc6", contentJson);
            planOption="";
            planTerm = "";
            premiumPlayingTerm = "";
            premiumInstallment = "";
            sumAssured = "";

            planDetails.addCell(planName);
            planDetails.addCell(new Cell().add(planOption).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));

            String premiumFrequency = jsonUtility.getJsonKeyValue("premiumFrequency", quotePlanSectionDetails);
            proposerMergedcell = new Cell(1, 6);
            p = new Paragraph();
            p.add("Premium Frequency:   ");
            if (premiumFrequency.equalsIgnoreCase("single")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Single      ");
            if (premiumFrequency.equalsIgnoreCase("yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yearly      ");
            if (premiumFrequency.equalsIgnoreCase("half yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Half Yearly      ");
            if (premiumFrequency.equalsIgnoreCase("quarterly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Quarterly      ");
            if (premiumFrequency.equalsIgnoreCase("ecs")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     # Monthly (Only ECS/ Direct debit).      ");
            proposerMergedcell.add(p);
            planDetails.addCell(proposerMergedcell);

            proposerMergedcell = new Cell(1, 6);
            p = new Paragraph();
            p.add("# ECS/DD with cancel cheque copy and DD mandate should be verified by bank branch             ");
            proposerMergedcell.add(p);
            p = new Paragraph();
            p.add("     Renewal Premium Payment Options: 1. Standing Instructions         ");
            p.add(imgUnchecked);
            p.add("     2. Cheque      ");
            p.add(imgUnchecked);
            proposerMergedcell.add(p);
            planDetails.addCell(proposerMergedcell);
            table.addCell(planDetails);

            Table deathBenefitOption = new Table(2);
            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add(new Text("Death Benefit Option:            ").setBold());
            p.add(imgChecked);
            p.add("    Lump Sum    ");
            p.add(imgUnchecked);
            p.add("    Income (5 Years)    ");
            proposerMergedcell.add(p);
            deathBenefitOption.addCell(proposerMergedcell);

            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add(new Text("Systematic Partial Withdrawal Option:            ").setBold());
            p.add(imgUnchecked);
            p.add("    Yes    ");
            p.add(imgChecked);
            p.add("    No    ");
            p.add("    If yes 1) Percentage of withdrawal (Between 0% - 20%):       ");
            p.add(new Text(" ").setUnderline());
            proposerMergedcell.add(p);
            deathBenefitOption.addCell(proposerMergedcell);

            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("2) Frequency:            ");
            if (premiumFrequency.equalsIgnoreCase("yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("    Yearly    ");
            if (premiumFrequency.equalsIgnoreCase("half yearly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("    Half Yearly    ");
            if (premiumFrequency.equalsIgnoreCase("quarterly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("    Quarterly    ");
            if (premiumFrequency.equalsIgnoreCase("monthly")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("    Monthly    ");
            if (premiumFrequency.equalsIgnoreCase("pop")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("    3) From Policy Year 0 to Policy Year 0    ");
            proposerMergedcell.add(p);
            p = new Paragraph();
            p.add("Please select either an investment strategy or the fund options in which you want to invest your premiums.");
            proposerMergedcell.add(p);
            deathBenefitOption.addCell(proposerMergedcell);

            String investmentId = jsonUtility.getJsonKeyValue("id", investmentDetailObj);
            Table investmentStrategy = new Table(new float[]{300F,100F,300F,110F});
            investmentStrategy.setTextAlignment(TextAlignment.CENTER);
            investmentStrategy.addCell("I. Automatic Trigger Based Investment Strategy (ATBIS): ");
            if (investmentId.equalsIgnoreCase("2")) {
                investmentStrategy.addCell(imgChecked);
            } else {
                investmentStrategy.addCell(imgUnchecked);
            }
            investmentStrategy.addCell("II. Fund Transfer Strategy: ");
            if (investmentId.equalsIgnoreCase("6")) {
                investmentStrategy.addCell(imgChecked);
            } else {
                investmentStrategy.addCell(imgUnchecked);
            }
            investmentStrategy.addCell("III. Age Based Investment Strategy: ");
            if (investmentId.equalsIgnoreCase("1")) {
                investmentStrategy.addCell(imgChecked);
            } else {
                investmentStrategy.addCell(imgUnchecked);
            }
            investmentStrategy.addCell("IV. Defined Allocation Strategy: ");
            if (investmentId.equalsIgnoreCase("3")) {
                investmentStrategy.addCell(imgChecked);
            } else {
                investmentStrategy.addCell(imgUnchecked);
            }
            investmentStrategy.addCell("V. Smart Switch Strategy: ");
            if (investmentId.equalsIgnoreCase("4")) {
                investmentStrategy.addCell(imgChecked);
            } else {
                investmentStrategy.addCell(imgUnchecked);
            }
            investmentStrategy.addCell("VI. Self Managed Strategy: ");
            if (investmentId.equalsIgnoreCase("5")) {
                investmentStrategy.addCell(imgChecked);
            } else {
                investmentStrategy.addCell(imgUnchecked);
            }
            proposerMergedcell = new Cell(1, 4);
            p = new Paragraph();
            p.add(new Text("Fund Options (Total Allocation to be 100% across all selected funds)").setBold());
            p.setTextAlignment(TextAlignment.CENTER);
            proposerMergedcell.add(p);
            investmentStrategy.addCell(proposerMergedcell);

            JsonArray fundsDetailsArray = new JsonArray();
            if (investmentDetailObj.has("funds")) {
                fundsDetailsArray = investmentDetailObj.get("funds").getAsJsonArray();
            }

            investmentStrategy.addCell(new Paragraph(new Text("Fund Name (SFIN No)").setBold()));
            investmentStrategy.addCell(new Paragraph(new Text("%").setBold()));
            investmentStrategy.addCell(new Paragraph(new Text("Fund Name (SFIN No)").setBold()));
            investmentStrategy.addCell(new Paragraph(new Text("%").setBold()));

            Map<String, String> stringIntegerMap = new HashMap<>();
            stringIntegerMap.put("Equity1Fund", "Equity1 (ULIF009010910EQUTY1FUND143)");
            stringIntegerMap.put("Debt1Fund", "Debt1 (ULIF010010910DEBT01FUND143)");
            stringIntegerMap.put("IndexTrackerFund", "Index Tracker (ULIF012010910INDTRAFUND143)");
            stringIntegerMap.put("EquityEliteOpportunitiesFund", "Equity Elite Opportunities (ULIF020280716EQUELITEOP143)");
            stringIntegerMap.put("DynamicAssetAllocation", "Dynamic Asset Allocation (ULIF015080811DYAALLFUND143)");
            stringIntegerMap.put("ValueFund", "Value (ULIF013010910VALUEFUND0143)");
            stringIntegerMap.put("SustainableEquityFund", "Sustainable Equity Fund (ULIF02221/02/22SUSTEQUFND143)");
            stringIntegerMap.put("Balanced1Fund", "Balanced1 (ULIF011010910BALAN1FUND143)");
            stringIntegerMap.put("flexiCapEquityFund", "Flexi Cap Equity Fund (ULIF02121/02/22FLEXCAPFND143)");
            stringIntegerMap.put("liquid1Fund", "Liquid1(ULIF014010910LIQUID1FND143)");

            Map<String, Integer> fundAllocationMap = new HashMap<>();
            for (JsonElement eachElement : fundsDetailsArray) {
                JsonObject eachFundObj = eachElement.getAsJsonObject();
                String allocationValue = jsonUtility.getJsonKeyValue("allocation", eachFundObj);
                if(allocationValue.isEmpty()){
                    allocationValue = "0";
                }
                String fundId = jsonUtility.getJsonKeyValue("id", eachFundObj);
                fundAllocationMap.put(fundId, Integer.valueOf(allocationValue));

            }
            for (String key : stringIntegerMap.keySet()) {
                if (fundAllocationMap.containsKey(key)) {
                    investmentStrategy.addCell(stringIntegerMap.get(key));
                    investmentStrategy.addCell(fundAllocationMap.get(key).toString());
                } else {
                    investmentStrategy.addCell(stringIntegerMap.get(key));
                    investmentStrategy.addCell("0");
                }
            }
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(investmentStrategy);
            deathBenefitOption.addCell(proposerMergedcell);
            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add(new Text("For Fund Transfer Strategy , please select one Equity Oriented Fund and one Debt Oriented Fund.").setBold());
            p.add(new Text("\nATBIS").setBold());
            p.add(" -  Select any one Equity Oriented Fund");
            p.add(new Text("\nDefined Allocation Strategy").setBold());
            p.add(" -  Select any 4 fund options to invest in & specify the allocation for each of these selected funds.");
            p.add(new Text("\nSelf Managed Strategy").setBold());
            p.add(" -  You may choose one or all fund options from listed to invest in");
            p.add(new Text("\nAt any given point you can select only one strategy").setBold());
            proposerMergedcell.add(p);
            deathBenefitOption.addCell(proposerMergedcell);
            table.addCell(deathBenefitOption);
            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(jsonUtility.getJsonKeyValue("content1", contentJson)));
            p = new Paragraph();
            p.add(new Text("Third Party payment:").setBold());
            p.add(jsonUtility.getJsonKeyValue("content2", contentJson));
            document.add(p);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("5. Benefit Payment Mode (Choose any one mode only)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            Table bankDetails = this.getBankDetails(imgUnchecked, imgChecked,primaryBankObj,eMandateObj,nomineeList,contentJson);
            table.addCell(new Cell().add(bankDetails));
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("6. Life to be Assured’s Family History (Please tick Yes or No)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            JsonObject familySufferObj = jsonUtility.getJsonObjectByKey("familySuffer", primarySecondaryMedicalObj);
            p = new Paragraph(
                    "Have either of your parents or any brothers or sisters suffered from or died due to any of the following conditions: Heart problems, diabetes, stroke, hypertension, raised cholesterol, cancer, or "
                            +
                            "any hereditary disease? If yes, please give full details below:    ");
            if (jsonUtility.getJsonKeyValue("status", familySufferObj).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", familySufferObj).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            table.addCell(p);

            JsonArray familyHistoryList = new JsonArray();
            Table familyHistory = new Table(5);
            familyHistory.addCell(new Paragraph("Family Members").setBold());
            familyHistory.addCell(new Paragraph("Age").setBold());
            familyHistory.addCell(new Paragraph("If Alive, Illness, if any").setBold());
            familyHistory.addCell(new Paragraph("Age").setBold());
            familyHistory.addCell(new Paragraph("If Deceased, exact cause of Death").setBold());

            if (familySufferObj.has("members") && familySufferObj.get("members").getAsJsonArray().size() > 0) {
                familyHistoryList = familySufferObj.get("members").getAsJsonArray();
            }
            for (JsonElement element : familyHistoryList) {
                JsonObject objNom = element.getAsJsonObject();
                familyHistory.addCell(jsonUtility.getJsonKeyValue("releationship", objNom));
                familyHistory.addCell(jsonUtility.getJsonKeyValue("age", objNom));
                String deceasedStatus = jsonUtility.getJsonKeyValue("deceased", objNom);
                if (deceasedStatus.equalsIgnoreCase("n")) {
                    familyHistory.addCell(jsonUtility.getJsonKeyValue("illness", objNom) + ", " +
                            jsonUtility.getJsonKeyValue("specifyIllness", objNom));
                } else {
                    familyHistory.addCell(new Paragraph(""));
                }
                familyHistory.addCell(jsonUtility.getJsonKeyValue("age", objNom));
                if (deceasedStatus.equalsIgnoreCase("y")) {
                    familyHistory.addCell(jsonUtility.getJsonKeyValue("illness", objNom) + ", " +
                            jsonUtility.getJsonKeyValue("specifyIllness", objNom));
                } else {
                    familyHistory.addCell(new Paragraph(""));
                }
            }
            table.addCell(familyHistory);
            document.add(table);

            String totalSumInsured = "";
            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("7. Proposer’s Insurance Details (Applicable to minor lives and housewives)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);
            Table insuranceDetails = new Table(2);
            insuranceDetails.addCell("Parents’/ Husband’s insurance details - total sum insured (Rs.): ");
            pointColumnWidths = new float[totalSumInsured.length()];
            for (int i = 0; i < totalSumInsured.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (totalSumInsured.length() > 0) {
                tableCodes = codeTable(totalSumInsured, pointColumnWidths);
                insuranceDetails.addCell(tableCodes);
            } else {
                insuranceDetails.addCell(new Paragraph(""));
            }

            table.addCell(insuranceDetails);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("desc7", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content2", contentJson));

            String appliedInsuranceStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("appliedInsurance", primaryOtherDetailObj));
            if (appliedInsuranceStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (appliedInsuranceStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            table.addCell(p);

            JsonArray insuranceHeld = new JsonArray();
            JsonObject obj = new JsonObject();
            if (appliedInsuranceStatus.equalsIgnoreCase("y")) {
                obj.addProperty("name", "Refer Previous Insurance data sheet for detail");
                obj.addProperty("companyName", "");
                obj.addProperty("policyNo", "");
                obj.addProperty("annualPremium", "");
                obj.addProperty("sumAssured", "");
                obj.addProperty("yearOfCommencement", "");
                obj.addProperty("status", "");
            } else {
                obj.addProperty("name", "NA");
                obj.addProperty("companyName", "NA");
                obj.addProperty("policyNo", "NA");
                obj.addProperty("annualPremium", "NA");
                obj.addProperty("sumAssured", "NA");
                obj.addProperty("yearOfCommencement", "NA");
                obj.addProperty("status", "NA");
            }

            insuranceHeld.add(obj);

            Table insurancePolicies = new Table(7);
            insurancePolicies.addCell("Name of Life to be Assured/ Proposer");
            insurancePolicies.addCell("Name of the Company");
            insurancePolicies.addCell("Policy/Proposal No.");
            insurancePolicies.addCell("Annual Premium");
            insurancePolicies.addCell("Sum Assured including riders");
            insurancePolicies.addCell("Year of Commencement");
            insurancePolicies.addCell("Present Status and Terms of Acceptance");

            for (JsonElement element : insuranceHeld) {
                JsonObject objNom = element.getAsJsonObject();
                insurancePolicies.addCell(objNom.get("name").getAsString());
                insurancePolicies.addCell(objNom.get("companyName").getAsString());
                insurancePolicies.addCell(objNom.get("policyNo").getAsString());
                insurancePolicies.addCell(objNom.get("annualPremium").getAsString());
                insurancePolicies.addCell(objNom.get("sumAssured").getAsString());
                insurancePolicies.addCell(objNom.get("yearOfCommencement").getAsString());
                insurancePolicies.addCell(objNom.get("status").getAsString());
                // p = new Paragraph();
                // p.add(imgUnchecked);
                // p.add(" Standard\n");
                // p.add(imgUnchecked);
                // p.add(" Rated up\n");
                // p.add(imgUnchecked);
                // p.add(" Declined\n");
                // p.add(imgUnchecked);
                // p.add(" Postponed\n");
                // p.add(imgUnchecked);
                // p.add(" Lapsed\n");
                // p.add(imgUnchecked);
                // p.add(" Rejected\n");
                // insurancePolicies.addCell(p);
            }
            table.addCell(insurancePolicies);
            p = new Paragraph();
            p.add("Additional sheets with relevant details signed by the life to be assured may be added if space is insufficient.");
            table.addCell(p);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(
                    "9. Lifestyle questions and personal medical history of the Life to be Assured (If 'Yes', please encircle the activity/ ailment/ disease)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);
            p = new Paragraph();
            p.add("Non disclosures or misrepresentation of facts will highly impact claim settlement").setBold();
            table.addCell(p);

            JsonObject heightObj = jsonUtility.getJsonObjectByKey("height", primarysecodaryHealthDetailObj);
            Table medicalLifestyleQ = new Table(new float[]{750F, 150F});
            String heightInCm = "";
            try {
                heightInCm = this.feetAndInchesToCms(Double.parseDouble(jsonUtility.getJsonKeyValue("feet", heightObj)),
                        Double.parseDouble(jsonUtility.getJsonKeyValue("inch", heightObj)));
            } catch (Exception e) {
                logger.info("");
            }
            p = new Paragraph();
            p.add("a. Height in cm:     ");
            p.add(new Text(heightInCm).setUnderline());
            p.add("     / Feet  ");
            p.add(new Text("  " + jsonUtility.getJsonKeyValue("feet", heightObj) + "  ").setUnderline());
            p.add("     inches:     ");
            p.add(new Text("  " + jsonUtility.getJsonKeyValue("inch", heightObj) + "  ").setUnderline());
            p.add("     Weight in kg:   ");
            p.add(new Text("  " + jsonUtility.getJsonKeyValue("weight", primarysecodaryHealthDetailObj) + "  ")
                    .setUnderline());

            medicalLifestyleQ.addCell(p);
            p = new Paragraph("");
            medicalLifestyleQ.addCell(p);
            medicalLifestyleQ.addCell(
                    "b. Have you taken part, or do you have plans to take part, in any hazardous/ dangerous activity such as ballooning, mountain cycling, motorbike racing, boxing, gliding, diving, horse riding, martial "
                            +
                            "arts, motor racing, mountain climbing, parachuting, sailing, skiing, weight lifting, white water rafting, wrestling and/ or flying other than as a fare paying passenger on a licensed service or any other "
                            +
                            "hazardous/ dangerous activity which is not listed. If yes, please provide details in the special questionnaire which your advisor will provide.");
            p = new Paragraph();

            if (jsonUtility
                    .getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("hazardous_activity", primarySecondaryLifestyleObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility
                    .getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("hazardous_activity", primarySecondaryLifestyleObj))
                    .equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);
            medicalLifestyleQ.addCell(
                    "c. Are you currently or do you intend to live or travel outside India for more than six months in a financial year? If yes, please provide full details of countries to be visited the purpose of visit and duration");
            p = new Paragraph();
            if (jsonUtility
                    .getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("outOfIndia", primarySecondaryLifestyleObj))
                    .equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (jsonUtility
                    .getJsonKeyValue("status",
                            jsonUtility.getJsonObjectByKey("outOfIndia", primarySecondaryLifestyleObj))
                    .equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            p = new Paragraph(
                    "d. Have you smoked or used any form of tobacco in the past 12 months? If yes, please indicate in which form: \n");
            JsonObject tobaccoConsumptionObj = jsonUtility.getJsonObjectByKey("tobacco_consumption",
                    primarySecondaryLifestyleObj);
            String tobaccoType = jsonUtility.getJsonKeyValue("tobaccoType", tobaccoConsumptionObj);
            if (tobaccoType.equalsIgnoreCase("Cigarette")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Cigarettes     ");
            if (tobaccoType.equalsIgnoreCase("Beedi")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Beedi     ");
            if (tobaccoType.equalsIgnoreCase("Chewity")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Chew     ");
            if (tobaccoType.equalsIgnoreCase("Gutka")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Gutka     ");
            p.add("     Quantity per day:     ");
            String tobaccoStatus = jsonUtility.getJsonKeyValue("status", tobaccoConsumptionObj);
            if (tobaccoStatus.equalsIgnoreCase("y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("tobaccoPerDay", tobaccoConsumptionObj)).setUnderline());
            } else {
                p.add(new Text(" ").setUnderline());
            }
            medicalLifestyleQ.addCell(p);

            p = new Paragraph();
            if (tobaccoStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (tobaccoStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            p = new Paragraph("e. Do you consume any form of alcohol? If yes, what type?: \n");
            JsonObject alcoholConsumptionObj = jsonUtility.getJsonObjectByKey("alcohol_consumption",
                    primarySecondaryLifestyleObj);
            String alcoholType = jsonUtility.getJsonKeyValue("alcoholType", alcoholConsumptionObj);
            String alcoholConsStatus = jsonUtility.getJsonKeyValue("status", alcoholConsumptionObj);
            if (alcoholType.equalsIgnoreCase("Beer")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Beer     ");
            if (alcoholType.equalsIgnoreCase("Wine")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Wine     ");
            if (alcoholType.equalsIgnoreCase("HardLiquor")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Hard liquor     ");
            p.add("     Quantity per week:     ");
            if (alcoholConsStatus.equalsIgnoreCase("y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("alcoholperweek", alcoholConsumptionObj)).setUnderline());
            } else {
                p.add(new Text("").setUnderline());
            }
            medicalLifestyleQ.addCell(p);

            p = new Paragraph();
            if (alcoholConsStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (alcoholConsStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "f. Are you currently taking any medication or drugs, other than for minor conditions, (e.g. cold and flu), either prescribed or not prescribed by a doctor, or have you suffered from any illness, disorder, "
                            +
                            "disability or injury during the past 5 years which has required any form of medical or specialised examination (including chest x-rays, gynecological investigations, pap smear, or blood tests), "
                            +
                            "consultation, hospitalisation or surgery?");
            JsonObject medicationObj = jsonUtility.getJsonObjectByKey("medication", primarySecondaryMedicalObj);
            String medicationStatus = jsonUtility.getJsonKeyValue("status",
                    medicationObj);
            p = new Paragraph();
            if (medicationStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (medicationStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "g. Do you have any congenital/birth defects, pain or problems in the back, spine, muscles or joint, arthritis, gout, severe injury or other physical disability and have you been incapable of working "
                            +
                            "attending the school during the last two years for more than three consecutive days or are you currently incapable of working / attending school? Please ignore normal pregnancy.");

            JsonObject congenitalObj = jsonUtility.getJsonObjectByKey("congenital", primarySecondaryMedicalObj);
            String congenitalStatus = jsonUtility.getJsonKeyValue("status", congenitalObj);
            p = new Paragraph();
            if (congenitalStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (congenitalStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "h. Do you suffer from or ever had any medical ailments such as diabetes, high blood pressure, cancer, respiratory disease (including asthma), kidney or liver disease, stroke, any blood disorder, "
                            +
                            "heart problems?");
            JsonObject healthHistoryObj = jsonUtility.getJsonObjectByKey("healthHistory", primarySecondaryMedicalObj);
            String healthHistoryStatus = jsonUtility.getJsonKeyValue("status", healthHistoryObj);
            p = new Paragraph();
            if (healthHistoryStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (healthHistoryStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "i. Do you suffer from or ever had any medical ailments such as Hepatitis B or C, or tuberculosis, psychiatric disorder, depression, colitis, or any other stomach problems, thyroid disorders, reproductive "
                            +
                            "organs, HIV AIDS or a related infection?");
            String diagnosedHepatitisStatus = jsonUtility.getJsonKeyValue("status",
                    jsonUtility.getJsonObjectByKey("diagnosedHepatitis", primarySecondaryMedicalObj));
            p = new Paragraph();
            if (diagnosedHepatitisStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (diagnosedHepatitisStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "j. Do you suffer from or ever had any medical ailments such as tumor growth, prostrate disorder, disorder of skin or lymph glands, multiple sclerosis, epilepsy, tremor, numbness, double vision "
                            +
                            "or giddiness, speech defect, paralysis?");
            JsonObject diagnosedObj = jsonUtility.getJsonObjectByKey("diagnosed", primarySecondaryMedicalObj);
            String diagnosedStatus = jsonUtility.getJsonKeyValue("status", diagnosedObj);
            p = new Paragraph();
            if (diagnosedStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (diagnosedStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "k. Have you ever been advised/ had a surgery or any medical investigations such as X-ray, CT scan, mammogram, pap smear etc?");
            JsonObject medicalInvestigationsObj = jsonUtility.getJsonObjectByKey("medicalInvestigations",
                    primarySecondaryMedicalObj);
            String medicalInvestigationsStatus = jsonUtility.getJsonKeyValue("status",
                    medicalInvestigationsObj);
            p = new Paragraph();
            if (medicalInvestigationsStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (medicalInvestigationsStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "l. Have you ever suffered from drug/ narcotics or alcohol addiction or been advised by a doctor to reduce your alcohol/ tobacco consumption?");
            JsonObject sufferedObj = jsonUtility.getJsonObjectByKey("suffered", primarySecondaryMedicalObj);
            String sufferedStatus = jsonUtility.getJsonKeyValue("status", sufferedObj);
            p = new Paragraph();
            if (sufferedStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (sufferedStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "m. In the last 3 years, have you been treated, are currently undergoing or have been advised for treatment from a doctor or specialist or undergone any cardiological, radiology or pathological "
                            +
                            "tests (excluding routine check ups)?");
            JsonObject specialistDoctorTreatmentObj = jsonUtility.getJsonObjectByKey("specialistDoctorTreatment",
                    primarySecondaryMedicalObj);
            String specialistDoctorTreatmentStatus = jsonUtility.getJsonKeyValue("status",
                    specialistDoctorTreatmentObj);
            p = new Paragraph();
            if (specialistDoctorTreatmentStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (specialistDoctorTreatmentStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "n. Is your occupation associated with any specific hazards which would render you susceptible to any injury or illness, e.g. chemical factory, mines, explosives, corrosive chemicals, etc.?");
            JsonObject injuryIllnessObj = jsonUtility.getJsonObjectByKey("injuryIllness", primarySecondaryMedicalObj);
            String injuryIllnessStatus = jsonUtility.getJsonKeyValue("status",
                    injuryIllnessObj);
            p = new Paragraph();
            if (injuryIllnessStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (injuryIllnessStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            JsonObject lostWeightObj = jsonUtility.getJsonObjectByKey("lostWeight", primarysecodaryHealthDetailObj);
            String lostWeightstatus = jsonUtility.getJsonKeyValue("status", lostWeightObj);
            String gainOrLoss = jsonUtility.getJsonKeyValue("gainorloss", lostWeightObj);
            p = new Paragraph();
            p.add("o. Has your weight altered (Gain/Loss) by more than 5 kgs. in the last 1 years?\n");
            p.add("If yes, please mention weight gain  ");
            if (lostWeightstatus.equalsIgnoreCase("y")) {
                if (gainOrLoss.equalsIgnoreCase("gained")) {
                    p.add(new Text(jsonUtility.getJsonKeyValue("weightGainedorLoss", lostWeightObj)).setUnderline());
                } else {
                    p.add(new Text("").setUnderline());
                }
                p.add("   (in Kgs)");
                p.add("  or Loss   ");
                if (gainOrLoss.equalsIgnoreCase("lost")) {
                    p.add(new Text(jsonUtility.getJsonKeyValue("weightGainedorLoss", lostWeightObj)).setUnderline());
                } else {
                    p.add(new Text("").setUnderline());
                }
                p.add("   (in Kgs)");
                p.add("\nReason for Gain / Loss:     ");
                p.add(new Text("").setUnderline());
            } else {
                p.add(new Text("").setUnderline());
                p.add("  or Loss   ");
                p.add(new Text("").setUnderline());
                p.add("   (in Kgs)");
                p.add("\nReason for Gain / Loss:     ");
                p.add(new Text("").setUnderline());
            }

            medicalLifestyleQ.addCell(p);
            p = new Paragraph();
            if (lostWeightstatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (lostWeightstatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ
                    .addCell("p. Have you ever been convicted for any Criminal convictions/activities /offences ?");
            JsonObject criminalConvictionsObj = jsonUtility.getJsonObjectByKey("criminalConvictions",
                    primarySecondaryMedicalObj);
            String criminalConvictionsStatus = jsonUtility.getJsonKeyValue("status",
                    criminalConvictionsObj);
            p = new Paragraph();
            if (criminalConvictionsStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (criminalConvictionsStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);

            medicalLifestyleQ.addCell(
                    "q. Have you ever been suffered/suffering Any other disease/disorder not mentioned above ?");
            JsonObject sufferedDiseaseObj = jsonUtility.getJsonObjectByKey("sufferedDisease",
                    primarySecondaryMedicalObj);
            String sufferedDiseaseStatus = jsonUtility.getJsonKeyValue("status", sufferedDiseaseObj);
            p = new Paragraph();
            if (sufferedDiseaseStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (sufferedDiseaseStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            medicalLifestyleQ.addCell(p);
            table.addCell(medicalLifestyleQ);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(
                    "10. If you have answered Yes, to any of the questions between 9(f) to 9(q) please provide details here");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);
            Table questionDetails = new Table(new float[]{150F, 800F});
            questionDetails.addCell("Question no.");
            questionDetails.addCell(
                    "For question No. 10a(f) to 10a(q) provide complete details including health condition, date of diagnosis, treatment prescribed, name/ address of doctor, if applicable");

            if (medicationStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("f");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("anyMedicalIssues", medicationObj));
            }
            if (congenitalStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("g");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", congenitalObj));
            }
            if (healthHistoryStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("h");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", healthHistoryObj));
            }
            if (diagnosedStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("j");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", diagnosedObj));
            }
            if (medicalInvestigationsStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("k");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", medicalInvestigationsObj));
            }
            if (sufferedStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("l");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", sufferedObj));
            }
            if (specialistDoctorTreatmentStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("m");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", specialistDoctorTreatmentObj));
            }
            if (injuryIllnessStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("n");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", injuryIllnessObj));
            }
            if (criminalConvictionsStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("p");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", criminalConvictionsObj));
            }
            if (sufferedDiseaseStatus.equalsIgnoreCase("y")) {
                questionDetails.addCell("q");
                questionDetails.addCell(jsonUtility.getJsonKeyValue("details", sufferedDiseaseObj));
            }
            table.addCell(questionDetails);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("11. For Female Life to be Assured only");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            Table femaleLifeAssured = new Table(2);
            femaleLifeAssured.addCell("a. Are you pregnant at present?: ");
            JsonObject pregnantObj = jsonUtility.getJsonObjectByKey("pregnant", primarySecondaryLifestyleObj);
            String pregnantStatus = jsonUtility.getJsonKeyValue("status", pregnantObj);
            p = new Paragraph();
            if (pregnantStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (pregnantStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            femaleLifeAssured.addCell(p);
            femaleLifeAssured.addCell("If yes duration in weeks: ");
            if (pregnantStatus.equalsIgnoreCase("y")) {
                femaleLifeAssured.addCell(jsonUtility.getJsonKeyValue("durationOfWeeks", pregnantObj));
                femaleLifeAssured.addCell("b. Date of last delivery:  ");
                femaleLifeAssured
                        .addCell(jsonUtility.getJsonKeyValue("dateOfLastDelivery", pregnantObj));
                femaleLifeAssured.addCell("c. Please state any complications during pregnancy?");
                femaleLifeAssured.addCell(jsonUtility.getJsonKeyValue("pregnancyComplications", pregnantObj));
            } else {
                femaleLifeAssured.addCell("");
                femaleLifeAssured.addCell("b. Date of last delivery:  ");
                femaleLifeAssured.addCell("");
                femaleLifeAssured.addCell("c. Please state any complications during pregnancy?");
                femaleLifeAssured.addCell("");
            }
            table.addCell(femaleLifeAssured);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("12. Insurance Repository");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

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
            table.addCell(p);
            Table eInsuranceDetails = new Table(2);
            eInsuranceDetails.addCell("E IA Number: ");
            eInsuranceDetails.addCell(eIANumber);
            eInsuranceDetails.addCell("IR Name: ");
            eInsuranceDetails.addCell(irName);
            table.addCell(eInsuranceDetails);
            p = new Paragraph("Open New e - Insurance Account - Please choose the repository from the below");
            table.addCell(p);
            eInsuranceDetails = new Table(3);
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
            table.addCell(eInsuranceDetails);

//            p = new Paragraph("Do you need a physical copy of Policy Document?     ");
//            p.add(imgUnchecked);
//            p.add("     Yes     ");
//            p.add(imgChecked);
//            p.add("     No      ");
//            table.addCell(p);

            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("13. Do you need a physical copy of Policy Document?     ");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgChecked);
            p.add("     No      ");
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("14. Declaration by Proposer/ Life to be Assured");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            p = new Paragraph(jsonUtility.getJsonKeyValue("consent1", contentJson));
            table.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("consent2", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("consent3", contentJson));
            p.add(jsonUtility.getJsonKeyValue("consent4", contentJson));
            table.addCell(new Cell().add(p));
            p = new Paragraph();
            p.add(new Text(jsonUtility.getJsonKeyValue("consent5", contentJson)).setBold());
            p.add(new Text(jsonUtility.getJsonKeyValue("consent6", contentJson)).setBold());
            p.add(new Text(jsonUtility.getJsonKeyValue("consent7", contentJson)).setBold());
            p.add("\n\n");
            if (jsonUtility.getJsonKeyValue("basbastatus",paymentDetailObj).equalsIgnoreCase("success")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("consent8", contentJson)).setBold());
            }
            p.add(new Text(jsonUtility.getJsonKeyValue("consent9", contentJson)).setBold());
            table.addCell(new Cell().add(p));
            p = new Paragraph();
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationHeading", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("declarationContent1", contentJson));
            table.addCell(new Cell().add(p));

            p=new Paragraph();
            if (isOmniDoc) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationContent2", contentJson)));
            p.add("\n");
            if (isOmniDoc) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add(new Text(jsonUtility.getJsonKeyValue("consent10", contentJson)));
            table.addCell(p);

            p = new Paragraph();
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("Life to be Assured’s Signature or Thumb Impression");
            p.add("\n(Not applicable in case of minor lives)");
            table.addCell(p);
            String laName = "";
            String laPlace = "";
            if (myself) {
                laName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
                laPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
            } else {
                laName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
                laPlace = jsonUtility.getJsonKeyValue("city", secondaryPersonalDetailObj);
            }

//            String laDate = String.valueOf(currentDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String laDate = currentDate.format(formatter);
            String witnessName = "";
            String proposerName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            String proposerPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
            String proposerDate = String.valueOf(currentDate);
            String proposerAddress = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj) + ", "
                    + jsonUtility.getJsonKeyValue("state", primaryPersonalDetailObj);

            Table laSignaturedetails = new Table(6);
            laSignaturedetails.addCell("Name: ");
            laSignaturedetails.addCell(laName);
            laSignaturedetails.addCell("Place: ");
            laSignaturedetails.addCell(laPlace);
            laSignaturedetails.addCell("Date: ");
            laSignaturedetails.addCell(laDate);
            table.addCell(laSignaturedetails);

            p = new Paragraph();
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("Witness's Signature or Thumb Impression");
            p.add("\nName:  ");
            p.add(new Text(witnessName).setUnderline());
            table.addCell(p);

            p = new Paragraph();
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("Proposer’s Signature or Thumb Impression");
            table.addCell(p);

            Table proposerSignatureDetails = new Table(2);
            proposerSignatureDetails.addCell("Name: ");
            proposerSignatureDetails.addCell(proposerName);
            proposerSignatureDetails.addCell("Place: ");
            proposerSignatureDetails.addCell(proposerPlace);
            proposerSignatureDetails.addCell("Date: ");
            proposerSignatureDetails.addCell(laDate);
            proposerSignatureDetails.addCell("Address: ");
            proposerSignatureDetails.addCell(proposerAddress);
            table.addCell(proposerSignatureDetails);

            if (isOmniDoc) {
                p = new Paragraph();
                p.add(new Text("   Life Assured OTP Verified  "));
                p.add(imgChecked);
                p.add(new Text("\nLife Assured Mobile No: "));
                p.add(new Text(onlineUtility.maskMobileNumber(mobileNo)));
                table.addCell(p);
            }
            p = new Paragraph();
            p.add(new Text(jsonUtility.getJsonKeyValue("content3", contentJson))).setBold();
            p.add(jsonUtility.getJsonKeyValue("content3", contentJson));
            table.addCell(p);

            p = new Paragraph();
            p.add("\n");
            p.add(new Text(jsonUtility.getJsonKeyValue("content4", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("content5", contentJson));
            table.addCell(p);

            p = new Paragraph();
            p.add("\n");
            p.add(new Text(jsonUtility.getJsonKeyValue("content6", contentJson)).setBold());
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("content7", contentJson));

            p.add(new Text(jsonUtility.getJsonKeyValue("content8", contentJson)));
            table.addCell(p);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content9", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            p = new Paragraph();
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("content10", contentJson));
            table.addCell(p);

            String nameOfDeclarant = "";
            String signatureOfDeclarant = "";
            String addressOfdeclarant = "";
            String declarantRelation = "";
            String language = "__________________";

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
            signatureDeclarants.addCell(new Cell().add("Relation with the Life Assured/Proposer:").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(declarantRelation).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signatureDeclarants));

            p=new Paragraph(jsonUtility.getJsonKeyValue("content11", contentJson));
            table.addCell(new Cell().add(p));

            p = new Paragraph();
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("content12", contentJson));
            p.add(new Text(language).setUnderline());
            p.add(jsonUtility.getJsonKeyValue("content13", contentJson));
            p.add("\n");
            table.addCell(new Cell().add(p));
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
            signatureDeclarants.addCell(new Cell().add("Relation with the Life Assured/Proposer:").setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph(declarantRelation).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signatureDeclarants));

            //////NEW section added/////////////////
//
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
            table.addCell(new Cell().add(p));

            boolean disclaimerStatus = jsonUtility.getBooleanKeyValue("disclamer", medicalDisabilityObj);
            boolean isOtpVerified = jsonUtility.getBooleanKeyValue("isOtpVerified", medicalDisabilityObj);

            signatureDeclarants = new Table(new float[]{300F, 200F, 300F, 150F});
            p=new Paragraph();
            p.add(isOtpVerified ? imgChecked : imgUnchecked);
            p.add(new Text("   Validated through the OTP sent to registered mobile no."));
            p.add(new Text(isOtpVerified ? onlineUtility.maskMobileNumber(representativeMobileNumber) : "").setBold());
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
            signatureDeclarants.addCell(new Cell(1,4).add(p).setPaddingRight(40f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            p=new Paragraph(" ");
            if (disclaimerStatus) {
                p.add(imgChecked);
            }else {
                p.add(imgUnchecked);
            }
            p.add(jsonUtility.getJsonKeyValue("content14", contentJson));
            signatureDeclarants.addCell(new Cell(1,4).add(p).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell(1,4).add("").setHeight(20F).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(jsonUtility.getJsonKeyValue("content15", contentJson)).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            signatureDeclarants.addCell(new Cell(2,4).add(jsonUtility.getJsonKeyValue("content16", contentJson)).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(signatureDeclarants));
            document.add(table);
            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content17", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);
            p = new Paragraph(jsonUtility.getJsonKeyValue("content18", contentJson));
            table.addCell(p);

            Table occupationTypes = new Table(new float[]{30F, 420F, 30F, 420F});
            occupationTypes.addCell(new Paragraph("Code").setBold());
            occupationTypes.addCell(new Paragraph("Occupation types").setBold());
            occupationTypes.addCell(new Paragraph("Code").setBold());
            occupationTypes.addCell(new Paragraph("Occupation types").setBold());

            occupationTypes.addCell("01");
            occupationTypes.addCell("Salaried - administrative employees, clerk, executive, accountant");
            occupationTypes.addCell("16");
            occupationTypes.addCell("Electricity Line Worker");

            occupationTypes.addCell("02");
            occupationTypes
                    .addCell("Professionals-doctor,chartered accountant/advocate-lawyer/teacher-lecturer,professors");
            occupationTypes.addCell("17");
            occupationTypes.addCell("Explosives handler - demolition experts");

            occupationTypes.addCell("03");
            occupationTypes.addCell("Salesman - including counter sales staff");
            occupationTypes.addCell("18");
            occupationTypes.addCell("Fireman");

            occupationTypes.addCell("04");
            occupationTypes.addCell("Retail / whole sale shop owner, commission agents");
            occupationTypes.addCell("19");
            occupationTypes.addCell("Fisherman");

            occupationTypes.addCell("05");
            occupationTypes.addCell("Retired / pensioner");
            occupationTypes.addCell("20");
            occupationTypes.addCell("Hotel industry other than 5 star");

            occupationTypes.addCell("06");
            occupationTypes.addCell("Student");
            occupationTypes.addCell("21");
            occupationTypes.addCell("Merchant navy others");

            occupationTypes.addCell("07");
            occupationTypes.addCell("House wife");
            occupationTypes.addCell("22");
            occupationTypes.addCell("Mining, coal miner, mining engineers");

            occupationTypes.addCell("08");
            occupationTypes.addCell(
                    "Agriculture - labourer, cleaner, maintenance workers, gardener, hawker, mill worker, porter / coolie");
            occupationTypes.addCell("23");
            occupationTypes.addCell("Oil Rig worker");

            occupationTypes.addCell("09");
            occupationTypes.addCell("Armed force personnel (military service)");
            occupationTypes.addCell("24");
            occupationTypes.addCell("Police");

            occupationTypes.addCell("10");
            occupationTypes.addCell("Aviation - includes all pilots");
            occupationTypes.addCell("25");
            occupationTypes.addCell("Well sinker / Bore well drillers");

            occupationTypes.addCell("11");
            occupationTypes.addCell("Blacksmith, boiler worker, furnace workers, welding workers, machine operators");
            occupationTypes.addCell("26");
            occupationTypes.addCell("Print / media involved in war");

            occupationTypes.addCell("12");
            occupationTypes.addCell("Weaver, lift operators, domestic servants, mason, mechanic");
            occupationTypes.addCell("27");
            occupationTypes.addCell("Professional sports person");

            occupationTypes.addCell("13");
            occupationTypes.addCell("Construction / building worker");
            occupationTypes.addCell("28");
            occupationTypes.addCell("Security guard");

            occupationTypes.addCell("14");
            occupationTypes.addCell("Diver - water, deep sea");
            occupationTypes.addCell("29");
            occupationTypes.addCell("Others (None of the above)");

            occupationTypes.addCell("15");
            occupationTypes.addCell("Driver - ambulance, armoured vehicle, lorry etc");
            occupationTypes.addCell("");
            occupationTypes.addCell("");
            table.addCell(occupationTypes);
            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("17. Intermediary details");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            String intermediateName = "ONL";
            String intermediateLicenseNumber = "";
            String agentName = "Online Channel";
            String licenseCode = "";

            Table intermediateDetails = new Table(new float[]{420F, 350F, 200F, 150F});
            p = new Paragraph("Name of the Intermediary: ");
            p.add("\n(Applicable for all channels except Individual Agents)");
            intermediateDetails.addCell(p);
            intermediateDetails.addCell(intermediateName);
            intermediateDetails.addCell("Intermediary License Number: ");
            intermediateDetails.addCell(intermediateLicenseNumber);
            table.addCell(intermediateDetails);

            pointColumnWidths = new float[]{250F, 180F, 280F};
            Table signatureTable = new Table(pointColumnWidths);
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add("Signature of the Agent / Specified Agents").setTextAlignment(TextAlignment.CENTER));
            signatureTable.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add("Stamp of the Intermediary").setTextAlignment(TextAlignment.CENTER));
            signatureTable.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(signatureTable);

            intermediateDetails = new Table(new float[]{420F, 350F, 200F, 150F});
            intermediateDetails.addCell("Name of the Agent / Specified Agents: ");
            intermediateDetails.addCell(agentName);
            intermediateDetails.addCell("License Code: ");
            intermediateDetails.addCell(licenseCode);
            table.addCell(intermediateDetails);

            document.add(table);

            table = new Table(1);
            table.setWidthPercent(100);
            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("18. Know Your Customer Certificate Issued by Bank");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(headingCell);

            String customerNameBank = "";
            String accountno = "";
            String customerId = "";
            String authorisedSignature = " ";
            String nameOfAuthorized = "";
            String nameOfBranch = "";

            p = new Paragraph();
            p.add("We hereby confirm that       ");
            p.add(new Text(customerNameBank).setUnderline());
            p.add("     holds Savings / Current / Fixed deposit loan account no.      ");
            p.add(new Text(accountno).setUnderline());
            p.add("     and bank customer ID    ");
            p.add(new Text(customerId).setUnderline());
            p.add("     with our bank. We confirm that we have obtained the necessary documentary evidence to establish the identity and address"
                    +
                    " of the customer as mentioned by him/ her in this proposal form, as per the “Know Your Customer” (KYC) norms for banks.");
            table.addCell(p);

            Table kycBankDetailsSignatures = new Table(new float[]{400F, 250F});
            kycBankDetailsSignatures.addCell("Signature of authorised signatory from bank :");
            kycBankDetailsSignatures.addCell(authorisedSignature);
            kycBankDetailsSignatures.addCell("Name of authorised signatory from bank :");
            kycBankDetailsSignatures.addCell(nameOfAuthorized);
            kycBankDetailsSignatures.addCell("Name of the bank branch :");
            kycBankDetailsSignatures.addCell(nameOfBranch);

            Table bankSeal = new Table(1);
            bankSeal.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            bankSeal.addCell(
                    new Cell().add("Bank Seal").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            bankSeal.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));

            Table mergedKyc = new Table(2);
            mergedKyc.addCell(kycBankDetailsSignatures);
            mergedKyc.addCell(bankSeal);

            table.addCell(mergedKyc);

            p = new Paragraph(
                    "Aforementioned details can be used by the Company to pay the proposer according to the terms of the plan. Payment options (cheque will be used if none of the below electronic "
                            +
                            "payout option is chosen). Further, the Company reserves the right to use any alternative payout option including demand draft / payable at par cheque in spite of option for Direct "
                            +
                            "credit .");
            table.addCell(p);
            document.add(table);

            p = new Paragraph();
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
            p.add("\n");
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
                    p.add(new Text(onlineUtility.maskMobileNumber(primaryMobileNo)).setBold());
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
            logger.info("Nominee list size is:{}",nomineeList.size());
            if(nomineeList.size() > 1){
                Table nomineeAddendum = this.generateNomineePDF(pdfUtility, jsonUtility, userData.get("applicationNumber").getAsString(), nomineeList, imagesJson);
                document.add(new AreaBreak(AreaBreakType.NEXT_AREA));
                document.add(nomineeAddendum);
            }
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception occurs in generate application form pdf:{}", e.getMessage());
            return null;
        }

    }

    public Table getBankDetails(Image imgUnchecked, Image imgChecked,JsonObject primaryBankObj,JsonObject eMandateObj,JsonArray nomineeList, JsonObject contentJson){
        String bankName = jsonUtility.getJsonKeyValue("Bank_Name", primaryBankObj);
        bankName=bankName.length()>21 ? bankName.substring(0,21) : bankName;
        String branchName = jsonUtility.getJsonKeyValue("Branch_Name", primaryBankObj);
        branchName=branchName.length()>16 ? branchName.substring(0,16) : branchName;
        String accountNo = jsonUtility.getJsonKeyValue("accountNumber", primaryBankObj);
        String micr = jsonUtility.getJsonKeyValue("MICR_Code", primaryBankObj);
        String ifscode = jsonUtility.getJsonKeyValue("ifscCode", primaryBankObj);
        String customerName = jsonUtility.getJsonKeyValue("accountHolderName", primaryBankObj);
        String accountTypeName = jsonUtility.getJsonKeyValue("accountType", primaryBankObj);
        String ecsStatus = jsonUtility.getJsonKeyValue("status", eMandateObj);
        Table table=new Table(1);
        ////
        Paragraph p = new Paragraph("For Proposer & Life Assured").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the proposer according to the terms of the plan. If none of the below electronic payout option is chosen, the Company reserves the right to use any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Table bankDetails = new Table(new float[] {70F ,450F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(ecsStatus.equalsIgnoreCase("success") ? imgUnchecked : imgChecked);
        p.add("  ECS  ");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("  Direct Credit (Bank of Baroda & Union Bank of India) ");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        float[]pointColumnWidths = new float[bankName.length()];
        for(int i=0;i<bankName.length();i++){
            pointColumnWidths[i] = 20F;
        }
        Table tableCodes = codeTable(bankName, pointColumnWidths);
        bankDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        Table accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        Table accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(accountTypeName.equalsIgnoreCase("current") ? imgChecked : imgUnchecked);
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(accountTypeName.equalsIgnoreCase("savings") ? imgChecked : imgUnchecked);
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[branchName.length()];
        for(int i=0;i<branchName.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = codeTable(branchName, pointColumnWidths);
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        String bankAccountNo = accountNo;
        pointColumnWidths = new float[bankAccountNo.length()];
        for(int i=0;i<bankAccountNo.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = codeTable(bankAccountNo, pointColumnWidths);
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        Table ifscDetails = new Table(new float[] {50F, 200F, 250F, 70F, 200F, 300F});
        ifscDetails.addCell(new Cell().add("MICR:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[micr.length()];
        for(int i = 0; i< micr.length(); i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = codeTable(micr, pointColumnWidths);
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for ECS mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        String ifscCode = ifscode;
        pointColumnWidths = new float[ifscCode.length()];
        for(int i=0;i<ifscCode.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = codeTable(ifscCode, pointColumnWidths);
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        Table nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Customer’s Name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[customerName.length()];
        for(int i=0;i<customerName.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = codeTable(customerName, pointColumnWidths);
        nameDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell(1,2).add(new Paragraph(new Text("Please provide a cancelled copy of your cheque if any of the above option is selected").setBold())).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ////
        JsonObject nomineeObj1 = nomineeList.isEmpty() ? new JsonObject() : nomineeList.get(0).getAsJsonObject();
        String nomineeName1 = jsonUtility.getJsonKeyValue("name", nomineeObj1);
        String modeOfPaymentNominee = jsonUtility.getJsonKeyValue("modeOfPayment", nomineeObj1);
        bankName = jsonUtility.getJsonKeyValue("Bank_Name", nomineeObj1);
        bankName=bankName.length()>21 ? bankName.substring(0,21) : bankName;
        branchName = jsonUtility.getJsonKeyValue("Branch_Name", nomineeObj1);
        branchName=branchName.length()>16 ? branchName.substring(0,16) : branchName;
        accountNo = jsonUtility.getJsonKeyValue("accountNumber", nomineeObj1);
        ifscode = jsonUtility.getJsonKeyValue("ifscCode", nomineeObj1);
        accountTypeName = jsonUtility.getJsonKeyValue("accountType", nomineeObj1);
        p = new Paragraph("For Nominee 1").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the Nominee according to the terms of the plan. If none of the below electronic payout option is chosen,the Company reserves the right to use any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        bankDetails = new Table(new float[] {30F ,480F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(modeOfPaymentNominee.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Direct Credit (Bank of Baroda & Union Bank of India)");
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

        accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(accountTypeName.equalsIgnoreCase("current") ? imgChecked : imgUnchecked);
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(accountTypeName.equalsIgnoreCase("savings") ? imgChecked : imgUnchecked);
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(branchName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(accountNo,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails = new Table(new float[] {80F, 220F, 400F});
        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add(pdfUtility.createDataTable(ifscode,"000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Nominee's name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell().add(pdfUtility.createDataTable(nomineeName1,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell(1,2).add(new Paragraph("Note: In case of multiple nominations, please add all nominees bank account details")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ///Appointee section started////
        String appointeeName1 = jsonUtility.getJsonKeyValue("appointeeName", nomineeObj1);
        String modeOfPaymentAppointee = jsonUtility.getJsonKeyValue("appointeeModeOfPayment", nomineeObj1);
        bankName = jsonUtility.getJsonKeyValue("Appointee_Bank_Name", nomineeObj1);
        bankName=bankName.length()>21 ? bankName.substring(0,21) : bankName;
        branchName = jsonUtility.getJsonKeyValue("Appointee_Branch_Name", nomineeObj1);
        branchName=branchName.length()>16 ? branchName.substring(0,16) : branchName;
        accountNo = jsonUtility.getJsonKeyValue("appointeeAccountNumber", nomineeObj1);
        ifscode = jsonUtility.getJsonKeyValue("appointeeIfscCode", nomineeObj1);
        accountTypeName = jsonUtility.getJsonKeyValue("appointeeAccountType", nomineeObj1);

        p = new Paragraph("For Appointee (In case the Nominee is minor)").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the Appointee according to the terms of the plan. If none of the below electronic payout option is chosen,the Company reserves the right to use any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        bankDetails = new Table(new float[] {30F ,480F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(modeOfPaymentAppointee.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Direct Credit (Bank of Baroda & Union Bank of India)");
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

        accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(accountTypeName.equalsIgnoreCase("current") ? imgChecked : imgUnchecked);
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(accountTypeName.equalsIgnoreCase("savings") ? imgChecked : imgUnchecked);
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(branchName,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(pdfUtility.createDataTable(accountNo,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails = new Table(new float[] {80F, 220F, 400F});
        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add(pdfUtility.createDataTable(ifscode,"00000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Appointee Name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails.addCell(new Cell().add(pdfUtility.createDataTable(appointeeName1,"00000000000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p=new Paragraph(new Text("Disclaimer: ").setBold());
        p.add(jsonUtility.getJsonKeyValue("content13", contentJson));
        nameDetails.addCell(new Cell(1,2).add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        return table;
    }

    public Table codeTable(String codeData, float[] pointColumnWidths) {
        Table tableCodes = new Table(pointColumnWidths);
        for (char c : codeData.toCharArray()) {
            Cell cellLeft = new Cell();
            Paragraph p = new Paragraph();
            p.add(new Text(String.valueOf(c).toUpperCase()));
            cellLeft.add(p);
            cellLeft.setTextAlignment(TextAlignment.CENTER);
            tableCodes.addCell(cellLeft);
        }
        return tableCodes;
    }

    public String feetAndInchesToCms(double feet, double inches) {
        double totalInches = (feet * 12) + inches;
        double centimeters = totalInches * 2.54;
        return String.valueOf(centimeters);
    }
}
