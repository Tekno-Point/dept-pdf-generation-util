package com.pdfGeneration.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

abstract class NomineeAddendumPDF {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    abstract Table codeTable(String codeData, float[] pointColumnWidths);

    public Table generateNomineePDF(PDFUtility pdfUtility, JsonUtility jsonUtility, String applicationNo, JsonArray nomineeList, JsonObject imagesJson){
        Table nomineeTable = new Table(1);
        Table table = new Table(1);

        String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
        String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
        String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
        String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

        Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);
        Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

        String logoFilename = "whatsappLogo.png";
        Image img = pdfUtility.getPDFLogo(logoFilename);
        img.setWidth(200f);
        img.setHeight(120f);

        String nominee1 = "";
        String nominee1AccountType = "";
        String nominee1BankName = "";
        String nominee1BranchName = "";
        String nominee1AccountNumber = "";
        String nominee1IfscCode = "";
        String modeOfPaymentNominee1="";
        String nominee2 = "";
        String nominee2AccountType = "";
        String nominee2BankName = "";
        String nominee2BranchName = "";
        String nominee2AccountNumber = "";
        String nominee2IfscCode = "";
        String modeOfPaymentNominee2="";
        String nominee3 = "";
        String nominee3AccountType = "";
        String nominee3BankName = "";
        String nominee3BranchName = "";
        String nominee3AccountNumber = "";
        String nominee3IfscCode = "";
        String modeOfPaymentNominee3="";
        String nominee4 = "";
        String nominee4AccountType = "";
        String nominee4BankName = "";
        String nominee4BranchName = "";
        String nominee4AccountNumber = "";
        String nominee4IfscCode = "";
        String modeOfPaymentNominee4="";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int count = 0;
        for (JsonElement nomineeElement : nomineeList) {
            if (count >= 5) break;
            JsonObject obj = nomineeElement.getAsJsonObject();
            String nomineeDob = jsonUtility.getJsonKeyValue("dateOfBirth", obj);
            int age = 0;
            try {
                LocalDate dob = LocalDate.parse(nomineeDob, formatter);
                age = Period.between(dob, LocalDate.now()).getYears();
            } catch (Exception e) {
                logger.error("Invalid dateOfBirth for nominee :",nomineeDob);
            }
            boolean isMinor = age < 18;
            if (count == 1) {
                nominee1 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeName" : "name", obj);
                nominee1AccountType = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountType" : "accountType", obj);
                nominee1BankName =  jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Bank_Name" : "Bank_Name", obj);
                nominee1BankName=nominee1BankName.length()>21 ? nominee1BankName.substring(0,21) : nominee1BankName;
                nominee1BranchName = jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Branch_Name" : "Branch_Name", obj);
                nominee1BranchName=nominee1BranchName.length()>16 ? nominee1BranchName.substring(0,16) : nominee1BranchName;
                nominee1AccountNumber = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountNumber" : "accountNumber", obj);
                nominee1IfscCode = jsonUtility.getJsonKeyValue(isMinor ? "appointeeIfscCode" : "ifscCode", obj);
                modeOfPaymentNominee1 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeModeOfPayment" : "modeOfPayment", obj);
            } else if (count == 2) {
                nominee2 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeName" : "name", obj);
                nominee2AccountType = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountType" : "accountType", obj);
                nominee2BankName =  jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Bank_Name" : "Bank_Name", obj);
                nominee2BankName=nominee2BankName.length()>21 ? nominee2BankName.substring(0,21) : nominee2BankName;
                nominee2BranchName = jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Branch_Name" : "Branch_Name", obj);
                nominee2BranchName=nominee2BranchName.length()>16 ? nominee2BranchName.substring(0,16) : nominee2BranchName;
                nominee2AccountNumber = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountNumber" : "accountNumber", obj);
                nominee2IfscCode = jsonUtility.getJsonKeyValue(isMinor ? "appointeeIfscCode" : "ifscCode", obj);
                modeOfPaymentNominee2 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeModeOfPayment" : "modeOfPayment", obj);
            } else if (count == 3) {
                nominee3 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeName" : "name", obj);
                nominee3AccountType = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountType" : "accountType", obj);
                nominee3BankName =  jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Bank_Name" : "Bank_Name", obj);
                nominee3BankName=nominee3BankName.length()>21 ? nominee3BankName.substring(0,21) : nominee3BankName;
                nominee3BranchName = jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Branch_Name" : "Branch_Name", obj);
                nominee3BranchName=nominee3BranchName.length()>16 ? nominee3BranchName.substring(0,16) : nominee3BranchName;
                nominee3AccountNumber = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountNumber" : "accountNumber", obj);
                nominee3IfscCode = jsonUtility.getJsonKeyValue(isMinor ? "appointeeIfscCode" : "ifscCode", obj);
                modeOfPaymentNominee3 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeModeOfPayment" : "modeOfPayment", obj);
            } else if (count == 4) {
                nominee4 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeName" : "name", obj);
                nominee4AccountType = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountType" : "accountType", obj);
                nominee4BankName =  jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Bank_Name" : "Bank_Name", obj);
                nominee4BankName=nominee4BankName.length()>21 ? nominee4BankName.substring(0,21) : nominee4BankName;
                nominee4BranchName = jsonUtility.getJsonKeyValue(isMinor ? "Appointee_Branch_Name" : "Branch_Name", obj);
                nominee4BranchName=nominee4BranchName.length()>16 ? nominee4BranchName.substring(0,16) : nominee4BranchName;
                nominee4AccountNumber = jsonUtility.getJsonKeyValue(isMinor ? "appointeeAccountNumber" : "accountNumber", obj);
                nominee4IfscCode = jsonUtility.getJsonKeyValue(isMinor ? "appointeeIfscCode" : "ifscCode", obj);
                modeOfPaymentNominee4 = jsonUtility.getJsonKeyValue(isMinor ? "appointeeModeOfPayment" : "modeOfPayment", obj);
            }
            count++;
        }

