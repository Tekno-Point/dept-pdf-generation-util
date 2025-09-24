package com.pdfGeneration.service.impl;

import com.google.gson.JsonObject;
import com.itextpdf.io.font.FontConstants;
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
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.TextAlignment;
import com.pdfGeneration.service.EMandatePDFService;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class EMandatePDFServiceImpl implements EMandatePDFService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PDFUtility pdfUtility;
    private final JsonUtility jsonUtility;

    public EMandatePDFServiceImpl(PDFUtility pdfUtility, JsonUtility jsonUtility) {
        this.pdfUtility = pdfUtility;
        this.jsonUtility = jsonUtility;
    }

    @Override
    public byte[] downloadEMandateForm(String eMandateReqObj) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos)) {

            JsonObject userData = jsonUtility.getJsonObject(eMandateReqObj);
            JsonObject basicDetailObj =  jsonUtility.getJsonObjectByKey("basicDetails",userData);
            logger.info("Basic details object:{}", basicDetailObj);
            JsonObject bankDetailObj = jsonUtility.getJsonObjectByKey("bankDetails",userData);
            logger.info("Bank detail object:{}", bankDetailObj);
            JsonObject planDetailObj = jsonUtility.getJsonObjectByKey("planDetails",userData);
            logger.info("Plan detail object:{}", planDetailObj);
            JsonObject nomineeDetailObj = jsonUtility.getJsonObjectByKey("nomineeDetails",userData);
            logger.info("Nominee detail object:{}", nomineeDetailObj);
            JsonObject personalDetailObj = jsonUtility.getJsonObjectByKey("personalDetails",userData);
            logger.info("Personal detail object:{}", personalDetailObj);
            JsonObject contentJson = jsonUtility.getJsonObjectByKey("content", userData);

            String applicationNumber = jsonUtility.getJsonKeyValue("applicationNumber", userData);
            boolean isOmniDoc = jsonUtility.getBooleanKeyValue("isOmniDoc", userData);

            JsonObject metadataObj = jsonUtility.getJsonObjectByKey("metadata",userData);
            String author = jsonUtility.getJsonKeyValue("author", metadataObj);
            String creator = jsonUtility.getJsonKeyValue("creator", metadataObj);
            String title = jsonUtility.getJsonKeyValue("title", metadataObj);
            PdfDocument pdfDoc = new PdfDocument(writer);
            ////////////////////
            PdfDocumentInfo pdfDocumentInfo=pdfDoc.getDocumentInfo();
            pdfDocumentInfo.setAuthor(author);
            pdfDocumentInfo.setCreator(creator);
            pdfDocumentInfo.setTitle(title);
            pdfDocumentInfo.addCreationDate();
            pdfDoc.addNewPage();

            JsonObject imagesJson = jsonUtility.getJsonObjectByKey("images",userData);
            String logoFilename = jsonUtility.getJsonKeyValue("logo", imagesJson);
            String logoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);
            img.setHeight(50f);

            logoFilename = jsonUtility.getJsonKeyValue("nachLogo", imagesJson);
            String nachLogoBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image nachLogo = pdfUtility.getPDFLogo(nachLogoBase64);
            nachLogo.setHeight(20F);

            logoFilename = jsonUtility.getJsonKeyValue("rupee", imagesJson);
            String rupeeBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image rupeeLogo = pdfUtility.getPDFLogo(rupeeBase64);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image img1 = pdfUtility.getPDFLogo(logoBase64);
            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);
            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            logoFilename = jsonUtility.getJsonKeyValue("tick", imagesJson);
            String tickBase64 = pdfUtility.getImageAsBase64(logoFilename);
            Image tickImg = pdfUtility.getPDFLogo(tickBase64).setHeight(8).setWidth(8);

            //PdfDocument pdf = new PdfDocument(writer);
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font);
            document.setFontSize(6);
            document.setFontKerning(FontKerning.YES);
            document.setMargins(5f, 10f, 10f, 10f);
            document.add(img1);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dates = new Date();
            String date = sdf.format(dates);

            Table headlingTable = new Table(new float[]{900F, 100F});
            Paragraph p = new Paragraph("Mandate Form - Direct Debit / NACH");
            p.setTextAlignment(TextAlignment.CENTER).setFontSize(8);
            p.setBold();
            headlingTable.addCell(new Cell().add(p).setPaddingTop(50).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            headlingTable.addCell(new Cell().add(img).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            document.add(headlingTable);

            JsonObject policyHolderBasicDetailObj = jsonUtility.getJsonObjectByKey("policyHolder", basicDetailObj);
            JsonObject primaryBankObj = jsonUtility.getJsonObjectByKey("primary", bankDetailObj);
            String buyFor = jsonUtility.getJsonKeyValue("buyFor", policyHolderBasicDetailObj);// global flag check for
            // policy state


            String policyStartDate = jsonUtility.getJsonKeyValue("startDate", primaryBankObj);
            String policyStart="";
            if (policyStartDate.length()>0) {
                sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date startDate = sdf.parse(policyStartDate);
                sdf = new SimpleDateFormat("dd/MM/yyyy");
                policyStart = sdf.format(startDate);
            }

            String policyEndDate = jsonUtility.getJsonKeyValue("endDate", primaryBankObj);
            String policyEnd="";
            if (policyEndDate.length()>0) {
                sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date endDate = sdf.parse(policyEndDate);
                sdf = new SimpleDateFormat("dd/MM/yyyy");
                policyEnd = sdf.format(endDate);
            }

            boolean myself = buyFor.equalsIgnoreCase("Myself");
            logger.info("---- flag---:{}", buyFor);

            JsonObject primaryPersonalDetail = jsonUtility.getJsonObjectByKey("primary", personalDetailObj);
            logger.info("Myself for health Obj -- primarySecondaryPersonalDetail: ");
            JsonObject secondaryPersonalDetail = jsonUtility.getJsonObjectByKey("secondary", personalDetailObj);

            Table bankDetails = new Table(new float[]{900F, 80F, 200F});
            bankDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("To,\n");
            p.add("The Branch Manager,\n");
            p.add("Bank Name: ");
            p.add(new Text(jsonUtility.getJsonKeyValue("Bank_Name", primaryBankObj)).setUnderline());
            String branchNameAddress = jsonUtility.getJsonKeyValue("Branch_Name", primaryBankObj) + ","
                    + jsonUtility.getJsonKeyValue("Branch_Address", primaryBankObj);

            p.add("\nBank Branch Name & Address: ");
            p.add(new Text(branchNameAddress).setUnderline());
            bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            bankDetails.addCell(new Cell().add("Date: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            bankDetails.addCell(new Cell().add(date).setUnderline().setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text("Ref: ").setBold());
            p.add(jsonUtility.getJsonKeyValue("content1" , contentJson));
            p.add(jsonUtility.getJsonKeyValue("content2" , contentJson));
            p.add(jsonUtility.getJsonKeyValue("content3" , contentJson));
            p.add(imgChecked);
            p.add("     Direct Debit");
            Cell proposerMergedcell = new Cell(1, 3);
            proposerMergedcell.add(p);
            bankDetails.addCell(proposerMergedcell.setBorder(Border.NO_BORDER));
            document.add(bankDetails);

            String amount = jsonUtility.getJsonKeyValue("totalPremiumAmount", planDetailObj);
            String amountWords="";
            try {
                amountWords = pdfUtility.convert(Long.parseLong(amount.replace(",", "").split("\\.")[0]))
                        + " Rupees Only";
            }catch (Exception e){
                logger.info("Exception in amount in words: ",e);
            }
            String frequency = jsonUtility.getJsonKeyValue("frequency", planDetailObj);
            String investmentFrequency = jsonUtility.getJsonKeyValue("investmentFrequency", planDetailObj);

            Table debitDetails = new Table(new float[]{300F, 800F, 60F, 100F, 100F, 200F});
            debitDetails.addCell(new Cell().add("Application No./Policy No").setBold().setTextAlignment(TextAlignment.CENTER));
            p=new Paragraph();
            p.add("Amount (");
            p.add(rupeeLogo.setHeight(5).setWidth(6));
            p.add(")(in words) ");
            debitDetails.addCell(new Cell().add(p.setBold()).setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add(new Paragraph("Amount").setBold()).setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add("Frequency (i.e.yearly/half/yearly/Quarterly/Monthly)").setBold().setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add(new Paragraph("Start Date").setBold()).setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add(new Paragraph("End Date").setBold()).setTextAlignment(TextAlignment.CENTER));

            debitDetails.addCell(new Cell().add(applicationNumber).setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add(amountWords).setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add(amount).setTextAlignment(TextAlignment.CENTER));
            if (frequency.equalsIgnoreCase("")) {
                debitDetails.addCell(new Cell().add(investmentFrequency).setTextAlignment(TextAlignment.CENTER));
            }else {
                debitDetails.addCell(new Cell().add(frequency).setTextAlignment(TextAlignment.CENTER));
            }
            debitDetails.addCell(new Cell().add(policyStart).setTextAlignment(TextAlignment.CENTER));
            debitDetails.addCell(new Cell().add(policyEnd).setTextAlignment(TextAlignment.CENTER));

            String bankName = jsonUtility.getJsonKeyValue("Bank_Name", primaryBankObj);
            String accountNo = jsonUtility.getJsonKeyValue("accountNumber", primaryBankObj);
            String micrCode = jsonUtility.getJsonKeyValue("MICR_Code", primaryBankObj);
            String ifscCode = jsonUtility.getJsonKeyValue("ifscCode", primaryBankObj);
            String customerName = jsonUtility.getJsonKeyValue("accountHolderName", primaryBankObj);

            String nameOfHolder = jsonUtility.getJsonKeyValue("fullName", policyHolderBasicDetailObj);
            String mobileNo=jsonUtility.getJsonKeyValue("mobileNumber", primaryPersonalDetail);
            String emailId;
            if (myself) {
                emailId = jsonUtility.getJsonKeyValue("emailId", primaryPersonalDetail);
            } else {
                emailId = jsonUtility.getJsonKeyValue("emailId", secondaryPersonalDetail);
            }

            p = new Paragraph(new Text("Name of the Account Holder: ").setBold());
            p.add(new Text("\n(As appearing in the Bank records)").setFontSize(5));
            proposerMergedcell = new Cell(1, 1);
            proposerMergedcell.add(p);
            debitDetails.addCell(proposerMergedcell.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            proposerMergedcell = new Cell(1, 5);
            Table nameCell = new Table(1);
            nameCell.addCell(new Paragraph(nameOfHolder).setTextAlignment(TextAlignment.LEFT));
            proposerMergedcell.add(nameCell).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell .setBorder(Border.NO_BORDER));

            debitDetails.addCell(new Cell().add(new Paragraph("Account No. ").setBold()).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 2);
            proposerMergedcell.add(new Cell().add(accountNo));
            debitDetails.addCell(proposerMergedcell).setBorder(Border.NO_BORDER);
            proposerMergedcell = new Cell(1, 3);
            Table accountTypeDetails = new Table(new float[]{250F, 50F, 150F, 50F, 150F});
            accountTypeDetails
                    .addCell(new Cell().add(new Paragraph("Account Type: ").setBold()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            String accountType = jsonUtility.getJsonKeyValue("accountType", primaryBankObj);
            if (accountType.equalsIgnoreCase("Savings")) {
                accountTypeDetails.addCell(new Cell().add(imgChecked).setBorder(Border.NO_BORDER));
            } else {
                accountTypeDetails.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
            }
            accountTypeDetails.addCell(new Cell().add(" Savings").setBorder(Border.NO_BORDER));
            if (accountType.equalsIgnoreCase("Current")) {
                accountTypeDetails.addCell(new Cell().add(imgChecked).setBorder(Border.NO_BORDER));
            } else {
                accountTypeDetails.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
            }
            accountTypeDetails.addCell(new Cell().add(" Current").setBorder(Border.NO_BORDER));
            proposerMergedcell.add(accountTypeDetails).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell).setBorder(Border.NO_BORDER);

            proposerMergedcell = new Cell(1, 6);
            proposerMergedcell.add("(Affixing of your proprietary firm / company stamp is mandatory, in case of a current account)").setFontSize(5).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell);

            proposerMergedcell = new Cell(1, 6);
            p = new Paragraph(new Text("MICR Code").setBold());
            p.add(new Text(" (Applicable in case ECS payment): ").setFontSize(5));
            p.add(new Text(micrCode).setUnderline());
            proposerMergedcell.add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell);

            proposerMergedcell = new Cell(1, 6);
            p = new Paragraph(new Text("(Is the 9 digit code on the cheque book issued by the bank, You are requested to attach a cancelled cheque for verification of the MICR Code)").setFontSize(5));
            proposerMergedcell.add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell);

            Table ifscColumnTable=new Table(new float[]{210F, 150F});
            proposerMergedcell = new Cell(1, 6);
            p = new Paragraph(new Text("IFSC Code").setBold());
            p.add(new Text(" (Applicable in case Non ECS payment) (If appearing on the Cheque book): ").setFontSize(5));
            ifscColumnTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            ifscColumnTable.addCell(new Cell().add(ifscCode));
            proposerMergedcell.add(ifscColumnTable).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell);

            Table mobileEmailDetails = new Table(new float[]{50F, 150F, 50F, 200F});
            mobileEmailDetails.setBorder(Border.NO_BORDER);
            mobileEmailDetails.addCell(new Cell().add("Mobile No: ").setBorder(Border.NO_BORDER));
            mobileEmailDetails.addCell(new Cell().add(new Paragraph(mobileNo)));
            mobileEmailDetails.addCell(new Cell().add(" Email ID: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            mobileEmailDetails.addCell(new Cell().add(new Paragraph(emailId).setUnderline()).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(1, 6);
            proposerMergedcell.add(new Cell().add(mobileEmailDetails).setBorder(Border.NO_BORDER))
                    .setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell).setBorder(Border.NO_BORDER);
            proposerMergedcell = new Cell(1, 6);
            p = new Paragraph(
                    "Request for Payment Mode change to NACH / Direct Debit OR Deactivation of NACH /DD mandate should be submitted 15 days prior to the due date or same would be effective from the next premium due date.");
            p.setFontSize(5);
            proposerMergedcell.add(p).setBorder(Border.NO_BORDER);
            debitDetails.addCell(proposerMergedcell);
            document.add(debitDetails);
            p = new Paragraph(
                    "----------------------------------------------------------------------------------------------------------------------------------------------");
            p.add("----------------------------------------------------------------------------------------------------------------------------------------------");
            document.add(p);

            Table autoDebitDetails = new Table(new float[]{600F, 600F});
            autoDebitDetails.setFontSize(5);
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationHeading" , contentJson)).setBold();
            autoDebitDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent1" , contentJson)).setBold();
            autoDebitDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            List autoList = new List(ListNumberingType.DECIMAL);
            autoList.setPadding(0);
            autoList.setMargin(0);
            autoList.setFontSize(5);
            ListItem autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent2" , contentJson));

            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1); // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent3" , contentJson));
            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1);  // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent4" , contentJson));
            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1);  // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent5" , contentJson));
            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1);  // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent6" , contentJson));
            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1);  // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent7" , contentJson)).setBold();
            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1);  // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoItem = new ListItem();
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent8" , contentJson));

            p.setMargins(0, 0, 0, 0); // Set margins to zero
            p.setMultipliedLeading(1);  // Set the line spacing factor to 1 (no extra spacing)
            autoItem.add(p);
            autoList.add(autoItem);

            autoDebitDetails.addCell(new Cell().add(autoList).setBorder(Border.NO_BORDER));

            proposerMergedcell = new Cell(6, 1);
            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent9" , contentJson));
            proposerMergedcell.add(p.setFontSize(5f));
            Table bankAuth = new Table(new float[]{200F, 500F});
            bankAuth.addCell(new Cell().add("Bank Stamp").setTextAlignment(TextAlignment.CENTER).setHeight(10).setBorder(Border.NO_BORDER));
            bankAuth.addCell(new Cell().add("Signature of Authorized Bank Official").setTextAlignment(TextAlignment.CENTER).setHeight(10).setBorder(Border.NO_BORDER));
            bankAuth.addCell(new Cell().add("").setHeight(5).setBorder(Border.NO_BORDER));
            bankAuth.addCell(new Cell().add("").setHeight(5).setBorder(Border.NO_BORDER));
            proposerMergedcell.add(bankAuth);

            p = new Paragraph(jsonUtility.getJsonKeyValue("declarationContent10" , contentJson)).setBold();
            proposerMergedcell.add(p);
            p = new Paragraph();
            p.add(jsonUtility.getJsonKeyValue("declarationContent11" , contentJson));
            proposerMergedcell.add(p);
            autoDebitDetails.addCell(new Cell().add(proposerMergedcell).setBorder(Border.NO_BORDER));
            document.add(autoDebitDetails);
            Table signatureTable = new Table(new float[]{350F, 350F, 350F, 350F});

            p = new Paragraph();
            signatureTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setHeight(10).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setHeight(10).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setHeight(10).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setHeight(10).setBorder(Border.NO_BORDER));

            signatureTable.addCell(new Cell()
                    .add(new Paragraph("Policy Holder's Signature").setTextAlignment(TextAlignment.CENTER).setBold()).setBorder(Border.NO_BORDER));
            p = new Paragraph(new Text("Primary account holder's Signature").setBold());
            p.add(new Text("\n(If Primary Account holder differs from PolicyHolder)").setFontSize(5));
            signatureTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add(
                    new Paragraph("Joint Account holder's 1 Signature").setTextAlignment(TextAlignment.CENTER).setBold()).setBorder(Border.NO_BORDER));
            signatureTable.addCell(new Cell().add(
                    new Paragraph("Joint Account Holder's 2 Signature").setTextAlignment(TextAlignment.CENTER).setBold()).setBorder(Border.NO_BORDER));

            document.add(signatureTable);

            String umrn = "";
            String sponsorBankCode = "";
            String utilityCode = "";
            String authorizeName = jsonUtility.getJsonKeyValue("heading" , contentJson);
            String amountInRupees = amountWords;
            String emailIdNach = emailId;
            String policyNoNach = "";

            Table nachTable = new Table(1);

            Table nachLogoContainer = new Table(new float[]{100F, 800F});

            Table innerNach = new Table(new float[]{100F, 300F, 100F, 300F});
            nachLogoContainer.addCell(new Cell().add(nachLogo).setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add("UMRN: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(umrn));
            innerNach.addCell(new Cell().add("Date: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(policyStart));

            innerNach.addCell(new Cell(1, 4).add("").setHeight(5).setBorder(Border.NO_BORDER));

            innerNach.addCell(new Cell().add("Sponsor Bank Code: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(sponsorBankCode));
            innerNach.addCell(new Cell().add("Utility Code: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(utilityCode));
            nachLogoContainer.addCell(new Cell().add(innerNach).setBorder(Border.NO_BORDER));

            Table tickTable = new Table(new float[]{100F, 50F});
            p=new Paragraph("Tick(");
            p.add(tickImg);
            p.add("  )");
            tickTable.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            tickTable.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            tickTable.addCell(new Cell().add("CREATE"));
            tickTable.addCell(new Cell().add(tickImg).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            tickTable.addCell(new Cell().add("MODIFY"));
            tickTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            tickTable.addCell(new Cell().add("CANCEL"));
            tickTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            nachLogoContainer.addCell(new Cell().add(tickTable).setBorder(Border.NO_BORDER));

            innerNach = new Table(new float[]{100F, 300F, 100F, 300F});
            innerNach.addCell(new Cell().add("I/We hereby authorize: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(new Paragraph(authorizeName).setBold()));
            p = new Paragraph("To debit (tick");
            p.add(tickImg);
            p.add(" )");
            innerNach.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(new Paragraph("SB/CA/CC/SB-NRE/SB-NRO/Other").setBold()));

            innerNach.addCell(new Cell(1, 4).add("").setBorder(Border.NO_BORDER).setHeight(5));

            innerNach.addCell(new Cell().add("Bank a/c number: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1, 3).add(new Paragraph(accountNo).setBold().setTextAlignment(TextAlignment.LEFT)));

            innerNach.addCell(new Cell(1, 4).add("").setBorder(Border.NO_BORDER).setHeight(5));
            nachLogoContainer.addCell(new Cell().add(innerNach).setBorder(Border.NO_BORDER));
            nachTable.addCell(new Cell().add(nachLogoContainer));

            Table newContainer = new Table(1);
            innerNach = new Table(new float[]{130F, 300F, 100F, 300F, 100F, 300F});
            innerNach.addCell(new Cell().add("with Bank: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(new Paragraph(bankName)));

            innerNach.addCell(new Cell().add("IFSC Code: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(new Paragraph(ifscCode)));

            innerNach.addCell(new Cell().add("MICR: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(new Paragraph(micrCode).setTextAlignment(TextAlignment.LEFT)));

            innerNach.addCell(new Cell(1, 6).add(" ").setHeight(5).setBorder(Border.NO_BORDER));

            innerNach.addCell(new Cell().add("An amount of Rupees: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1,3).add(new Paragraph(amountInRupees)));
            innerNach.addCell(new Cell().add(" ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1, 2).add(new Paragraph(amount)));

            p = new Paragraph("Frequency:                     ");
            p.add(imgUnchecked);
            p.add("     ");
            p.add(new Text("Monthly").setLineThrough());
            p.add("     ");

            p.add(imgUnchecked);
            p.add("     ");
            p.add(new Text("Quarterly").setLineThrough());
            p.add("     ");

            p.add(imgUnchecked);
            p.add("     ");
            p.add(new Text("Half-Yearly").setLineThrough());
            p.add("     ");

            p.add(imgUnchecked);
            p.add("     ");
            p.add(new Text("Yearly").setLineThrough());
            p.add("     ");

            p.add(imgChecked);
            p.add("     As & When presented     ");

            innerNach.addCell(new Cell(1, 4).add(p).setBorder(Border.NO_BORDER));

            innerNach.addCell(new Cell().add("Debit Type: ").setBorder(Border.NO_BORDER));
            accountTypeDetails = new Table(new float[]{50F, 150F, 50F, 150F});
            accountTypeDetails.addCell(new Cell().add(imgUnchecked).setBorder(Border.NO_BORDER));
            accountTypeDetails.addCell(new Cell().add(" Fixed Amount").setLineThrough().setFontSize(5).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            accountTypeDetails.addCell(new Cell().add(imgChecked).setBorder(Border.NO_BORDER));
            accountTypeDetails.addCell(new Cell().add(" Maximum Amount").setFontSize(5).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell().add(accountTypeDetails).setBorder(Border.NO_BORDER));

            innerNach.addCell(new Cell().add("Application No: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1,3).add(new Paragraph(applicationNumber).setTextAlignment(TextAlignment.LEFT)));
            innerNach.addCell(new Cell().add("Mobile No: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1,3).add(new Paragraph(mobileNo).setTextAlignment(TextAlignment.LEFT)));

            innerNach.addCell(new Cell().add("Policy No: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1,3).add(new Paragraph(policyNoNach).setTextAlignment(TextAlignment.LEFT)));
            innerNach.addCell(new Cell().add("Email ID: ").setBorder(Border.NO_BORDER));
            innerNach.addCell(new Cell(1,3).add(new Paragraph(emailIdNach).setTextAlignment(TextAlignment.LEFT)));
            innerNach.addCell(new Cell(1, 6).add("I agree for the debit of mandate processing charges by the Bank whom I am authorizing to debit my account as per the latest schedule of charges of the bank.").setBorder(Border.NO_BORDER));
            newContainer.addCell(new Cell().add(innerNach).setBorder(Border.NO_BORDER));
            nachTable.addCell(newContainer);

            Table periodSignatureContainer = new Table(new float[]{200F, 800F});
            String toPeriod = "";

            Table periodTable = new Table(new float[]{50F, 100F});
            periodTable.addCell(new Cell(1, 2).add("PERIOD: ").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            periodTable.addCell(new Cell().add("From: ").setBorder(Border.NO_BORDER));
            if (policyStart.length() > 0) {
                periodTable.addCell(new Cell().add(policyStart));
            } else {
                periodTable.addCell(new Paragraph("                 "));
            }
            periodTable.addCell(new Cell().add("To: ").setBorder(Border.NO_BORDER));
            if (policyEnd.length() > 0) {
                periodTable.addCell(new Cell().add(policyEnd));
            } else {
                periodTable.addCell(new Paragraph("                 "));
            }
            periodTable.addCell(new Cell(1,2).add("Maximum period of mandate is 40 years").setTextAlignment(TextAlignment.CENTER).setFontSize(5).setBorder(Border.NO_BORDER));
            periodSignatureContainer.addCell(new Cell().add(periodTable));

            Table signatureChecks = new Table(new float[]{350F, 300F, 300F});
            p = new Paragraph();
            p.add(new Text("Maximum period of mandate is 40 years").setFontSize(5).setBorder(Border.NO_BORDER));
            p.add("\n\n\n");
            signatureChecks.addCell(new Cell(1, 3).add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            signatureChecks.addCell(new Cell().add("1.Signature Primary Account holder").setBorder(Border.NO_BORDER));
            signatureChecks.addCell(new Cell().add("2.Signature Account holder").setBorder(Border.NO_BORDER));
            signatureChecks.addCell(new Cell().add("3.Name as in Bank records").setBorder(Border.NO_BORDER));
            signatureChecks.addCell(new Cell().add(new Paragraph(customerName))).setBorder(Border.NO_BORDER);
            signatureChecks.addCell(new Cell().add(new Paragraph("").setUnderline())).setBorder(Border.NO_BORDER);
            signatureChecks.addCell(new Cell().add(new Paragraph("").setUnderline())).setBorder(Border.NO_BORDER);
            periodSignatureContainer.addCell(signatureChecks);

            nachTable.addCell(new Cell().add(periodSignatureContainer).setBorder(Border.NO_BORDER));
            p = new Paragraph(
                    "* This is to confirm that the declaration has been carefully read, understood & made by me / us. I am authorizing the user entity / corporate to debit my account.\n");
            p.add("* I have understood that I am authorized to cancel / amend this mandate by appropriately communicating the cancellation / amendment request to the user entity / corporate or the bank where I have authorized the debit.")
                    .setTextAlignment(TextAlignment.LEFT);
            p.setFontSize(5);
            nachTable.addCell(p);

            document.add(nachTable);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Exception occurs in generate E-mandate pdf:", e);
            return baos.toByteArray();
        }
    }

}
