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
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.*;
import com.pdfGeneration.constants.PdfConstant;
import com.pdfGeneration.service.GoldFormPDFService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoldFormPDFServiceImpl extends NomineeAddendumPDF implements GoldFormPDFService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;

    public GoldFormPDFServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;
    }
    @Override
    public byte[] goldForm(String goldFormReqObj) {
try {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    JsonObject userData = jsonUtility.getJsonObject(goldFormReqObj);
    JsonObject basicDetailObj = jsonUtility.getJsonObjectByKey("basicDetails", userData);
    logger.info("Basic details object:{}", basicDetailObj);
    JsonObject bankDetailObj = jsonUtility.getJsonObjectByKey("bankDetails", userData);
    logger.info("Bank details object:{}", bankDetailObj);
    JsonObject planDetailObj = jsonUtility.getJsonObjectByKey("planDetails", userData);
    logger.info("Plan details object:{}", planDetailObj);
    JsonObject nomineeDetailObj = jsonUtility.getJsonObjectByKey("nomineeDetails", userData);
    logger.info("Nominee details object:{}", nomineeDetailObj);
    JsonObject lifestyleDetailObj = jsonUtility.getJsonObjectByKey("lifestyleQ", userData);
    logger.info("Lifestyle details object:{}", lifestyleDetailObj);
    JsonObject medicalDetailObj = jsonUtility.getJsonObjectByKey("medicalQ", userData);
    logger.info("Medical details object:{}", medicalDetailObj);
    JsonObject investmentDetailObj = jsonUtility.getJsonObjectByKey("investmentStretegic", userData);
    logger.info("Investment details object:{}", investmentDetailObj);
    JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails", userData);
    logger.info("Personal details object:{}", personalDetailObj);
    JsonObject employmentDetailObj = jsonUtility.getJsonObjectByKey("employeementData", userData);
    logger.info("Employment details object:{}", employmentDetailObj);
    JsonObject fatcaDetailObj = jsonUtility.getJsonObjectByKey("fatcaDetails", userData);
    logger.info("Fatca details object:{}", fatcaDetailObj);
    JsonObject otherDetailObj = jsonUtility.getJsonObjectByKey("otherPolicyDetails", userData);
    logger.info("Other details object:{}", otherDetailObj);
    JsonObject healthDetailObj = jsonUtility.getJsonObjectByKey("healthDetails", userData);
    logger.info("Health details object:{}", healthDetailObj);
    JsonObject eMandateObj = jsonUtility.getJsonObjectByKey("emandateDetails", userData);
    logger.info("EMandate object:{}", eMandateObj);
    JsonObject documentDetailObj = jsonUtility.getJsonObjectByKey("document", userData);
    logger.info("Document details object:{}", documentDetailObj);
    JsonObject paymentDetailObj = jsonUtility.getJsonObjectByKey("payment", userData);
    logger.info("Payment details object:{}", paymentDetailObj);


    JsonObject policyHolderBasicDetailObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
    JsonObject insuredPersonBasicDetailObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);
    String buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderBasicDetailObj);

    boolean myself = buyFor.equalsIgnoreCase("Myself");
    logger.info("---- flag---:{}", buyFor);

    JsonObject primaryBankObj = jsonUtility.getJsonObjectByKey("primary", bankDetailObj);

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

    JsonObject primaryHealthObj = jsonUtility.getJsonObjectByKey("primary", healthDetailObj);
    JsonObject secondaryHealthObj = jsonUtility.getJsonObjectByKey("secondary", healthDetailObj);
    JsonObject primaryLifestyleObj = jsonUtility.getJsonObjectByKey("primary", lifestyleDetailObj);
    JsonObject secondaryLifestyleObj = jsonUtility.getJsonObjectByKey("secondary", lifestyleDetailObj);
    JsonObject primaryMedicalObj = jsonUtility.getJsonObjectByKey("primary", medicalDetailObj);
    JsonObject secondaryMedicalObj = jsonUtility.getJsonObjectByKey("secondary", medicalDetailObj);

    String agentCode = "0N000001";
    String branchCodeValue = jsonUtility.getJsonKeyValue("branchCode", policyHolderBasicDetailObj);
    String branchCode = branchCodeValue.isEmpty() ? "DM001" : branchCodeValue;
    String branchManagerCode = "";
    String rmCode = "ON000001";
    String channelCode = "Online";
    String dbmMobile = "";

    String applicationNumber = jsonUtility.getJsonKeyValue("applicationNumber", userData);
    boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);

    try {

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();


        String logoFilename = "commonlogo.png";
        String logoBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
        String checkedLogo = "checked_2_20x21.png";
        String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
        String uncheckedLogo = "unchecked_20x21.png";
        String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);
        Image img = pdfUtility.getPDFLogo(logoBase64);
        img.setWidth(200f);
        img.setHeight(120f);
        Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

        Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A3).setFont(font);
        document.setFontSize(7);
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
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("Proposal Form");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        headingCell = new Cell();
        p = new Paragraph("UNDER UNIT LINKED INSURANCE PLANS, INVESTMENT RISK IN INVESTMENT PORTFOLIO IS BORNE BY THE POLICYHOLDER. THE UNIT LINKED INSURANCE PRODUCTS DO NOT OFFER ANY LIQUIDITY DURING THE FIRST FIVE YEARS " + "OF THE CONTRACT. THE POLICYHOLDER WILL NOT BE ABLE TO SURRENDER OR WITHDRAW THE MONIES INVESTED IN UNIT LINKED INSURANCE PRODUCTS COMPLETELY OR PARTIALLY TILL THE END OF THE FIFTH YEAR.");
        headingCell.add(new Cell().setPaddingTop(10).add(p).setPaddingBottom(10));
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        float[] pointColumnWidths = new float[]{200F, 680F};
        Table firstBlockTable = new Table(pointColumnWidths);

        Table firstBlockLeft = new Table(1);
        firstBlockLeft.setMarginTop(20);
        firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
        firstBlockLeft.setWidth(150);
        SolidBorder solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);

        byte[] photoBytes = Base64.getDecoder().decode(jsonUtility.getJsonKeyValue("proposerPhotoBase64",userData));
        ImageData dataPhoto = ImageDataFactory.create(photoBytes);
        Image photoImg = new Image(dataPhoto);
        photoImg.setHeight(150);
        photoImg.setWidth(150);
        firstBlockLeft.addCell(new Cell().add(photoImg).setBorder(solidBorder));
        firstBlockTable.addCell(new Cell().add(firstBlockLeft).setBorder(Border.NO_BORDER));

        Table firstBlockRight = new Table(4);

        firstBlockRight.addCell(new Cell(1, 4).add("For Branch Sales Use Only").setBorder(Border.NO_BORDER));
        firstBlockRight.addCell(new Cell().add("LG / Agent Code: ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[agentCode.length()];
        for (int i = 0; i < agentCode.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        Table tableCodes;
        if (agentCode.length() > 0) {
            tableCodes = pdfUtility.codeTable(agentCode, pointColumnWidths).setWidth(120);
            firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        firstBlockRight.addCell(new Cell().add("Branch Code: ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[branchCode.length()];
        for (int i = 0; i < branchCode.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (branchCode.length() > 0) {
            tableCodes = pdfUtility.codeTable(branchCode, pointColumnWidths);
            firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        firstBlockRight.addCell(new Cell(1, 4).add(new Paragraph("(LG code to be written for Banca, Agent Code to be written for Agency.)").setFontSize(6f)).setBorder(Border.NO_BORDER));
        firstBlockRight.addCell(new Cell().add("Branch Manager Code: ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[branchManagerCode.length()];
        for (int i = 0; i < branchManagerCode.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (branchManagerCode.length() > 0) {
            tableCodes = pdfUtility.codeTable(branchManagerCode, pointColumnWidths);
            firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        firstBlockRight.addCell(new Cell().add("BDM / RM Code: ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[rmCode.length()];
        for (int i = 0; i < rmCode.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (rmCode.length() > 0) {
            tableCodes = pdfUtility.codeTable(rmCode, pointColumnWidths);
            firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        firstBlockRight.addCell(new Cell().add("Channel Code: ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[channelCode.length()];
        for (int i = 0; i < channelCode.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (channelCode.length() > 0) {
            tableCodes = pdfUtility.codeTable(channelCode, pointColumnWidths);
            firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        firstBlockRight.addCell(new Cell().add("BDM Mobile No: ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[dbmMobile.length()];
        for (int i = 0; i < dbmMobile.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (dbmMobile.length() > 0) {
            tableCodes = pdfUtility.codeTable(dbmMobile, pointColumnWidths);
            firstBlockRight.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        Cell others = new Cell(1, 4);
        p = new Paragraph("Bancassurance/ Agency/ Broker/ Corporate Agency/ Direct Sales/ Marketing Associate, Any Others (pls specify): ");
        p.add(new Text("Direct Sales").setBold().setUnderline());
        others.add(p);
        others.setBorder(Border.NO_BORDER);
        firstBlockRight.addCell(others);

        p = new Paragraph("Important Guidelines:1.This form is to be filled by the proposer in BLOCKLETTERS in black/blue ink or to be filled electronically and leave a space blank between each part " + "of the name. 2. If the Proposer/ Life to beAssured is unable to fill the form due to inability to read or understand the language,the help of a person other than the advisor/ " + "our employee/insurance intermediary may be used.(Refer to declaration for signing in vernacular language or for uneducated/ illiterate persons) 3. Before filling up the form " + "please read the sales literature to understand the features, benefits, advantages and terms and conditions of the product. 4. If the space provided in the form is not sufficient " + "for providing details, please attach separate sheets signed by the Proposer/ Life to be Assured. 5. All details should be filled completely including email ID, mobile number, " + "etc. 6. If annual premium is equal to Rs. 50000 or more per customer by any mode of payment, a copy of PAN card and if annual premium is equal to or more than Rs.100000 " + "per customer by any mode of payment, income proof document needs to be submitted. 7.Customers are advised not to hand over the premium to IndiaFirst Life insurance " + "advisors to meet the premium dues (including initial premium). Customers are requested to visit the nearest IndiaFirst Life, Bank of Baroda and Union Bank of India branch " + "to deposit the premium directly. Premium payment made to IndiaFirst Life insurance advisors is at the customer’s own risk. 8. Encashment of cheque/ DD does not mean " + "the policy has been approved and the Company reserves the right to call for additional requirements subject to underwriting (if any). 9. While answering questions in the " + "proposal form and providing any other information in respect of the insurance,the Policyholder must make a full and frank disclosure of all material facts with respect to the " + "questions available in proposal form. 10. In case the Proposer and Life to be Assured are two separate individuals,the proposal form will be signed by both.The life to be " + "assured can sign only if he/she is 18 years or above.");
        firstBlockRight.addCell(new Cell(1, 4).add(p).setMarginTop(10).setMarginBottom(10).setBorder(Border.NO_BORDER));
        firstBlockTable.addCell(new Cell().add(firstBlockRight).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(firstBlockTable).setBorder(Border.NO_BORDER));

        JsonObject indiaFirstStaffObj = jsonUtility.getJsonObjectByKey("indiaFirstStaff", primaryPersonalDetailObj);
        String staffStatus = jsonUtility.getJsonKeyValue("status", indiaFirstStaffObj);
        String staffEmpCodeNo = jsonUtility.getJsonKeyValue("empCode", indiaFirstStaffObj);

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph(new Text("Is the customer an employee of Bank of Baroda, Union Bank of India, eDena, eVijaya, IndiaFirst Life InsuranceCo. Ltd. Or an " + "Individual agent or their family member?   ").setBold());
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        Table tableContent = new Table(new float[]{1000F, 250F, 250F, 200F});
        tableContent.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (staffStatus.equalsIgnoreCase("Y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (staffStatus.equalsIgnoreCase("N")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        tableContent.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableContent.addCell(new Cell().add("Employee code/ Ref. no :").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        if (staffStatus.equalsIgnoreCase("Y")) {
            tableContent.addCell(new Cell().add(staffEmpCodeNo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        } else {
            tableContent.addCell(new Cell().add(" ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        }
        headingCell.add(new Cell().add(tableContent).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("1. Proposer/ Policy Owner Details (Please fill in details of Life to be Assured if same as Proposer)");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        String countryCode = jsonUtility.getJsonKeyValue("countryCode", policyHolderBasicDetailObj);
        String mobileNo = jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetailObj);
        String maritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", primaryPersonalDetailObj);
        String gender = jsonUtility.getJsonKeyValue("gender", policyHolderBasicDetailObj);

        Table proposerDetails = new Table(2);
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
        String clientId = "00000000";

        proposerMergedcell.add(pdfUtility.createDataTable(fullName, "0000000000000")).setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);

        proposerDetails.addCell(new Cell().add("Existing IndiaFirst Policy Owner, Kindly enter policy number / client id").setBorder(Border.NO_BORDER));
        Table innerProposer = new Table(new float[]{120F, 80F, 120F, 180F, 120F, 200F});
        innerProposer.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
        for (int i = 0; i < policyNo.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        innerProposer.addCell(new Cell().add("     Policy No:      ").setBorder(Border.NO_BORDER));
        innerProposer.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
        innerProposer.addCell(new Cell().add("     Client ID:      ").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[clientId.length()];
        for (int i = 0; i < clientId.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (!clientId.isEmpty()) {
            tableCodes = pdfUtility.codeTable(clientId, pointColumnWidths).setFontColor(Color.WHITE);
            innerProposer.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            innerProposer.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        }
        proposerDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));

         /*   String address1 = jsonUtility.getJsonKeyValue("addressline1", primaryPersonalDetailObj);
            if (address1.length()>0) {
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
            String permanentlandmark = jsonUtility.getJsonKeyValue("permanentlandmark", primaryPersonalDetailObj);            if (landmark.length()>0) {
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
            if (!state.isEmpty()) {
                if (state.length() <= 10) {
                    state += "                                 ";
                } else if (state.length() > 10 && state.length() <= 20) {
                    state += "                       ";
                } else {
                    state += "              ";
                }
            }
            p = new Paragraph("Communication Address of the Proposer (Address to which policy document will be dispatched)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);

            Table addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(landmark, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);*/

        String address1 = jsonUtility.getJsonKeyValue("addressline1", primaryPersonalDetailObj);

        if (address1.length() > 0) {
            if (address1.length() <= 10) {
                address1 += "                                        ";
            } else if (address1.length() > 10 && address1.length() <= 20) {
                address1 += "                              ";
            } else {
                address1 += "                ";
            }
        }
        String address2 = jsonUtility.getJsonKeyValue("addressline2", primaryPersonalDetailObj);
        if (!address2.isEmpty()) {
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
        if (!address3.isEmpty()) {
            if (address3.length() <= 10) {
                address3 += "                                        ";
            } else if (address3.length() > 10 && address3.length() <= 20) {
                address3 += "                              ";
            } else {
                address3 += "                     ";
            }
        }

        String landmark = jsonUtility.getJsonKeyValue("landmark", primaryPersonalDetailObj);
        if (landmark.length() > 0) {
            if (landmark.length() <= 10) {
                landmark += "                                        ";
            } else if (landmark.length() > 10 && landmark.length() <= 20) {
                landmark += "                              ";
            } else {
                landmark += "                ";
            }
        }
        String permanentlandmark = jsonUtility.getJsonKeyValue("permanentlandmark", primaryPersonalDetailObj);
        if (permanentlandmark.length() > 0) {
            if (permanentlandmark.length() <= 10) {
                permanentlandmark += "                                        ";
            } else if (permanentlandmark.length() > 10 && permanentlandmark.length() <= 20) {
                permanentlandmark += "                              ";
            } else {
                permanentlandmark += "                ";
            }
        }


        String permanentaddressline1 = jsonUtility.getJsonKeyValue("permanentaddressline1", primaryPersonalDetailObj);

        if (permanentaddressline1.length() > 0) {
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
        } else {
            pincode += "      ";
        }


        String permanentcity = jsonUtility.getJsonKeyValue("permanentcity", primaryPersonalDetailObj);
        if (permanentcity.length() > 0) {
            if (permanentcity.length() <= 10) {
                permanentcity += "                                        ";
            } else if (permanentcity.length() > 10 && permanentcity.length() <= 20) {
                permanentcity += "                              ";
            } else {
                permanentcity += "                ";
            }
        }
        String permanentstate = jsonUtility.getJsonKeyValue("permanentstate", primaryPersonalDetailObj);
        if (permanentstate.length() > 0) {
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
        p = new Paragraph("Communication Address of the Proposer (Address to which policy document will be dispatched)");
        p.setPaddingBottom(10);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(p);
        //proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);
        Table addressBlock = new Table(1);
           /* pointColumnWidths = new float[address1.length()];
            for (int i = 0; i < address1.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!address1.isEmpty()) {
                Table address1Block = pdfUtility.codeTableForEmail(address1, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address1Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            pointColumnWidths = new float[address2.length()];
            for (int i = 0; i < address2.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!address2.isEmpty()) {
                Table address2Block = pdfUtility.codeTableForEmail(address2, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address2Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            pointColumnWidths = new float[address3.length()];
            for (int i = 0; i < address3.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!address3.isEmpty()) {
                Table address3Block = pdfUtility.codeTableForEmail(address3, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            }

            pointColumnWidths = new float[landmark.length()];
            for (int i = 0; i < landmark.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!landmark.isEmpty()) {
                Table address3Block = pdfUtility.codeTableForEmail(landmark, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            }

            pointColumnWidths = new float[city.length()];
            for (int i = 0; i < city.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!city.isEmpty()) {
                Table address3Block = pdfUtility.codeTableForEmail(city, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            pointColumnWidths = new float[state.length()];
            for (int i = 0; i < state.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!state.isEmpty()) {
                Table address3Block = pdfUtility.codeTableForEmail(state, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }*/
        //Permanent Address Section addition started
        p = new Paragraph("Communication Address of the Proposer (Address to which policy document will be dispatched)");
        p.setPaddingBottom(10);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(p);
        // proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);
          /*  addressBlock = new Table(1);
            pointColumnWidths = new float[permanentaddressline1.length()];
            for (int i = 0; i < permanentaddressline1.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (permanentaddressline1.length() > 0) {
                Table address1Block = pdfUtility.codeTableForEmail(permanentaddressline1, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address1Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }

            pointColumnWidths = new float[permanentaddressline2.length()];
            for (int i = 0; i < permanentaddressline2.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (permanentaddressline2.length() > 0) {
                Table address2Block = pdfUtility.codeTableForEmail(permanentaddressline2, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address2Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            pointColumnWidths = new float[permanentaddressline3.length()];
            for (int i = 0; i < permanentaddressline3.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (permanentaddressline3.length() > 0) {
                Table address3Block = pdfUtility.codeTableForEmail(permanentaddressline3, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            }
            pointColumnWidths = new float[permanentlandmark.length()];
            for (int i = 0; i < permanentlandmark.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (permanentlandmark.length() > 0) {
                Table address3Block = pdfUtility.codeTableForEmail(permanentlandmark, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            }
            pointColumnWidths = new float[city.length()];
            for (int i = 0; i < city.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (city.length() > 0) {
                Table address3Block = pdfUtility.codeTableForEmail(city, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            pointColumnWidths = new float[state.length()];
            for (int i = 0; i < state.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (state.length() > 0) {
                Table address3Block = pdfUtility.codeTableForEmail(state, pointColumnWidths);
                addressBlock.addCell(new Cell().add(address3Block).setBorder(Border.NO_BORDER));
            } else {
                addressBlock.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);*/
        addressBlock = new Table(1);
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(landmark, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

        addressBlock = new Table(new float[]{600F, 100F, 200F});
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state, "0000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(pincode, "000000")).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
        //Permanent Address Section addition started

        p = new Paragraph("Permanent Address (If different from the above Address)");
        p.setPaddingBottom(10);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(p);

        addressBlock = new Table(1);
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressline1, "000000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressline2, "000000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressline3, "000000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentlandmark, "000000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentcity, "000000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentstate, "000000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
        //Permanent Address Section addition ended

        String landLine = "";

        Table contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F, 200F});
        contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("+").setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(countryCode, "00")).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mobile No* ");
        p.add(new Text("\n*Receive alerts through SMS and WhatsApp for this proposal / policy").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(mobileNo, "0000000000")).setBorder(Border.NO_BORDER));
        String pinCode = jsonUtility.getJsonKeyValue("permanentpincode", primaryPersonalDetailObj);
        contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(pinCode, "000000")).setBorder(Border.NO_BORDER));

        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(contactTable);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);

        String emailId = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetailObj);
        String natureOfWork = jsonUtility.getJsonKeyValue("natureOfWork", primaryEmploymentDetailObj);

        proposerMergedcell = new Cell(1, 2);
        contactTable = new Table(new float[]{200F, 430F, 200F, 400F});
        p = new Paragraph("Email ID* ");
        p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(emailId, "0000000000000")).setBorder(Border.NO_BORDER));

        p = new Paragraph("Landline:");
        p.add(new Text("\nSTD/ISD").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(landLine, "00000000")).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Gender:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
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
        contactTable.addCell(new Cell().add("Nationality:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
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
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = LocalDate.parse(dob);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = dateTimeFormatter.format(birthDate);

        int age = Period.between(birthDate, currentDate).getYears();
        contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("DOB :   ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE));
        pointColumnWidths = new float[date.length()];
        for (int i = 0; i < date.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        tableCodes = pdfUtility.codeTable(date, pointColumnWidths);
        contactTable.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Age:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        contactTable.addCell(new Cell().add(age + " Years").setBorder(Border.NO_BORDER));
//            contactTable.addCell(new Cell().add("Nature of work/duties:  ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
//            contactTable.addCell(new Cell(1, 3).add(new Paragraph(natureOfWork).setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        contactTable.addCell(new Cell().add("Marital Status :   ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE));
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

        String residentialStatus = jsonUtility.getJsonKeyValue("residentialstatus", primaryPersonalDetailObj);
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        contactTable.addCell(new Cell().add("  Residential Status:    ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
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

        String educationType = jsonUtility.getJsonKeyValue("educationType", primaryEmploymentDetailObj);
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        contactTable.addCell(new Cell().add("Education: ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
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

        String occupation = jsonUtility.getJsonKeyValue("occupation", primaryEmploymentDetailObj);
        contactTable.addCell(new Cell(1, 3).add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        contactTable.addCell(new Cell().add("Occupation: ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
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

        String industryType = jsonUtility.getJsonKeyValue("industryType", primaryEmploymentDetailObj);
        contactTable.addCell(new Cell(1, 3).add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        contactTable.addCell(new Cell().add("Nature of work/duties:  ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        contactTable.addCell(new Cell(1, 3).add(new Paragraph(natureOfWork).setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        contactTable.addCell(new Cell().add("Industry Type: ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
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
//            contactTable.addCell(new Cell(1, 3).add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
//            contactTable.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
//            p = new Paragraph();
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

        String organizationType = jsonUtility.getJsonKeyValue("organizationType", primaryEmploymentDetailObj);
        contactTable.addCell(new Cell(1, 3).add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        contactTable.addCell(new Cell().add("Organisation Type: ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
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
        contactTable.addCell(new Cell(1, 3).add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        proposerMergedcell.add(contactTable);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);

        String nameOfOrg = jsonUtility.getJsonKeyValue("nameOfOrganisation", primaryEmploymentDetailObj);
        String yearsInService = jsonUtility.getJsonKeyValue("experience", primaryEmploymentDetailObj);
        String income = jsonUtility.getJsonKeyValue("annualIncome", primaryEmploymentDetailObj);
        String sourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", primaryEmploymentDetailObj);
        // Age Proof Map
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

        // Address Proof Map
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

        // Identity Proof Map
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

        if (isOmniDoc && idProofMap.containsKey(primaryIdDocType)) {
            identityProof = idProofMap.get(primaryIdDocType);
        }
        if (isOmniDoc && ageProofMap.containsKey(primaryAgeDocType)) {
            ageProof = ageProofMap.get(primaryAgeDocType);
        }
        if (isOmniDoc && addressProofMap.containsKey(primaryAddressDocType)) {
            addressProof = addressProofMap.get(primaryAddressDocType);
        }

        proposerMergedcell = new Cell(1, 2);
        contactTable = new Table(new float[]{250F, 300F, 250F, 300F, 250F, 300F});
        p = new Paragraph("Identity Proof: ");
        p.add(new Text("\n(Proposer)").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(new Paragraph(identityProof).setUnderline()).setBorder(Border.NO_BORDER));
        p = new Paragraph("Address Proof:    ");
        p.add(new Text("\n(Proposer)").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(new Paragraph(addressProof).setUnderline()).setBorder(Border.NO_BORDER));
        p = new Paragraph("Age Proof:    ");
        p.add(new Text("\n(Proposer)").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(new Paragraph(ageProof).setUnderline()).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(new Cell().add(contactTable).setBorder(Border.NO_BORDER));
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);

        String panCardNo = jsonUtility.getJsonKeyValue("pancard", primaryPersonalDetailObj);

        Table otherDetails = new Table(new float[]{300F, 200F, 300F, 200F});
        otherDetails.addCell(new Cell().add("Name of the Org./Business :").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(new Paragraph(nameOfOrg).setUnderline()).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("Total Years in Service/ Business").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(yearsInService).setUnderline().setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("Income (Annual): ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        otherDetails.addCell(new Cell().add(new Paragraph(income).setUnderline()).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("Source of Income: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        otherDetails.addCell(new Cell().add(sourceOfIncome).setUnderline().setBorder(Border.NO_BORDER));

        p = new Paragraph("PAN: ");
        p.add("(Please provide Form 60, if PAN is not available)");
        otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(pdfUtility.createDataTable(panCardNo, "00000000000")).setBorder(Border.NO_BORDER));

        String primaryAgeProofDoc = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof", primaryDocumentDetailObj));
        String primaryPanCardName = jsonUtility.getJsonKeyValue("name", jsonUtility.getJsonObjectByKey("panCard", primaryDocumentDetailObj));
        p = new Paragraph("PAN ");
        p.add(" (photocopy Enclosed):");
        otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
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
        } else {
            p.add(imgUnchecked);
            p.add("  Yes  ");
            p.add(imgUnchecked);
            p.add("  No  ");
        }
        otherDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("Is this policy self proposed?").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        if (myself) {
            otherDetails.addCell(new Cell().add(imgChecked).setBorder(Border.NO_BORDER));
        } else {
            otherDetails.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
        }
        String relation = jsonUtility.getJsonKeyValue("relation", insuredPersonBasicDetailObj);
        otherDetails.addCell(new Cell().add("Relationship with Life to be Assured: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(new Paragraph(myself ? " " : relation).setUnderline()).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("Are you a Politically Exposed Person? 1) Proposer: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("politicallyExposed", primaryEmploymentDetailObj).equalsIgnoreCase("Yes")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("  Yes   ");
        if (jsonUtility.getJsonKeyValue("politicallyExposed", primaryEmploymentDetailObj).equalsIgnoreCase("No")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("  No  ");
        otherDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("2) Life to be Assured: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("politicallyExposed", secondaryEmploymentDetailObj).equalsIgnoreCase("Yes")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("  Yes   ");
        if (jsonUtility.getJsonKeyValue("politicallyExposed", secondaryEmploymentDetailObj).equalsIgnoreCase("No")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("  No  ");
        otherDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(new Cell().add(otherDetails).setBorder(Border.NO_BORDER));
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerDetails.addCell(proposerMergedcell);
        table.addCell(new Cell().add(proposerDetails).setBorder(Border.NO_BORDER));
        document.add(table);

        table = new Table(new float[]{60F, 400F});
        String ckycNo = "";
        p = new Paragraph("CKYC No.:    ");
        table.addCell(new Cell().add(p).setPaddingLeft(10F).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(pdfUtility.createDataTable(ckycNo, "0000000000")).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));


        JsonObject medicalDisabilityObj = jsonUtility.getJsonObjectByKey("disabilityQuestions", primaryOtherDetailObj);
        JsonObject haveDisabilityObj = jsonUtility.getJsonObjectByKey("have_disability", medicalDisabilityObj);
        boolean medicalStatus = jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("y");
        p = new Paragraph("Do you have any disability that restricts you from providing consent/ signature in the proposal form?  ");
        p.add(medicalStatus ? imgChecked : imgUnchecked);
        p.add("   Yes   ");
        boolean medicalStatusNo = jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("n");
        p.add(medicalStatusNo ? imgChecked : imgUnchecked);
        p.add("  No   ");
        table.addCell(new Cell(1, 2).add(p).setPaddingLeft(10F).setBorder(Border.NO_BORDER));
        document.add(table);
        document.add(new Paragraph("Politically Exposed Persons (PEPs) are individuals who are or have been entrusted with prominent public functions in a foreign country, example, Heads of State or of Governments, " + "senior politicians, senior government/judicial/military officials, senior executives of state owned corporations, important political party officials, etc., including their family members and " + "close relatives."));

        String proposerPlaceOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth", jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));
        String countryOfBirth = jsonUtility.getJsonKeyValue("countryOfBirthLabel", jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));

        table = new Table(1);
        table.setWidthPercent(100);
        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        contactTable = new Table(new float[]{350F, 200F, 350F, 200F});
        contactTable.addCell(new Cell().add("(a) Place of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(proposerPlaceOfBirth).setUnderline();
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("   and Country of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(countryOfBirth).setUnderline();
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Table additionalDetails = new Table(2);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(contactTable);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        additionalDetails.addCell(proposerMergedcell);

        additionalDetails.addCell(new Cell().add("(b) Are you a citizen of any other country also (Dual / Multiple): ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (!jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No      ");
        additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        additionalDetails.addCell(new Cell().add("(c) Are you a resident (For tax purposes) of any other country other than India.:  ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (!jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No      ");
        additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        additionalDetails.addCell(new Cell().add("(d) Do you hold a green card of US or any similar card for any other country: ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (!jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj)).equalsIgnoreCase("y")) {
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
        p = new Paragraph();
        p.setTextAlignment(TextAlignment.RIGHT);
        p.add(new Text("Application No. : " + applicationNumber).setBold());
        proposerMergedcell.add(p);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        additionalDetails.addCell(proposerMergedcell);
        table.addCell(new Cell().add(additionalDetails).setBorder(Border.NO_BORDER));
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
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));


//            Table lifeAssuredDetailDifferFromProposerDetails = this.getLifeAssuredDetailDifferFromProposer(imgChecked,imgUnchecked,insuredPersonBasicDetailObj,secondaryPersonalDetailObj,secondaryEmploymentDetailObj,secondaryFatcaDetailObj,otherSetOccupation, myself,innerProposer,isOmniDoc,secondaryDocumentDetailObj);
//            table.addCell(new Cell().add(lifeAssuredDetailDifferFromProposerDetails).setBorder(Border.NO_BORDER));

        Table lifeAssuredDetails = new Table(2);

        String lifeAssuredFullName = "";
        String lifeaAssuredDoB = "";
        String lifeAssuredNameOfOrg = "";
        String lifeAssuredYearsInService = "";
        String lifeAssuredIncome = "";
        String lifeAssuredSourceOfIncome = "";

        if (!myself) {

            lifeAssuredFullName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
            lifeaAssuredDoB = jsonUtility.getJsonKeyValue("dateOfBirth", insuredPersonBasicDetailObj);
            birthDate = LocalDate.parse(lifeaAssuredDoB);
            lifeaAssuredDoB = dateTimeFormatter.format(birthDate);
            age = Period.between(birthDate, currentDate).getYears();
            String insuredGender = jsonUtility.getJsonKeyValue("gender", insuredPersonBasicDetailObj);

            lifeAssuredDetails.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)").setBorder(Border.NO_BORDER));
            p = new Paragraph("Mr.  ");
            if (insuredGender.equalsIgnoreCase("MALE")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mrs. ");
            if (insuredGender.equalsIgnoreCase("FEMALE") && maritalStatus.equalsIgnoreCase("MARRIED")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Ms.  ");
            if (insuredGender.equalsIgnoreCase("FEMALE") && maritalStatus.equalsIgnoreCase("SINGLE")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mx.  ");
            if (insuredGender.equalsIgnoreCase("OTHER")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            lifeAssuredDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName, "00000000000000000000")).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell);

            /////////////////////
            lifeAssuredDetails.addCell(new Cell().add("Existing IndiaFirst Policy Owner, Kindly enter policy number / client id").setBorder(Border.NO_BORDER));
            lifeAssuredDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));
            p = new Paragraph("Communication Address of the Life Assured (Address to which policy document will be dispatched)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);

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
            if (landmarkSecondary.length() > 0) {
                if (landmarkSecondary.length() <= 10) {
                    landmarkSecondary += "                                        ";
                } else if (landmarkSecondary.length() > 10 && landmarkSecondary.length() <= 20) {
                    landmarkSecondary += "                              ";
                } else {
                    landmarkSecondary += "                ";
                }
            }
            String permanentlandmarkSecondary = jsonUtility.getJsonKeyValue("permanentlandmark", secondaryPersonalDetailObj);
            if (permanentlandmarkSecondary.length() > 0) {
                if (permanentlandmarkSecondary.length() <= 10) {
                    permanentlandmarkSecondary += "                                        ";
                } else if (permanentlandmarkSecondary.length() > 10 && permanentlandmarkSecondary.length() <= 20) {
                    permanentlandmarkSecondary += "                              ";
                } else {
                    permanentlandmarkSecondary += "                ";
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
            if (!state.isEmpty()) {
                if (state.length() <= 10) {
                    state += "                                 ";
                } else if (state.length() > 10 && state.length() <= 20) {
                    state += "                       ";
                } else {
                    state += "              ";
                }
            }
            String permanentaddressSecline1 = jsonUtility.getJsonKeyValue("permanentaddressline1", secondaryPersonalDetailObj);

            if (permanentaddressSecline1.length() > 0) {
                if (permanentaddressSecline1.length() <= 10) {
                    permanentaddressSecline1 += "                                        ";
                } else if (permanentaddressSecline1.length() > 10 && permanentaddressSecline1.length() <= 20) {
                    permanentaddressSecline1 += "                              ";
                } else {
                    permanentaddressSecline1 += "                ";
                }
            }
            String permanentaddressSecline2 = jsonUtility.getJsonKeyValue("permanentaddressline2", secondaryPersonalDetailObj);
            if (!permanentaddressSecline2.isEmpty()) {
                if (permanentaddressSecline2.length() <= 10) {
                    permanentaddressSecline2 += "                                        ";
                } else if (permanentaddressSecline2.length() > 10 && permanentaddressSecline2.length() <= 20) {
                    permanentaddressSecline2 += "                              ";
                } else if (permanentaddressSecline2.length() > 20 && permanentaddressSecline2.length() <= 30) {
                    permanentaddressSecline2 += "                  ";
                } else if (permanentaddressSecline2.length() > 30 && permanentaddressSecline2.length() <= 40) {
                    permanentaddressSecline2 += "           ";
                } else {
                    permanentaddressSecline2 += "";
                }
            }
            String permanentaddressSecline3 = jsonUtility.getJsonKeyValue("permanentaddressline3", secondaryPersonalDetailObj);
            if (!permanentaddressSecline3.isEmpty()) {
                if (permanentaddressSecline3.length() <= 10) {
                    permanentaddressSecline3 += "                                        ";
                } else if (permanentaddressSecline3.length() > 10 && permanentaddressSecline3.length() <= 20) {
                    permanentaddressSecline3 += "                              ";
                } else {
                    permanentaddressSecline3 += "                     ";
                }
            }
            String pincodeSecondary = jsonUtility.getJsonKeyValue("pincode", secondaryPersonalDetailObj);
            if (pincodeSecondary.length() > 0) {
            } else {
                pincodeSecondary += "      ";
            }


            String permanentcitySecondary = jsonUtility.getJsonKeyValue("permanentcity", secondaryPersonalDetailObj);
            if (permanentcitySecondary.length() > 0) {
                if (permanentcitySecondary.length() <= 10) {
                    permanentcitySecondary += "                                        ";
                } else if (permanentcitySecondary.length() > 10 && permanentcitySecondary.length() <= 20) {
                    permanentcitySecondary += "                              ";
                } else {
                    permanentcitySecondary += "                ";
                }
            }
            String permanentstateSecondary = jsonUtility.getJsonKeyValue("permanentstate", secondaryPersonalDetailObj);
            if (permanentstateSecondary.length() > 0) {
                if (permanentstateSecondary.length() <= 10) {
                    permanentstateSecondary += "                                        ";
                } else if (permanentstateSecondary.length() > 10 && permanentstateSecondary.length() <= 20) {
                    permanentstateSecondary += "                              ";
                } else {
                    permanentstateSecondary += "                ";
                }
            }
            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(landmarkSecondary, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            addressBlock = new Table(new float[]{600F, 100F, 200F});
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state, "000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add("Pin Code:  ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(pincodeSecondary, "000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            //Permanent Address Section addition started
            p = new Paragraph("Permanent Address (If different from the above Address)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);
            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressSecline1, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressSecline2, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentaddressSecline3, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentlandmarkSecondary, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentcitySecondary, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(permanentstateSecondary, "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //Permanent Address Section addition ended

            countryCode = jsonUtility.getJsonKeyValue("countryCode", insuredPersonBasicDetailObj);
            contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F, 200F});
            contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("+").setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(countryCode, "00")).setBorder(Border.NO_BORDER));

            mobileNo = jsonUtility.getJsonKeyValue("mobileNumber", secondaryPersonalDetailObj);
            pinCode = jsonUtility.getJsonKeyValue("permanentpincode", secondaryPersonalDetailObj);
            p = new Paragraph("Mobile No* ");
            p.add(new Text("\n*Receive alerts through SMS and WhatsApp for this proposal / policy").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(mobileNo, "0000000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(pinCode, "000000")).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell);
            /////////////////////

            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(new float[]{200F, 600F, 200F, 400F});
            emailId = jsonUtility.getJsonKeyValue("emailId", secondaryPersonalDetailObj);
            landLine = "";
            p = new Paragraph("Email ID* ");
            p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(emailId, "000000000000000")).setBorder(Border.NO_BORDER));

            p = new Paragraph("Landline:");
            p.add(new Text("\nSTD/ISD").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(landLine, "00000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Gender:    ").setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph("Male: ");
            if (insuredGender.equalsIgnoreCase("MALE")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Female:     ");
            if (insuredGender.equalsIgnoreCase("FEMALE")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Transgender:    ");
            if (insuredGender.equalsIgnoreCase("OTHER")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Nationality:    ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            String secondaryNationality = jsonUtility.getJsonKeyValue("nationality", secondaryPersonalDetailObj);
            p = new Paragraph("Indian:      ");
            if (secondaryNationality.equalsIgnoreCase("INDIAN")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Non Indian:     ");
            if (secondaryNationality.equalsIgnoreCase("NON INDIAN")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            pointColumnWidths = new float[dob.length()];
            contactTable.addCell(new Cell().add("DOB :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            for (int i = 0; i < lifeaAssuredDoB.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!lifeaAssuredDoB.isEmpty()) {
                tableCodes = pdfUtility.codeTable(lifeaAssuredDoB, pointColumnWidths);
                contactTable.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            } else {
                contactTable.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }
            contactTable.addCell(new Cell().add("Age:  ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(age + " Years").setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Marital Status :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            String insuredMaritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", secondaryPersonalDetailObj);
            p = new Paragraph("Unmarried:   ");
            if (insuredMaritalStatus.equalsIgnoreCase("UNMARRIED")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Married:  ");
            if (insuredMaritalStatus.equalsIgnoreCase("MARRIED")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Widow(er):  ");
            if (insuredMaritalStatus.equalsIgnoreCase("WIDOW")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Divorced:   ");
            if (insuredMaritalStatus.equalsIgnoreCase("DIVORCE")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Residential Status:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            String insuredResidentialStatus = jsonUtility.getJsonKeyValue("residentialstatus", secondaryPersonalDetailObj);
            p = new Paragraph();
            p.add("Resident:    ");
            if (insuredResidentialStatus.equalsIgnoreCase("RNT")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     NRI:    ");
            if (insuredResidentialStatus.equalsIgnoreCase("NRI")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     PIO:    ");
            if (insuredResidentialStatus.equalsIgnoreCase("PIO")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Education:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            String empSecEducationType = jsonUtility.getJsonKeyValue("educationType", secondaryEmploymentDetailObj);
            p = new Paragraph();
            p.add("     Post Grad:   ");
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
            contactTable.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Occupation:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            String empSecOccupation = jsonUtility.getJsonKeyValue("occupation", secondaryEmploymentDetailObj);
            p = new Paragraph();
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
            contactTable.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(new Cell().add(contactTable).setBorder(Border.NO_BORDER));
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell);

            lifeAssuredNameOfOrg = jsonUtility.getJsonKeyValue("nameOfOrganisation", secondaryEmploymentDetailObj);
            lifeAssuredYearsInService = jsonUtility.getJsonKeyValue("experience", secondaryEmploymentDetailObj);
            lifeAssuredIncome = jsonUtility.getJsonKeyValue("annualIncome", secondaryEmploymentDetailObj);
            lifeAssuredSourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", secondaryEmploymentDetailObj);

            otherDetails = new Table(new float[]{300F, 200F, 300F, 200F});
            otherDetails.addCell(new Cell().add("Name of the Org./Business :").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Paragraph(lifeAssuredNameOfOrg).setUnderline()).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add("Total Years in Service/ Business").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(lifeAssuredYearsInService).setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Income (Annual): ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Paragraph(lifeAssuredIncome).setUnderline()).setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Source of Income: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(lifeAssuredSourceOfIncome).setUnderline().setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Nature of work/duties: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(lifeAssuredSourceOfIncome).setUnderline().setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Age Proof:    ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));

            String secondaryAgeDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof-all", secondaryDocumentDetailObj));
            String secondaryIdDocType = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("idProof", secondaryDocumentDetailObj));
            String secondaryAgeProofDoc = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof", secondaryDocumentDetailObj));
            String secondaryPanCardName = jsonUtility.getJsonKeyValue("name", jsonUtility.getJsonObjectByKey("panCard", secondaryDocumentDetailObj));
            p = new Paragraph("PAN  ");
            p.add(" (photocopy Enclosed):     ");
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
            } else {
                p.add(imgUnchecked);
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No  ");
            }
            otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            panCardNo = jsonUtility.getJsonKeyValue("pancard", secondaryPersonalDetailObj);
            p = new Paragraph("PAN: ");
            p.add("(Please provide Form 60, if PAN is not available)  ");
            otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[panCardNo.length()];
            for (int i = 0; i < panCardNo.length(); i++) {
                pointColumnWidths[i] = 20F;
            }
            if (!panCardNo.isEmpty()) {
                tableCodes = pdfUtility.codeTable(panCardNo, pointColumnWidths);
                otherDetails.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            } else {
                otherDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            }
//                otherDetails.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Cell().add(" ").setUnderline()).setBorder(Border.NO_BORDER));
            lifeAssuredDetails.addCell(new Cell(1, 2).add(otherDetails).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(lifeAssuredDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            String lifeAssuredPlaceOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth", jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaDetailObj));
            String lifeAssuredCountryOfBirth = jsonUtility.getJsonKeyValue("countryOfBirthLabel", jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaDetailObj));

            contactTable = new Table(new float[]{350F, 200F, 350F, 200F});
            contactTable.addCell(new Cell().add("(a) Place of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(lifeAssuredPlaceOfBirth).setUnderline();
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("   and Country of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(lifeAssuredCountryOfBirth).setUnderline();
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Table lifeAdditionalDetails = new Table(2);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAdditionalDetails.addCell(proposerMergedcell);
            lifeAdditionalDetails.addCell(new Cell().add("(b) Are you a citizen of any other country also (Dual / Multiple): ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            lifeAdditionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            lifeAdditionalDetails.addCell(new Cell().add("(c) Are you a resident (For tax purposes) of any other country other than India.:  ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            lifeAdditionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            lifeAdditionalDetails.addCell(new Cell().add("(d) Do you hold a green card of US or any similar card for any other country: ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes     ");
            if (!jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaDetailObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            lifeAdditionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
            proposerMergedcell.add(p);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAdditionalDetails.addCell(proposerMergedcell);
            table.addCell(new Cell().add(lifeAdditionalDetails).setBorder(Border.NO_BORDER));
            /********************** -------------------- END -------------------  *****************************/
        } else {

            lifeAssuredDetails.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)").setBorder(Border.NO_BORDER));
            p = new Paragraph("Mr.  ");
            p.add(imgUnchecked);
            p.add("     Mrs. ");
            p.add(imgUnchecked);
            p.add("     Ms.  ");
            p.add(imgUnchecked);
            p.add("     Mx.  ");
            p.add(imgUnchecked);
            lifeAssuredDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName, "00000000000000000000")).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell);

            /////////////////////
            lifeAssuredDetails.addCell(new Cell().add("Existing IndiaFirst Policy Owner, Kindly enter policy number / client id").setBorder(Border.NO_BORDER));
            lifeAssuredDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));
            p = new Paragraph("Communication Address of the Proposer (Address to which policy document will be dispatched)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);

            proposerMergedcell.add(p);
            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            addressBlock = new Table(new float[]{600F, 100F, 200F});
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add("Pin Code:  ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

            //Permanent Address Section addition started
            p = new Paragraph("Permanent Address (If different from the above Address)");
            p.setPaddingBottom(10);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(p);
            addressBlock = new Table(1);
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000000000000000000000000000000000")).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //Permanent Address Section addition ended

            contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F, 200F});
            contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("+").setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable("", "00")).setBorder(Border.NO_BORDER));

            p = new Paragraph("Mobile No* ");
            p.add(new Text("\n*Receive alerts through SMS and WhatsApp for this proposal / policy").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable("", "0000000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable("", "000000")).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell);
            /////////////////////

            proposerMergedcell = new Cell(1, 2);
            contactTable = new Table(new float[]{200F, 600F, 200F, 400F});
            emailId = "";
            landLine = "";
            p = new Paragraph("Email ID* ");
            p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(emailId, "000000000000000")).setBorder(Border.NO_BORDER));

            p = new Paragraph("Landline:");
            p.add(new Text("\nSTD/ISD").setFontSize(5F));
            contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(landLine, "00000000")).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Gender:    ").setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph("Male: ");
            p.add(imgUnchecked);
            p.add("     Female:     ");
            p.add(imgUnchecked);
            p.add("     Transgender:    ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Nationality:    ").setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph("Indian:      ");
            p.add(imgUnchecked);
            p.add("     Non Indian:     ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("DOB :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            contactTable.addCell(new Cell().add(pdfUtility.createDataTable(lifeaAssuredDoB, "00000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Age:  ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add(" Years").setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Marital Status :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            p = new Paragraph("Unmarried:   ");
            p.add(imgUnchecked);
            p.add("     Married:  ");
            p.add(imgUnchecked);
            p.add("     Widow(er):  ");
            p.add(imgUnchecked);
            p.add("     Divorced:   ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Residential Status:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            p = new Paragraph();
            p.add("Resident:    ");
            p.add(imgUnchecked);
            p.add("     NRI:    ");
            p.add(imgUnchecked);
            p.add("     PIO:    ");
            p.add(imgUnchecked);
            contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Education:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            p = new Paragraph();
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
            contactTable.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("Occupation:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            p = new Paragraph();
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
            contactTable.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(new Cell().add(contactTable).setBorder(Border.NO_BORDER));
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAssuredDetails.addCell(proposerMergedcell);

            otherDetails = new Table(new float[]{300F, 200F, 300F, 200F});
            otherDetails.addCell(new Cell().add("Name of the Org./Business :").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Paragraph(lifeAssuredNameOfOrg).setUnderline()).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add("Total Years in Service/ Business").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(lifeAssuredYearsInService).setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Income (Annual): ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Paragraph(lifeAssuredIncome).setUnderline()).setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Source of Income: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(lifeAssuredSourceOfIncome).setUnderline().setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Nature of work/duties: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(lifeAssuredSourceOfIncome).setUnderline().setBorder(Border.NO_BORDER));

            otherDetails.addCell(new Cell().add("Age Proof:    ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("PAN  ");
            p.add(" (photocopy Enclosed): ");
            p.add(imgUnchecked);
            p.add("  Yes  ");
            p.add(imgUnchecked);
            p.add("  No  ");
            otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph("PAN: ");
            p.add("(Please provide Form 60, if PAN is not available)  ");
            otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(pdfUtility.createDataTable("", "00000000000")).setBorder(Border.NO_BORDER));
            otherDetails.addCell(new Cell().add(new Cell().add(" ").setUnderline()).setBorder(Border.NO_BORDER));
            lifeAssuredDetails.addCell(new Cell(1, 2).add(otherDetails).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(lifeAssuredDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(Color.GRAY, 100);
            p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

            String lifeAssuredPlaceOfBirth = " ";
            String lifeAssuredCountryOfBirth = " ";

            contactTable = new Table(new float[]{350F, 200F, 350F, 200F});
            contactTable.addCell(new Cell().add("(a) Place of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(lifeAssuredPlaceOfBirth).setUnderline();
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            contactTable.addCell(new Cell().add("   and Country of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(lifeAssuredCountryOfBirth).setUnderline();
            contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            Table lifeAdditionalDetails = new Table(2);
            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(contactTable);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAdditionalDetails.addCell(proposerMergedcell);
            lifeAdditionalDetails.addCell(new Cell().add("(b) Are you a citizen of any other country also (Dual / Multiple): ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No      ");
            lifeAdditionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            lifeAdditionalDetails.addCell(new Cell().add("(c) Are you a resident (For tax purposes) of any other country other than India.:  ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No      ");
            lifeAdditionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            lifeAdditionalDetails.addCell(new Cell().add("(d) Do you hold a green card of US or any similar card for any other country: ").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(imgUnchecked);
            p.add("     Yes     ");
            p.add(imgUnchecked);
            p.add("     No      ");
            lifeAdditionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            p = new Paragraph();
            p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
            proposerMergedcell.add(p);
            proposerMergedcell.setBorder(Border.NO_BORDER);
            lifeAdditionalDetails.addCell(proposerMergedcell);
            table.addCell(new Cell().add(lifeAdditionalDetails).setBorder(Border.NO_BORDER));
            /********************** -------------------- END -------------------  *****************************/
        }

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("3. Nominee/ Appointee Details (To be filled in case life to be assured and proposer are same. Appointee details required only if nominee is a minor)");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        JsonArray nomineeList = nomineeDetailObj.get("nominees").getAsJsonArray();

        Table nomineeDetails = new Table(new float[]{100F, 70F, 70F, 70F, 100F, 100F, 140F, 140F, 90F});
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
        table.addCell(new Cell().add(nomineeDetails).setBorder(Border.NO_BORDER));

        Table appointeeDetails = new Table(6);
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
                age = Period.between(birthDate, currentDate).getYears();
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
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("4. Plan Details");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        String planName = jsonUtility.getJsonKeyValue("planName", planDetailObj);
        String planTerm = jsonUtility.getJsonKeyValue("policyTerm", planDetailObj);
        String premiumPlayingTerm = jsonUtility.getJsonKeyValue("premiumPayingTerm", planDetailObj);
        String premiumInstallment = jsonUtility.getJsonKeyValue("totalPremiumAmount", planDetailObj);
        String sumAssured = jsonUtility.getJsonKeyValue("sum_assured", planDetailObj);

        Table planDetails = new Table(new float[]{400F, 100F, 200F, 150F, 200F});
        planDetails.addCell(new Cell().add("Plan Name").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("Policy Term").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("Premium Paying Term").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("Installment Premium").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("Sum Assured").setTextAlignment(TextAlignment.CENTER));

        planDetails.addCell(planName);
        planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));

        planName = "IndiaFirst Term Rider";
        planTerm = "";
        premiumPlayingTerm = "";
        premiumInstallment = "";
        sumAssured = "";

        planDetails.addCell(planName);
        planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));

        JsonObject responseObj = jsonUtility.getJsonObjectByKey("response", planDetailObj);
        planName = "IndiaFirst Life Waiver Of Premium Rider";
        planTerm = jsonUtility.getJsonKeyValue("policyTerm", planDetailObj);
        premiumPlayingTerm = jsonUtility.getJsonKeyValue("premiumPayingTerm", planDetailObj);
        premiumInstallment = jsonUtility.getJsonKeyValue("totalRiderPremiumamtBaseRiderPremiumRiderGST", responseObj);
        sumAssured = jsonUtility.getJsonKeyValue("riderSumAssured", responseObj);
        p = new Paragraph(planName);
        p.add("\n");

        String wOPRiskCover = jsonUtility.getJsonKeyValue("wOPRiskCover", planDetailObj);
        if (wOPRiskCover.equalsIgnoreCase("WOP on Death")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Option 1    ");
        if (wOPRiskCover.equalsIgnoreCase("WOP on Death or Accidental Total Permanent Disability or Critical Illness")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Option 2    ");
        if (wOPRiskCover.equalsIgnoreCase("WOP on Accidental Total Permanent Disability or diagnosis of Critical Illness")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Option 3    ");

        planDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE));
        if (!wOPRiskCover.isEmpty()) {
            planDetails.addCell(new Cell().add(planTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumPlayingTerm).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(premiumInstallment).setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add(sumAssured).setTextAlignment(TextAlignment.CENTER));
        } else {
            planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
            planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        }

        //2 new added plan
        planDetails.addCell(new Cell().add("IndiaFirst Life Accidental Death Benefit Rider (ADB)").setVerticalAlignment(VerticalAlignment.MIDDLE));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));

        planDetails.addCell(new Cell().add("IndiaFirst Life Total and Permanent Disability Rider (TPD)").setVerticalAlignment(VerticalAlignment.MIDDLE));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));
        planDetails.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER));

        String frequency = jsonUtility.getJsonKeyValue("frequency", planDetailObj);
        String investmentFrequency = jsonUtility.getJsonKeyValue("investmentFrequency", planDetailObj);
        p = new Paragraph("Premium Frequency:   ");
        if (frequency.equalsIgnoreCase("single") || investmentFrequency.equalsIgnoreCase("single")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Single      ");
        if (frequency.equalsIgnoreCase("yearly") || investmentFrequency.equalsIgnoreCase("yearly")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yearly      ");
        if (frequency.equalsIgnoreCase("half yearly") || investmentFrequency.equalsIgnoreCase("half yearly")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Half Yearly      ");
        if (frequency.equalsIgnoreCase("quarterly") || investmentFrequency.equalsIgnoreCase("quarterly")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Quarterly      ");
        if (frequency.equalsIgnoreCase("Monthly") || investmentFrequency.equalsIgnoreCase("Monthly")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     * Monthly (Only ECS/ Direct debit).      ");
        planDetails.addCell(new Cell(1, 5).add(p));
        table.addCell(new Cell().add(planDetails).setBorder(Border.NO_BORDER));
        p = new Paragraph("* ECS/DD with cancel cheque copy and DD mandate should be verified by bank branch   ");
        p.add("     Renewal Premium Payment " + "Options : 1. *Standing Instructions      ");
        p.add(imgUnchecked);
        p.add("     2. Cheque      ");
        p.add(imgUnchecked);
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Note: ");
        p.add(new Text("IndiaFirst Term Rider").setBold());
        p.add(" is applicable for IndiaFirst Maha Jeevan Plan, IndiaFirst Life Long Guaranteed Income Plan & IndiaFirst Life Mahajeevan Plus Plan. ");
        p.add(new Text("\nIndiaFirst Life Waiver Of Premium Rider").setBold());
        p.add(" is applicable for IndiaFirst Maha Jeevan Plan, IndiaFirst Life Smart Pay Plan, IndiaFirst Life Long Guaranteed Income Plan, " + "IndiaFirst Life Guaranteed Benefit Plan, IndiaFirst Life Mahajeevan Plus Plan, IndiaFirst Life Fortune Plus Plan & IndiaFirst Life Guarantee Of Life Dreams Plan.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("I would like to fund my future premium with the survival benefit –       ");
        p.add("     Yes     ");
        p.add(imgUnchecked);
        p.add("     No      ");
        p.add(imgUnchecked);
        p.add("     (Applicable only for IndiaFirst Life Smart Pay Plan & IndiaFirst Life Mahajeevan Plus Plan)");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        p = new Paragraph("(Please select the appropriate option for ");
        p.add(new Text(" IndiaFirst Life Smart Pay Plan, IndiaFirst Life Long Guaranteed Income Plan, IndiaFirst Life Guaranteed Benefit Plan, IndiaFirst Life Mahajeevan Plus Plan & IndiaFirst Life Guarantee Of Life Dreams Plan").setBold());
        p.add(")");
        p.add(new Text("\nDeath Benefit Option         ").setBold());
        p.add(imgChecked);
        p.add("     Lump Sum    ");
        p.add(imgUnchecked);
        p.add("     Income    ");
        p.add(imgUnchecked);
        p.add("     5 Years    ");


        Table champDetails = new Table(1);
        p = new Paragraph("(Please select the appropriate option for ");
        p.add(new Text("IndiaFirst Life Little Champ Plan").setBold());
        p.add(")");
        champDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph(new Text("Risk Cover Option").setBold());
        p.add("     ");
        p.add(imgUnchecked);
        p.add("     Death Cover    ");
        p.add(imgUnchecked);
        p.add("     Accidental Death Cover    ");
        p.add(imgUnchecked);
        p.add("     Accidental Disability Cover    ");
        p.add(imgUnchecked);
        p.add("     Comprehensive Cover    ");
        champDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph(new Text("Total Payout Option").setBold());
        p.add("     ");
        p.add(imgUnchecked);
        p.add("     1) 101%    ");
        p.add(imgUnchecked);
        p.add("     2) 102%    ");
        p.add(imgUnchecked);
        p.add("     3) 105%    ");
        p.add(imgUnchecked);
        p.add("     4) 107%    ");
        p.add(imgUnchecked);
        p.add("     5) 110%    ");
        p.add(imgUnchecked);
        p.add("     6) 115%    ");
        p.add(imgUnchecked);
        p.add("     7) 120%    ");
        p.add(imgUnchecked);
        p.add("     8) 125%    ");
        champDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(champDetails).setVerticalAlignment(VerticalAlignment.MIDDLE));

        champDetails = new Table(1);
        p = new Paragraph("(Please select the appropriate option for IndiaFirst Life Little Champ Plan,");
        p.add(new Text("IndiaFirst Life Guaranteed Monthly Income Plan, IndiaFirst Life Smart Pay Plan, IndiaFirst Life Long Guaranteed Income Plan and IndiaFirst " + "Life Guaranteed Benefit Plan").setBold());
        Table newGoldSection = new Table(new float[]{800F, 300F});
        p = new Paragraph("Select Income Option for ");
        p.add(new Text("IndiaFirst Life Guarantee Of Life Dreams Plan (").setBold());
        p.add("(select one of the below options)");
        newGoldSection.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Special Date (For 'Save the Date', if opted)");
        newGoldSection.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        String incomeOption = jsonUtility.getJsonKeyValue("incomeOption", planDetailObj);
        p = new Paragraph("     ");
        if (incomeOption.equalsIgnoreCase("Immediate Income Option")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Immediate Income     ");
        if (incomeOption.equalsIgnoreCase("Intermediate Income Option")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Intermediate Income     ");
        if (incomeOption.equalsIgnoreCase("Deferred Income Option")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Deferred Income     ");
        newGoldSection.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        String dd = "";
        String mm = "";

        String savePayoutDate = jsonUtility.getJsonKeyValue("savePayoutDate", planDetailObj);

        if (savePayoutDate.equalsIgnoreCase("yes")) {
            dd = jsonUtility.getJsonKeyValue("payoutDate", planDetailObj).split("/")[0];
            mm = jsonUtility.getJsonKeyValue("payoutDate", planDetailObj).split("/")[1];
        }
        logger.info("savePayoutDate : {}, ", savePayoutDate);
        pointColumnWidths = new float[dd.length()];
        Table dateOpted = new Table(new float[]{40F, 25F, 40F});
        for (int i = 0; i < dd.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (dd.length() > 0) {
            dateOpted.addCell(new Cell().add(pdfUtility.codeTable(dd, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            dateOpted.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        dateOpted.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[mm.length()];
        for (int i = 0; i < mm.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (mm.length() > 0) {
            dateOpted.addCell(new Cell().add(pdfUtility.codeTable(mm, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            dateOpted.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        String payoutDate = jsonUtility.getJsonKeyValue("payoutDate", planDetailObj);
        String day = payoutDate.isBlank() ? "" : payoutDate.split("/")[0];
        String month = payoutDate.isBlank() ? "" : payoutDate.split("/")[1];
        dateOpted.addCell(new Cell().add("DD").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        dateOpted.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        dateOpted.addCell(new Cell().add("MM").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        newGoldSection.addCell(new Cell().add(dateOpted).setBorder(Border.NO_BORDER));

        String incomePayoutFrequency = jsonUtility.getJsonKeyValue("incomePayoutFrequency", planDetailObj);
        p = new Paragraph("Please choose appropriate Income Benefit Frequency       ");

        if (incomePayoutFrequency.equalsIgnoreCase("YEARLY")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yearly     ");
        if (incomePayoutFrequency.equalsIgnoreCase("HALF YEARLY") || incomePayoutFrequency.equalsIgnoreCase("HALF-YEARLY")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Half Yearly     ");
        if (incomePayoutFrequency.equalsIgnoreCase("QUARTERLY")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Quarterly     ");
        if (incomePayoutFrequency.equalsIgnoreCase("MONTHLY")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Monthly     ");
        newGoldSection.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(newGoldSection));

        p = new Paragraph("For ");
        p.add(new Text("IndiaFirst Life Long Guaranteed Income Plan").setBold());
        p.add(" please choose appropriate Income Benefit Frequency");
        p.add("     ");
        p.add(imgUnchecked);
        p.add("     Yearly    ");
        p.add(imgUnchecked);
        p.add("     Half Yearly    ");
        p.add(imgUnchecked);
        p.add("     Quarterly    ");
        p.add(imgUnchecked);
        p.add("     Monthly    ");
        table.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE));

        Table table2 = new Table(1);
        Table champBenefitOptions = new Table(new float[]{500F, 500F});
        p = new Paragraph("(Please select the appropriate option for ");
        p.add(new Text("IndiaFirst Life Guaranteed Benefit Plan").setBold());
        p.add("\n");
        p.add(new Text("Benefit Option").setBold());
        p.add("\n");
        champBenefitOptions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("If Income Benefit Option is chosen, please mention below details:-");
        p.add("\nMonthly Income(Rs)");
        p.add(new Text("______________________"));
        p.add("GAP Period(Years)");
        p.add(new Text("______________________"));
        p.add("Income Period(Years)");
        p.add(new Text("______________________"));
        champBenefitOptions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        Table benefitOptions = new Table(1);
        p = new Paragraph();
        p.add("     ");
        p.add(imgUnchecked);
        p.add("                 Lumpsum Benefit    \n");
        benefitOptions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("                 Income Benefit    ");
        benefitOptions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        champBenefitOptions.addCell(new Cell().add(benefitOptions).setPaddingLeft(40).setBorder(Border.NO_BORDER));
        champDetails.addCell(new Cell().add(champBenefitOptions).setBorder(Border.NO_BORDER));
        table2.addCell(new Cell().add(champDetails));
        table.addCell(new Cell().add(table2).setBorder(Border.NO_BORDER));

//            Table benefitOptionDetails = new Table(new float[]{300F, 800F});
//            p = new Paragraph("(Please select the appropriate option for IndiaFirst Life Little Champ Plan,  ");
//            p.add(new Text("IndiaFirst Life Guaranteed Monthly Income Plan, IndiaFirst Life Smart Pay Plan, IndiaFirst Life Long Guaranteed Income Plan and IndiaFirst " + "Life Guaranteed Benefit Plan)").setBold());
//            benefitOptionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//            p = new Paragraph("For  ");
//            p.add(new Text("IndiaFirst Life Long Guaranteed Income Plan").setBold());
//            p.add(" please choose appropriate Gap Year:");
//            p.add("     0 Years    ");
//            p.add(imgUnchecked);
//            p.add("     3 Years    ");
//            p.add(imgUnchecked);
//            p.add("     5 Years    ");
//            p.add("\n(Please select the appropriate option for IndiaFirst Life Little Champ Plan)");
//            benefitOptionDetails.addCell(new Cell(1, 2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            benefitOptionDetails.addCell(new Cell().add("Risk Cover Option").setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Death Cover    ");
//            p.add(imgUnchecked);
//            p.add("     Accidental Death Cover    ");
//            p.add(imgUnchecked);
//            p.add("     Accidental Disability Cover    ");
//            p.add(imgUnchecked);
//            p.add("     Comprehensive Cover    ");
//            benefitOptionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            benefitOptionDetails.addCell(new Cell().add("Total Payout Option").setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     1) 101%    ");
//            p.add(imgUnchecked);
//            p.add("     2) 102%    ");
//            p.add(imgUnchecked);
//            p.add("     3) 105%    ");
//            p.add(imgUnchecked);
//            p.add("     4) 107%    ");
//            p.add(imgUnchecked);
//            p.add("     5) 110%    ");
//            p.add(imgUnchecked);
//            p.add("     6) 115%    ");
//            p.add(imgUnchecked);
//            p.add("     7) 120%    ");
//            p.add(imgUnchecked);
//            p.add("     8) 125%    ");
//            benefitOptionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("For  ");
//            p.add(new Text("IndiaFirst Life Long Guaranteed Income Plan").setBold());
//            p.add(" please choose appropriate Income Benefit Frequency");
//            benefitOptionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Yearly    ");
//            p.add(imgUnchecked);
//            p.add("     Half Yearly    ");
//            p.add(imgUnchecked);
//            p.add("     Quarterly    ");
//            p.add(imgUnchecked);
//            p.add("     Monthly    ");
//            benefitOptionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//
//            p = new Paragraph("(Please select the appropriate option for\n ");
//            p.add(new Text("IndiaFirst Life Guaranteed Benefit Plan").setBold());
//            p.add(")\n");
//            p.add(new Text("Benefit Option").setBold());
//            p.add("\n");
//            benefitOptionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            Table optionDetails = new Table(new float[]{100F, 700F});
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add("     Lumpsum Benefit    ");
//            optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add("     Income Benefit    ");
//            optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//
//            String monthlyIncome = "1000000";
//            String gapYears = "5";
//            String incomePeriod = "10";
//
//            p = new Paragraph("If Income Benefit Option is chosen, please mention below details:-\n");
//            p.add("Monthly Income(Rs)  ");
//            p.add(new Text(monthlyIncome).setUnderline());
//            p.add("     GAP Period(Years)  ");
//            p.add(new Text(gapYears).setUnderline());
//            p.add("     Income Period(Years) ");
//            p.add(new Text(incomePeriod).setUnderline());
//            benefitOptionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//
//            String sumAssuredReduced = "   ";
        // p=new Paragraph("
        // ropriate Options for ")
        // p = new Paragraph(new Text("IndiaFirst Life Guaranteed Protection Plus Plan
        // ").setBold());
        // p.add("(Select any one of the below Options)");
        Table fortunePlusDetails = new Table(new float[]{800F, 300F});
        p = new Paragraph("For ");
        p.add(new Text("IndiaFirst Life Fortune Plus Plan").setBold());
        p.add(" please choose appropriate option for Guaranteed Survival Benefit & Cash Bonus: ");
        fortunePlusDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Payout       ");
        p.add(imgUnchecked);
        p.add("     Accrual       ");
        fortunePlusDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("If Payout Option is chosen, then Payout Frequency ");
        p.add(new Text("_________________"));
        fortunePlusDetails.addCell(new Cell(1, 2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(fortunePlusDetails));

        Table benefitOptionDetails = new Table(new float[]{300F, 800F});
        p = new Paragraph("Please Select Appropriate Options for ");
        p.add(new Text("IndiaFirst Life Guaranteed Protection Plus Plan").setBold());
        p.add(" (Select any one of the below Options)");
        benefitOptionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
        Table optionDetails = new Table(new float[]{100F, 700F});

        String planOptionName = jsonUtility.getJsonKeyValue("planOption", planDetailObj);
        p = new Paragraph();
        if (planOptionName.equalsIgnoreCase("Life Option")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("     Life Option    ");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (planOptionName.equalsIgnoreCase("Return of Premium Option")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("     Return of Premium Option   ");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        benefitOptionDetails.addCell(new Cell(1, 2).add(optionDetails).setBorder(Border.NO_BORDER));

        String lumpsumLevelIncomePercentage = " ";
        p = new Paragraph(new Text("Pay Out Options ").setBold());
        p.add("(Select any one of the below Options)");
        benefitOptionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
        optionDetails = new Table(new float[]{100F, 700F});
        p = new Paragraph();
        p.add(imgUnchecked);
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("     Lumpsum    ");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("     Lumpsum and Level Income,\n" + "Select Percentage of Sum Assured to be Paid as Lumpsum  ");
        p.add(new Text(lumpsumLevelIncomePercentage).setUnderline());
        p.add("% (Choose between 10% and 50% in multiple of 10%)    ");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        optionDetails.addCell(new Cell(1, 2).add(new Paragraph("Additional Benefits/Options").setBold()).setBorder(Border.NO_BORDER));

        String riderId = "";
        JsonArray riderArray = new JsonArray();
        if (planDetailObj.has("riders") && planDetailObj.get("riders").isJsonArray() && planDetailObj.get("riders").getAsJsonArray().size() > 0) {
            riderArray = planDetailObj.get("riders").getAsJsonArray();
        }
        for (JsonElement element : riderArray) {
            riderId = jsonUtility.getJsonKeyValue("riderId", element.getAsJsonObject());
        }
        p = new Paragraph();
        if (riderId.equalsIgnoreCase("WOP")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("     Waiver of Premium Benefit (applicable with Smart Life & Life Option)  ");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (riderId.equalsIgnoreCase("joint Life")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("     Joint Life Option (Applicable with Life option) Please fill in details in Section 5 and Section 10b Respectively Please  ");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("");
        optionDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("");
        optionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        benefitOptionDetails.addCell(new Cell(1, 2).add(optionDetails).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(benefitOptionDetails));

//            fortunePlusDetails = new Table(new float[]{800F, 300F});
//            p = new Paragraph("For ");
//            p.add(new Text("IndiaFirst Life Fortune Plus Plan").setBold());
//            p.add(" please choose appropriate option for Guaranteed Survival Benefit & Cash Bonus: ");
//            fortunePlusDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Payout       ");
//            p.add(imgUnchecked);
//            p.add("     Accrual       ");
//            fortunePlusDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
//            p = new Paragraph("If Payout Option is chosen, then Payout Frequency ");
//            p.add(new Text("_________________"));
//            fortunePlusDetails.addCell(new Cell(1, 2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//
//            table.addCell(new Cell().add(fortunePlusDetails));

//            Table deathBenefitOptions = new Table(new float[]{700F, 300F});
//            deathBenefitOptions.addCell(new Cell().add("Death Benefit Option for IndiaFirst Life Wealth Maximizer Plan, IndiaFirst Life Money Balance Plan and " + "IndiaFirst Smart Save Plan").setBold().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Lumpsum       ");
//            p.add(imgUnchecked);
//            p.add("     Income (5 Years)       ");
//            deathBenefitOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//
//            Table deathWithdrawal = new Table(new float[]{600F, 500F, 500F});
//            deathWithdrawal.addCell(new Cell().add(new Paragraph("Systematic Partial Withdrawal Option").setBold()).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Yes       ");
//            p.add(imgUnchecked);
//            p.add("     No       ");
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("If yes 1) Percentage of withdrawal ");
//            p.add(new Text(" ").setUnderline());
//            p.add(" (Between 0% - 25%)");
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
//            p = new Paragraph("Systematic Partial Withdrawal option is applicable only for IndiaFirst Life Wealth Maximizer Plan after completion of first 5 policy years.");
//            deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            deathWithdrawal.addCell(new Cell().add(new Paragraph("2) Frequency")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Yearly       ");
//            p.add(imgUnchecked);
//            p.add("     Half Yearly       ");
//            p.add(imgUnchecked);
//            p.add("     Quarterly       ");
//            p.add(imgUnchecked);
//            p.add("     Monthly       ");
//
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("From Policy Year  ");
//            p.add(new Text("  ").setUnderline());
//            p.add(" to ");
//            p.add(new Text("  ").setUnderline());
//            p.add(" Policy Year");
//
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
//            p = new Paragraph("Note: ATBIS is applicable for IndiaFirst Life Money Balance Plan and IndiaFirst Life Wealth Maximizer Plan. For IndiaFirst Life Wealth Maximizer Plan please select either an investment " + "strategy or the fund options in which you want to invest your premiums.");
//            deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            //deathWithdrawal.addCell(new Cell(1, 2).add(new Paragraph("I. Automatic Trigger Based Investment Strategy (ATBIS)")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("I. Automatic Trigger Based Investment Strategy (ATBIS)       ");
//            p.add(imgUnchecked);
//            p.add("     Yes       ");
//            p.add(imgUnchecked);
//            p.add("     No       ");
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            //deathWithdrawal.addCell(new Cell(1, 2).add(new Paragraph("II. Fund Transfer Strategy")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("II. Fund Transfer Strategy       ");
//            p.add(imgUnchecked);
//            p.add("     Yes       ");
//            p.add(imgUnchecked);
//            p.add("     No       ");
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            //deathWithdrawal.addCell(new Cell(1, 2).add(new Paragraph("III. Age Based Investment Strategy")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("III. Age Based Investment Strategy       ");
//            p.add(imgUnchecked);
//            p.add("     Yes       ");
//            p.add(imgUnchecked);
//            p.add("     No       ");
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("Select only one option from I to III for IndiaFirst Life Wealth Maximizer Plan");
//            deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            deathBenefitOptions.addCell(new Cell(1, 2).add(deathWithdrawal).setBorder(Border.NO_BORDER));
//            table.addCell(new Cell().add(deathBenefitOptions));

        Table deathBenefitOptions = new Table(new float[]{740F, 300F});
        p = new Paragraph("Choose options for ");
        p.add(new Text("IndiaFirst Life Wealth Maximizer Plan, IndiaFirst Life Money Balance Plan, IndiaFirst Life Smart Save Plan and IndiaFirst Life Term with Unit Linked Insurance Plan").setBold());
        deathBenefitOptions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
//            p = new Paragraph();
//            p.add(imgUnchecked);
//            p.add("     Lumpsum       ");
//            p.add(imgUnchecked);
//            p.add("     Income (5 Years)       ");
//            deathBenefitOptions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        Table deathWithdrawal = new Table(new float[]{570F, 480F, 560F});
        p = new Paragraph(new Text("Death Benefit Option:    ").setBold());
        p.add(imgUnchecked);
        p.add("     Lumpsum       ");
        p.add(imgChecked);
        p.add("     Income (5 Years)       ");
        deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        //deathWithdrawal = new Table(new float[]{600F, 500F, 500F});
        deathWithdrawal.addCell(new Cell().add(new Paragraph("Systematic Partial Withdrawal Option:").setBold()).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Yes       ");
        p.add(imgUnchecked);
        p.add("     No       ");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph("(If yes, % of withdrawal ______ (between 0 to 25%), from year ______ to year ______");
//            p.add(new Text(" ").setUnderline());
//            p.add(" (Between 0% - 25%)");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph("Systematic Partial Withdrawal option is applicable only for IndiaFirst Life Wealth Maximizer Plan after completion of first 5 policy years.");
//            deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            deathWithdrawal.addCell(new Cell().add(new Paragraph("2) Frequency")).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph();

//            if (frequency.equalsIgnoreCase("Yearly")) {
//                p.add(imgUnchecked);
//            } else {
//                p.add(imgUnchecked);
//            }
        p.add("                        in frequency    ");
        p.add(imgUnchecked);
        p.add("     Yearly       ");
//            if (frequency.equalsIgnoreCase("half yearly") || frequency.equalsIgnoreCase("half-yearly")) {
//                p.add(imgUnchecked);
//            } else {
//                p.add(imgUnchecked);
//            }
        p.add(imgUnchecked);
        p.add("     Half Yearly       ");
//            if (frequency.equalsIgnoreCase("Quarterly")) {
//                p.add(imgUnchecked);
//            } else {
//                p.add(imgUnchecked);
//            }
        p.add(imgUnchecked);
        p.add("     Quarterly       ");
//            if (frequency.equalsIgnoreCase("Monthly")) {
//                p.add(imgUnchecked);
//            } else {
//                p.add(imgUnchecked);
//            }
        p.add(imgUnchecked);
        p.add("     Monthly       ");

        deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        deathBenefitOptions.addCell(new Cell(1, 2).add(deathWithdrawal).setBorder(Border.NO_BORDER));
        //            p = new Paragraph("3) From Policy Year  ");
//            p.add(new Text("  ").setUnderline());
//            p.add(" to ");
//            p.add(new Text("  ").setUnderline());
//            p.add(" Policy Year");
//
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
//            p = new Paragraph("Note: ATBIS is applicable for IndiaFirst Life Money Balance Plan and IndiaFirst Life Wealth Maximizer Plan. For IndiaFirst Life Wealth Maximizer Plan & IndiaFirst Life Money Balance Plan please select either an investment " + "strategy or the fund options in which you want to invest your premiums.");
//            deathWithdrawal.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        deathWithdrawal = new Table(new float[]{150, 460F, 600F, 420F});
        deathWithdrawal.addCell(new Cell().add("Investment Strategies:   ").setBold().setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("I. Self-Managed       ");
        //p.add();
        //String ageBasedAllocation = jsonUtility.getJsonKeyValue("ageBasedAllocation", planDetailObj);
//            if (ageBasedAllocation.equalsIgnoreCase("yes")) {
//                p.add(imgChecked);
//            } else {
//                p.add(imgUnchecked);
//            }
        p.add(imgUnchecked);
        p.add("     Yes       ");
//            if (ageBasedAllocation.equalsIgnoreCase("no")) {
//                p.add(imgChecked);
//            } else {
//                p.add(imgUnchecked);
//            }
        p.add(imgUnchecked);
        p.add("     No       ");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p = new Paragraph("II. Automatic Trigger Based Investment Strategy (ATBIS)       ");
        p = new Paragraph("III. Fund Transfer Strategy       ");
        p.add(imgUnchecked);
        p.add("     Yes       ");
        p.add(imgUnchecked);
        p.add("     No       ");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        deathWithdrawal.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        deathWithdrawal.addCell(new Cell().add("").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p = new Paragraph("III. Fund Transfer Strategy       ");
        p.add(imgUnchecked);
        p.add("     Yes       ");
        p.add(imgUnchecked);
        p.add("     No       ");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p = new Paragraph("IV. Age Based Investment Strategy       ");
        p.add(imgUnchecked);
        p.add("     Yes       ");
        p.add(imgUnchecked);
        p.add("     No       ");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p = new Paragraph("V. Smart Switch Strategy       ");
        p.add(imgUnchecked);
        p.add("     Yes       ");
        p.add(imgUnchecked);
        p.add("     No       ");
        deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
//            p = new Paragraph("ATBIS is applicable for IndiaFirst Life Money Balance Plan & IndiaFirst Life Wealth Maximizer Plan");
//            deathWithdrawal.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        deathBenefitOptions.addCell(new Cell(1, 2).add(deathWithdrawal).setBorder(Border.NO_BORDER));
        deathBenefitOptions.addCell(new Cell().add("ATBIS is applicable for IndiaFirst Life Money Balance Plan & IndiaFirst Life Wealth Maximizer Plan").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(deathBenefitOptions));

        Table fundsDetails = new Table(new float[]{350F, 80F, 300F, 80F, 300F, 80F});
        fundsDetails.addCell(new Cell(1, 6).add(new Paragraph(new Text("Fund Options").setBold())).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        fundsDetails.addCell(new Cell(1, 6).add("Fund total to be 100%").setTextAlignment(TextAlignment.CENTER));
        fundsDetails.addCell(new Cell().add("Equity1 (SFIN:ULIF009010910EQUTY1FUND143"));
        fundsDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("allocatefund1", planDetailObj)));
        fundsDetails.addCell(new Cell().add("Value (SFIN:ULIF013010910VALUEFUND0143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Liquid 1 Fund (SFIN: ULIF014010910LIQUID1FND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Debt1 (SFIN:ULIF010010910DEBT01FUND143)"));
        fundsDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("debt1", planDetailObj)));
        fundsDetails.addCell(new Cell().add("Balanced1 (SFIN: ULIF011010910BALAN1FUND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Index Tracker (SFIN:ULIF012010910INDTRAFUND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Dynamic Asset Allocation Fund (SFIN:ULIF015080811DYAALLFUND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Flexi Cap Equity (SFIN: ULIF02121/02/22FLEXCAPFND143)"));
        fundsDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("flexiCapEquityFund", planDetailObj)));
        fundsDetails.addCell(new Cell().add("Equity Elite Opportunities (SFIN:ULIF020280716EQUELITEOP143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Sustainable Equity (SFIN: ULIF02221/02/22SUSTEQUFND143)"));
        fundsDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("SustainableEquityFund", planDetailObj)));
        fundsDetails.addCell(new Cell().add("Macro Trends Fund (SFIN: ULIF025010824MACREQUFND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Multi Cap Equity Fund (SFIN: ULIF026101024MULTEQUFND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("Large Cap Equity Fund (SFIN: ULIF027060125LARGEQUFND143)"));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("   "));
        fundsDetails.addCell(new Cell().add("   "));

        p = new Paragraph("For Fund Transfer Strategy, please select one Equity Oriented Fund and one Debt Oriented Fund.");
        p.add(new Text("\nNote: ").setBold());
        p.add(" The first three months premium is to be paid as first installment for the monthly mode option (not applicable for pure protection plans). Any cash/cheque/DD payment made towards first or renewal premium is deemed to be received by ”IndiaFirst Life Insurance Company Ltd.” only when the same has been received by any of its offices or its authorised banking partners or collection point and after an official printed receipt is issued by the Company. Cheques must be drawn only in favour of IndiaFirst Life Insurance Company Ltd. (Application no. for first premium/ policy no. for renewal premium should be written behind the cheque).");
        p.add(new Text("\nNote: ").setBold());
        p.add("The collections points/ centers for accepting payment in cash/ cheque/ DD will be as specified by the Company from time to time. ");
        fundsDetails.addCell(new Cell(1, 6).add(p));
        table.addCell(new Cell().add(fundsDetails).setBorder(Border.NO_BORDER));
        p = new Paragraph(new Text("Third Party payment: ").setBold());
        p.add(" I hereby declare that the payment mode as availed by me under my policy belongs to me and I take sole responsibility for the same in respect of any incorrectness of any statement in this regard.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("5. Details of Secondary Life Assured (If Joint Life option or Better Half Benefit is chosen). Only applicable for IndiaFirst Life Guaranteed Protection Plus Plan");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        Table secondaryLifeAssuredDetails = this.getSecondaryLifeAssuredDetails(imgUnchecked, innerProposer);
        table.addCell(new Cell().add(secondaryLifeAssuredDetails).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("6.Benefit Payment Mode (Choose any one mode only)");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        Table bankDetails = this.getBankDetails(imgUnchecked, imgChecked, primaryBankObj, eMandateObj, nomineeList);
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));
        //////////////////////////

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("7. Life to be Assured’s Family History (Please tick Yes or No)");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        JsonObject familySufferObj = myself ? jsonUtility.getJsonObjectByKey("familySuffer", primaryMedicalObj) : jsonUtility.getJsonObjectByKey("familySuffer", secondaryMedicalObj);
        p = new Paragraph("Have either of your parents or any brothers or sisters suffered from or died due to any of the following conditions: Heart problems, diabetes, stroke, hypertension, raised cholesterol, cancer, or " + "any hereditary disease? If yes, please give full details below:    \n");
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
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Table familyHistory = new Table(5);
        familyHistory.addCell(new Paragraph("Family Members").setBold());
        familyHistory.addCell(new Paragraph("Age").setBold());
        familyHistory.addCell(new Paragraph("If Alive, Illness, if any").setBold());
        familyHistory.addCell(new Paragraph("Age").setBold());
        familyHistory.addCell(new Paragraph("If Deceased, exact cause of Death").setBold());

        JsonArray familyHistoryList = new JsonArray();
        if (familySufferObj.has("members") && familySufferObj.get("members").getAsJsonArray().size() > 0) {
            familyHistoryList = familySufferObj.get("members").getAsJsonArray();
        }
        for (JsonElement element : familyHistoryList) {
            JsonObject objNom = element.getAsJsonObject();
            familyHistory.addCell(jsonUtility.getJsonKeyValue("releationship", objNom));
            familyHistory.addCell(jsonUtility.getJsonKeyValue("age", objNom));
            String deceasedStatus = jsonUtility.getJsonKeyValue("deceased", objNom);
            if (deceasedStatus.equalsIgnoreCase("n")) {
                familyHistory.addCell(jsonUtility.getJsonKeyValue("illness", objNom) + ", " + jsonUtility.getJsonKeyValue("specifyIllness", objNom));
            } else {
                familyHistory.addCell(new Paragraph(""));
            }
            familyHistory.addCell(jsonUtility.getJsonKeyValue("age", objNom));
            if (deceasedStatus.equalsIgnoreCase("y")) {
                familyHistory.addCell(jsonUtility.getJsonKeyValue("illness", objNom) + ", " + jsonUtility.getJsonKeyValue("specifyIllness", objNom));
            } else {
                familyHistory.addCell(new Paragraph(""));
            }
        }
        table.addCell(new Cell().add(familyHistory).setBorder(Border.NO_BORDER));
        String totalSumInsured = "";

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph(new Text("8. Proposer’s Insurance Details ").setBold());
        p.add("(Applicable to minor lives and housewives)");
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(new Cell().add(p).setBorder(Border.NO_BORDER));
        table.addCell(headingCell);
        Table insuranceDetails = new Table(2);

        logoFilename = "rupee.png";
        String logoBase641 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
        Image rupeeLogo = pdfUtility.getPDFLogo(logoBase641);

        p = new Paragraph("Parents’/ Husband’s insurance details - total sum insured (");
        p.add(rupeeLogo.setHeight(10).setWidth(10));
        p.add(".)");
        insuranceDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[totalSumInsured.length()];
        for (int i = 0; i < totalSumInsured.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (totalSumInsured.length() > 0) {
            tableCodes = pdfUtility.codeTable(totalSumInsured, pointColumnWidths);
            insuranceDetails.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
        } else {
            insuranceDetails.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        }
        table.addCell(new Cell().add(insuranceDetails).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph(new Text("9a. Details of life insurance policies held/ proposals applied with life insurance companies ").setBold());
        p.add("(including existing policies with IndiaFirst Life Insurance Co. Ltd.)");
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph("Have you ever applied for life insurance policies with IndiaFirst Life Insurance Co. Ltd and with other insurers? If yes, please give full details below, with present status and terms of acceptance " + "for all proposals/ policies applied:    ");
        String appliedInsuranceStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("appliedInsurance", primaryOtherDetailObj));
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
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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
        p = new Paragraph(new Text("Name of Life to be Assured/ Proposer").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Name of the Company").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Policy/Proposal No.").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Annual Premium").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Sum Assured including riders").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Year of Commencement").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Present Status and Terms of Acceptance").setBold());
        insurancePolicies.addCell(p);

        for (JsonElement element : insuranceHeld) {
            JsonObject objNom = element.getAsJsonObject();
            insurancePolicies.addCell(objNom.get("name").getAsString());
            insurancePolicies.addCell(objNom.get("companyName").getAsString());
            insurancePolicies.addCell(objNom.get("policyNo").getAsString());
            insurancePolicies.addCell(objNom.get("annualPremium").getAsString());
            insurancePolicies.addCell(objNom.get("sumAssured").getAsString());
            insurancePolicies.addCell(objNom.get("yearOfCommencement").getAsString());
            insurancePolicies.addCell(objNom.get("status").getAsString());
        }
        table.addCell(new Cell().add(insurancePolicies).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Additional sheets with relevant details signed by the life to be assured may be added if space is insufficient.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph(new Text("9b . Details of life insurance policies held/ proposals applied with life insurance companies ").setBold());
        p.add("(including existing policies with IndiaFirst Life Insurance Co. Ltd.)(Details of " + "Secondary Life Assured (If Joint Life option or Better Half Benefit is chosen). Only applicable for IndiaFirst Life Guaranteed Protection Plus Plan)");
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph("Have you ever applied for life insurance policies with IndiaFirst Life Insurance Co. Ltd and with other insurers? If yes, please give full details below, with present status and terms of acceptance " + "for all proposals/ policies applied:    ");
        appliedInsuranceStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("appliedInsurance", secondaryOtherDetailObj));
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
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        insuranceHeld = new JsonArray();
        obj = new JsonObject();
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

        insurancePolicies = new Table(7);
        p = new Paragraph(new Text("Name of Life to be Assured/ Proposer").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Name of the Company").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Policy/Proposal No.").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Annual Premium").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Sum Assured including riders").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Year of Commencement").setBold());
        insurancePolicies.addCell(p);
        p = new Paragraph(new Text("Present Status and Terms of Acceptance").setBold());
        insurancePolicies.addCell(p);

        for (JsonElement element : insuranceHeld) {
            JsonObject objNom = element.getAsJsonObject();
            insurancePolicies.addCell(objNom.get("name").getAsString());
            insurancePolicies.addCell(objNom.get("companyName").getAsString());
            insurancePolicies.addCell(objNom.get("policyNo").getAsString());
            insurancePolicies.addCell(objNom.get("annualPremium").getAsString());
            insurancePolicies.addCell(objNom.get("sumAssured").getAsString());
            insurancePolicies.addCell(objNom.get("yearOfCommencement").getAsString());
            insurancePolicies.addCell(objNom.get("status").getAsString());
        }
        table.addCell(new Cell().add(insurancePolicies).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Additional sheets with relevant details signed by the life to be assured may be added if space is insufficient.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph(new Text("10a. Lifestyle questions and personal medical history of the Life to be Assured ").setBold());
        p.add("(If 'Yes', please encircle the activity/ ailment/ disease)");
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Non disclosures of facts will highly impact claim settlement").setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject heightObj = jsonUtility.getJsonObjectByKey("height", primaryHealthObj);
        /****** Height Table Row START ********/
        Table medicalLifestyleQ = new Table(new float[]{750F, 150F});
        String heightInCm = "";
        try {
            heightInCm = this.feetAndInchesToCms(Double.parseDouble(jsonUtility.getJsonKeyValue("feet", heightObj)), Double.parseDouble(jsonUtility.getJsonKeyValue("inch", heightObj)));
        } catch (Exception e) {
            logger.info("");
        }
        p = new Paragraph();

        Table up = new Table(new float[]{120F, 80F, 60F, 20F, 60F, 20F, 80F, 70F});
        Table valueTable = new Table(1);

        p.add("a.       Height in cm: ").setTextAlignment(TextAlignment.LEFT);
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        if (!heightInCm.isBlank()) {
            valueTable = new Table(heightInCm.length()).setWidth(70F);
            valueTable.setBorder(Border.NO_BORDER);

            for (char c : heightInCm.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.CENTER);
        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        p = new Paragraph();
        p.add("/ Feet");
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        String heightInFeet = jsonUtility.getJsonKeyValue("feet", heightObj);
        if (!heightInFeet.isBlank()) {
            valueTable = new Table(heightInFeet.length()).setWidth(15);
            for (char c : heightInFeet.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER));
        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        p = new Paragraph();
        p.add(" inches: ");
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

        String heightInInches = jsonUtility.getJsonKeyValue("inch", heightObj);

        if (!heightInInches.isBlank()) {
            valueTable = new Table(heightInInches.length()).setWidth(15);
            for (char c : heightInInches.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER));

        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        p = new Paragraph();
        p.add("         Weight in kg: ");
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

        String weight = jsonUtility.getJsonKeyValue("weight", primaryHealthObj);

        if (!weight.isBlank()) {
            valueTable = new Table(weight.length()).setWidth(60F);
            for (char c : weight.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER));

        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        medicalLifestyleQ.addCell(new Cell().add(up).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);

        /****** Height Table Row END ********/

        p = new Paragraph("");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        medicalLifestyleQ.addCell(new Cell().add("b. Have you taken part, or do you have plans to take part, in any hazardous/ dangerous activity such as ballooning, mountain cycling, motorbike racing, boxing, gliding, diving, horse riding, martial " + "arts, motor racing, mountain climbing, parachuting, sailing, skiing, weight lifting, white water rafting, wrestling and/ or flying other than as a fare paying passenger on a licensed service or any other " + "hazardous/ dangerous activity which is not listed. If yes, please provide details in the special questionnaire which your advisor will provide.").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("hazardous_activity", primaryLifestyleObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("hazardous_activity", primaryLifestyleObj)).equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        medicalLifestyleQ.addCell(new Cell().add("c. Are you currently or do you intend to live or travel outside India for more than six months in a financial year? If yes, please provide full details of countries to be visited the purpose of visit and duration").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("outOfIndia", primaryLifestyleObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("outOfIndia", primaryLifestyleObj)).equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject tobaccoConsumptionObj = jsonUtility.getJsonObjectByKey("tobacco_consumption", primaryLifestyleObj);
        String tobaccoType = jsonUtility.getJsonKeyValue("tobaccoType", tobaccoConsumptionObj);
        p = new Paragraph("d. Have you smoked or used any form of tobacco in the past 12 months? If yes, please indicate in which form: \n");
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject alcoholConsumptionObj = jsonUtility.getJsonObjectByKey("alcohol_consumption", primaryLifestyleObj);
        String alcoholType = jsonUtility.getJsonKeyValue("alcoholType", alcoholConsumptionObj);
        String alcoholConsStatus = jsonUtility.getJsonKeyValue("status", alcoholConsumptionObj);
        p = new Paragraph("e. Do you consume any form of alcohol? If yes, what type?: \n");
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject medicationObj = jsonUtility.getJsonObjectByKey("medication", primaryMedicalObj);
        String medicationStatus = jsonUtility.getJsonKeyValue("status", medicationObj);
        medicalLifestyleQ.addCell(new Cell().add("f. Are you currently taking any medication or drugs, other than for minor conditions, (e.g. cold and flu), either prescribed or not prescribed by a doctor, or have you suffered from any illness, disorder, " + "disability or injury during the past 5 years which has required any form of medical or specialised examination (including chest x-rays, gynecological investigations, pap smear, or blood tests), " + "consultation, hospitalisation or surgery?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject congenitalObj = jsonUtility.getJsonObjectByKey("congenital", primaryMedicalObj);
        String congenitalStatus = jsonUtility.getJsonKeyValue("status", congenitalObj);
        medicalLifestyleQ.addCell(new Cell().add("g. Do you have any congenital/birth defects, pain or problems in the back, spine, muscles or joint, arthritis, gout, severe injury or other physical disability and have you been incapable of working " + "attending the school during the last two years for more than three consecutive days or are you currently incapable of working / attending school? Please ignore normal pregnancy.").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject healthHistoryObj = jsonUtility.getJsonObjectByKey("healthHistory", primaryMedicalObj);
        String healthHistoryStatus = jsonUtility.getJsonKeyValue("status", healthHistoryObj);
        medicalLifestyleQ.addCell(new Cell().add("h. Do you suffer from or ever had any medical ailments such as diabetes, high blood pressure, cancer, respiratory disease (including asthma), kidney or liver disease, stroke, any blood disorder, " + "heart problems?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        String diagnosedHepatitisStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("diagnosedHepatitis", primaryMedicalObj));
        medicalLifestyleQ.addCell(new Cell().add("i. Do you suffer from or ever had any medical ailments such as Hepatitis B or C, or tuberculosis, psychiatric disorder, depression, colitis, or any other stomach problems, thyroid disorders, reproductive " + "organs, HIV AIDS or a related infection?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject diagnosedObj = jsonUtility.getJsonObjectByKey("diagnosed", primaryMedicalObj);
        String diagnosedStatus = jsonUtility.getJsonKeyValue("status", diagnosedObj);
        medicalLifestyleQ.addCell(new Cell().add("j. Do you suffer from or ever had any medical ailments such as tumor growth, prostrate disorder, disorder of skin or lymph glands, multiple sclerosis, epilepsy, tremor, numbness, double vision " + "or giddiness, speech defect, paralysis?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject medicalInvestigationsObj = jsonUtility.getJsonObjectByKey("medicalInvestigations", primaryMedicalObj);
        String medicalInvestigationsStatus = jsonUtility.getJsonKeyValue("status", medicalInvestigationsObj);
        medicalLifestyleQ.addCell(new Cell().add("k. Have you ever been advised/ had a surgery or any medical investigations such as X-ray, CT scan, mammogram, pap smear etc?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject sufferedObj = jsonUtility.getJsonObjectByKey("suffered", primaryMedicalObj);
        String sufferedStatus = jsonUtility.getJsonKeyValue("status", sufferedObj);
        medicalLifestyleQ.addCell(new Cell().add("l. Have you ever suffered from drug/ narcotics or alcohol addiction or been advised by a doctor to reduce your alcohol/ tobacco consumption?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject specialistDoctorTreatmentObj = jsonUtility.getJsonObjectByKey("specialistDoctorTreatment", primaryMedicalObj);
        String specialistDoctorTreatmentStatus = jsonUtility.getJsonKeyValue("status", specialistDoctorTreatmentObj);
        medicalLifestyleQ.addCell(new Cell().add("m. In the last 3 years, have you been treated, are currently undergoing or have been advised for treatment from a doctor or specialist or undergone any cardiological, radiology or pathological " + "tests (excluding routine check ups)?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject injuryIllnessObj = jsonUtility.getJsonObjectByKey("injuryIllness", primaryMedicalObj);
        String injuryIllnessStatus = jsonUtility.getJsonKeyValue("status", injuryIllnessObj);
        medicalLifestyleQ.addCell(new Cell().add("n. Is your occupation associated with any specific hazards which would render you susceptible to any injury or illness, e.g. chemical factory, mines, explosives, corrosive chemicals, etc.?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject lostWeightObj = jsonUtility.getJsonObjectByKey("lostWeight", primaryHealthObj);
        String lostWeightstatus = jsonUtility.getJsonKeyValue("status", lostWeightObj);
        String gainOrLoss = jsonUtility.getJsonKeyValue("gainorloss", lostWeightObj);
        p = new Paragraph();
        p.add("o. Has your weight altered (Gain/Loss) by more than 5 kgs. in the last 1 years?\n");
        p.add("If yes, please mention weight gain (in Kgs) ");
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
        } else {
            p.add(new Text("").setUnderline());
            p.add("  or Loss   ");
            p.add(new Text("").setUnderline());
        }
        p.add("   (in Kgs)");
        p.add("\nReason for Gain / Loss:     ");
        p.add(new Text("").setUnderline());

        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject criminalConvictionsObj = jsonUtility.getJsonObjectByKey("criminalConvictions", primaryMedicalObj);
        String criminalConvictionsStatus = jsonUtility.getJsonKeyValue("status", criminalConvictionsObj);
        medicalLifestyleQ.addCell(new Cell().add("p. Have you ever been convicted for any Criminal convictions/activities /offences ?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject sufferedDiseaseObj = jsonUtility.getJsonObjectByKey("sufferedDisease", primaryMedicalObj);
        String sufferedDiseaseStatus = jsonUtility.getJsonKeyValue("status", sufferedDiseaseObj);
        medicalLifestyleQ.addCell(new Cell().add("q. Have you ever been suffered/suffering Any other disease/disorder not mentioned above ?").setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        table.addCell(medicalLifestyleQ);

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph(new Text("10b. Lifestyle questions and personal medical history of the Secondary Life to be Assured \n").setBold());

        p.add("(If 'Yes', please encircle the activity/ ailment/ disease) If Joint Life option is chosen. Only applicable for IndiaFirst Life Guaranteed Protection Plus Plan");
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Non disclosures of facts will highly impact claim settlement").setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        heightObj = jsonUtility.getJsonObjectByKey("height", secondaryHealthObj);
        heightInCm = "";
        try {
            heightInCm = this.feetAndInchesToCms(Double.parseDouble(jsonUtility.getJsonKeyValue("feet", heightObj)), Double.parseDouble(jsonUtility.getJsonKeyValue("inch", heightObj)));
        } catch (Exception e) {
            logger.info("");
        }

        /****** Height Table Row START ********/
        medicalLifestyleQ = new Table(new float[]{750F, 150F});

        p = new Paragraph();
        up = new Table(new float[]{120F, 80F, 60F, 20F, 60F, 20F, 80F, 70F});
        p.add("a.       Height in cm: ").setTextAlignment(TextAlignment.LEFT);
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        if (heightInCm.length() > 0) {
            valueTable = new Table(heightInCm.length()).setWidth(70F);
            valueTable.setBorder(Border.NO_BORDER);
            for (char c : heightInCm.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.CENTER);

        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        p = new Paragraph();
        p.add("/ Feet");
        heightInFeet = jsonUtility.getJsonKeyValue("feet", heightObj);
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        if (!heightInFeet.isBlank()) {
            valueTable = new Table(heightInFeet.length()).setWidth(15);
            for (char c : heightInFeet.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER));
        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        p = new Paragraph();
        p.add(" inches: ");
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

        heightInInches = jsonUtility.getJsonKeyValue("inch", heightObj);
        if (!heightInInches.isBlank()) {
            valueTable = new Table(heightInInches.length()).setWidth(15);
            for (char c : heightInInches.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER));
        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        p = new Paragraph();
        p.add("         Weight in kg: ");
        up.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

        weight = jsonUtility.getJsonKeyValue("weight", secondaryHealthObj);
        if (!weight.isBlank()) {
            valueTable = new Table(weight.length()).setWidth(60F);
            for (char c : weight.toCharArray()) {
                valueTable.addCell(new Cell().add(String.valueOf(c))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            }
            up.addCell(new Cell().add(valueTable).setBorder(Border.NO_BORDER));
        } else {
            up.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }

        medicalLifestyleQ.addCell(new Cell().add(up).setBorder(Border.NO_BORDER));

        /****** Height Table Row END ********/

        p = new Paragraph("");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("b. Have you taken part, or do you have plans to take part, in any hazardous/ dangerous activity such as ballooning, mountain cycling, motorbike racing, boxing, gliding, diving, horse riding, martial " + "arts, motor racing, mountain climbing, parachuting, sailing, skiing, weight lifting, white water rafting, wrestling and/ or flying other than as a fare paying passenger on a licensed service or any other " + "hazardous/ dangerous activity which is not listed. If yes, please provide details in the special questionnaire which your advisor will provide.").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("hazardous_activity", secondaryLifestyleObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("hazardous_activity", secondaryLifestyleObj)).equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        medicalLifestyleQ.addCell(new Cell().add("c. Are you currently or do you intend to live or travel outside India for more than six months in a financial year? If yes, please provide full details of countries to be visited the purpose of visit and duration").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("outOfIndia", secondaryLifestyleObj)).equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("outOfIndia", secondaryLifestyleObj)).equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        tobaccoConsumptionObj = jsonUtility.getJsonObjectByKey("tobacco_consumption", secondaryLifestyleObj);
        tobaccoType = jsonUtility.getJsonKeyValue("tobaccoType", tobaccoConsumptionObj);
        p = new Paragraph("d. Have you smoked or used any form of tobacco in the past 12 months? If yes, please indicate in which form: \n");
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
        tobaccoStatus = jsonUtility.getJsonKeyValue("status", tobaccoConsumptionObj);
        if (tobaccoStatus.equalsIgnoreCase("y")) {
            p.add(new Text(jsonUtility.getJsonKeyValue("tobaccoPerDay", tobaccoConsumptionObj)).setUnderline());
        } else {
            p.add(new Text(" ").setUnderline());
        }
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        alcoholConsumptionObj = jsonUtility.getJsonObjectByKey("alcohol_consumption", secondaryLifestyleObj);
        alcoholType = jsonUtility.getJsonKeyValue("alcoholType", alcoholConsumptionObj);
        alcoholConsStatus = jsonUtility.getJsonKeyValue("status", alcoholConsumptionObj);
        p = new Paragraph("e. Do you consume any form of alcohol? If yes, what type?: \n");
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("f. Are you currently taking any medication or drugs, other than for minor conditions, (e.g. cold and flu), either prescribed or not prescribed by a doctor, or have you suffered from any illness, disorder, " + "disability or injury during the past 5 years which has required any form of medical or specialised examination (including chest x-rays, gynecological investigations, pap smear, or blood tests), " + "consultation, hospitalisation or surgery?").setBorder(Border.NO_BORDER));
        JsonObject secondaryMedicationObj = jsonUtility.getJsonObjectByKey("medication", secondaryMedicalObj);
        String secondaryMedicationStatus = jsonUtility.getJsonKeyValue("status", secondaryMedicationObj);
        p = new Paragraph();
        if (secondaryMedicationStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryMedicationStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("g. Do you have any congenital/birth defects, pain or problems in the back, spine, muscles or joint, arthritis, gout, severe injury or other physical disability and have you been incapable of working " + "attending the school during the last two years for more than three consecutive days or are you currently incapable of working / attending school? Please ignore normal pregnancy.").setBorder(Border.NO_BORDER));
        JsonObject secondaryCongenitalObj = jsonUtility.getJsonObjectByKey("congenital", secondaryMedicalObj);
        String secondaryCongenitalStatus = jsonUtility.getJsonKeyValue("status", secondaryCongenitalObj);
        p = new Paragraph();
        if (secondaryCongenitalStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryCongenitalStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("h. Do you suffer from or ever had any medical ailments such as diabetes, high blood pressure, cancer, respiratory disease (including asthma), kidney or liver disease, stroke, any blood disorder, " + "heart problems?").setBorder(Border.NO_BORDER));
        JsonObject secondaryHealthHistoryObj = jsonUtility.getJsonObjectByKey("healthHistory", secondaryMedicalObj);
        String secondaryHealthHistoryStatus = jsonUtility.getJsonKeyValue("status", secondaryHealthHistoryObj);
        p = new Paragraph();
        if (secondaryHealthHistoryStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryHealthHistoryStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("i. Do you suffer from or ever had any medical ailments such as Hepatitis B or C, or tuberculosis, psychiatric disorder, depression, colitis, or any other stomach problems, thyroid disorders, reproductive " + "organs, HIV AIDS or a related infection?").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        diagnosedHepatitisStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("diagnosedHepatitis", secondaryMedicalObj));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("j. Do you suffer from or ever had any medical ailments such as tumor growth, prostrate disorder, disorder of skin or lymph glands, multiple sclerosis, epilepsy, tremor, numbness, double vision " + "or giddiness, speech defect, paralysis?").setBorder(Border.NO_BORDER));
        JsonObject secondaryDiagnosedObj = jsonUtility.getJsonObjectByKey("diagnosed", secondaryMedicalObj);
        String secondaryDiagnosedStatus = jsonUtility.getJsonKeyValue("status", secondaryDiagnosedObj);
        p = new Paragraph();
        if (secondaryDiagnosedStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryDiagnosedStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("k. Have you ever been advised/ had a surgery or any medical investigations such as X-ray, CT scan, mammogram, pap smear etc?").setBorder(Border.NO_BORDER));
        JsonObject secondaryMedicalInvestigationsObj = jsonUtility.getJsonObjectByKey("medicalInvestigations", secondaryMedicalObj);
        String secondaryMedicalInvestigationsStatus = jsonUtility.getJsonKeyValue("status", secondaryMedicalInvestigationsObj);
        p = new Paragraph();
        if (secondaryMedicalInvestigationsStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryMedicalInvestigationsStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("l. Have you ever suffered from drug/ narcotics or alcohol addiction or been advised by a doctor to reduce your alcohol/ tobacco consumption?").setBorder(Border.NO_BORDER));
        JsonObject secondarySufferedObj = jsonUtility.getJsonObjectByKey("suffered", secondaryMedicalObj);
        String secondarySufferedStatus = jsonUtility.getJsonKeyValue("status", secondarySufferedObj);
        p = new Paragraph();
        if (secondarySufferedStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondarySufferedStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("m. In the last 3 years, have you been treated, are currently undergoing or have been advised for treatment from a doctor or specialist or undergone any cardiological, radiology or pathological " + "tests (excluding routine check ups)?").setBorder(Border.NO_BORDER));
        JsonObject secondarySpecialistDoctorTreatmentObj = jsonUtility.getJsonObjectByKey("specialistDoctorTreatment", secondaryMedicalObj);
        String secondarySpecialistDoctorTreatmentStatus = jsonUtility.getJsonKeyValue("status", secondarySpecialistDoctorTreatmentObj);
        p = new Paragraph();
        if (secondarySpecialistDoctorTreatmentStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondarySpecialistDoctorTreatmentStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("n. Is your occupation associated with any specific hazards which would render you susceptible to any injury or illness, e.g. chemical factory, mines, explosives, corrosive chemicals, etc.?").setBorder(Border.NO_BORDER));
        JsonObject secondaryInjuryIllnessObj = jsonUtility.getJsonObjectByKey("injuryIllness", secondaryMedicalObj);
        String secondaryInjuryIllnessStatus = jsonUtility.getJsonKeyValue("status", secondaryInjuryIllnessObj);
        p = new Paragraph();
        if (secondaryInjuryIllnessStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryInjuryIllnessStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        JsonObject secondaryLostWeightObj = jsonUtility.getJsonObjectByKey("lostWeight", secondaryHealthObj);
        lostWeightstatus = jsonUtility.getJsonKeyValue("status", secondaryLostWeightObj);
        gainOrLoss = jsonUtility.getJsonKeyValue("gainorloss", secondaryLostWeightObj);
        p = new Paragraph();
        p.add("o. Has your weight altered (Gain/Loss) by more than 5 kgs. in the last 1 years?\n");
        p.add("If yes, please mention weight gain (in Kgs) ");
        if (lostWeightstatus.equalsIgnoreCase("y")) {
            if (gainOrLoss.equalsIgnoreCase("gained")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("weightGainedorLoss", secondaryLostWeightObj)).setUnderline());
            } else {
                p.add(new Text("").setUnderline());
            }
            p.add("   (in Kgs)");
            p.add("  or Loss   ");
            if (gainOrLoss.equalsIgnoreCase("lost")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("weightGainedorLoss", secondaryLostWeightObj)).setUnderline());
            } else {
                p.add(new Text("").setUnderline());
            }
        } else {
            p.add(new Text("").setUnderline());
            p.add("  or Loss   ");
            p.add(new Text("").setUnderline());
        }
        p.add("   (in Kgs)");
        p.add("\nReason for Gain / Loss:     ");
        p.add(new Text("").setUnderline());

        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
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
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("p. Have you ever been convicted for any Criminal convictions/activities /offences ?").setBorder(Border.NO_BORDER));
        JsonObject secondaryCriminalConvictionsObj = jsonUtility.getJsonObjectByKey("criminalConvictions", secondaryMedicalObj);
        String secondaryCriminalConvictionsStatus = jsonUtility.getJsonKeyValue("status", secondaryCriminalConvictionsObj);
        p = new Paragraph();
        if (secondaryCriminalConvictionsStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondaryCriminalConvictionsStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        medicalLifestyleQ.addCell(new Cell().add("q. Have you ever been suffered/suffering Any other disease/disorder not mentioned above ?").setBorder(Border.NO_BORDER));
        JsonObject secondarySufferedDiseaseObj = jsonUtility.getJsonObjectByKey("sufferedDisease", secondaryMedicalObj);
        String secondarySufferedDiseaseStatus = jsonUtility.getJsonKeyValue("status", secondarySufferedDiseaseObj);
        p = new Paragraph();
        if (secondarySufferedDiseaseStatus.equalsIgnoreCase("y")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     Yes     ");
        if (secondarySufferedDiseaseStatus.equalsIgnoreCase("n")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("     No     ");
        medicalLifestyleQ.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        table.addCell(medicalLifestyleQ);

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("10c. If you have answered Yes, to any of the questions between 10a(f) to 10a(q) please provide details here");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        Table questionDetails = new Table(new float[]{150F, 800F});
        questionDetails.addCell("Question no.");
        questionDetails.addCell("For question No. 10a(f) to 10a(q) provide complete details including health condition, date of diagnosis, treatment prescribed, name/ address of doctor, if applicable");
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
        table.addCell(new Cell().add(questionDetails).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("10d. If you have answered Yes, to any of the questions between 10b(f) to 10b(q) please provide details here");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        questionDetails = new Table(new float[]{150F, 800F});
        questionDetails.addCell("Question no.");
        questionDetails.addCell("For question No. 10b(f) to 10b(q) provide complete details including health condition, date of diagnosis, treatment prescribed, name/ address of doctor, if applicable");
        if (secondaryMedicationStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("f");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("anyMedicalIssues", secondaryMedicationObj));
        }
        if (secondaryCongenitalStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("g");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondaryCongenitalObj));
        }
        if (secondaryHealthHistoryStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("h");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondaryHealthHistoryObj));
        }
        if (secondaryDiagnosedStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("j");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondaryDiagnosedObj));
        }
        if (secondaryMedicalInvestigationsStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("k");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondaryMedicalInvestigationsObj));
        }
        if (secondarySufferedStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("l");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondarySufferedDiseaseObj));
        }
        if (secondarySpecialistDoctorTreatmentStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("m");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondarySpecialistDoctorTreatmentObj));
        }
        if (secondaryInjuryIllnessStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("n");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondaryInjuryIllnessObj));
        }
        if (secondaryCriminalConvictionsStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("p");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondaryCriminalConvictionsObj));
        }
        if (secondarySufferedDiseaseStatus.equalsIgnoreCase("y")) {
            questionDetails.addCell("q");
            questionDetails.addCell(jsonUtility.getJsonKeyValue("details", secondarySufferedDiseaseObj));
        }
        table.addCell(new Cell().add(questionDetails).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("10e. For Female Life to be Assured only");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        JsonObject pregnantObj = jsonUtility.getJsonObjectByKey("pregnant", primaryLifestyleObj);
        String pregnantStatus = jsonUtility.getJsonKeyValue("status", pregnantObj);
        Table femaleLifeAssured = new Table(2);
        femaleLifeAssured.addCell(new Cell().add("a. Are you pregnant at present?: ").setBorder(Border.NO_BORDER));
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
        femaleLifeAssured.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        if (pregnantStatus.equalsIgnoreCase("y")) {
            femaleLifeAssured.addCell(new Cell().add("If yes duration in weeks: ").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add(jsonUtility.getJsonKeyValue("durationOfWeeks", pregnantObj)).setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add("b. Date of last delivery:  ").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add(jsonUtility.getJsonKeyValue("dateOfLastDelivery", pregnantObj)).setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add("c. Please state any complications during pregnancy?").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add(jsonUtility.getJsonKeyValue("pregnancyComplications", pregnantObj)).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(femaleLifeAssured).setBorder(Border.NO_BORDER));
        } else {
            femaleLifeAssured.addCell(new Cell().add("If yes duration in weeks: ").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add(" ").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add("b. Date of last delivery:  ").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add(" ").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add("c. Please state any complications during pregnancy?").setBorder(Border.NO_BORDER));
            femaleLifeAssured.addCell(new Cell().add(" ").setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(femaleLifeAssured).setBorder(Border.NO_BORDER));
        }

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("11. Health Declaration of Life to be Assured - Limited Underwriting");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph(new Text("Non disclosures of facts will highly impact claim settlement").setBold());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        String heightInCM = "";
        String feet = "";
        String inches = "";
        String weightInKg = "";
        pointColumnWidths = new float[heightInCM.length()];
        for (int i = 0; i < heightInCM.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        Table bodyDetails = new Table(new float[]{100F, 100F, 50F, 100F, 50F, 100F, 100F, 150F});
        bodyDetails.addCell(new Cell().add("Height in cm: ").setBorder(Border.NO_BORDER));
        if (heightInCM.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(heightInCM, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        bodyDetails.addCell(new Cell().add(" / Feet").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[feet.length()];
        for (int i = 0; i < feet.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (feet.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(feet, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        bodyDetails.addCell(new Cell().add(" inches:").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[inches.length()];
        for (int i = 0; i < inches.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (inches.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(inches, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        bodyDetails.addCell(new Cell().add(" Weight in kg:").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[weightInKg.length()];
        for (int i = 0; i < weightInKg.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (weightInKg.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(weightInKg, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        table.addCell(new Cell().add(bodyDetails).setBorder(Border.NO_BORDER));

        Table healthQuestions = new Table(new float[]{50F, 700F, 50F, 50F});
        healthQuestions.addCell(new Cell().add("S.No"));
        healthQuestions.addCell(new Cell().add("Question"));
        healthQuestions.addCell(new Cell(1, 2).add("Life Assured"));

        healthQuestions.addCell(new Cell().add(""));
        healthQuestions.addCell(new Cell().add("I here by agree that: -"));
        healthQuestions.addCell(new Cell().add("Yes"));
        healthQuestions.addCell(new Cell().add("No"));

        healthQuestions.addCell(new Cell().add("1").setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add("Are you currently taking any medication or drugs, other than for minor conditions, (e.g. cold and flu), either prescribed or not prescribed by a doctor, " + "or have you suffered from any illness, disorder, disability or injury during the past 5 years which has required any form of medical or specialised " + "examination (including chest x-rays, gynecological investigations, pap smear, or blood tests), consultation, hospitalisation or surgery?"));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));

        healthQuestions.addCell(new Cell().add("2").setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add("Do you have any congenital/birth defects, pain or problems in the back, spine, muscles or joint, arthritis, gout, severe injury or other physical " + "disability and have you been incapable of working/attending the school during the last two years for more than three consecutive days or are " + "you currently incapable of working / attending school? Please ignore normal pregnancy."));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));

        healthQuestions.addCell(new Cell().add("3").setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add("Do you suffer from or ever had any medical ailments such as diabetes, high blood pressure, cancer, respiratory disease (including asthma), " + "kidney or liver disease, stroke, any blood disorder, heart problems, Hepatitis B or C, or tuberculosis, psychiatric disorder, depression, colitis, " + "or any other stomach problems, thyroid disorders, reproductive organs, HIV AIDS or a related infection, tumor growth, prostrate disorder, " + "disorder of skin or lymph glands, multiple sclerosis, epilepsy, tremor, numbness, double vision or giddiness, speech defect, paralysis?"));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));

        String duration = "";
        healthQuestions.addCell(new Cell().add("4").setTextAlignment(TextAlignment.CENTER));
        p = new Paragraph("Are you currently or do you intend to live or travel outside India for more than six months in a financial year? If yes, please provide full details " + "of countries to be visited the purpose of visit and duration  ");
        p.add(new Text(duration).setUnderline());
        healthQuestions.addCell(new Cell().add(p));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));

        healthQuestions.addCell(new Cell().add("5").setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add("For females only- Have you ever suffered from or suffering or is currently suffering from any diseases of breast / uterus / cervix, or presently pregnant?"));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));

        healthQuestions.addCell(new Cell().add("6").setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add("Have you ever been suffered/suffering from Any other disease/disorder not mentioned above ?"));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));

        healthQuestions.addCell(new Cell().add("7").setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add("Have you ever suffered from drug/ narcotics or alcohol addiction or been advised by a doctor to reduce your" + "alcohol/ tobacco consumption?"));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        healthQuestions.addCell(new Cell().add(imgUnchecked).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell().add(healthQuestions).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("For Female Life to be Assured only");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        femaleLifeAssured = new Table(2);
        femaleLifeAssured.addCell(new Cell().add("a. Are you pregnant at present?: ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Yes     ");
        p.add(imgUnchecked);
        p.add("     No     ");
        femaleLifeAssured.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        femaleLifeAssured.addCell(new Cell().add("If yes duration in weeks: ").setBorder(Border.NO_BORDER));
        femaleLifeAssured.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        femaleLifeAssured.addCell(new Cell().add("b. Date of last delivery:  ").setBorder(Border.NO_BORDER));
        femaleLifeAssured.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        femaleLifeAssured.addCell(new Cell().add("c. Please state any complications during pregnancy?").setBorder(Border.NO_BORDER));
        femaleLifeAssured.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(femaleLifeAssured).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("12. Health Declaration of Life to be Assured - Simplified Underwriting");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph(new Text("Non disclosures of facts will highly impact claim settlement").setBold());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        heightInCM = "";
        feet = "";
        inches = "";
        weightInKg = "";
        pointColumnWidths = new float[heightInCM.length()];
        for (int i = 0; i < heightInCM.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        bodyDetails = new Table(new float[]{100F, 100F, 50F, 100F, 50F, 100F, 100F, 150F});
        bodyDetails.addCell(new Cell().add("Height in cm: ").setBorder(Border.NO_BORDER));
        if (heightInCM.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(heightInCM, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        bodyDetails.addCell(new Cell().add(" / Feet").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[feet.length()];
        for (int i = 0; i < feet.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (feet.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(feet, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        bodyDetails.addCell(new Cell().add(" inches:").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[inches.length()];
        for (int i = 0; i < inches.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (inches.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(inches, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        bodyDetails.addCell(new Cell().add(" Weight in kg:").setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[inches.length()];
        for (int i = 0; i < weightInKg.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (weightInKg.length() > 0) {
            bodyDetails.addCell(new Cell().add(pdfUtility.codeTable(weightInKg, pointColumnWidths)).setBorder(Border.NO_BORDER));
        } else {
            bodyDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
        }
        table.addCell(new Cell().add(bodyDetails).setBorder(Border.NO_BORDER));
        p = new Paragraph("I declare that I am in a sound state of health. I hereby declare that, as of the date of this declaration, I do not have any history of, have never suffered from or currently suffering from " + "medical conditions such as, but not limited to, high blood pressure, chest pain, heart attack or any other heart condition; stroke, transient ischemic attack or any other cerebrovascular " + "disease; diabetes or any other endocrinal disease; kidney disease; HIV / AIDS or AIDS related complex; any cancer or tumor; asthma or any other respiratory disease; any mental or " + "nervous disease; hepatitis or any other liver disease; blood disorders; digestive and bowel disorders; paraplegia, physical disability or any other disorder of the bones, spine or muscle; any " + "other disease, disorder or disability, not mentioned above and excluding minor impairment such as common cough or cold. I have never undergone any surgical procedure for any illness, " + "ailment, disease or disability. In the last 5 years, I have not received any form of medication for more than 7 consecutive days or been absent from work for more than 7 days due to any " + "health reasons.\n" + "For Female Lives: I further declare that presently I am not pregnant and I have not ever had any disease of breast, uterus, cervix, ovaries or any other part of the reproductive system.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("13. Insurance Repository");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        JsonObject existingInsuranceAccountObj = jsonUtility.getJsonObjectByKey("existingInsuranceAccount", primaryOtherDetailObj);
        String insuranceRepositoryName = jsonUtility.getJsonKeyValue("insuranceRepository", existingInsuranceAccountObj);
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
        Table eInsuranceDetails = new Table(2);
        eInsuranceDetails.addCell("E IA Number: ");
        // eInsuranceDetails.addCell(eIANumber);
        pointColumnWidths = new float[eIANumber.length()];
        for (int i = 0; i < eIANumber.length(); i++) {
            pointColumnWidths[i] = 20F;
        }
        if (eIANumber.length() > 0) {
            tableCodes = pdfUtility.codeTable(eIANumber, pointColumnWidths);
            eInsuranceDetails.addCell(new Cell().add(tableCodes));
        } else {
            eInsuranceDetails.addCell(new Cell().add(""));
        }
        eInsuranceDetails.addCell("IR Name: ");
        eInsuranceDetails.addCell(irName);
        table.addCell(new Cell().add(eInsuranceDetails).setBorder(Border.NO_BORDER));
        p = new Paragraph("Open New e - Insurance Account - Please choose the repository from the below");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
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
        table.addCell(new Cell().add(eInsuranceDetails).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("14. Do you need a physical copy of Policy Document?     ");
        p.add(imgUnchecked);
        p.add("     Yes     ");
        p.add(imgChecked);
        p.add("     No      ");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("15. Declaration by Proposer/ Life to be Assured/ Secondary Life Assured");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        p = new Paragraph("I / we have understood the questions in the proposal form and I / we have answered them truthfully, completely and correctly. I / we further declare that I / we have not withheld any material fact or information which may affect the " + "decision of IndiaFirst Life Insurance Company Limited (Hereafter called the “Company”) in underwriting the risk, and the information provided by me / us in the proposal form, the supplementary documents and information provided " + "to the medical examiner in case of being medically examined will form the basis of the contract between me/us and the Company and in case of fraud, misrepresentation and suppression of material facts the policy contract shall be " + "treated in accordance with the Sec 45 of Insurance Act,1938 as amended from time to time. I / we hereby authorize and direct any doctor, hospital, or employer (past and present) to disclose to the Company any information relating " + "to my present state of health, past health history and nature of work performed by me / us. I / we undertake to undergo all medicals as may be required by the Company to assess the risk and grant the insurance. I / we further agree " + "that if after the date of submission of the proposal but before the issuance of policy (i) there is an adverse change in my / us occupation, financial condition, health condition which will affect the decision of the Company in underwriting " + "risk or (ii) if a proposal for assurance or an application for revival of the policy on my / our life or the life to be assured made to any insurer is withdrawn or dropped, deferred, declined or accepted at an increased premium or subject to " + "a lien or on terms other than as proposed, I / we shall forthwith intimate the same to the Company in writing. Failure to do this on my / our part may render this assurance invalid and the policy will be dealt in accordance with section " + "45 of the Insurance Act, 1938 as amended from time to time. I / we understand that the cover applied for under this application will commence after approval of my application and receipt of the required premium by the Company. I  " + "we, hereby declare that the premium have not been generated from proceeds of any criminal activities / offences listed in the Prevention of Money Laundering Act 2002 or under any other applicable law. I understand that in case of " + "withdrawal of this application by me post undergoing medicals or part thereof, the Company shall return the premium deposit after deducting the expenses incurred on the medical test/examination, if any.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph(new Text("Digital Policy Document declaration: \n").setBold());
        p.add("By submitting my details, I authorize hereby IndiaFirst Life and its authorized representatives to contact me and send information/communication through SMS/Email/ Phone/Letter/WhatsApp and/or any other " +
                "electronic mode of communication to my registered email id/ phone number. I hereby consent IndiaFirst Life to store, share, process, use my personal data/information with government agencies/statutory " +
                "authorities/entities as authorized by the IRDAI, third party service providers and partners & affiliates for the purposes of issuance of policy, data analytics, and other related due diligence activities as may be " +
                "requires by IndiaFirst Life.\n");
        p.add("The disclosure of information made on this platform is with my wilful consent. I further acknowledge and agree that IndiaFirst Life shall not be held liable for any breach, loss, or unauthorized disclosure of the data " +
                "provided herein.\n");
        p.add(new Text("I/We hereby give my consent to the Company to carry out due diligence in respect of information, as provided by me in the proposal form, including AML-eKYC verification, and also to store/share the data/information with government agencies/ statutory authorities/ entities as authorized by the regulator - IRDAI for necessary verification purposes and/or policy servicing purpose.\n").setBold());
        p.add(new Text("Having gone through the “Needs Analysis” process, I hereby confirm that the recommended product and its features have been explained to my satisfaction, and it matches my current insurance needs.\n").setBold());
        p.add(new Text("“I/We hereby give consent to the Company for obtaining/downloading CKYC record from Central KYC Records Registry. I/We hereby give consent to the Company for obtaining/downloading KYC details/ from " +
                "government agencies/ entities as authorized by the regulator/ statutory authorities like Passport Seva, Parivahan Sewa, Election Commission of India”.").setBold());
        p.add("\n\n");
        if (jsonUtility.getJsonKeyValue("basbastatus", paymentDetailObj).equalsIgnoreCase("success")) {
            p.add(new Text("I hereby provide my express consent and authorise IndiaFirst Life Insurance Company Ltd to block an amount as quoted in this proposal form (including applicable taxes), for the purpose of premium payment towards insurance. I agree and understand that this mandate shall be valid for a period of 14 days from the date of premium block mandate or date of acceptance of this proposal, whichever is earlier and that the blocked amount will be utilised towards premium payment upon proposal acceptance. I further authorise IndiaFirst Life Insurance Company Ltd to share information with the relevant entities for the purpose of blocking/releasing the premium amount.\n").setBold());
        }
        p.add(new Text("The premium for the proposed insurance policy is payable only after the IndiaFirst Life Insurance Company Ltd has communicated its decision regarding the acceptance of this proposal. The policy shall commence only upon acceptance of the proposal by the IndiaFirst Life Insurance Company Ltd  and Risk Cover shall commence only after receipt of premium.").setBold());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(new Text("NRI/PIO declaration: ").setBold());
        p.add("By providing consent through OTP on the application form I/We hereby confirm that NRI/PIO details provided by me / us in the questionnaire are correct and to the best of my knowledge. By feeding in the said " + "number in the system, I hereby acknowledge the above declaration in its entirety and the same would create a legally binding agreement between the Company and me.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        if (isOmniDoc) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add(new Text("  I hereby authorize IndiaFirst Life Insurance. Co. Ltd., to collect my KYC records from the Central KYC Records Registry (CERSAI) / Third Party for the purpose of KYC verification for my insurance application. Or I hereby authorize IndiaFirst Life Insurance. Co. Ltd., to collect my KYC details/CKYC number from the Bank who is working as a Corporate Agent with them for issuing my insurance policy."));
        p.add("\n");
        if (isOmniDoc) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add(new Text("  I hereby authorize IndiaFirst Life Insurance. Co. Ltd., to share my personal details including KYC details with third party for the purpose of policy printing, dispatch, and all policy servicing purposes wherever required."));
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add("\n");
        p.add("\n");
        if (isOmniDoc) {
            p.add(imgChecked);
            if (countryCode.equalsIgnoreCase("91")) {
                p.add(new Text("   Validated through the OTP sent to " + "registered mobile no. "));
                p.add(new Text(pdfUtility.maskMobileNumber(mobileNo)).setBold());
                p.add("\n\n");
            } else {
                p.add(new Text("   Validated through the OTP sent to " + "registered email id.\n\n"));
            }
        }
        p.add("Life to be Assured’s Signature or Thumb Impression");
        p.add("\n(Not applicable in case of minor lives)");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        String laName = "";
        String laPlace = "";
        if (myself) {
            laName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            laPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
        } else {
            laName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
            laPlace = jsonUtility.getJsonKeyValue("city", secondaryPersonalDetailObj);
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String laDate = LocalDate.now().format(format);
        String witnessName = "";
        String proposerName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
        String proposerPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
        DateTimeFormatter formated = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String proposerDate = LocalDate.now().format(formated);
        String proposerAddress = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj) + ", " + jsonUtility.getJsonKeyValue("state", primaryPersonalDetailObj);

        Table laSignaturedetails = new Table(new float[]{80F, 80F, 80F, 50F, 80F, 70F});
        laSignaturedetails.addCell(new Cell().add("Name: ").setBorder(Border.NO_BORDER));
        laSignaturedetails.addCell(new Cell().add(proposerName).setUnderline().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        laSignaturedetails.addCell(new Cell().add("Place: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        laSignaturedetails.addCell(new Cell().add(proposerPlace).setUnderline().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        laSignaturedetails.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        laSignaturedetails.addCell(new Cell().add(proposerDate).setUnderline().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell().add(laSignaturedetails).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add("\n");
        p.add("\n");
        p.add("Witness's Signature or Thumb Impression");
        p.add("\nName:  ");
        p.add(new Text(witnessName).setUnderline());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        /////
        p = new Paragraph();
        p.add("\n");
        p.add("\n");
        p.add("Secondary Life to be Assured signature (if Joint Life option or better Half benefit is chosen.\n" + "Only applicable for IndiaFirst Life Guaranteed Protection Plus Plan)");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Table proposerSignatureDetails = new Table(new float[]{80F, 80F, 80F, 50F, 80F, 70F});
        proposerSignatureDetails.addCell(new Cell().add("Name: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add(laName).setUnderline().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add("Place: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        proposerSignatureDetails.addCell(new Cell().add(laPlace).setUnderline().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        proposerSignatureDetails.addCell(new Cell().add(laDate).setUnderline().setUnderline().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell().add(proposerSignatureDetails).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add("\n");
        p.add("\n");
        p.add("Proposer’s Signature or Thumb Impression");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        proposerSignatureDetails = new Table(new float[]{100F, 200F});
        proposerSignatureDetails.addCell(new Cell().add("Name: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add(proposerName).setUnderline().setBorder(Border.NO_BORDER));
        proposerSignatureDetails.addCell(new Cell().add("Place: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add(proposerPlace).setUnderline().setBorder(Border.NO_BORDER));
        proposerSignatureDetails.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add(proposerDate).setUnderline().setBorder(Border.NO_BORDER));
        proposerSignatureDetails.addCell(new Cell().add("Address: ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        proposerSignatureDetails.addCell(new Cell().add(proposerAddress).setUnderline().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(proposerSignatureDetails).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add("\n");
        p.add("\n");
        p.add("Witness's Signature or Thumb Impression");
        p.add("\nName:  ");
        p.add(new Text(witnessName).setUnderline());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add(new Text("   Life Assured OTP Verified  "));
        if (isOmniDoc) {
            p.add(imgChecked);
            p.add(new Text("\nLife Assured Mobile No : "));
            p.add(new Text(pdfUtility.maskMobileNumber(mobileNo)));
        } else {
            p.add(imgUnchecked);
            p.add(new Text("\nLife Assured Mobile No : "));
            p.add(new Text("________"));
        }
        p.add("\n\n");
        p.add(new Text("Signature authentication (Single factor authentication): ").setBold());
        p.add("An OTP authentication number has been sent on your registered mobile number. By feeding in the said number in the system, you hereby acknowledge the above declaration " + "in its entirety and the same would create a legally binding agreement between the Company and You");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add(new Text("Section 41 of Insurance Act 1938, as amended from time to time: ").setBold());
        p.add("No person shall allow or offer to allow, either directly or indirectly, as an inducement to any person to take or renew or continue an insurance in respect of " + "any kind of risk relating to lives or property in India, any rebate of the whole or part of the commission payable or any rebate of the premium shown on the policy, nor shall any person taking out or renewing or continuing a policy " + "accept any rebate, except such rebate as may be allowed in accordance with the published prospectus or tables of the insurer. Any person making default in complying with the provisions of this section shall be liable for a penalty " + "which may extend to ten lakh rupees.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add(new Text("Extract of Section 45 of the Insurance Act, 1938, as amended from time to time: ").setBold());
        p.add("No policy of life insurance shall be called into question on any ground whatsoever after the expiry of three years from the date of policy. A policy of life insurance may be called into question at anytime within three years from the " + "date of policy, on the ground of fraud or on the ground that any statement of or suppression of a fact material to the expectancy of the life of the insured was incorrectly made in the proposal or other document on the basis of which " + "the policy was issued or revived or rider issued. The insurer shall have to communicate in writing to the insured or legal representatives or nominees or assignees of the insured, the grounds and materials on which such decision " + "is based. No insurer shall repudiate a life insurance policy on the ground of fraud if the insured can prove that the misstatement or suppression of material fact was true to the best of his knowledge and belief or that there was no " + "deliberate intention to suppress the fact or that such misstatement or suppression are within the knowledge of the insurer. In case of fraud, the onus of disproving lies upon the beneficiaries, in case the policyholder is not alive. In " + "case of repudiation of the policy on the ground of misstatement or suppression of a material fact and not on the grounds of fraud, the premiums collected on the policy till the date of repudiation shall be paid. Nothing in this section " + "shall prevent the insurer from calling for proof of age at any time if he is entitled to do so, and no policy shall be deemed to be called in question merely because the terms of the policy are adjusted on subsequent proof that the age " + "of the life insured was incorrectly stated in the proposal. For complete details of the section and the definition of 'date of policy', please refer Section 45 of the Insurance Act, 1938, as amended from time to time.");
        p.add(new Text("""
                \nCustomers are advised not to pay Insurance premium (initial or renewal) through cash or bearer instrument to IndiaFirst Life insurance Advisors/Employees/Insurance Agents. IndiaFirst Life Insurance Advisors/ Employees/Insurance Agents are not authorised to receive the premium in cash or bearer instrument. Handing over cash or bearer instrument to any IndiaFirst Life Insurance Advisor / Employee/Insurance Agents is solely at your own risk and the company in no way be held responsible for any loss in this regard. Insurance Premium Cheques must be drawn only in favour of IndiaFirst Life Insurance Company Ltd. (Proposal no. for first premium/ policy no. for renewal premium should be written behind the cheque). Any Cheque payment made shall be deemed to be received by IndiaFirst Life Insurance only when the same has been received by any office of IndiaFirst Life Insurance or its authorized collection points and after an official receipt is issued by the Company""").setBold());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("16. Declaration For Signing In Vernacular Or For Uneducated Persons");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        //p.add("\n");
        p.add("1. Vernacular Declaration by the person filling in the form (In case form is filled up / signed in a language different from that of the Proposal Form) \n   I do hereby state that I have read out and explained the contents of the proposal form from IndiaFirst Life Insurance Co. Ltd to the proposer/life assured and he/she have understood the same.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        String nameOfDeclarant = "___________";
        String signatureOfDeclarant = "___________";
        String addressOfdeclarant = "___________";
        String declarantRelation = "___________";
        String language = "__________________";

        Table signatureDeclarants = new Table(new float[]{300F, 200F, 200F, 200F});
        signatureDeclarants.addCell(new Cell().add("Name of the Declarant:").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph(nameOfDeclarant).setUnderline()).setBorder(Border.NO_BORDER));
        p = new Paragraph("Signature:     ");
        p.add(new Text(signatureOfDeclarant).setUnderline());
        signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Or OTP verified    ");
        p.add(imgUnchecked);
        signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add("Address of the Declarant: ").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph(addressOfdeclarant).setUnderline()).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add("Relation with the Life Assured/Proposer:").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph(declarantRelation).setUnderline()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));
        p = new Paragraph("Note: The Declarant identity should be easily established and he/she should not be connected to insurer in any capacity.\n");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

//            signatureDeclarants = new Table(new float[]{300F, 300F, 300F, 150F});
//            signatureDeclarants.addCell(new Cell().add("Signature or thumb impression of the person whose life is proposed to be assured").setBorder(Border.NO_BORDER));
//            signatureDeclarants.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
//            signatureDeclarants.addCell(new Cell().add("Signature or thumb impression of the secondary life to be assured(if Joint Life option or Better Half benefit is chosen.Only applicable for IndiaFirst Life Guaranteed Protection Plus Plan)").setBorder(Border.NO_BORDER));
//            signatureDeclarants.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
//            table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("2. In case the Life Assured / Proposer is illiterate, his/her thumb impression should be attested by a person of standing whose identity can easily be established, but unconnected with the insurer and this declaration should be made by him. “I hereby declare that I have fully explained the above questions and contents of the proposal form to the proposer in ");
        p.add(new Text(language).setUnderline());
        p.add("  language, and that the life assured / proposer has affixed the thumb impression above after fully understanding the contents thereof.”");
//            p.add("\n");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        signatureDeclarants = new Table(new float[]{300F, 200F, 200F, 200F});
        signatureDeclarants.addCell(new Cell().add("Name of the Declarant:").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph(nameOfDeclarant).setUnderline()).setBorder(Border.NO_BORDER));
        p = new Paragraph("Signature:     ");
        p.add(new Text(signatureOfDeclarant).setUnderline());
        signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Or OTP verified    ");
        p.add(imgUnchecked);
        signatureDeclarants.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add("Address of the Declarant: ").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph(addressOfdeclarant).setUnderline()).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add("Relation with the Life Assured/Proposer:").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph(declarantRelation).setUnderline()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));

        //////NEW section added/////////////////

        String authorisedRepresentativeName = language;
        String authorisedPersonResident = language;
        String relationWithAuthPerson = language;
        String fatherMotherName = language;
        String locationAt = language;
        String formattedDate = language;
        String fullNames = language;
        String representativeMobileNumber = "";
        if (jsonUtility.getJsonKeyValue("status", haveDisabilityObj).equalsIgnoreCase("y")) {
            authorisedRepresentativeName = jsonUtility.getJsonKeyValue("authorisedRepresentativeName", haveDisabilityObj);
            authorisedPersonResident = jsonUtility.getJsonKeyValue("authorisedPersonResident", haveDisabilityObj);
            relationWithAuthPerson = jsonUtility.getJsonKeyValue("relationWhitAuthPreson", haveDisabilityObj);
            fatherMotherName = jsonUtility.getJsonKeyValue("fatherMotherName", haveDisabilityObj);
            locationAt = jsonUtility.getJsonKeyValue("locationAt", haveDisabilityObj);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            formattedDate = LocalDate.now().format(formatter);
            fullNames = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            representativeMobileNumber = jsonUtility.getJsonKeyValue("representativeMobileNumber", haveDisabilityObj);
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

        signatureDeclarants = new Table(new float[]{300F, 200F, 300F, 150F});
        p = new Paragraph();
        p.add(isOtpVerified ? imgChecked : imgUnchecked);
        p.add(new Text("   Validated through the OTP sent to registered mobile no."));
        p.add(new Text(isOtpVerified ? pdfUtility.maskMobileNumber(representativeMobileNumber) : "").setBold());
        signatureDeclarants.addCell(new Cell(1, 4).add(p).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        p = new Paragraph("Signature of the Authorised Representative");
        p.add("  Or OTP verified  ");
        if (disclaimerStatus) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        signatureDeclarants.addCell(new Cell(1, 4).add(p).setPaddingRight(10f).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell(1, 4).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
        p = new Paragraph("Relationship of the Authorized Representative");
        p.add("    ");
        p.add(new Text(relationWithAuthPerson).setUnderline());
        signatureDeclarants.addCell(new Cell(1, 4).add(p).setPaddingRight(40f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        p = new Paragraph(" ");
        if (disclaimerStatus) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        p.add("   I certify that the contents of the proposal form have been clearly explained to me and I have fully understood them. I further certify that the replies in the proposal form have been recorded as per the information provided by me.");
        signatureDeclarants.addCell(new Cell(1, 4).add(p).setBorder(Border.NO_BORDER));

        signatureDeclarants.addCell(new Cell(1, 4).add("").setHeight(20F).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add("Signature or thumb impression of the person whose life is proposed to be assured").setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
        signatureDeclarants.addCell(new Cell(2, 4).add("Signature or thumb impression of the secondary life to be assured (if Joint Life option or Better Half benefit is chosen.Only applicable for IndiaFirst Life Guaranteed Protection Plus Plan)").setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(signatureDeclarants).setBorder(Border.NO_BORDER));
        //////NEW section ended/////////////////

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("17. Occupation Details of the Life to be Assured");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));
        p = new Paragraph("(Please tick one of the occupation types that best describes your current occupation as chosen in S.No 1 or 2)");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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
        occupationTypes.addCell("Professionals-doctor,chartered accountant/advocate-lawyer/teacher-lecturer,professors");
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
        occupationTypes.addCell("Agriculture - labourer, cleaner, maintenance workers, gardener, hawker, mill worker, porter / coolie");
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
        table.addCell(new Cell().add(occupationTypes).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("18. Intermediary details");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        String intermediateName = "ONL";
        String intermediateLicenseNumber = " ";
        String agentName = "Online Channel";
        String licenseCode = " ";

        Table intermediateDetails = new Table(new float[]{320F, 350F, 200F, 150F});
        p = new Paragraph("Name of the Intermediary: ");
        p.add("\n(Applicable for all channels except Individual Agents)");
        intermediateDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        intermediateDetails.addCell(new Cell().add(intermediateName).setTextAlignment(TextAlignment.LEFT).setUnderline().setBorder(Border.NO_BORDER));
        intermediateDetails.addCell(new Cell().add("LicenseNumber. ").setBorder(Border.NO_BORDER));
        intermediateDetails.addCell(new Cell().add(intermediateLicenseNumber).setUnderline().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(intermediateDetails).setBorder(Border.NO_BORDER));

        pointColumnWidths = new float[]{250F, 180F, 280F};
        Table signatureTable = new Table(pointColumnWidths);
        signatureTable.addCell(new Cell().setMinHeight(20).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().setMinHeight(20).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().setMinHeight(20).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().add("Signature of the Agent / Specified Agents").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().setMinHeight(5).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().add("Stamp of the Intermediary").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().setMinHeight(5).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().setMinHeight(5).setBorder(Border.NO_BORDER));
        signatureTable.addCell(new Cell().setMinHeight(5).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(signatureTable).setBorder(Border.NO_BORDER));

        intermediateDetails = new Table(new float[]{320F, 350F, 200F, 150F});
        intermediateDetails.addCell(new Cell().add("Name of the Agent / Specified Agents: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        intermediateDetails.addCell(new Cell().add(agentName).setUnderline().setBorder(Border.NO_BORDER));
        intermediateDetails.addCell(new Cell().add("License Code: ").setBorder(Border.NO_BORDER));
        intermediateDetails.addCell(new Cell().add(licenseCode).setUnderline().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(intermediateDetails).setBorder(Border.NO_BORDER));

        headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("19. Know Your Customer Certificate Issued by Bank");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        String customerNameBank = "            ";
        String accountno = "            ";
        String customerId = "            ";
        String authorisedSignature = "_____________________________________";
        String nameOfAuthorized = "_____________________________________";
        String nameOfBranch = "_____________________________________";

        p = new Paragraph();
        p.add("We hereby confirm that   ");
        p.add(new Text(customerNameBank).setUnderline());
        p.add("   holds Savings/Current//Fixed Deposit Loan Account no.   ");
        p.add(new Text(accountno).setUnderline());
        p.add("   and Bank Customer ID   ");
        p.add(new Text(customerId).setUnderline());
        p.add("   with our bank. We confirm that we have obtained the necessary documentary evidence to establish the identity and address" + " of the customer as mentioned by him/ her in this proposal form, as per the “Know Your Customer” (KYC) norms for banks.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Table kycBankDetailsSignatures = new Table(new float[]{400F, 250F});
        kycBankDetailsSignatures.addCell(new Cell().add("Signature of authorised signatory from bank :").setBorder(Border.NO_BORDER));
        kycBankDetailsSignatures.addCell(new Cell().add(authorisedSignature).setBorder(Border.NO_BORDER));
        kycBankDetailsSignatures.addCell(new Cell().add("Name of authorised signatory from bank :").setBorder(Border.NO_BORDER));
        kycBankDetailsSignatures.addCell(new Cell().add(nameOfAuthorized).setBorder(Border.NO_BORDER));
        kycBankDetailsSignatures.addCell(new Cell().add("Name of the bank branch :").setBorder(Border.NO_BORDER));
        kycBankDetailsSignatures.addCell(new Cell().add(nameOfBranch).setBorder(Border.NO_BORDER));

        Table bankSeal = new Table(1);
        bankSeal.addCell(new Cell().setMinHeight(20).setBorder(new SolidBorder(1)));
        bankSeal.addCell(new Cell().add("Bank Seal").setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
        bankSeal.addCell(new Cell().setMinHeight(5).setBorder(Border.NO_BORDER));

        Table mergedKyc = new Table(2);
        mergedKyc.addCell(new Cell().add(kycBankDetailsSignatures).setBorder(Border.NO_BORDER));
        mergedKyc.addCell(new Cell().add(bankSeal).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(mergedKyc).setBorder(Border.NO_BORDER));

        p = new Paragraph("Aforementioned details can be used by the Company to pay the proposer according to the terms of the plan. Payment options (cheque will be used if none of the below electronic " + "payout option is chosen). Further, the Company reserves the right to use any alternative payout option including demand draft / payable at par cheque in spite of option for Direct " + "credit .");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        table.addCell(new Cell().add(uinList()).setBorder(Border.NO_BORDER));
        document.add(table);
        p = new Paragraph();
//            p.add("\n");
//            p.add("\n");
//            p.add("\n");
//            p.add("\n");
        document.add(p);

        if (isOmniDoc) {
            p = new Paragraph();
            p.add(pdfUtility.getCheckedImage(imgCheckBase64));
            String primaryMobileNo = jsonUtility.getJsonKeyValue("mobileNumber", jsonUtility.getJsonObjectByKey("primary", personalDetailObj));
            if (countryCode.equalsIgnoreCase("91")) {
                p.add(new Text("   Validated through the OTP sent to " + "registered mobile no."));
                p.add(new Text(pdfUtility.maskMobileNumber(primaryMobileNo)).setBold());
            } else {
                p.add(new Text("   Validated through the OTP sent to " + "registered email id."));
            }
            document.add(p.setTextAlignment(TextAlignment.CENTER));
            //document.add(new Paragraph("\n"));
            document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
            //document.add(new Paragraph("\n"));
        }

        Table grandFooterTable = new Table(new float[]{600F, 500F});
        grandFooterTable.setTextAlignment(TextAlignment.CENTER);
        Paragraph companyText = new Paragraph(new Text("IndiaFirst Life Insurance Company Ltd.").setBold());
        companyText.add(new Text("\n12th and 13th Floor, North [C] Wing, Tower 4, Nesco IT Park, Nesco Center,\n" + "Western Express Highway, Goregaon (East), Mumbai – 400063,\n" + "IRDA Reg. No. 143. CIN: U66010MH2008PLC183679."));
        grandFooterTable.addCell(companyText).setTextAlignment(TextAlignment.LEFT);

        grandFooterTable.addCell(companyContact());
        grandFooterTable.setWidthPercent(100).setFixedPosition(15, 20, 800F);
        document.add(grandFooterTable.setVerticalAlignment(VerticalAlignment.BOTTOM)).setTextAlignment(TextAlignment.JUSTIFIED);
        logger.info("Nominee list size is:{}", nomineeList.size());
        if (nomineeList.size() > 1) {
            Table nomineeAddendum = this.generateNomineePDF(pdfUtility, jsonUtility, applicationNumber, nomineeList);
            document.add(new AreaBreak(AreaBreakType.NEXT_AREA));
            document.add(nomineeAddendum);
        }
        document.close();
        return baos.toByteArray();
    } catch (Exception e) {
        logger.info("Exception occurs in gpp pdf generation method:{}", e);
        return null;
    }
}catch (Exception e){
    logger.info("Exception occurs in gpp pdf generation method:{}", e);
}
return null;
    }

    public Table getSecondaryLifeAssuredDetails(Image imgUnchecked,Table innerProposer){

        Table table = new Table(1);
        Table additionalDetails = new Table(2);
        float[] pointColumnWidths = new float[]{200F, 200F};
        Table firstBlockTable = new Table(pointColumnWidths);
        Table firstBlockLeft = new Table(1);
        firstBlockLeft.setMarginTop(20);
        firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
        firstBlockLeft.setWidth(150);
        Border solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);
        String photoBase64="iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4//8/AAX+Av4N70a4AAAAAElFTkSuQmCC";
        byte[] photoBytes = Base64.getDecoder().decode(photoBase64.getBytes());
        ImageData dataPhoto = ImageDataFactory.create(photoBytes);
        Image photoImg = new Image(dataPhoto);
        photoImg.setHeight(150);
        photoImg.setWidth(150);
        firstBlockLeft.addCell(new Cell().add(photoImg).setBorder(solidBorder));
        firstBlockTable.addCell(new Cell().add(firstBlockLeft).setBorder(Border.NO_BORDER));
        Table secondaryDetails = new Table(new float[]{200F, 1050F});
        secondaryDetails.addCell(new Cell().add(firstBlockTable).setBorder(Border.NO_BORDER));
        Table lifeAssuredDetails = new Table(2);
        lifeAssuredDetails.setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(new Cell().add("Full Name (Leave a blank space between First and Last Name)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        Paragraph p = new Paragraph("Mr.  ");
        p.add(imgUnchecked);
        p.add("     Mrs. ");
        p.add(imgUnchecked);
        p.add("     Ms.  ");
        p.add(imgUnchecked);
        p.add("     Mx.  ");
        p.add(imgUnchecked);
        lifeAssuredDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Cell proposerMergedcell = new Cell(1, 2);
        String lifeAssuredFullName = "";
        proposerMergedcell.add(pdfUtility.createDataTable(lifeAssuredFullName,"00000000000000000000")).setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(proposerMergedcell);

        /////////////////////
        lifeAssuredDetails.addCell(new Cell().add("Existing IndiaFirst Policy Owner, Kindly enter policy number / client id").setBorder(Border.NO_BORDER));
        lifeAssuredDetails.addCell(new Cell().add(innerProposer).setBorder(Border.NO_BORDER));

        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("  Select, if the Communication Address is same as Primary Life Assured ");
        p.add("\n\nIf not, Communication Address of Secondary Life Assured");
        p.setPaddingBottom(10);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(p);

        String address1 = "";
        String address2 = "";
        String address3 = "";
        String city = "";
        String state = "";

        Table addressBlock = new Table(1);

        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

        addressBlock=new Table(new float[]{400F,70F,100F});
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"0000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable("","000000")).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);

        //Permanent Address Section addition started

        p = new Paragraph("Permanent Address (If different from the above Address)");
        p.setPaddingBottom(10);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(p);
        // proposerMergedcell.add(communicationAddress.toUpperCase()).setBorder(Border.NO_BORDER);
        addressBlock = new Table(1);
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address1,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address2,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(address3,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(city,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        addressBlock.addCell(new Cell().add(pdfUtility.createDataTable(state,"0000000000000000000000000000000")).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(addressBlock).setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(proposerMergedcell).setVerticalAlignment(VerticalAlignment.MIDDLE);
        //Permanent Address Section addition ended

        String countryCode = "";
        Table contactTable = new Table(new float[]{120F, 10F, 80F, 120F, 180F, 120F, 200F});
        contactTable.addCell(new Cell().add("Country Code").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("+").setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(countryCode,"00")).setBorder(Border.NO_BORDER));
        String mobileNo = "";
        //contactTable.addCell(new Cell().add("Mobile No:      ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mobile No* ");
        p.add(new Text("\n*Receive alerts through SMS and WhatsApp for this proposal / policy").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(mobileNo,"0000000000")).setBorder(Border.NO_BORDER));
        String pinCode = "";
        contactTable.addCell(new Cell().add("Pin Code:  ").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(pinCode,"000000")).setBorder(Border.NO_BORDER));

        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.add(contactTable);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(proposerMergedcell);
        /////////////////////
        ///////////////////////////


        proposerMergedcell = new Cell(1, 2);
        pointColumnWidths = new float[]{200F, 600F, 200F, 400F};
        contactTable = new Table(pointColumnWidths);
        String emailId = "";
        String landLine = "";
        p = new Paragraph("Email ID* ");
        p.add(new Text("\n*Receive communication via e-mail").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        // contactTable.addCell(new Cell().add(emailId).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(emailId,"000000000000000")).setBorder(Border.NO_BORDER));

        p=new Paragraph("Landline:");
        p.add(new Text("\nSTD/ISD").setFontSize(5F));
        contactTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(landLine,"00000000")).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Gender:    ").setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Male: ");
        p.add(imgUnchecked);
        p.add("     Female:     ");
        p.add(imgUnchecked);
        p.add("     Transgender:    ");
        p.add(imgUnchecked);
        contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Nationality:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Indian:      ");
        p.add(imgUnchecked);
        p.add("     Non Indian:     ");
        p.add(imgUnchecked);
        contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        String lifeAssuredDoB = "";
        contactTable.addCell(new Cell().add("DOB :   ").setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(pdfUtility.createDataTable(lifeAssuredDoB,"00000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        contactTable.addCell(new Cell().add("Age:  ").setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add(" " + " Years").setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Marital Status :   ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph("Unmarried:   ");
        p.add(imgUnchecked);
        p.add("     Married:  ");
        p.add(imgUnchecked);
        p.add("     Widow(er):  ");
        p.add(imgUnchecked);
        p.add("     Divorced:   ");
        p.add(imgUnchecked);
        contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));


        contactTable.addCell(new Cell().add("Residential Status:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
        p.add("Resident:    ");
        p.add(imgUnchecked);
        p.add("     NRI:    ");
        p.add(imgUnchecked);
        p.add("     PIO:    ");
        p.add(imgUnchecked);
//                p.add("     FNIO:    ");
//                p.add(imgUnchecked);
//                p.add("     OCI:    ");
//                p.add(imgUnchecked);
        contactTable.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Education:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
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
        contactTable.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("Occupation:    ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
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
        contactTable.addCell(new Cell(1, 4).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        proposerMergedcell.add(new Cell().add(contactTable).setBorder(Border.NO_BORDER));
        proposerMergedcell.setBorder(Border.NO_BORDER);
        lifeAssuredDetails.addCell(proposerMergedcell);

        Table otherDetails = new Table(new float[]{300F, 200F, 300F, 200F});
        otherDetails.addCell(new Cell().add("Name of the Org./Business :").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(new Paragraph(" ").setUnderline()).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add("Total Years in Service/ Business").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(" ").setBorder(Border.NO_BORDER));

        otherDetails.addCell(new Cell().add("Income (Annual): ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(new Paragraph(" ").setUnderline()).setBorder(Border.NO_BORDER));

        otherDetails.addCell(new Cell().add("Source of Income: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(" ").setUnderline().setBorder(Border.NO_BORDER));

        otherDetails.addCell(new Cell().add("Nature of work/duties: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(" ").setUnderline().setBorder(Border.NO_BORDER));

        otherDetails.addCell(new Cell().add("Age Proof:    ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(new Paragraph(" ").setUnderline()).setBorder(Border.NO_BORDER));

        p = new Paragraph("PAN: ");
        p.add("(Please provide Form 60, if PAN is not available)");
        otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(new Cell().add(pdfUtility.createDataTable("","000000000")).setUnderline()).setBorder(Border.NO_BORDER));

        p = new Paragraph("PAN  ");
        p.add(" (photocopy Enclosed): ");
        otherDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        otherDetails.addCell(new Cell().add(imgUnchecked).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        otherDetails.addCell(new Cell().add("Are you a Politically Exposed Person ?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Yes     ");
        p.add(imgUnchecked);
        p.add("     No     ");
        otherDetails.addCell(new Cell(1, 3).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        lifeAssuredDetails.addCell(new Cell(1, 2).add(otherDetails).setBorder(Border.NO_BORDER));
        secondaryDetails.addCell(new Cell().add(lifeAssuredDetails).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(secondaryDetails).setBorder(Border.NO_BORDER));
        p = new Paragraph("Politically Exposed Persons (PEPs) are individuals who are or have been entrusted with prominent public functions in a foreign country, example, Heads of State or of Governments, " + "senior politicians, senior government/judicial/military officials, senior executives of state owned corporations, important political party officials, etc., including their family members and " + "close relatives.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        String relationshipWithPR = " ";
        p = new Paragraph("Relationship with Primary Life Assured- ");
        p.add(new Text(relationshipWithPR).setBold().setUnderline());
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Cell headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));

        contactTable = new Table(new float[]{350F, 200F, 350F, 200F});
        contactTable.addCell(new Cell().add("(a) Place of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" ").setUnderline();
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        contactTable.addCell(new Cell().add("   and Country of birth: ").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("").setUnderline();
        contactTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        additionalDetails = new Table(2);
        proposerMergedcell = new Cell(1, 2);
        proposerMergedcell.setBorder(Border.NO_BORDER);
        proposerMergedcell.add(contactTable);
        additionalDetails.addCell(proposerMergedcell);
        additionalDetails.addCell(new Cell().add("(b) Are you a citizen of any other country also (Dual / Multiple): ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Yes     ");
        p.add(imgUnchecked);
        p.add("     No      ");
        additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        additionalDetails.addCell(new Cell().add("(c) Are you a resident (For tax purposes) of any other country other than India.:  ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Yes     ");
        p.add(imgUnchecked);
        p.add("     No      ");
        additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        additionalDetails.addCell(new Cell().add("(d) Do you hold a green card of US or any similar card for any other country: ").setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(imgUnchecked);
        p.add("     Yes     ");
        p.add(imgUnchecked);
        p.add("     No      ");
        additionalDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        proposerMergedcell = new Cell(1, 2);
        p = new Paragraph();
        p.add("If answer to any /all of the above is yes, please do fill all the details in the Insurance FATCA Declaration");
        proposerMergedcell.add(new Cell().add(p).setBorder(Border.NO_BORDER));
        proposerMergedcell.setBorder(Border.NO_BORDER);
        additionalDetails.addCell(proposerMergedcell);
        table.addCell(new Cell().add(additionalDetails).setBorder(Border.NO_BORDER));
        return table;
    }

    public Table getBankDetails(Image imgUnchecked, Image imgChecked,JsonObject primaryBankObj,JsonObject eMandateObj,JsonArray nomineeList){
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
        //String bankName = "State Bank of India";
        float[]pointColumnWidths = new float[bankName.length()];
        for(int i=0;i<bankName.length();i++){
            pointColumnWidths[i] = 20F;
        }
        Table tableCodes = pdfUtility.codeTable(bankName, pointColumnWidths);
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
        //String branchName = "Powai";
        pointColumnWidths = new float[branchName.length()];
        for(int i=0;i<branchName.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = pdfUtility.codeTable(branchName, pointColumnWidths);
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        String bankAccountNo = accountNo;
        pointColumnWidths = new float[bankAccountNo.length()];
        for(int i=0;i<bankAccountNo.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = pdfUtility.codeTable(bankAccountNo, pointColumnWidths);
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        Table ifscDetails = new Table(new float[] {50F, 200F, 250F, 70F, 200F, 300F});
        ifscDetails.addCell(new Cell().add("MICR:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[micr.length()];
        for(int i = 0; i< micr.length(); i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = pdfUtility.codeTable(micr, pointColumnWidths);
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for ECS mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        String ifscCode = ifscode;
        pointColumnWidths = new float[ifscCode.length()];
        for(int i=0;i<ifscCode.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = pdfUtility.codeTable(ifscCode, pointColumnWidths);
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        Table nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Customer’s Name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        pointColumnWidths = new float[customerName.length()];
        for(int i=0;i<customerName.length();i++){
            pointColumnWidths[i] = 20F;
        }
        tableCodes = pdfUtility.codeTable(customerName, pointColumnWidths);
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
        ifscDetails.addCell(new Cell().add(pdfUtility.createDataTable(ifscode,"0000000000")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
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
        p.add("In case of non credit to my bank account with/without assigning any reasons there of or if the transaction is delayed or not credited at all for reasons of incomplete/incorrect " +
                "information, I will not hold IndiaFirst Life Insurance Co. Ltd. responsible. Further, the Company reserves the right to use any alternative payout option including demand draft/payable at par " +
                "cheque inspite of not opting for the direct credit option.");
        nameDetails.addCell(new Cell(1,2).add(p).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        return table;
    }

    private Table uinList() {
        Table uinList = new Table(new float[]{450F, 450F, 400F});
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Money Balance Plan 143L017V07*").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Term Rider 143B001V02").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Plan 143N007V03#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Wealth Maximizer Plan 143L029V05*").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Cash Back Plan 143N024V05#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Smart Pay Plan 143N051V04#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Waiver of Premium Rider 143B017V01").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Long Guaranteed Income Plan 143N054V06#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Guaranteed Benefit Plan 143N056V07#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Fortune Plus Plan 143N065V03#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Mahajeevan Plus Plan 143N059V03#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Guarantee Of Life Dreams Plan 143N080V03#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Elite Term Plan 143N070V01").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Term with Unit Linked Insurance Plan 143L072V02*").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Term with ULIP Plus 143L073V02*").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Guaranteed Single Premium Plan 143N068V04*").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Little Champ Plan 143N035V02").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Super Protection Plan (UIN: 143N075V01)#").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Total and Permanent Disability Rider (ULIP) 143A022V01").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Accidental Death Benefit Rider (ULIP) 143A020V01 ").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell().add("• UIN for IndiaFirst Life Accidental Death Benefit Rider (Traditional) 143B019V01").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell(1,3).add("• UIN for IndiaFirst Life Total and Permanent Disability Rider (Traditional) 143B021V01").setHeight(20F).setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell(1,3).add("*IndiaFirst Life Accidental Death Benefit Rider (143A020V01) and IndiaFirst Life Total and Permanent Disability Rider (143A022V01) are available with the * marked product.").setBorder(Border.NO_BORDER));
        uinList.addCell(new Cell(1,3).add("#IndiaFirst Life Accidental Death Benefit Rider (143B019V01) and IndiaFirst Life Total and Permanent Disability Rider (143B021V01) are available with the # marked product").setBorder(Border.NO_BORDER));
        return uinList;
    }

    public String feetAndInchesToCms(double feet, double inches) {
        double totalInches = (feet * 12) + inches;
        double centimeters = totalInches * 2.54;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(centimeters);
    }

    private Paragraph companyContact() {
        Paragraph companyContact = new Paragraph(new Text("Tel: ").setBold());
        companyContact.add(new Text("+91 22 6165 8700"));
        companyContact.add(new Text("  Fax: ").setBold());
        companyContact.add(new Text("+91 22 6857 0600"));
        companyContact.add(new Text("  Toll Free: ").setBold());
        companyContact.add(new Text("1800-209-8700"));

        companyContact.add(new Text("\n---------------------------------------------------------------------------------------------------------------------------------------------------------"));
        companyContact.add(new Text("\nE-mail: ").setBold());
        companyContact.add(new Text("customer.\u001Afirst@india\u001Arstlife.com"));
        companyContact.add(new Text("  Website: ").setBold());
        companyContact.add(new Text("www.india\u001Afirstlife.com"));

        return companyContact;
    }

    @Override
    Table codeTable(String codeData, float[] pointColumnWidths) {
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