        Paragraph p = new Paragraph();
        p.add(img);
        table.addCell(new Cell().add(p).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
        nomineeTable.addCell(new Cell().add(table).setBorder(Border.NO_BORDER));

        table = new Table(1);
        p = new Paragraph("Nominee Addendum").setBold().setFontSize(16);
        p.add(new Text("\n(Applicable in case of Multiple Nominations)").setFontSize(8));
        table.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        p = new Paragraph("Application No: " + applicationNo);
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Cell headingCell = new Cell();
        headingCell.setBackgroundColor(Color.GRAY, 100);
        p = new Paragraph("1. Benefit Payment Mode (Choose any one mode only)");
        p.setBold();
        p.setFontKerning(FontKerning.YES);
        p.setFontColor(Color.WHITE);
        headingCell.add(p);
        table.addCell(new Cell().add(headingCell).setBorder(Border.NO_BORDER));


        p = new Paragraph("For Nominee 2 / Appointee 2 (In case of Nominee is minor)").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the nominee according to the terms of the plan. If none of the below electronic payout option is chosen, the Company reserves the right to use " +
                "any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        Table bankDetails = new Table(new float[] {30F ,480F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(modeOfPaymentNominee1.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Direct Credit (Bank of Baroda & Union Bank of India)");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentNominee1.equalsIgnoreCase("NEFT") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        Table tableCodes = createDataTable(nominee1BankName, "000000000000000000");
        bankDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        Table accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        Table accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee1AccountType.equalsIgnoreCase("current")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee1AccountType.equalsIgnoreCase("savings")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee1BranchName, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee1AccountNumber, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        Table ifscDetails = new Table(new float[] {80F, 220F, 400F});
        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee1IfscCode, "000000000000000");
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        Table nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Nominee's name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee1, "000000000000000");
        nameDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nomineeTable.addCell(new Cell().add(table).setBorder(Border.NO_BORDER));

        table = new Table(1);
        p = new Paragraph("For Nominee 3 / Appointee 3 (In case of Nominee is minor)").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the nominee according to the terms of the plan. If none of the below electronic payout option is chosen, the Company reserves the right to use " +
                "any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        bankDetails = new Table(new float[] {30F ,480F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(modeOfPaymentNominee2.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Direct Credit (Bank of Baroda & Union Bank of India)");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentNominee2.equalsIgnoreCase("NEFT") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee2BankName, "000000000000000000");
        bankDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee2AccountType.equalsIgnoreCase("current")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee2AccountType.equalsIgnoreCase("savings")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee2BranchName, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee2AccountNumber, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails = new Table(new float[] {80F, 220F, 400F});
        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee2IfscCode, "00000000000");
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Nominee's name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee2, "000000000000000000000000");
        nameDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nomineeTable.addCell(new Cell().add(table).setBorder(Border.NO_BORDER));

        table = new Table(1);
        p = new Paragraph("For Nominee 4 / Appointee 4 (In case of Nominee is minor)").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the nominee according to the terms of the plan. If none of the below electronic payout option is chosen, the Company reserves the right to use " +
                "any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        bankDetails = new Table(new float[] {30F ,480F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(modeOfPaymentNominee3.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Direct Credit (Bank of Baroda & Union Bank of India)");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentNominee3.equalsIgnoreCase("NEFT") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee3BankName, "000000000000000000");
        bankDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee3AccountType.equalsIgnoreCase("current")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee3AccountType.equalsIgnoreCase("savings")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee3BranchName, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee3AccountNumber, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails = new Table(new float[] {80F, 220F, 400F});
        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee3IfscCode, "00000000000");
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Nominee's name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee3, "0000000000000000000");
        nameDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nomineeTable.addCell(new Cell().add(table).setBorder(Border.NO_BORDER));

        table = new Table(1);
        p = new Paragraph("For Nominee 5 / Appointee 5 (In case of Nominee is minor)").setUnderline().setBold();
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph("Mode selected will be used by the Company to pay the nominee according to the terms of the plan. If none of the below electronic payout option is chosen, the Company reserves the right to use " +
                "any alternative payout option.");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

        bankDetails = new Table(new float[] {30F ,480F, 30F, 60F, 100F, 400F});
        p = new Paragraph();
        p.add(modeOfPaymentNominee4.equalsIgnoreCase("direct credit") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add("Direct Credit (Bank of Baroda & Union Bank of India)");
        bankDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(modeOfPaymentNominee4.equalsIgnoreCase("NEFT") ? imgChecked : imgUnchecked);
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("NEFT");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph("Bank Name: ");
        bankDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee4BankName, "000000000000000000");
        bankDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(bankDetails).setBorder(Border.NO_BORDER));

        accountDetails = new Table(new float[] {320F, 120F, 200F, 160F, 300F});
        accountType = new Table(new float[]{120F, 30F, 60F, 30F, 60F});
        p = new Paragraph("Account Type: ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee4AccountType.equalsIgnoreCase("current")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Current ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        if (nominee4AccountType.equalsIgnoreCase("savings")) {
            p.add(imgChecked);
        } else {
            p.add(imgUnchecked);
        }
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        p = new Paragraph();
        p.add(" Savings ");
        accountType.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add(accountType).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Branch Name").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee4BranchName, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        accountDetails.addCell(new Cell().add("Bank Account No:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee4AccountNumber, "000000000000000000");
        accountDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(accountDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        ifscDetails = new Table(new float[] {80F, 220F, 400F});
        ifscDetails.addCell(new Cell().add("IFSC Code:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee4IfscCode, "00000000000");
        ifscDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        ifscDetails.addCell(new Cell().add("(Mandatory for NEFT mode)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(ifscDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        nameDetails = new Table(new float[] {220F, 600F});
        nameDetails.addCell(new Cell().add("Nominee's name as per the Bank Account.:").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        tableCodes = createDataTable(nominee4, "0000000000000000000000000000000");
        nameDetails.addCell(new Cell().add(tableCodes).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(nameDetails).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

        p = new Paragraph("Note: In case of multiple nominations, please add all nominees bank account details");
        table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
        nomineeTable.addCell(new Cell().add(table).setBorder(Border.NO_BORDER));
        return nomineeTable;
    }

    public Table createDataTable(String actualData,String emptyDataColumn) {
        String panNumberData = actualData.isEmpty() ? emptyDataColumn : actualData;
        float[] pointColumnWidths = new float[panNumberData.length()];
        for (int i = 0; i < panNumberData.length(); i++) {
            pointColumnWidths[i] = 20F;
        }return !actualData.isEmpty()
                ? codeTable(actualData, pointColumnWidths)
                : codeTable(panNumberData, pointColumnWidths).setFontColor(Color.WHITE);
    }
}
