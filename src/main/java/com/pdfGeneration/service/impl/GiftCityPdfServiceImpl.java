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
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.pdfGeneration.service.GiftCityPdfService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

@Service
public class GiftCityPdfServiceImpl implements GiftCityPdfService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;

    public GiftCityPdfServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;
    }
    
    @Override
    public byte[] generateGiftCityPdf(String giftCityPdfObj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            JsonObject userData = jsonUtility.getJsonObject(giftCityPdfObj);
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

            JsonObject policyHolderBasicDetailObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            JsonObject insuredPersonBasicDetailObj = jsonUtility.getJsonObjectByKey("insuredPerson", basicDetailObj);
            String buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderBasicDetailObj);

            boolean isSelfProposed = jsonUtility.getJsonKeyValue("isSelfproposed", policyHolderBasicDetailObj).equalsIgnoreCase("Yes");
            logger.info("---- is self proposed---:{}", isSelfProposed);
            JsonObject primaryBankObj = jsonUtility.getJsonObjectByKey("primary", bankDetailObj);

            JsonObject primaryPersonalDetailObj = jsonUtility.getJsonObjectByKey("primary", personalDetailObj);
            JsonObject secondaryPersonalDetailObj = jsonUtility.getJsonObjectByKey("secondary", personalDetailObj);
            JsonObject primaryEmploymentDetailObj = jsonUtility.getJsonObjectByKey("primary", employmentDetailObj);
            JsonObject secondaryEmploymentDetailObj = jsonUtility.getJsonObjectByKey("secondary", employmentDetailObj);
            JsonObject primaryFatcaDetailObj = jsonUtility.getJsonObjectByKey("primary", fatcaDetailObj);
            JsonObject secondaryFatcaDetailObj = jsonUtility.getJsonObjectByKey("secondary", fatcaDetailObj);
            JsonObject nomineeFatcaDetailObj = jsonUtility.getJsonObjectByKey("nominee", fatcaDetailObj);
            JsonObject primaryOtherDetailObj = jsonUtility.getJsonObjectByKey("primary", otherDetailObj);
            JsonObject secondaryOtherDetailObj = jsonUtility.getJsonObjectByKey("secondary", otherDetailObj);
            JsonObject primaryDocumentDetailObj = jsonUtility.getJsonObjectByKey("primary", documentDetailObj);
            JsonObject secondaryDocumentDetailObj = jsonUtility.getJsonObjectByKey("secondary", documentDetailObj);

            String applicationNumber = jsonUtility.getJsonKeyValue("applicationNumber", userData);
            
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

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A3).setFont(font);
            document.setFontSize(9);
            document.setFontKerning(FontKerning.YES);
            document.setMargins(0F, 0f, 0f, 0f);

            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images", userData);
            String logoFilename = jsonUtility.getJsonKeyValue("whatsappLogo", imagesJson);
            String logoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);
            img.setHeight(80);
            img.setWidth(120);

            document.setMargins(10F, 10f, 10f, 10f);

            Table table = new Table(1);
            Paragraph p = new Paragraph();
            p.add(img);
            table.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text("Policy No.  ").setBold());
            p.add(applicationNumber);
            p.setMarginRight(60);
            table.addCell(new Cell().add(p).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            Cell headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph(jsonUtility.getJsonKeyValue("heading", contentJson));
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("disclaimer", contentJson)).setBold());
            p.setMarginTop(10);
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            float[] pointColumnWidths = new float[]{200F, 680F};
            Table firstBlockTable = new Table(pointColumnWidths);
            Table firstBlockLeft = new Table(1);
            firstBlockLeft.setMarginTop(20);
            firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
            firstBlockLeft.setWidth(150);
            SolidBorder solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);
            String photoBase64 = jsonUtility.getJsonKeyValue("proposerPhotoBase64",userData);
            byte[] photoBytes = Base64.getDecoder().decode(photoBase64.getBytes());
            ImageData dataPhoto = ImageDataFactory.create(photoBytes);
            Image photoImg = new Image(dataPhoto);
            photoImg.setHeight(150);
            photoImg.setWidth(150);
            firstBlockLeft.addCell(new Cell().add(photoImg).setBorder(solidBorder));
            firstBlockTable.addCell(new Cell().add(firstBlockLeft).setBorder(Border.NO_BORDER));

            String agentCode = "0N000001";
            String branchCodeValue = jsonUtility.getJsonKeyValue("branchCode", policyHolderBasicDetailObj);
            String branchCode = branchCodeValue.isEmpty() ? "DM001" : branchCodeValue;
            String branchManagerCode = "";
            String rmCode = "ON000001";
            String channelCode = "Online";
            String dbmMobile = "";

            Table firstBlockRight = new Table(6);
            p = new Paragraph(new Text("For Branch Sales Use Only").setBold());
            firstBlockRight.addCell(new Cell(1, 6).add(p).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph("LG / Agent Code: ").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph(agentCode)).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph("Branch Code: ").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph(branchCode)).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph("Branch Manager Code: ").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph(branchManagerCode)).setBorder(Border.NO_BORDER));
            p = new Paragraph("(LG code to be written for Banca, Agent Code to be written for Agency.)");
            p.setFontSize(7);
            firstBlockRight.addCell(new Cell(1, 6).add(p).setBorder(Border.NO_BORDER));

            firstBlockRight.addCell(new Cell().add(new Paragraph("BDM/ RM Code: ").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph(rmCode)).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph("Channel Code: ").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph(channelCode)).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph("BDM Mobile No. : ").setBold()).setBorder(Border.NO_BORDER));
            firstBlockRight.addCell(new Cell().add(new Paragraph(dbmMobile)).setBorder(Border.NO_BORDER));

            p = new Paragraph("Bancassurance/Agency/Broker/Corporate Agency/Direct Sales/ Marketng Associate, Any others (pls specify):  ");
            p.add(new Text("Direct Sales").setUnderline().setBold());
            firstBlockRight.addCell(new Cell(1, 6).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph(new Text("Important Guidelines:").setBold());
            p.add("1. This form is to be filled electronically and leave a space blank between each part of the name. 2. Before filling up the form please read the sales literature to understand the features, benefits, advantages and terms and conditions of the product. 3. If the space provided in the form is not sufficient for providing details, please attach separate sheets signed by the Proposer/ Life to be Assured. 4. All details should be filled completely including email ID, mobile number, etc. 5. Customers are advised not to hand over the premium to any individual (in any capacity) to meet the premium dues (including initial premium). Premium payment made to any individual (in any capacity) is at the customer’s own risk. 6. While answering questions in the proposal form and providing any other information in respect of the insurance, the Policy holder must make a full and frank disclosure of all material facts with respect to the questions available in proposal form. 7. In case the Proposer and Life to be Assured are two separate individuals, the proposal form will be signed by both. The life to be assured can sign only if he/she is 18 years or above.");
            firstBlockRight.addCell(new Cell(1, 6).add(p).setMarginTop(10).setMarginBottom(10).setBorder(Border.NO_BORDER));
            firstBlockTable.addCell(new Cell().add(firstBlockRight).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(firstBlockTable).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("1. Proposer/ Policy Owner Details (Please fill in details of Life to be Assured if same as Proposer)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            String fullName = jsonUtility.getJsonKeyValue("fullName", primaryPersonalDetailObj);
            String communicationAddress = jsonUtility.getJsonKeyValue("addressline1", primaryPersonalDetailObj)+","+jsonUtility.getJsonKeyValue("addressline2", primaryPersonalDetailObj)+","+jsonUtility.getJsonKeyValue("addressline3", primaryPersonalDetailObj)+","+
                    jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj)+","+jsonUtility.getJsonKeyValue("state", primaryPersonalDetailObj)+" -"+jsonUtility.getJsonKeyValue("pincode", primaryPersonalDetailObj);
            String permanentAddress = jsonUtility.getJsonKeyValue("permanentaddressline1", primaryPersonalDetailObj)+","+jsonUtility.getJsonKeyValue("permanentaddressline2", primaryPersonalDetailObj)+","+jsonUtility.getJsonKeyValue("permanentaddressline3", primaryPersonalDetailObj)+","+
                    jsonUtility.getJsonKeyValue("permanentcity", primaryPersonalDetailObj)+","+jsonUtility.getJsonKeyValue("permanentstate", primaryPersonalDetailObj)+" -"+jsonUtility.getJsonKeyValue("permanentpincode", primaryPersonalDetailObj);
            String countryCode = jsonUtility.getJsonKeyValue("countryCode", primaryPersonalDetailObj);
            String mobile = "(+"+countryCode+") "+jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetailObj);
            String emailId = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetailObj);
            String dob = jsonUtility.getJsonKeyValue("dateOfBirth", primaryPersonalDetailObj);
            LocalDate currentDate = LocalDate.now();
            LocalDate birthDate = LocalDate.parse(dob);
            String age = String.valueOf(Period.between(birthDate, currentDate).getYears());
            String gender = jsonUtility.getJsonKeyValue("gender", policyHolderBasicDetailObj);
            String nationality = jsonUtility.getJsonKeyValue("nationality", primaryPersonalDetailObj);

            String passportNo = jsonUtility.getJsonKeyValue("passportNumber",primaryPersonalDetailObj);
            String passportIssueDate = jsonUtility.getJsonKeyValue("passportIssueDate",primaryPersonalDetailObj);
            String passportExpiryDate = jsonUtility.getJsonKeyValue("passportExpDate",primaryPersonalDetailObj);
            String placeOfPassport = jsonUtility.getJsonKeyValue("placeOfPassportIssued",primaryPersonalDetailObj);
            JsonObject primaryFatcaObj = jsonUtility.getJsonObjectByKey("primary", fatcaDetailObj);
            JsonObject secondaryFatcaObj = jsonUtility.getJsonObjectByKey("secondary", fatcaDetailObj);
            JsonObject primaryResidentOtherThanUsObj = jsonUtility.getJsonObjectByKey("residentOtherThanUS", primaryFatcaObj);
            JsonObject secResidentOtherThanUsObj = jsonUtility.getJsonObjectByKey("residentOtherThanUS", secondaryFatcaObj);
            JsonObject nomineeResidentOtherThanUsObj = jsonUtility.getJsonObjectByKey("residentOtherThanUS", nomineeFatcaDetailObj);
            String tin = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", primaryFatcaObj)).equalsIgnoreCase("y") ? jsonUtility.getJsonKeyValue("tinNumber", primaryResidentOtherThanUsObj) : "";
            String maritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", primaryPersonalDetailObj);
            String residentialStatus = jsonUtility.getJsonKeyValue("residentialstatus", primaryPersonalDetailObj);
            String education = jsonUtility.getJsonKeyValue("educationType", primaryEmploymentDetailObj);
            String occupationType = jsonUtility.getJsonKeyValue("occupation", primaryEmploymentDetailObj);
            String natureOfWorkDuties = jsonUtility.getJsonKeyValue("natureOfWork", primaryEmploymentDetailObj);
            String industryType = jsonUtility.getJsonKeyValue("industryType", primaryEmploymentDetailObj);
            String organizationType = jsonUtility.getJsonKeyValue("organizationType", primaryEmploymentDetailObj);
            String nameOfOrg = jsonUtility.getJsonKeyValue("nameOfOrganisation", primaryEmploymentDetailObj);
            String totalYearsInService = jsonUtility.getJsonKeyValue("experience", primaryEmploymentDetailObj);
            String income = jsonUtility.getJsonKeyValue("annualIncome", primaryEmploymentDetailObj);
            String sourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", primaryEmploymentDetailObj);
            String identityProof = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("idProof", primaryDocumentDetailObj));
            String pan = jsonUtility.getJsonKeyValue("pancard", primaryPersonalDetailObj);

            pointColumnWidths = new float[]{200F, 200F, 200F, 200F, 200F, 200F};
            Table proposerDetails = new Table(pointColumnWidths);
            proposerDetails.addCell(new Cell().add(new Paragraph("Full Name :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell(1, 6).add(fullName).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Communication Address of the Proposer :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell(1, 6).add(communicationAddress).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Mobile :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(mobile).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Email ID :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell(1, 6).add(emailId).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Permanent Address of the Proposer (Same as above) :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell(1, 6).add(permanentAddress).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("DOB :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(dob).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Age :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(age).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Gender :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(gender).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Nationality :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(nationality).setBorder(Border.NO_BORDER));

            proposerDetails.addCell(new Cell().add(new Paragraph("Passport No. : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(passportNo).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Passport Issue Date :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(passportIssueDate).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Passport Exp. Date :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(passportExpiryDate).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Place Of Issue of Passport :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(placeOfPassport).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("TIN (If Nationality is United States of America) :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(tin).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Marital Status : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(maritalStatus).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Residental Status : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(residentialStatus).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Education :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(education).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Occupation :").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(occupationType).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Nature of work/duties: ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(natureOfWorkDuties).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Industry Type : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(industryType).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Organisation Type : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(organizationType).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Name of the Org/Business : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(nameOfOrg).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Total Years in Service/Business : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(totalYearsInService).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Income : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(income).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Source of Income : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(sourceOfIncome).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Identity Proof : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(identityProof).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Pan : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(pan).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Is this policy self proposed : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(isSelfProposed ? "Yes" : "No").setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(new Paragraph("Relationship with Life to be Assured : ").setBold()).setBorder(Border.NO_BORDER));
            proposerDetails.addCell(new Cell().add(isSelfProposed ? "Self" : jsonUtility.getJsonKeyValue("relationtoProposer",primaryPersonalDetailObj)).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(proposerDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            String placeOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth", jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));
            String countryOfBirth = jsonUtility.getJsonKeyValue("countryOfBirthLabel", jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaDetailObj));
            String areYouCitizenOfMultiple = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";
            String areResidentOtherIndia = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";
            String doYouHoldGreenCard  = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";

            p = new Paragraph("a. Place of birth : ");
            p.setMarginTop(10);
            p.add(new Text(placeOfBirth));
            p.add("   and Country of birth : ");
            p.add(countryOfBirth);
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("b. Are you a citizen of any other country also (Dual/Multiple) : ");
            p.add(new Text(areYouCitizenOfMultiple));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("c. Are you a resident (For tax purposes) of any other country other than India : ");
            p.add(new Text(areResidentOtherIndia));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("Do you hold a green card of US or any similar card for any other country : ");
            p.add(new Text(doYouHoldGreenCard));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("If answer to any /all of the above (b,c & d) is yes, please do fill all the details in the Insurance FATCA Declaration");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));


            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("2. Details of the Life to be Assured (Please fill section 2 only if Life to be Assured is different from Proposer)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            if (!isSelfProposed) {
                pointColumnWidths = new float[]{200F, 680F};
                firstBlockTable = new Table(pointColumnWidths);
                firstBlockLeft = new Table(1);
                firstBlockLeft.setMarginTop(20);
                firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
                firstBlockLeft.setWidth(150);
                solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);
                photoBase64 = jsonUtility.getJsonKeyValue("insuredPhotoBase64",userData);
                photoBytes = Base64.getDecoder().decode(photoBase64.getBytes());
                dataPhoto = ImageDataFactory.create(photoBytes);
                photoImg = new Image(dataPhoto);
                photoImg.setHeight(150);
                photoImg.setWidth(150);
                firstBlockLeft.addCell(new Cell().add(photoImg).setBorder(solidBorder));
                firstBlockTable.addCell(new Cell().add(firstBlockLeft).setBorder(Border.NO_BORDER));


                fullName = jsonUtility.getJsonKeyValue("fullName", secondaryPersonalDetailObj);
                dob = jsonUtility.getJsonKeyValue("dateOfBirth", secondaryPersonalDetailObj);
                birthDate = LocalDate.parse(dob);
                age = String.valueOf(Period.between(birthDate, currentDate).getYears());
                gender = jsonUtility.getJsonKeyValue("gender", insuredPersonBasicDetailObj);
                nationality = jsonUtility.getJsonKeyValue("nationality", secondaryPersonalDetailObj);

                passportNo = jsonUtility.getJsonKeyValue("passportNumber",secondaryPersonalDetailObj);
                passportIssueDate = jsonUtility.getJsonKeyValue("passportIssueDate",secondaryPersonalDetailObj);
                passportExpiryDate = jsonUtility.getJsonKeyValue("passportExpDate",secondaryPersonalDetailObj);
                placeOfPassport = jsonUtility.getJsonKeyValue("placeOfPassportIssued",secondaryPersonalDetailObj);
                tin = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", secondaryFatcaObj)).equalsIgnoreCase("y") ? jsonUtility.getJsonKeyValue("tinNumber", secResidentOtherThanUsObj) : "";
                maritalStatus = jsonUtility.getJsonKeyValue("maritalstatus", secondaryPersonalDetailObj);
                residentialStatus = jsonUtility.getJsonKeyValue("residentialstatus", secondaryPersonalDetailObj);
                education = jsonUtility.getJsonKeyValue("educationType", secondaryEmploymentDetailObj);
                occupationType = jsonUtility.getJsonKeyValue("occupation", secondaryEmploymentDetailObj);
                natureOfWorkDuties = jsonUtility.getJsonKeyValue("natureOfWork", secondaryEmploymentDetailObj);
                industryType = jsonUtility.getJsonKeyValue("industryType", secondaryEmploymentDetailObj);
                organizationType = jsonUtility.getJsonKeyValue("organizationType", secondaryEmploymentDetailObj);
                nameOfOrg = jsonUtility.getJsonKeyValue("nameOfOrganisation", secondaryEmploymentDetailObj);
                totalYearsInService = jsonUtility.getJsonKeyValue("experience", secondaryEmploymentDetailObj);
                income = jsonUtility.getJsonKeyValue("annualIncome", secondaryEmploymentDetailObj);
                sourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", secondaryEmploymentDetailObj);
                pan = jsonUtility.getJsonKeyValue("pancard", secondaryPersonalDetailObj);
                String panEnclosed = pan.equalsIgnoreCase("pan card") ? "Yes" : "No";
                String ageProof = jsonUtility.getJsonKeyValue("documentType", jsonUtility.getJsonObjectByKey("ageProof-all", secondaryDocumentDetailObj));

                firstBlockRight = new Table(6);
                firstBlockRight.addCell(new Cell().add(new Paragraph("Full Name :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell(1, 6).add(fullName).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("DOB :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(dob).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Age :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(age).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Gender :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(gender).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Nationality :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(nationality).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Passport No. : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(passportNo).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Passport Issue Date : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(passportIssueDate).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Passport Exp. Date : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(passportExpiryDate).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Place Of Issue of Passport : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(placeOfPassport).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Marital Status : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(maritalStatus).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Residental Status : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(residentialStatus).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Education : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(education).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Occupation : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(occupationType).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("TIN (If Nationality is United States of America) : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(tin).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Nature of Work/duties : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(natureOfWorkDuties).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("PAN (photocopy Enclosed): ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(panEnclosed).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Industry Type: ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(industryType).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Organisation Type: ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(organizationType).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("PAN :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(pan).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Source Of Income : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(sourceOfIncome).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Age Proof  :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(ageProof).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Name of the Org./Business : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(nameOfOrg).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Total Years in Service/ Business :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(totalYearsInService).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Income(Annual) :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(income).setBorder(Border.NO_BORDER));
                firstBlockTable.addCell(new Cell().add(firstBlockRight).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(firstBlockTable).setBorder(Border.NO_BORDER));


                placeOfBirth = jsonUtility.getJsonKeyValue("placeOfBirth", jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaDetailObj));
                countryOfBirth = jsonUtility.getJsonKeyValue("countryOfBirthLabel", jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaDetailObj));
                areYouCitizenOfMultiple = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";
                areResidentOtherIndia = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";
                doYouHoldGreenCard  = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";
                headingCell = new Cell();
                headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
                p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
                p.setBold();
                p.setFontKerning(FontKerning.YES);
                p.setFontColor(Color.WHITE);
                headingCell.add(p);
                headingCell.setBorder(Border.NO_BORDER);
                headingCell.setMarginTop(15);
                table.addCell(headingCell);

                p = new Paragraph("a. Place of birth : ");
                p.setMarginTop(10);
                p.add(new Text(placeOfBirth));
                p.add("   and Country of birth : ");
                p.add(countryOfBirth);
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("b. Are you a citizen of any other country also (Dual/Multiple) : ");
                p.add(new Text(areYouCitizenOfMultiple));
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("c. Are you a resident (For tax purposes) of any other country other than India : ");
                p.add(new Text(areResidentOtherIndia));
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("Do you hold a green card of US or any similar card for any other country : ");
                p.add(new Text(doYouHoldGreenCard));
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("If answer to any /all of the above (b,c & d) is yes, please do fill all the details in the Insurance FATCA Declaration");
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            }else {
                pointColumnWidths = new float[]{200F, 680F};
                firstBlockTable = new Table(pointColumnWidths);
                firstBlockLeft = new Table(1);
                firstBlockLeft.setMarginTop(20);
                firstBlockLeft.setHorizontalAlignment(HorizontalAlignment.CENTER);
                firstBlockLeft.setWidth(150);
                solidBorder = new SolidBorder(Color.GRAY, 1f, 2f);
                photoBase64="iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4//8/AAX+Av4N70a4AAAAAElFTkSuQmCC";
                photoBytes = Base64.getDecoder().decode(photoBase64.getBytes());
                dataPhoto = ImageDataFactory.create(photoBytes);
                photoImg = new Image(dataPhoto);
                photoImg.setHeight(150);
                photoImg.setWidth(150);
                firstBlockLeft.addCell(new Cell().add(photoImg).setBorder(solidBorder));
                firstBlockTable.addCell(new Cell().add(firstBlockLeft).setBorder(Border.NO_BORDER));

                firstBlockRight = new Table(6);
                firstBlockRight.addCell(new Cell().add(new Paragraph("Full Name :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell(1, 6).add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("DOB :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Age :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Gender :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Nationality :").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Passport No. : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Passport Issue Date : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Passport Exp. Date : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Place Of Issue of Passport : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Marital Status : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Residental Status : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Education : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Occupation : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("TIN (If Nationality is United States of America) : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Nature of Work/duties : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("PAN (photocopy Enclosed): ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Industry Type: ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Organisation Type: ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("PAN :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Source Of Income : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Age Proof  :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Name of the Org./Business : ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Total Years in Service/ Business :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add(new Paragraph("Income(Annual) :  ").setBold()).setBorder(Border.NO_BORDER));
                firstBlockRight.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                firstBlockTable.addCell(new Cell().add(firstBlockRight).setBorder(Border.NO_BORDER));
                table.addCell(new Cell().add(firstBlockTable).setBorder(Border.NO_BORDER));

                headingCell = new Cell();
                headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
                p = new Paragraph("Additional Details - Indicator for Residence / Tax status");
                p.setBold();
                p.setFontKerning(FontKerning.YES);
                p.setFontColor(Color.WHITE);
                headingCell.add(p);
                headingCell.setBorder(Border.NO_BORDER);
                headingCell.setMarginTop(15);
                table.addCell(headingCell);


                p = new Paragraph("a. Place of birth : ");
                p.setMarginTop(10);
                p.add(new Text(""));
                p.add("   and Country of birth : ");
                p.add("");
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("b. Are you a citizen of any other country also (Dual/Multiple) : ");
                p.add(new Text(""));
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("c. Are you a resident (For tax purposes) of any other country other than India : ");
                p.add(new Text(""));
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("Do you hold a green card of US or any similar card for any other country : ");
                p.add(new Text(""));
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                p = new Paragraph("If answer to any /all of the above (b,c & d) is yes, please do fill all the details in the Insurance FATCA Declaration");
                table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            }


            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("3. Nominee/ Appointee Details (To be filled in case life to be assured and proposer are same. Appointee details required only if nominee is a minor)");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            Table nomineeDetails = new Table(9);
            nomineeDetails.setMarginTop(10);
            nomineeDetails.setTextAlignment(TextAlignment.CENTER);
            nomineeDetails.addCell(new Paragraph("Nominee Name").setBold());
            nomineeDetails.addCell(new Paragraph("Percentage Share").setBold());
            nomineeDetails.addCell(new Paragraph("DOB of Nominee").setBold());
            nomineeDetails.addCell(new Paragraph("Relationship of Nominee with the Proposer").setBold());
            nomineeDetails.addCell(new Paragraph("Gender of Nominee").setBold());
            nomineeDetails.addCell(new Paragraph("Mobile No. of Nominee").setBold());
            nomineeDetails.addCell(new Paragraph("Appointee Name(if Applicable)").setBold());
            nomineeDetails.addCell(new Paragraph("Appointee Relationship with Nominee").setBold());
            nomineeDetails.addCell(new Paragraph("Gender of Appointee").setBold());

            JsonArray nomineeList = jsonUtility.getJsonArrayByKey("nominees",nomineeDetailObj);

            for(JsonElement element: nomineeList){
                JsonObject objNom = element.getAsJsonObject();
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("name",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("allocation",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("dateOfBirth",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("relation",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("gender",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("nomineeMobileNumber",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("appointeeName",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("appointeeRelation",objNom));
                nomineeDetails.addCell(jsonUtility.getJsonKeyValue("appointeegender",objNom));
            }

            table.addCell(new Cell().add(nomineeDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("4. Plan Details");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            String planName = jsonUtility.getJsonKeyValue("planName", planDetailObj);
            String policyTerm = jsonUtility.getJsonKeyValue("policyTerm", planDetailObj);
            String premiumPlayingTerm = jsonUtility.getJsonKeyValue("premiumPayingTerm", planDetailObj);
            String premiumInstallment = jsonUtility.getJsonKeyValue("investmentAmount", planDetailObj);

            Table planDetails = new Table(new float[]{200F, 200F, 200F, 200F});
            planDetails.setMarginTop(10);
            planDetails.setTextAlignment(TextAlignment.CENTER);
            planDetails.addCell(new Paragraph("Plan Name").setBold());
            planDetails.addCell(new Paragraph("Policy Term").setBold());
            planDetails.addCell(new Paragraph("Premium Paying Term").setBold());
            planDetails.addCell(new Paragraph("First Installment Premium").setBold());

            planDetails.addCell(planName);
            planDetails.addCell(policyTerm);
            planDetails.addCell(premiumPlayingTerm);
            planDetails.addCell(premiumInstallment);

            String fundStrategy = jsonUtility.getJsonKeyValue("fundStartegy", planDetailObj);
            p = new Paragraph("Please select either an investment strategy or the fund options in which you want to invest your premiums\n");
            p.add("I. Self-Managed Strategy :  ");
            p.add(fundStrategy.equalsIgnoreCase("Self") ? "Yes" : "No");
            p.add("        II. Automatic Trigger Based Investment Strategy :  ");
            p.add(fundStrategy.equalsIgnoreCase("ATBIS") ? "Yes" : "No");
            planDetails.addCell(new Cell(1, 4).add("").setBorder(Border.NO_BORDER).setHeight(15));
            planDetails.addCell(new Cell(1, 4).add(p).setTextAlignment(TextAlignment.LEFT));
            table.addCell(new Cell().add(planDetails).setBorder(Border.NO_BORDER));

            Table fundDetails = new Table(new float[]{400F, 100F, 400F, 100F});
            fundDetails.setTextAlignment(TextAlignment.CENTER);
            fundDetails.setMarginTop(10);
            fundDetails.addCell(new Cell(1, 4).add("Fund Options (Total Allocation to be 100% across all selected funds)").setBold());
            fundDetails.addCell(new Cell().add("Fund Name (SFIN No)").setBold());
            fundDetails.addCell(new Cell().add("%").setBold());
            fundDetails.addCell(new Cell().add("Fund Name (SFIN No)").setBold());
            fundDetails.addCell(new Cell().add("%").setBold());

            fundDetails.addCell(new Cell().add("Global Equity Fund - ULIF002260324GLEQUITYFN143"));
            fundDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("equityFund",planDetailObj)));
            fundDetails.addCell(new Cell().add("Global Equity Growth Fund - ULIF003260324GLEQTYGWFN143"));
            fundDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("equityGrowthFund",planDetailObj)));
            fundDetails.addCell(new Cell().add("Global Balanced Fund - ULIF005260324GLBLNCEDFN143"));
            fundDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("balancedFund",planDetailObj)));
            fundDetails.addCell(new Cell().add("Global Fixed Income Fund - ULIF006260324GLFXDINCFN143"));
            fundDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("fixedIncomeFund",planDetailObj)));
            fundDetails.addCell(new Cell().add("Global Gold Fund - ULIF004260324GLGOLDFUND143"));
            fundDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("goldFund",planDetailObj)));
            fundDetails.addCell(new Cell().add("Global India Equity Fund - ULIF001260324GLINEQTYFN143"));
            fundDetails.addCell(new Cell().add(jsonUtility.getJsonKeyValue("indiaEquityFund",planDetailObj)));

            table.addCell(new Cell().add(fundDetails).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("5. Benefit Payment Mode");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            String bankName = jsonUtility.getJsonKeyValue("bankName", primaryBankObj);
            String branchName = jsonUtility.getJsonKeyValue("branchName", primaryBankObj);
            String bankAccountNo = jsonUtility.getJsonKeyValue("accountNumber", primaryBankObj);
            String customerName = jsonUtility.getJsonKeyValue("accountHolderName", primaryBankObj);
            String accountType = jsonUtility.getJsonKeyValue("accountType", primaryBankObj);
            String swiftCode = jsonUtility.getJsonKeyValue("swiftcode", primaryBankObj);

            Table paymentModeDetails = new Table(new float[] {200F, 200F, 200F, 200F, 200F, 200F});
            paymentModeDetails.addCell(new Cell(1, 6).add(new Paragraph("For Non Indian/NRI").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph("Bank Name :").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell(1, 6).add(new Paragraph(bankName)).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph("Account Type :").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph(accountType)).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph("Branch Name :").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph(branchName)).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph("Bank Account No :").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph(bankAccountNo)).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph("SWIFT Code :").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell().add(new Paragraph(swiftCode)).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell(1, 2).add(new Paragraph("Customer’s Name as per the Bank Account :").setBold()).setBorder(Border.NO_BORDER));
            paymentModeDetails.addCell(new Cell(1, 6).add(new Paragraph(customerName)).setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text("Disclaimer: ").setBold());
            p.add(jsonUtility.getJsonKeyValue("desc1", contentJson));
            paymentModeDetails.addCell(new Cell(1, 6).add(p).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(paymentModeDetails).setBorder(Border.NO_BORDER));


            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("6. Insurance Repository");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            p = new Paragraph("Existing e - Insurance Account (e-IA) holder, please provide the e IA and IR name");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

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

            Table insuranceDetails = new Table(new float[] { 150F, 600F});
            insuranceDetails.addCell(new Cell().add("E IA Number"));
            insuranceDetails.addCell(new Cell().add(eIANumber));
            insuranceDetails.addCell(new Cell().add("IR Name"));
            insuranceDetails.addCell(new Cell().add(irName));
            table.addCell(new Cell().add(insuranceDetails).setBorder(Border.NO_BORDER));

            p = new Paragraph("Open New e - Insurance Account - Please choose the repository from the below");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            insuranceDetails = new Table(new float[] { 150F, 600F});
            insuranceDetails.addCell(new Cell().add(new Paragraph("IR Code").setBold()));
            insuranceDetails.addCell(new Cell().add("01."));
            insuranceDetails.addCell(new Cell().add(new Paragraph("IR Name").setBold()));
            insuranceDetails.addCell(new Cell().add("NSDL Database Management Limited"));
            table.addCell(new Cell().add(insuranceDetails).setBorder(Border.NO_BORDER));


            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("7. Declaration by Proposer/ Life to be Assured");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            p = new Paragraph(jsonUtility.getJsonKeyValue("consent1", contentJson));
            p.add("\n");
            p.add(new Text(jsonUtility.getJsonKeyValue("consent2", contentJson))).setBold();
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationHeading1", contentJson))).setBold();
            p.add(jsonUtility.getJsonKeyValue("declarationContent1", contentJson)));
            p.add(new Text(jsonUtility.getJsonKeyValue("declarationHeading2", contentJson)).setBold());
            p.add(jsonUtility.getJsonKeyValue("declarationContent2", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));


            p = new Paragraph("\n\n__________________________________________");
            p.add(jsonUtility.getJsonKeyValue("declarationHeading3", contentJson));
            p.add(jsonUtility.getJsonKeyValue("declarationHeading4", contentJson));
            p.add("\n\n");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            String laName = "";
            String laPlace = "";
            if (isSelfProposed) {
                laName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
                laPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
            } else {
                laName = jsonUtility.getJsonKeyValue("fullName", insuredPersonBasicDetailObj);
                laPlace = jsonUtility.getJsonKeyValue("city", secondaryPersonalDetailObj);
            }
            String laDate = String.valueOf(currentDate);

            Table declarationDetails = new Table(new float[] {200F, 200F, 200F, 200F, 200F, 200F});
            declarationDetails.addCell(new Cell().add("Name: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add(new Paragraph(laName).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("Place: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add(new Paragraph(laPlace).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add(new Paragraph(laDate).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(declarationDetails).setBorder(Border.NO_BORDER));

            p = new Paragraph("\n\n");
            p.add(new Text("Witness’s Signature or Thumb Impression").setUnderline());
            p.add("             Name: ");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("\n\n");
            p.add(new Text("Proposer’s Signature or Thumb Impression").setUnderline());
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            String witnessName = "";
            String proposerName = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            String proposerPlace = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj);
            String proposerDate = String.valueOf(currentDate);
            String proposerAddress = jsonUtility.getJsonKeyValue("city", primaryPersonalDetailObj) + ", " + jsonUtility.getJsonKeyValue("state", primaryPersonalDetailObj);
            String emailVerified = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetailObj);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateAndTime = now.format(formatter);
            declarationDetails = new Table(new float[] {200F, 200F, 200F, 200F, 200F, 200F});
            declarationDetails.addCell(new Cell().add("Name: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add(new Paragraph(proposerName).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("Place: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add(new Paragraph(proposerPlace).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add(new Paragraph(proposerDate).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("Address: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell(1, 6).add(new Paragraph(proposerAddress).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("OTP verified by Email Through: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell(1, 6).add(new Paragraph(emailVerified).setUnderline()).setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell().add("Date and Time: ").setBorder(Border.NO_BORDER));
            declarationDetails.addCell(new Cell(1, 6).add(new Paragraph(dateAndTime).setUnderline()).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(declarationDetails).setBorder(Border.NO_BORDER));

            p = new Paragraph(new Text("Signature authentication (Single factor authentication)").setBold());
            p.add("\nAn OTP authentication number has been sent on your registered email id. By feeding in the said number in the system, you hereby acknowledge the above " +
                    "declaration in its entirety and the same would create a legally binding agreement between the Company and You.");
            p.add("\n\n");
            p.add(new Text("Extract of Section 45 of the Insurance Act, 1938, as amended from time to time: ").setBold());
            p.add("No policy of life insurance shall be called into question on any ground whatsoever after the expiry of three years from the date of policy. A " +
                    "policy of life insurance may be called into question at anytime within three years from the date of policy, on the ground of fraud or on the ground that any statement of or suppression of a fact material to the expectancy of the " +
                    "life of the insured was incorrectly made in the proposal or other document on the basis of which the policy was issued or revived or rider issued. The insurer shall have to communicate in writing to the insured or legal " +
                    "representatives or nominees or assignees of the insured, the grounds and materials on which such decision is based. No insurer shall repudiate a life insurance policy on the ground of fraud if the insured can prove that the " +
                    "misstatement or suppression of material fact was true to the best of his knowledge and belief or that there was no deliberate intention to suppress the fact or that such misstatement or suppression are within the knowledge of " +
                    "the insurer. In case of fraud, the onus of disproving lies upon the beneficiaries, in case the policyholder is not alive. In case of repudiation of the policy on the ground of misstatement or suppression of a material fact and not on " +
                    "the grounds of fraud, the premiums collected on the policy till the date of repudiation shall be paid. Nothing in this section shall prevent the insurer from calling for proof of age at any time if he is entitled to do so, and no " +
                    "policy shall be deemed to be called in question merely because the terms of the policy are adjusted on subsequent proof that the age of the life insured was incorrectly stated in the proposal. For complete details of the section" +
                    "and the definition of ’date of policy’, please refer Section 45 of the Insurance Act, 1938, as amended from time to time.");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("8. Intermediary details");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            String intermediateName = "ONL";
            String intermediateLicenseNumber = " ";
            String agentName = "Online Channel";
            String licenseCode = " ";
            Table intermediateDetails = new Table(new float[] {200F, 300F, 200F, 300F});
            insuranceDetails.setMarginTop(10);
            intermediateDetails.addCell(new Cell().add("Name of the Intermediary :").setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add(intermediateName).setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add("License Number :").setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add(intermediateLicenseNumber).setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell(1, 4).add("(Applicable for all channels except Individual Agents)").setBorder(Border.NO_BORDER));
            p = new Paragraph("_______________________________");
            p.add("\nSignature of the Agent/Specified Agents");
            intermediateDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("_____________________");
            p.add("\nStamp of the Intermediary");
            intermediateDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add("Name of the Agent/Specified Agents :").setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add(agentName).setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add("License Code :").setBorder(Border.NO_BORDER));
            intermediateDetails.addCell(new Cell().add(licenseCode).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(intermediateDetails).setBorder(Border.NO_BORDER));
            document.add(table);


            Table grandFooterTable = new Table(1);
            grandFooterTable.setTextAlignment(TextAlignment.CENTER);
            Paragraph companyText = new Paragraph(new Text(jsonUtility.getJsonKeyValue("signature", contentJson)).setBold());
            companyText.add(new Text(jsonUtility.getJsonKeyValue("addr", contentJson))));
            grandFooterTable.addCell(companyText).setTextAlignment(TextAlignment.CENTER);
            grandFooterTable.setWidth(UnitValue.createPercentValue(100));
            pdfDoc.getDefaultPageSize();
            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), UnitValue.createPercentValue(100));
            document.add(grandFooterTable);

            document.add(new AreaBreak());

            table = new Table(1);
            p = new Paragraph();
            p.add(img);
            table.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("INSURANCE FATCA/CRS DECLARATION");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            p = new Paragraph(new Text("Individual declaration- US Person* or Person Residing outside India").setBold());
            p.setMarginTop(10);
            p.add("\nYou are requested to consult a legal/tax advisor for Residential Status\n");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph(new Text("\nNote: ").setBold());
            p.add(jsonUtility.getJsonKeyValue("footerContent1", contentJson));
            p.add(jsonUtility.getJsonKeyValue("footerContent2", contentJson));
            p.add(new Text(jsonUtility.getJsonKeyValue("footerContent3", contentJson)).setBold());
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("footerLink1", contentJson));
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("footerLink2", contentJson));
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("footerLink4", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));


            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(128, 0, 0), 100);
            p = new Paragraph("ALL THE FIELDS GIVEN BELOW ARE MANDATORY. PLEASE DO NOT LEAVE THEM BLANK");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p);
            headingCell.setBorder(Border.NO_BORDER);
            headingCell.setMarginTop(15);
            table.addCell(headingCell);

            String proposerFatcaName = jsonUtility.getJsonKeyValue("fullName", primaryPersonalDetailObj);
            String laFactcaName = jsonUtility.getJsonKeyValue("fullName", secondaryPersonalDetailObj);
            String nomineeFatcaName = jsonUtility.getJsonKeyValue("name", nomineeList.get(0).getAsJsonObject());


            JsonObject primaryBornInIndiaObj = jsonUtility.getJsonObjectByKey("bornInIndia", primaryFatcaObj);
            JsonObject secondaryBornInIndiaObj = jsonUtility.getJsonObjectByKey("bornInIndia", secondaryFatcaObj);
            JsonObject nomineeBornInIndiaObj = jsonUtility.getJsonObjectByKey("bornInIndia", nomineeFatcaDetailObj);
            logger.info("Nominee born in india object is:{}",nomineeFatcaDetailObj);
            String proposerFatherName=jsonUtility.getJsonKeyValue("status", primaryBornInIndiaObj).equalsIgnoreCase("y") ? jsonUtility.getJsonKeyValue("fatherName", primaryBornInIndiaObj) : "";
            String lifeInsuredFatherName=jsonUtility.getJsonKeyValue("status", secondaryBornInIndiaObj).equalsIgnoreCase("y") ? jsonUtility.getJsonKeyValue("fatherName", secondaryBornInIndiaObj) : "";
            String nomineeFatherName = jsonUtility.getJsonKeyValue("status", nomineeBornInIndiaObj).equalsIgnoreCase("y") ? jsonUtility.getJsonKeyValue("fatherName", nomineeBornInIndiaObj) : "";

            String proposerUSPerson = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", primaryFatcaObj)).equalsIgnoreCase("y") ? "Yes" : "No";
            String laUSPerson = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", secondaryFatcaObj)).equalsIgnoreCase("y") ? "Yes" : "No";
            String nomineeUSPerson = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("isUsCitizen", nomineeFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";


            String proposerResidentUS = jsonUtility.getJsonKeyValue("status", primaryResidentOtherThanUsObj).equalsIgnoreCase("y") ? "Yes" : "No";
            String laResidentUS = jsonUtility.getJsonKeyValue("status", secResidentOtherThanUsObj).equalsIgnoreCase("y") ? "Yes" : "No";
            String nomineeResidentUS = jsonUtility.getJsonKeyValue("status", nomineeFatcaDetailObj).equalsIgnoreCase("y") ? "Yes" : "No";
            String proposerCountryOFRes = jsonUtility.getJsonKeyValue("countryOfResidencyLabel", primaryResidentOtherThanUsObj);
            String laCountryOFRes = jsonUtility.getJsonKeyValue("countryOfResidencyLabel", secResidentOtherThanUsObj);
            String nomineeCountryOfRes = jsonUtility.getJsonKeyValue("countryOfResidencyLabel", nomineeResidentOtherThanUsObj);

            String primaryCitizenStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", primaryFatcaObj)).equalsIgnoreCase("y") ? "Yes" : "No";
            String secondaryCitizenStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", secondaryFatcaObj)).equalsIgnoreCase("y") ? "Yes" : "No";
            String nomineeCitizenStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("citizenOtherThanIndia", nomineeFatcaDetailObj)).equalsIgnoreCase("y") ? "Yes" : "No";

            String primaryResidentStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", primaryFatcaObj));
            String secondaryResidentStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", secondaryFatcaObj));
            String nomineeResidentStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("residentOtherThanIndia", nomineeFatcaDetailObj));

            String primaryGreenCardStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", primaryFatcaObj));
            String secondaryGreenCardStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", secondaryFatcaObj));
            String nomineeGreenCardStatus = jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("greenCardHolder", nomineeFatcaDetailObj));

            String proposerTinNumber = "";
            String insuredTinNumber = "";
            String nomineeTin = "";
            if (primaryCitizenStatus.equalsIgnoreCase("yes") || primaryResidentStatus.equalsIgnoreCase("y") || primaryGreenCardStatus.equalsIgnoreCase("y")) {
                proposerTinNumber = jsonUtility.getJsonKeyValue("tinNumber", primaryResidentOtherThanUsObj);
            }
            if (secondaryCitizenStatus.equalsIgnoreCase("yes") || secondaryResidentStatus.equalsIgnoreCase("y") || secondaryGreenCardStatus.equalsIgnoreCase("y")) {
                insuredTinNumber = jsonUtility.getJsonKeyValue("tinNumber", secResidentOtherThanUsObj);
            }
            if (nomineeCitizenStatus.equalsIgnoreCase("yes") || nomineeResidentStatus.equalsIgnoreCase("y") || nomineeGreenCardStatus.equalsIgnoreCase("y")) {
                nomineeTin = jsonUtility.getJsonKeyValue("tinNumber", nomineeResidentOtherThanUsObj);
            }

            String proposerExempted = jsonUtility.getJsonKeyValue("Excemption", primaryResidentOtherThanUsObj);
            String laProposeExempted = jsonUtility.getJsonKeyValue("Excemption", secResidentOtherThanUsObj);
            String nomineeExempted = jsonUtility.getJsonKeyValue("Excemption", nomineeResidentOtherThanUsObj);

            String proposerIdentityProof = jsonUtility.getJsonKeyValue("countryOfIssuingIdLabel", primaryResidentOtherThanUsObj);
            String laIdentityProof = jsonUtility.getJsonKeyValue("countryOfIssuingIdLabel", secResidentOtherThanUsObj);
            String nomineeIdentityProof = jsonUtility.getJsonKeyValue("countryOfIssuingIdLabel", nomineeResidentOtherThanUsObj);

            String proposerTelephoneOutside = jsonUtility.getJsonKeyValue("status", primaryResidentOtherThanUsObj).equalsIgnoreCase("y") ? "Yes" : "No";
            String laTelephoneOutside = jsonUtility.getJsonKeyValue("status", secResidentOtherThanUsObj).equalsIgnoreCase("y") ? "Yes" : "No";
            String nomineeTelephoneOutside = jsonUtility.getJsonKeyValue("status", nomineeResidentOtherThanUsObj).equalsIgnoreCase("y") ? "Yes" : "No";

            String proposerTelephoneNo = jsonUtility.getJsonKeyValue("telephoneNumber", primaryResidentOtherThanUsObj);
            String laTelephoneNo = jsonUtility.getJsonKeyValue("telephoneNumber", secResidentOtherThanUsObj);
            String nomineeTelephoneNo = jsonUtility.getJsonKeyValue("telephoneNumber", nomineeResidentOtherThanUsObj);

            String countryOfCitizenProposer = jsonUtility.getJsonKeyValue("countryOfCitizenshipLabel", primaryResidentOtherThanUsObj);
            String countryOfCitizenLA = jsonUtility.getJsonKeyValue("countryOfCitizenshipLabel", secResidentOtherThanUsObj);
            String countryOfCitizenNominee = jsonUtility.getJsonKeyValue("countryOfCitizenshipLabel", nomineeResidentOtherThanUsObj);

            JsonObject primaryPermanentAddOutIndia = jsonUtility.getJsonObjectByKey("permanentAddressOutsideIndia", primaryFatcaObj);
            JsonObject secondaryPermanentAddOutIndia = jsonUtility.getJsonObjectByKey("permanentAddressOutsideIndia", secondaryFatcaObj);
            JsonObject nomineePermanentAddOutIndia = jsonUtility.getJsonObjectByKey("permanentAddressOutsideIndia", nomineeFatcaDetailObj);

            String addressOutSideIndiaProposer = jsonUtility.getJsonKeyValue("status", primaryPermanentAddOutIndia).equalsIgnoreCase("y") ? "Yes" : "No";
            String addressOutSideIndiaLA = jsonUtility.getJsonKeyValue("status", secondaryPermanentAddOutIndia).equalsIgnoreCase("y") ? "Yes" : "No";
            String addressOutSideIndiaNominee = jsonUtility.getJsonKeyValue("status", nomineePermanentAddOutIndia).equalsIgnoreCase("y") ? "Yes" : "No";

            String proposerPermanentAddress = jsonUtility.getJsonKeyValue("addressLine1", primaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("addressLine2", primaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine3", primaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("pincode", primaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("city", primaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("state", primaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("countryLabel", primaryPermanentAddOutIndia);
            String laPermanentAddress = jsonUtility.getJsonKeyValue("addressLine1", secondaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine2", secondaryPermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine3", secondaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("pincode", secondaryPermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("city", secondaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("state", secondaryPermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("countryLabel", secondaryPermanentAddOutIndia);
            String nomineePermanentAddress = jsonUtility.getJsonKeyValue("addressLine1", nomineePermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine2", nomineePermanentAddOutIndia)
                    + " " + jsonUtility.getJsonKeyValue("addressLine3", nomineePermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("pincode", nomineePermanentAddOutIndia) + " "
                    + jsonUtility.getJsonKeyValue("city", nomineePermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("state", nomineePermanentAddOutIndia) + " " +
                    jsonUtility.getJsonKeyValue("countryLabel", nomineePermanentAddOutIndia);

            boolean primaryTaxStatus = jsonUtility.getBooleanKeyValue("tax", primaryPermanentAddOutIndia);
            boolean secondaryTaxStatus = jsonUtility.getBooleanKeyValue("tax", secondaryPermanentAddOutIndia);
            boolean nomineeTaxResidence = jsonUtility.getBooleanKeyValue("tax", nomineePermanentAddOutIndia);

            boolean primaryAttorneyStatus = jsonUtility.getBooleanKeyValue("attorney", primaryPermanentAddOutIndia);
            boolean secondaryAttorneyStatus = jsonUtility.getBooleanKeyValue("attorney", secondaryPermanentAddOutIndia);
            boolean nomineeAttorneyStatus = jsonUtility.getBooleanKeyValue("attorney", nomineePermanentAddOutIndia);

            String properPower = primaryAttorneyStatus ? "Yes" : "No";
            String laPower = secondaryAttorneyStatus ? "Yes" : "No";
            String nomineePower = nomineeAttorneyStatus ? "Yes" : "No";

            String proposerSourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", jsonUtility.getJsonObjectByKey("primary", employmentDetailObj));
            String laSourceOfIncome = jsonUtility.getJsonKeyValue("sourceOfIncome", jsonUtility.getJsonObjectByKey("secondary", employmentDetailObj));
            String nomineeSourceOfIncome = "";


            Table fatcaDetails = new Table(new float[]{200F, 200F, 200F, 200F});
            fatcaDetails.setMarginTop(10);
            fatcaDetails.addCell(new Cell(1, 4).add(new Paragraph("CLIENT ID").setBold()));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Parameters").setBold()).setBackgroundColor(new DeviceRgb(229, 228, 226)));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Proposer").setBold()).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(new DeviceRgb(229, 228, 226)));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Life Insured").setBold()).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(new DeviceRgb(229, 228, 226)));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Nominee").setBold()).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(new DeviceRgb(229, 228, 226)));

            fatcaDetails.addCell(new Cell().add(new Paragraph("Name")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerFatcaName)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laFactcaName)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeFatcaName)).setTextAlignment(TextAlignment.CENTER));

            fatcaDetails.addCell(new Cell().add(new Paragraph("Father's name")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerFatherName)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(lifeInsuredFatherName)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeFatherName)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("US Person")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerUSPerson)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laUSPerson)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeUSPerson)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Resident of any other country other than US")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerResidentUS)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laResidentUS)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeResidentUS)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Country of Residence- Please specify the country")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerCountryOFRes)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laCountryOFRes)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeCountryOfRes)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Taxpayer Identification Number (TIN) (Mention complete number and Submit a copy - Mandatory)")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerTinNumber)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(insuredTinNumber)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeTin)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Exemption claimed, if any (to be supported by necessary documents)")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerExempted)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laProposeExempted)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeExempted)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell(1, 4).add(new Paragraph("“COUNTRY OUTSIDE INDIA” Indicia").setBold()).setBackgroundColor(new DeviceRgb(229, 228, 226)));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Country issuing the \"Identity Proof\"")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerIdentityProof)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laIdentityProof)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeIdentityProof)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Telephone No. Outside India?")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerTelephoneOutside)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laTelephoneOutside)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeTelephoneOutside)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("If Yes, provide Telephone no.?")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerTelephoneNo)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laTelephoneNo)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeTelephoneNo)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Citizenship Outside India.")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(primaryCitizenStatus)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(secondaryCitizenStatus)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeCitizenStatus)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("If Yes, provide country of citizenship")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(countryOfCitizenProposer)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(countryOfCitizenLA)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(countryOfCitizenNominee)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Communication / Permanent Address Outside India?")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(addressOutSideIndiaProposer)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(addressOutSideIndiaLA)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(addressOutSideIndiaNominee)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("If Yes, provide the address")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerPermanentAddress)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laPermanentAddress)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineePermanentAddress)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Country of Residence for Tax purpose is outside India?")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(primaryTaxStatus ? "Yes" : "No")).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(secondaryTaxStatus ? "Yes" : "No")).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeTaxResidence ? "Yes" : "No")).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Power of Attorney (POA) of a person outside India?")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(properPower)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laPower)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineePower)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph("Source of Income")));
            fatcaDetails.addCell(new Cell().add(new Paragraph(proposerSourceOfIncome)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(laSourceOfIncome)).setTextAlignment(TextAlignment.CENTER));
            fatcaDetails.addCell(new Cell().add(new Paragraph(nomineeSourceOfIncome)).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(fatcaDetails).setBorder(Border.NO_BORDER));

            p = new Paragraph(new Text("\nNote :- ").setBold());
            p.add(jsonUtility.getJsonKeyValue("desc2", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("\n");
            p.add(jsonUtility.getJsonKeyValue("desc2", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("\n");
            p.add(jsonUtility.getJsonKeyValue("desc3", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("\n");
            p.add(jsonUtility.getJsonKeyValue("desc4", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("\n");
            p.add(jsonUtility.getJsonKeyValue("desc5", contentJson));
            p.add("\n");
            p.add(jsonUtility.getJsonKeyValue("desc6", contentJson));
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            String signatureProposer = "";
            String signatureLA = "";
            String signatureNominee = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String currentDateFatca = sdf.format(date);

            Table signatureDetails = new Table(new float[]{100F, 300F, 300F, 200F});
            signatureDetails.setMarginTop(10);
            signatureDetails.setTextAlignment(TextAlignment.CENTER);
            signatureDetails.addCell("");
            signatureDetails.addCell(new Paragraph("PROPOSER").setBold());
            signatureDetails.addCell(new Paragraph("LIFE INSURED").setBold());
            signatureDetails.addCell(new Paragraph("NOMINEE").setBold());
            signatureDetails.addCell(new Paragraph("Name").setBold());
            signatureDetails.addCell(proposerFatcaName);
            signatureDetails.addCell(laFactcaName);
            signatureDetails.addCell(nomineeFatcaName);
            signatureDetails.addCell(new Paragraph("Signature").setBold());
            signatureDetails.addCell(new Cell().add(signatureProposer).setHeight(30));
            signatureDetails.addCell(new Cell().add(signatureLA).setHeight(30));
            signatureDetails.addCell(new Cell().add(signatureNominee).setHeight(30));
            signatureDetails.addCell(new Paragraph("Date").setBold());
            signatureDetails.addCell(proposerFatcaName.isEmpty() ? "" : currentDateFatca);
            signatureDetails.addCell(laFactcaName.isEmpty() ? "" : currentDateFatca);
            signatureDetails.addCell(nomineeFatcaName.isEmpty() ? "" : currentDateFatca);
            table.addCell(new Cell().add(signatureDetails).setBorder(Border.NO_BORDER));
            document.add(table);
            document.add(new Paragraph("\n\n"));

            grandFooterTable = new Table(1);
            grandFooterTable.setTextAlignment(TextAlignment.CENTER);
            companyText = new Paragraph(new Text(jsonUtility.getJsonKeyValue("signature", contentJson)).setBold());
            companyText.add(new Text(jsonUtility.getJsonKeyValue("addr", contentJson)));
            grandFooterTable.addCell(companyText).setTextAlignment(TextAlignment.CENTER);
            grandFooterTable.setWidth(UnitValue.createPercentValue(100));
            pdfDoc.getDefaultPageSize();
            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), UnitValue.createPercentValue(100));
            document.add(grandFooterTable);
            document.close();
            return baos.toByteArray();
        }catch (Exception e){
            logger.info("Exception occurs in gift city pdf generation method:", e);
            return null;
        }
    }
}
