package com.pdfGeneration.service.impl;

import com.google.gson.JsonObject;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.FontKerning;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.pdfGeneration.service.GenerateHealthPDF;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GenerateHealthPDFImpl implements GenerateHealthPDF {

    private final Logger logger = LoggerFactory.getLogger(GenerateHealthPDFImpl.class);

    @Autowired
    private PDFUtility pdfUtility;

    @Autowired
    private JsonUtility jsonUtility;

    @Override
    public byte[] generateHyperTensionPDF(String applicationNumber, String nameOfLifeAssured, JsonObject hypertension, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode, JsonObject imagesJson) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();
            logger.info("Hypertension json object:{}", hypertension);

            String logoFilename = jsonUtility.getJsonKeyValue("hypertension", imagesJson);
            String logoBase64 =  pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph("\n"));

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
            p.add("[To be filled by the medical examiner]\n");
            document.add(p);

            p = new Paragraph();
            p.add("Application No: ");
            applicationTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            pointColumnWidths = new float[appTextLength];
            for (int i = 0; i < appTextLength; i++) {
                pointColumnWidths[i] = 20F;
            }
            Table table = new Table(pointColumnWidths);
            int startIndex = 0;
            for (char c : applicationNumber.toCharArray()) {
                Cell cellLeft = new Cell();
                p = new Paragraph();
                p.add(new Text(String.valueOf(c)));
                if ((applicationNumber.charAt(startIndex) == appTextLength) || (startIndex == 0)) {
                    cellLeft.add(p).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER);
                } else {
                    cellLeft.add(p).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER);
                }
                cellLeft.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cellLeft);
                startIndex++;
            }
            applicationTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            document.add(applicationTable);

            p = new Paragraph();
            p.add("Full Name of life to be assured: ");
            document.add(p);
            appTextLength = nameOfLifeAssured.length();
            pointColumnWidths = new float[appTextLength];
            for (int i = 0; i < appTextLength; i++) {
                pointColumnWidths[i] = 20F;
            }
            table = new Table(appTextLength);
            for (char c : nameOfLifeAssured.toCharArray()) {
                Cell cellLeft = new Cell();
                p = new Paragraph();
                p.add(new Text(String.valueOf(c)));
                cellLeft.add(p);
                cellLeft.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cellLeft);
            }
            document.add(table);

            Table hyperTensionDetails = new Table(new float[]{800F, 200F});
            hyperTensionDetails.setBorder(Border.NO_BORDER);
            hyperTensionDetails.addCell(new Cell(1, 2).add("1. Current blood pressure measurement of the Iife to be assured.").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            Table t1 = new Table(new float[]{300F, 300F, 300F});
            t1.setTextAlignment(TextAlignment.CENTER);
            p = new Paragraph("Blood Pressure");
            t1.addCell(p);
            p = new Paragraph("1st Reading");
            t1.addCell(p);
            p = new Paragraph("2nd Reading");
            t1.addCell(p);
            p = new Paragraph("Systolic");
            t1.addCell(p);
            JsonObject currentBloodPressure = jsonUtility.getJsonObjectByKey("currentBloodPressure", hypertension);
            JsonObject systolic = jsonUtility.getJsonObjectByKey("systolic", currentBloodPressure);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("firstReading", systolic)).setUnderline());
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("secondReading", systolic)).setUnderline());
            t1.addCell(p);
            p = new Paragraph("Diastolic");
            JsonObject diastolic = jsonUtility.getJsonObjectByKey("diastolic", currentBloodPressure);
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("firstReading", diastolic)).setUnderline());
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("secondReading", diastolic)).setUnderline());
            t1.addCell(p);
            hyperTensionDetails.addCell(new Cell(1, 2).add(t1).setBorder(Border.NO_BORDER));

            String dateMonthYear = jsonUtility.getJsonKeyValue("firstNotice", hypertension);
            p = new Paragraph("2. a. Date / month and year when elevated blood pressure was first noticed. ");
            p.add(new Text(dateMonthYear).setUnderline());
            hyperTensionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("b. State blood pressure readings at that time.");
            hyperTensionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            t1 = new Table(new float[]{300F, 300F, 300F});
            t1.setTextAlignment(TextAlignment.CENTER);
            p = new Paragraph("Blood Pressure");
            t1.addCell(p);
            p = new Paragraph("1st Reading");
            t1.addCell(p);
            p = new Paragraph("2nd Reading");
            t1.addCell(p);

            JsonObject elevatedBloodPressure = jsonUtility.getJsonObjectByKey("dateWhenElevatedBloodPressureFirstTime", hypertension);
            JsonObject systolicOfElevated = jsonUtility.getJsonObjectByKey("systolic", elevatedBloodPressure);
            p = new Paragraph("Systolic");
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("firstReading", systolicOfElevated)).setUnderline());
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("secondReading", systolicOfElevated)).setUnderline());
            t1.addCell(p);

            p = new Paragraph("Diastolic");
            JsonObject distolicOfElevated = jsonUtility.getJsonObjectByKey("diastolic", elevatedBloodPressure);
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("firstReading", distolicOfElevated)).setUnderline());
            t1.addCell(p);
            p = new Paragraph(new Text(jsonUtility.getJsonKeyValue("secondReading", distolicOfElevated)).setUnderline());
            t1.addCell(p);
            hyperTensionDetails.addCell(new Cell(1, 2).add(t1).setBorder(Border.NO_BORDER));

            String typeOfHypertension = jsonUtility.getJsonKeyValue("typeOfHypertension", hypertension);
            p = new Paragraph("3. a. Type of hypertension       ");
            p.add("Essential    ");
            if (typeOfHypertension.equalsIgnoreCase("essential")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Secondary    ");
            if (typeOfHypertension.equalsIgnoreCase("secondary")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("\nb. If secondary, indicate the cause.\n");
            p.add(new Text(jsonUtility.getJsonKeyValue("typeOfHypertensionCause", hypertension)).setUnderline());
            p.add("\nAnswer \"Yes\" or \"No\" to question 4-6. If \"Yes\". Give details.");
            hyperTensionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell().add("4.\n A. Is he/she under treatment for hypertension?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject underTreatmentHypertension = jsonUtility.getJsonObjectByKey("underTreatmentHypertension", hypertension);
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", underTreatmentHypertension).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", underTreatmentHypertension).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("  B. How long is he/she under medication?  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("howLongUnderMedication", underTreatmentHypertension)).setUnderline());
            hyperTensionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("  C. Details of current treatment  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("detailsOfCurrentTreatment", underTreatmentHypertension)).setUnderline());
            hyperTensionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell().add("5. Is he/she taking medicine for hypertension regularly? ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject takingHypertensionMedicineRegularly = jsonUtility.getJsonObjectByKey("takingHypertensionMedicineRegularly", hypertension);
            if (jsonUtility.getJsonKeyValue("status", takingHypertensionMedicineRegularly).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", takingHypertensionMedicineRegularly).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell(1, 2).add("6. How frequently is the blood pressure measured to assess the adequacy of control? ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("toAssessAdequacyControl", hypertension)).setUnderline()).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell().add("7. Has he/she been able to achieve a level of 130/80 or 120/80?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject isAchievedHighBloodPressure = jsonUtility.getJsonObjectByKey("isAchievedHighBloodPressure", hypertension);

            if (jsonUtility.getJsonKeyValue("status", isAchievedHighBloodPressure).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", isAchievedHighBloodPressure).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell(1, 2).add("8. Is there any complaint suggestive of").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell().add("a) Renal disease?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject renalDisease = jsonUtility.getJsonObjectByKey("renalDisease", hypertension);
            if (jsonUtility.getJsonKeyValue("status", renalDisease).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", renalDisease).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell(1, 2).add("").setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("b) Visual defects?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject visualdefects = jsonUtility.getJsonObjectByKey("visualdefects", hypertension);
            if (jsonUtility.getJsonKeyValue("status", visualdefects).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", visualdefects).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("c) Neurological symptoms?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject neurologicalSymptoms = jsonUtility.getJsonObjectByKey("neurologicalSymptoms", hypertension);
            if (jsonUtility.getJsonKeyValue("status", neurologicalSymptoms).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", neurologicalSymptoms).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("d) Any other disease?\nSince when are you completely free from symptoms?  ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject anyOtherDisease = jsonUtility.getJsonObjectByKey("anyOtherDisease", hypertension);
            if (jsonUtility.getJsonKeyValue("status", anyOtherDisease).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", anyOtherDisease).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            JsonObject dateWhenElevatedBloodPressureFirstTime = jsonUtility.getJsonObjectByKey("dateWhenElevatedBloodPressureFirstTime", hypertension);
            p.add(new Text(jsonUtility.getJsonKeyValue("date", dateWhenElevatedBloodPressureFirstTime)).setUnderline());
            hyperTensionDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell(1, 2).add("9. If a regular blood pressure record has been maintained, give 4 representative readings done at intervals of 15 days or 1 month.").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("intervalReadingDetails", hypertension)).setUnderline()).setBorder(Border.NO_BORDER));

            hyperTensionDetails.addCell(new Cell(1, 2).add("10. At the time of diagnosis: Were the following investigations done?\n" + "If yes give reports.").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("1. S. Creatinine").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject sCreatinine = jsonUtility.getJsonObjectByKey("sCreatinine", hypertension);
            if (jsonUtility.getJsonKeyValue("status", sCreatinine).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", sCreatinine).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("2. X Ray Chest").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject xRayChest = jsonUtility.getJsonObjectByKey("xRayChest", hypertension);
            if (jsonUtility.getJsonKeyValue("status", xRayChest).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", xRayChest).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("3. ECG").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject ecg = jsonUtility.getJsonObjectByKey("ecg", hypertension);
            if (jsonUtility.getJsonKeyValue("status", ecg).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", ecg).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            hyperTensionDetails.addCell(new Cell().add("4. Eye examination for change in fund\nIf yes, mention the reports with the dates\n").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject eyeExaminationForFund = jsonUtility.getJsonObjectByKey("eyeExaminationForFund", hypertension);
            if (jsonUtility.getJsonKeyValue("status", eyeExaminationForFund).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", eyeExaminationForFund).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            hyperTensionDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            document.add(hyperTensionDetails);

            p = new Paragraph();
            p.add("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company.   ");
            document.add(p);

            document.add(new Paragraph("\n\n\n"));
            pointColumnWidths = new float[]{60F, 70F};
            Table dateTable = new Table(pointColumnWidths);
            p = new Paragraph();
            p.add("Date:   ");
            dateTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            pointColumnWidths = new float[]{20F, 20F, 20F};

            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String[] currentDateStr = sdf.format(currentDate).split("/");
            table = new Table(pointColumnWidths);
            table.addCell(new Cell().add(currentDateStr[0]).setTextAlignment(TextAlignment.CENTER).setBorderRight(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[1]).setTextAlignment(TextAlignment.CENTER).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[2]).setTextAlignment(TextAlignment.CENTER).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER).setBorderRight(Border.NO_BORDER));
            dateTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            dateTable.addCell(new Cell().add(new Paragraph(new Text("Place: ")).setTextAlignment(TextAlignment.CENTER)));
            dateTable.addCell(new Cell().add(new Paragraph(new Text(placeName)).setTextAlignment(TextAlignment.CENTER)));
            document.add(dateTable);

            pointColumnWidths = new float[]{250F, 180F, 280F};
            table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of Life to be assured / Proposer").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            document.add(table);

            if (isOmniDoc) {
                p = new Paragraph();
                p.add(imgChecked);
                if (countryCode.equalsIgnoreCase("91")) {
                    p.add(new Text("   Validated through the OTP sent to " + "registered mobile no."));
                    p.add(new Text(pdfUtility.maskMobileNumber(primaryMobileNo)).setBold());
                } else {
                    p.add(new Text("   Validated through the OTP sent to " + "registered email id."));
                }
                document.add(p.setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
            }

            Table grandFooterTable = pdfUtility.createFooter();
            PageSize ps = pdfDoc.getDefaultPageSize();

            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), ps.getWidth() - document.getLeftMargin() - document.getRightMargin());
            document.add(grandFooterTable);

            document.close();

            logger.info("Hyper Tension PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateHyperTensionPDF: {}", ex);
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateThyroidPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medical, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode, JsonObject imagesJson) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = jsonUtility.getJsonKeyValue("thyroid", imagesJson);
            String logoBase64 =  pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph(""));

            Paragraph p = new Paragraph();
            p.add("[To be filled by the medical examiner]\n");
            document.add(p);

            p = new Paragraph();
            p.add("Application No: ");
            p.add(new Text(applicationNumber).setUnderline());
            document.add(p);

            p = new Paragraph();
            p.add("Full Name of life to be assured: ");
            p.add(new Text(nameOfLifeAssured.toUpperCase()).setUnderline());
            document.add(p);

            Table thyroidDetails = new Table(new float[]{800F, 200F});
            thyroidDetails.setBorder(Border.NO_BORDER);
            thyroidDetails.addCell(new Cell().add("1. Have you ever suffered from any Thyroid disorder? (If yes Give details)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject thyroidDisorder = jsonUtility.getJsonObjectByKey("thyroidDisorder", medical);
            if (jsonUtility.getJsonKeyValue("status", thyroidDisorder).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", thyroidDisorder).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            thyroidDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            List nestedList = new List(ListNumberingType.ENGLISH_LOWER);
            ListItem nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Date of Diagnosis:  ");
            if (jsonUtility.getJsonKeyValue("status", thyroidDisorder).equalsIgnoreCase("Y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("diagnosis", thyroidDisorder)).setUnderline());
            } else {
                p.add(new Text("   "));
            }
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Type of Disorder:  ");
            if (jsonUtility.getJsonKeyValue("status", thyroidDisorder).equalsIgnoreCase("Y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("disorder", thyroidDisorder)).setUnderline());
            } else {
                p.add(new Text("   "));
            }
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Treatment Details:  ");
            if (jsonUtility.getJsonKeyValue("status", thyroidDisorder).equalsIgnoreCase("Y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("treatment", thyroidDisorder)).setUnderline());
            } else {
                p.add(new Text("   "));
            }
            nestedItem.add(p);
            nestedList.add(nestedItem);
            thyroidDetails.addCell(new Cell(1, 2).add(nestedList).setBorder(Border.NO_BORDER));

            thyroidDetails.addCell(new Cell().add("2. Have you experienced any weight gain or loss? ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject weightGainLoss = jsonUtility.getJsonObjectByKey("weightGainLoss", medical);
            if (jsonUtility.getJsonKeyValue("status", weightGainLoss).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", weightGainLoss).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            thyroidDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("If yes, Gained/Lost  ");
            if (jsonUtility.getJsonKeyValue("status", weightGainLoss).equalsIgnoreCase("Y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("weightGained", weightGainLoss)).setUnderline());
            } else {
                p.add(new Text("   ").setUnderline());
            }
            p.add(" Kg");
            thyroidDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            thyroidDetails.addCell(new Cell().add("\n3. Have you ever suffered or are suffering from any tumor problem? (If yes Give details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject tumor = jsonUtility.getJsonObjectByKey("tumorProblem", medical);
            if (jsonUtility.getJsonKeyValue("status", tumor).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", tumor).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            thyroidDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            nestedList = new List(ListNumberingType.ENGLISH_LOWER);
            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Date of Diagnosis:  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("diagnosis", tumor)).setUnderline());
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Type of Tumor:  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("tumor", tumor)).setUnderline());
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Grade of Tumor:  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("grade", tumor)).setUnderline());
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Treatment Details:  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("treatment", tumor)).setUnderline());
            nestedItem.add(p);
            nestedList.add(nestedItem);
            thyroidDetails.addCell(new Cell(1, 2).add(nestedList).setBorder(Border.NO_BORDER));

            thyroidDetails.addCell(new Cell().add("\n4. Have you ever undergone any Surgery? (If yes Give details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject surgery = jsonUtility.getJsonObjectByKey("surgery", medical);
            if (jsonUtility.getJsonKeyValue("status", surgery).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", surgery).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            thyroidDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            nestedList = new List(ListNumberingType.ENGLISH_LOWER);
            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Date of Surgery:  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("dateOfSurgery", surgery)).setUnderline());
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Diagnosis for surgery:  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("diagnosisOfSurgery", surgery)).setUnderline());
            nestedList.add(nestedItem);
            nestedItem.add(p);

            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Type of Surgery  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("typeOfSurgery", surgery)).setUnderline());
            nestedList.add(nestedItem);
            nestedItem.add(p);

            thyroidDetails.addCell(new Cell(1, 2).add(nestedList).setBorder(Border.NO_BORDER));

            thyroidDetails.addCell(new Cell().add("5.  Have you followed up for the same? ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject followed = jsonUtility.getJsonObjectByKey("followed", medical);
            if (jsonUtility.getJsonKeyValue("status", followed).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", followed).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            thyroidDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            thyroidDetails.addCell(new Cell(1, 2).add(new Paragraph("(Please attach a copy of reports pertaining to follow up & treatment details if any)").setBold()).setBorder(Border.NO_BORDER));

            document.add(thyroidDetails);
            p = new Paragraph();
            p.add(new Text("\n\nI hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            // document.add(new AreaBreak());

            float[] pointColumnWidths = new float[]{250F, 180F, 280F};
            Table table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of Medical Examiner").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of Life to be Assured").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            document.add(table);

            pointColumnWidths = new float[]{60F, 70F};
            Table dateTable = new Table(pointColumnWidths);
            p = new Paragraph();
            p.add("Date:   ");
            dateTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            pointColumnWidths = new float[]{20F, 20F, 20F};
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String[] currentDateStr = sdf.format(currentDate).split("/");

            table = new Table(pointColumnWidths);
            table.addCell(new Cell().add(currentDateStr[0]).setTextAlignment(TextAlignment.CENTER).setBorderRight(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[1]).setTextAlignment(TextAlignment.CENTER).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[2]).setTextAlignment(TextAlignment.CENTER).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER).setBorderRight(Border.NO_BORDER));
            dateTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            dateTable.addCell(new Cell().add(new Paragraph(new Text("Place: ")).setTextAlignment(TextAlignment.CENTER)));
            dateTable.addCell(new Cell().add(new Paragraph(new Text(placeName)).setTextAlignment(TextAlignment.CENTER)));
            document.add(dateTable);

            if (isOmniDoc) {
                p = new Paragraph();
                p.add(imgChecked);
                if (countryCode.equalsIgnoreCase("91")) {
                    p.add(new Text("   Validated through the OTP sent to " + "registered mobile no."));
                    p.add(new Text(pdfUtility.maskMobileNumber(primaryMobileNo)).setBold());
                } else {
                    p.add(new Text("   Validated through the OTP sent to " + "registered email id."));
                }
                document.add(p.setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
            }

            Table grandFooterTable = pdfUtility.createFooter();
            PageSize ps = pdfDoc.getDefaultPageSize();

            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), ps.getWidth() - document.getLeftMargin() - document.getRightMargin());
            document.add(grandFooterTable);

            document.close();
            logger.info("Thyroid PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateThyroidPDF: {}", ex);
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateRespiratoryPDF(String applicationNumber, String nameOfLifeAssured, JsonObject respiratoryDisorder, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode, JsonObject imagesJson) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = jsonUtility.getJsonKeyValue("respiratory", imagesJson);
            String logoBase64 =  pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph(""));

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
            p.add("[To be filled by the medical examiner]\n");
            document.add(p);

            p = new Paragraph();
            p.add("Application No: ");
            applicationTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            pointColumnWidths = new float[appTextLength];
            for (int i = 0; i < appTextLength; i++) {
                pointColumnWidths[i] = 20F;
            }
            Table table = new Table(pointColumnWidths);
            int startIndex = 0;
            for (char c : applicationNumber.toCharArray()) {
                Cell cellLeft = new Cell();
                p = new Paragraph();
                p.add(new Text(String.valueOf(c)));
                if ((applicationNumber.charAt(startIndex) == appTextLength) || (startIndex == 0)) {
                    cellLeft.add(p).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER);
                } else {
                    cellLeft.add(p).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER);
                }
                cellLeft.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cellLeft);
                startIndex++;
            }
            applicationTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            document.add(applicationTable);

            p = new Paragraph();
            p.add("Full Name of life to be assured: ");
            document.add(p);
            appTextLength = nameOfLifeAssured.length();
            table = new Table(appTextLength);
            for (char c : nameOfLifeAssured.toCharArray()) {
                Cell cellLeft = new Cell();
                p = new Paragraph();
                p.add(new Text(String.valueOf(c)));
                cellLeft.add(p);
                cellLeft.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cellLeft);
            }
            document.add(table);

            document.add(new Paragraph("\n"));

            Table respiratoryDetails = new Table(new float[]{800F, 200F});
            respiratoryDetails.setBorder(Border.NO_BORDER);
            respiratoryDetails.addCell(new Cell(1, 2).add("1. Please state the precise diagnosis (if known).").setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("respDiagnosis", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add("2. When was this condition first diagnosed? (Give exact age / year of onset).").setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("disgnosedAge", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell().add("3. Have you had any x-rays, PFT or other investigations for this condition?\n" + "If YES, please provide details including dates of investigations and results.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            JsonObject xRayPTF = jsonUtility.getJsonObjectByKey("xRay_PTF", respiratoryDisorder);
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", xRayPTF).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", xRayPTF).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", xRayPTF).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("xRay_PTF_description", xRayPTF)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            respiratoryDetails.addCell(new Cell().add("4. Have you been admitted to hospital for this condition?\n" + "If YES, attach attending physician's report / hospital discharge card.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            JsonObject admittedToHospital = jsonUtility.getJsonObjectByKey("addmittedToHospital", respiratoryDisorder);
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", admittedToHospital).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", admittedToHospital).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", admittedToHospital).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("addmittedToHospital_description", admittedToHospital)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            respiratoryDetails.addCell(new Cell(1, 2).add("5. Regarding your symptoms:\n" + "A. Please describe your symptoms.").setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("symptomsDescription", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add("B. How frequently do symptoms occur ? e.g. how many attacks on an average do you have in a year.").setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("symptomsOccurance", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell().add("6. Do your symptoms wake you at night? If YES, how often per month?").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject symptomsNightAwake = jsonUtility.getJsonObjectByKey("symptomsNightAwake", respiratoryDisorder);
            if (jsonUtility.getJsonKeyValue("status", symptomsNightAwake).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", symptomsNightAwake).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", symptomsNightAwake).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("symptomsNightAwake_description", symptomsNightAwake)).setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setVerticalAlignment(VerticalAlignment.MIDDLE).setUnderline()).setBorder(Border.NO_BORDER));
            }
            respiratoryDetails.addCell(new Cell().add("7. Are your attacks seasonal? If YES, give number of attacks per season").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject seasonalAttacks = jsonUtility.getJsonObjectByKey("seasonalAttacks", respiratoryDisorder);
            if (jsonUtility.getJsonKeyValue("status", seasonalAttacks).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", seasonalAttacks).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", seasonalAttacks).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("seasonalAttacks_description", seasonalAttacks)).setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            }

            respiratoryDetails.addCell(new Cell().add("C. Are you aware of any specific provoking cause(s) which trigger your symptoms? e.g. exercise, stress, allergy.\n" + "If YES, please provide details.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject provokingCause = jsonUtility.getJsonObjectByKey("provokingCause", respiratoryDisorder);
            if (jsonUtility.getJsonKeyValue("status", provokingCause).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", provokingCause).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", provokingCause).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("provokingCause_description", provokingCause)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            respiratoryDetails.addCell(new Cell(1, 2).add("D. When was the last occurrence of symptoms and how long the symptoms usually last?").setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("symptomsLastOccurance", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add("8. How many days (total) you have been away from work due to this condition during last 2 years?").setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("awayFromWork", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add("9. Please provide details of your treatment. Include names of medication (e.g. Asthalin, Bricanyl, Vent, Derifylline etc), dosage and how often taken. Include " + "details of tablets, injections and inhalers:").setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("A. Currently :")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("currentTreatment", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("B. In the past :")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("pastTreatment", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell().add("C. Have you ever taken steroids? e.g. Beclomet hasone, Prednisolone etc.\n" + "If YES, please provide full details including duration and type of treatment like Inha ler,tablets etc.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            JsonObject takenSteroids = jsonUtility.getJsonObjectByKey("takenSteroids", respiratoryDisorder);
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", takenSteroids).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", takenSteroids).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", takenSteroids).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("takenSteroids_description", takenSteroids)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("Regarding the monitoring of your condition:\n" + "A. Who is in charge of your follow-up?")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("followUpIncharge", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("B. How often do you attend for follow-up?")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("followUpAttendance", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("C. When was your last consultation?")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("lastConsultation", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell().add(new Paragraph("D. Do you use a peak flow meter and record the results?\n" + "If YES, please provide your lowest and highest readings in the last 3 months.")).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject flowMeter = jsonUtility.getJsonObjectByKey("flowMeter", respiratoryDisorder);
            if (jsonUtility.getJsonKeyValue("status", flowMeter).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", flowMeter).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", flowMeter).equalsIgnoreCase("Y")) {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("flowMeter_description", flowMeter)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            p = new Paragraph("10. Do you smoke*  ");
            JsonObject smoking = jsonUtility.getJsonObjectByKey("smoking", respiratoryDisorder);
            p.add(new Text("cigarettes"));
            p.add(" / ");
            p.add(new Text("beedis"));
            p.add(" / ");
            p.add(new Text("cigar"));
            p.add(" / ");
            p.add(new Text("pipes"));
            p.add(" ?");
            p.add(new Text("  \n* Strike off whichever is not applicable "));

            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", smoking).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", smoking).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            respiratoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph("If YES, how many ");
            if (jsonUtility.getJsonKeyValue("status", smoking).equalsIgnoreCase("Y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("smoking_count", smoking)).setUnderline());
            } else {
                p.add(new Text("____________"));
            }
            p.add(" per day, since last ");
            if (jsonUtility.getJsonKeyValue("status", smoking).equalsIgnoreCase("Y")) {
                p.add(new Text(jsonUtility.getJsonKeyValue("smoking_yrs", smoking)).setUnderline());
            } else {
                p.add(new Text("___________"));
            }
            p.add(" years");
            respiratoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("11. What is the level of your exercise tolerance? Mention distance, which you can walk and number of stairs you can climb without causing breathlessness.")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("excersiceTolerance", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph("12. Please provide any additional information on your condition, which you feel,will be helpful in process ing your application.")).setBorder(Border.NO_BORDER));
            respiratoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("additionalInformation", respiratoryDisorder)).setUnderline()).setBorder(Border.NO_BORDER));

            document.add(respiratoryDetails);

            p = new Paragraph();
            p.add(new Text("I declare that the answers I have given are, to the best of my knowledge, true and that I have not withheld any material information that may influence the assessment or acceptance of this application."));
            document.add(p);
            p = new Paragraph();
            p.add(new Text("I agree that this form will constitute part of my application for life assurance and that failure to disclose any material fact known to me may invalidate the contract.\n\n\n"));
            document.add(p);

            //document.add(new AreaBreak());

            pointColumnWidths = new float[]{60F, 70F};
            Table dateTable = new Table(pointColumnWidths);
            p = new Paragraph();
            p.add("Date:   ");
            dateTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            pointColumnWidths = new float[]{20F, 20F, 20F};
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String[] currentDateStr = sdf.format(currentDate).split("/");

            table = new Table(pointColumnWidths);
            table.addCell(new Cell().add(currentDateStr[0]).setTextAlignment(TextAlignment.CENTER).setBorderRight(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[1]).setTextAlignment(TextAlignment.CENTER).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[2]).setTextAlignment(TextAlignment.CENTER).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER).setBorderRight(Border.NO_BORDER));
            dateTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            dateTable.addCell(new Cell().add(new Paragraph(new Text("Place: ")).setTextAlignment(TextAlignment.CENTER)));
            dateTable.addCell(new Cell().add(new Paragraph(new Text(placeName)).setTextAlignment(TextAlignment.CENTER)));
            document.add(dateTable);

            Table signatureTable = new Table(2);
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER).add(""));
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER).add("Signature of the Life Assured.").setTextAlignment(TextAlignment.CENTER));
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER).add(""));
            signatureTable.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER).add("Signature of the Medical Examiner").setTextAlignment(TextAlignment.CENTER));
            document.add(signatureTable);

            if (isOmniDoc) {
                p = new Paragraph();
                p.add(imgChecked);
                if (countryCode.equalsIgnoreCase("91")) {
                    p.add(new Text("   Validated through the OTP sent to " + "registered mobile no."));
                    p.add(new Text(pdfUtility.maskMobileNumber(primaryMobileNo)).setBold());
                } else {
                    p.add(new Text("   Validated through the OTP sent to " + "registered email id."));
                }
                document.add(p.setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
            }

            Table grandFooterTable = pdfUtility.createFooter();
            PageSize ps = pdfDoc.getDefaultPageSize();

            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), ps.getWidth() - document.getLeftMargin() - document.getRightMargin());
            document.add(grandFooterTable);

            document.close();

            logger.info("Respiratory PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateRespiratoryPDF: {}", ex);
        }
        return bytesPdf;
    }
    @Override
    public byte[] generateDiabetesPDF(String applicationNumber, String nameOfLifeAssured, JsonObject diabetes, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode, JsonObject imagesJson) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = jsonUtility.getJsonKeyValue("diabetes", imagesJson);
            String logoBase64 =  pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);
            document.add(new Paragraph(""));

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
            p.add("[To be filled by the medical examiner]\n");
            document.add(p);

            p = new Paragraph();
            p.add("Application No: ");
            applicationTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            pointColumnWidths = new float[appTextLength];
            for (int i = 0; i < appTextLength; i++) {
                pointColumnWidths[i] = 20F;
            }
            Table table = new Table(pointColumnWidths);
            int startIndex = 0;
            for (char c : applicationNumber.toCharArray()) {
                Cell cellLeft = new Cell();
                p = new Paragraph();
                p.add(new Text(String.valueOf(c)));
                if ((applicationNumber.charAt(startIndex) == appTextLength) || (startIndex == 0)) {
                    cellLeft.add(p).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER);
                } else {
                    cellLeft.add(p).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER);
                }
                cellLeft.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cellLeft);
                startIndex++;
            }
            applicationTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            document.add(applicationTable);

            p = new Paragraph();
            p.add("Full Name of life to be assured: ");
            document.add(p);
            appTextLength = nameOfLifeAssured.length();
            pointColumnWidths = new float[appTextLength];
            for (int i = 0; i < appTextLength; i++) {
                pointColumnWidths[i] = 20F;
            }
            table = new Table(appTextLength);
            for (char c : nameOfLifeAssured.toCharArray()) {
                Cell cellLeft = new Cell();
                p = new Paragraph();
                p.add(new Text(String.valueOf(c)));
                cellLeft.add(p);
                cellLeft.setTextAlignment(TextAlignment.CENTER);
                table.addCell(cellLeft);
            }
            document.add(table);
            document.add(new Paragraph("\n"));

            Table diabetesDetails = new Table(new float[]{800F, 200F});
            diabetesDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. Date or year of diagnosis :");
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            diabetesDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("DateNWeight", diabetes)).setUnderline()).setBorder(Border.NO_BORDER));
            diabetesDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("2. Weight\n" + "Weight at the time of diagnosis (if known) ");
            p.add(new Text(jsonUtility.getJsonKeyValue("weightAtDaignosisTime", diabetes)).setUnderline());
            p.add(new Text(" kgs\n"));
            p.add("Present weight ");
            p.add(new Text(jsonUtility.getJsonKeyValue("presentWeight", diabetes)).setUnderline());
            p.add(" kgs.");

            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            JsonObject oraldrug = jsonUtility.getJsonObjectByKey("Oraldrug", diabetes);
            JsonObject insulinDrug = jsonUtility.getJsonObjectByKey("insulindrug", diabetes);
            p = new Paragraph("3. ls he / she on:\n" + "a) Oral drug treatment? State names of drugs and dose received.  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("oralDrugDetails", oraldrug)).setUnderline());
            p.add("\nb) Insulin (type / dose)? And frequency of injections per day.  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("insulinDetails", insulinDrug)).setUnderline());
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("4. How often do he/ she check his / her blood sugar? ");
            JsonObject bloodSugar = jsonUtility.getJsonObjectByKey("bloodSugar", diabetes);
            p.add(new Text(jsonUtility.getJsonKeyValue("bloodSugarTest", bloodSugar)).setUnderline());
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            diabetesDetails.addCell(new Cell(1, 2).add("5. Last blood sugar reports: ").setBorder(Border.NO_BORDER));

            List nestedList = new List();
            nestedList.setListSymbol("\u2022  ");
            ListItem nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Fasting ");
            nestedItem.add(p);
            nestedList.add(nestedItem);
            nestedItem = new ListItem();
            p = new Paragraph();
            p.add("Post lunch ");
            nestedItem.add(p);
            nestedList.add(nestedItem);
            nestedItem = new ListItem();
            p = new Paragraph();
            p.add(new Text("Random"));
            nestedItem.add(p);
            nestedList.add(nestedItem);
            diabetesDetails.addCell(new Cell(1, 2).add(nestedList).setBorder(Border.NO_BORDER));

            JsonObject lipids = jsonUtility.getJsonObjectByKey("lipidsEstimation", diabetes);
            p = new Paragraph("6. Has he / she been subjected to:\n" + "a) Estimation of lipids: Total S. cholesterol, Serum Triglycerides, HDL Cholesterol. If so, attach the report.");
            diabetesDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", lipids).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", lipids).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            diabetesDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject hemoglobin = jsonUtility.getJsonObjectByKey("hemoglobinEstimation", diabetes);
            String hemoglobinEstimate = jsonUtility.getJsonKeyValue("hemoglobinestimate", hemoglobin);
            String hemoglobinStatus = jsonUtility.getJsonKeyValue("status", hemoglobin);
            p = new Paragraph("b) Glycosylated hemoglobin estimation.\n" + "If so, how often and based on these reports was he / she told that his control is");
            if (hemoglobinStatus.equalsIgnoreCase("Y")){
                if (hemoglobinEstimate.equalsIgnoreCase("good")){
                    p.add(" good");
                }
                if (hemoglobinEstimate.equalsIgnoreCase("fair")){
                    p.add(" fair");
                }
                if (hemoglobinEstimate.equalsIgnoreCase("poor")){
                    p.add(" poor");
                }
                p.add(".");
            }else {
                p.add(" good / fair / poor.");
            }
            diabetesDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (hemoglobinStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (hemoglobinStatus.equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            diabetesDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            // p=new Paragraph();
            // JsonObject hemoglobinEstimation =
            // jsonUtility.getJsonObjectByKey("hemoglobinEstimation",diabetes);
            // if (jsonUtility.getBooleanKeyValue("Fair",hemoglobinEstimation)) {
            // p.add(new Text("Fair "));
            // } else {
            // p.add(new Text("Fair ").setLineThrough());
            // }
            // if (jsonUtility.getBooleanKeyValue("Good",hemoglobinEstimation)) {
            // p.add(new Text("Good "));
            // } else {
            // p.add(new Text("Good ").setLineThrough());
            // }
            // if (jsonUtility.getBooleanKeyValue("Poor",hemoglobinEstimation)) {
            // p.add(new Text("Poor"));
            // } else {
            // p.add(new Text("Poor").setLineThrough());
            // }
            // diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            JsonObject bloodPressure = jsonUtility.getJsonObjectByKey("bloodPressure", diabetes);
            p = new Paragraph("7. Blood pressure: If reading at the time of diagnosis is known, mention that reading of BP. Is he on any antihypertensive drugs what is his / her present BP?\n" + "What drugs is he/ she receiving for hypertension.  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("hypertensionDrug", bloodPressure)).setUnderline());
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            // diabetesDetails.addCell(new Cell().add(new
            // Paragraph().setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("8. ECG: How often has been subjected to recording an Electrocardiogram. Please submit the last report.");
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("9. Were there any of the following complications during the course of his / her diabetes");
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            Table complicationDetails = new Table(new float[]{300F, 300F});

            JsonObject complications = jsonUtility.getJsonObjectByKey("complications", diabetes);
            //JsonObject complication = jsonUtility.getJsonObjectByKey("complications", complications);
            if (jsonUtility.getJsonKeyValue("status", complications).equalsIgnoreCase("y")) {
                complicationDetails.addCell(new Cell().add("a. Polyneuritis").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Polyneuritis", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Polyneuritis", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                complicationDetails.addCell(new Cell().add("b. Myocardial infarction").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Myocardialinfarction", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Myocardialinfarction", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                complicationDetails.addCell(new Cell().add("c. Unstable angina").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Unstableangina", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Unstableangina", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                complicationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                complicationDetails.addCell(new Cell().add("d. Gangrene of the foot or the upper limb").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Gangreneofthefootortheupperlimb", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Gangreneofthefootortheupperlimb", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                complicationDetails.addCell(new Cell().add("e. Foot infection and ulceration").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Footinfectionandulceration", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Footinfectionandulceration", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                complicationDetails.addCell(new Cell().add("f. Recurrent urinary tract infection").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Recurrenturinarytractinfection", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Recurrenturinarytractinfection", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                complicationDetails.addCell(new Cell().add("g. Recurrent skin infection").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                if (jsonUtility.getBooleanKeyValue("Recurrentskininfection", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("  No   ");
                if (!jsonUtility.getBooleanKeyValue("Recurrentskininfection", complications)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
            } else {
                complicationDetails.addCell(new Cell().add("a. Polyneuritis").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));


                complicationDetails.addCell(new Cell().add("b. Myocardial infarction").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                complicationDetails.addCell(new Cell().add("c. Unstable angina").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                complicationDetails.addCell(new Cell().add("d. Gangrene of the foot or the upper limb").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                complicationDetails.addCell(new Cell().add("e. Foot infection and ulceration").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));


                complicationDetails.addCell(new Cell().add("f. Recurrent urinary tract infection").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
                complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

                complicationDetails.addCell(new Cell().add("g. Recurrent skin infection").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("  Yes  ");
                p.add(imgUnchecked);
                p.add("  No   ");
                p.add(imgUnchecked);
            }
            complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            diabetesDetails.addCell(new Cell(1, 2).add(complicationDetails).setBorder(Border.NO_BORDER));

            p = new Paragraph("10 Hospitalization: Was he / she hospitalized for any of the following?\n" + "a) Investigation and management by a cardiologist.");
            diabetesDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject cardiologist = jsonUtility.getJsonObjectByKey("cardiologist", diabetes);
            if (jsonUtility.getJsonKeyValue("status", cardiologist).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", cardiologist).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            diabetesDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            diabetesDetails.addCell(new Cell().add(new Paragraph("b) Treatment of hypoglycemia or uncontrolled diabetesor hyperglycemia or diabetic ketosis or coma. If yes, give details.")).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject hyperglycemia = jsonUtility.getJsonObjectByKey("hypoglycemia", diabetes);
            if (jsonUtility.getJsonKeyValue("status", hyperglycemia).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", hyperglycemia).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            diabetesDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            if (jsonUtility.getJsonKeyValue("status", hyperglycemia).equalsIgnoreCase("Y")) {
                diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("hypoglycemiaDetails", hyperglycemia)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("11. Vision: Is vision normal?\n" + "If No- was he examined for evidence of diabetic changes in the eye");
            diabetesDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject visionNormal = jsonUtility.getJsonObjectByKey("visionNormal", diabetes);
            if (jsonUtility.getJsonKeyValue("status", visionNormal).equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", visionNormal).equalsIgnoreCase("N")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            diabetesDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph("If yes, give findings..")).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", visionNormal).equalsIgnoreCase("Y")) {
                diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("visionDetails", visionNormal)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                diabetesDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("12. Do you consider his / her diabetic control as:");
            diabetesDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            complicationDetails = new Table(new float[]{300F, 300F});
            complicationDetails.addCell(new Cell().add("a) Very good (super control)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject diabeticControl = jsonUtility.getJsonObjectByKey("diabeticIsControl", diabetes);
            String diabeticReason = jsonUtility.getJsonKeyValue("reason", diabeticControl);

            switch (diabeticReason) {
                case "Verygood" -> {
                    p.add(imgChecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                    complicationDetails.addCell(new Cell().add("b) Reasonably controlled").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgUnchecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                    complicationDetails.addCell(new Cell().add("c) Poor control").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgUnchecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                    diabetesDetails.addCell(new Cell(1, 2).add(complicationDetails).setBorder(Border.NO_BORDER));
                }
                case "Reasonablycontrolled" -> {
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgUnchecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                    complicationDetails.addCell(new Cell().add("b) Reasonably controlled").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgChecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                    complicationDetails.addCell(new Cell().add("c) Poor control").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgUnchecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                    diabetesDetails.addCell(new Cell(1, 2).add(complicationDetails).setBorder(Border.NO_BORDER));

                }
                case "Poorcontrol" -> {
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgUnchecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

                    complicationDetails.addCell(new Cell().add("b) Reasonably controlled").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgUnchecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                    complicationDetails.addCell(new Cell().add("c) Poor control").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
                    p = new Paragraph();
                    p.add("  Yes  ");
                    p.add(imgChecked);
                    p.add("  No   ");
                    p.add(imgUnchecked);
                    complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
                    diabetesDetails.addCell(new Cell(1, 2).add(complicationDetails).setBorder(Border.NO_BORDER));
                }
            }

            /*if (diabeticReason.equalsIgnoreCase("Verygood")) {
                Verygood = true;
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!diabeticReason.equalsIgnoreCase("Verygood")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            complicationDetails.addCell(new Cell().add("b) Reasonably controlled")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            //check key value ---
            if (diabeticReason.equalsIgnoreCase("Reasonablycontrolled")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!diabeticReason.equalsIgnoreCase("Reasonablycontrolled")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            complicationDetails.addCell(new Cell().add("c) Poor control").setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (diabeticReason.equalsIgnoreCase("Poorcontrol")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!diabeticReason.equalsIgnoreCase("Poorcontrol")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            complicationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            diabetesDetails.addCell(new Cell(1, 2).add(complicationDetails).setBorder(Border.NO_BORDER));
*/
            document.add(diabetesDetails);

            p = new Paragraph();
            p.add("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company");
            document.add(p);

            pointColumnWidths = new float[]{250F, 180F, 280F};
            table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of Life to be assured / Proposer").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of Medical Examiner with Code No").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            document.add(table);

            document.add(new Paragraph("\n"));
            pointColumnWidths = new float[]{60F, 70F};
            Table dateTable = new Table(pointColumnWidths);
            p = new Paragraph();
            p.add("Date:   ");
            dateTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));
            pointColumnWidths = new float[]{20F, 20F, 20F};

            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String[] currentDateStr = sdf.format(currentDate).split("/");

            table = new Table(pointColumnWidths);
            table.addCell(new Cell().add(currentDateStr[0]).setTextAlignment(TextAlignment.CENTER).setBorderRight(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[1]).setTextAlignment(TextAlignment.CENTER).setBorderBottom(Border.NO_BORDER).setBorderTop(Border.NO_BORDER));
            table.addCell(new Cell().add(currentDateStr[2]).setTextAlignment(TextAlignment.CENTER).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER).setBorderRight(Border.NO_BORDER));
            dateTable.addCell(new Cell().add(table).setTextAlignment(TextAlignment.CENTER));
            dateTable.addCell(new Cell().add(new Paragraph(new Text("Place: ")).setTextAlignment(TextAlignment.CENTER)));
            dateTable.addCell(new Cell().add(new Paragraph(new Text(placeName)).setTextAlignment(TextAlignment.CENTER)));
            document.add(dateTable);

            if (isOmniDoc) {
                p = new Paragraph();
                p.add(imgChecked);
                if (countryCode.equalsIgnoreCase("91")) {
                    p.add(new Text("   Validated through the OTP sent to " + "registered mobile no."));
                    p.add(new Text(pdfUtility.maskMobileNumber(primaryMobileNo)).setBold());
                } else {
                    p.add(new Text("   Validated through the OTP sent to " + "registered email id."));
                }
                document.add(p.setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
            }

            Table grandFooterTable = pdfUtility.createFooter();
            PageSize ps = pdfDoc.getDefaultPageSize();

            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), ps.getWidth() - document.getLeftMargin() - document.getRightMargin());
            document.add(grandFooterTable);

            document.close();

            logger.info("Diabetes PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateDiabetesPDF: {}", ex);
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateCovidPDF(String applicationNumber, String nameOfLifeAssured, JsonObject medicalObj, boolean isOmniDoc, String placeName, String primaryMobileNo, JsonObject travelObj, String countryCode, JsonObject imagesJson, JsonObject contentJson) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(0f, 10f, 10f, 10f);

            String logoFilename = jsonUtility.getJsonKeyValue("covidsmallLogo", imagesJson);
            String logoBase64 =  pdfUtility.getImageAsBase64(logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            Table logoDetails = new Table(new float[]{800F, 900F});
            logoDetails.addCell(new Cell().add(img).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

            logoFilename = jsonUtility.getJsonKeyValue("covidlogo", imagesJson);
            logoBase64 =  pdfUtility.getImageAsBase64(logoFilename);
            img = pdfUtility.getPDFLogo(logoBase64);
            img.setHeight(150);
            Paragraph p = new Paragraph();
            p.add(img);
            p.setTextAlignment(TextAlignment.RIGHT);
            logoDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            document.add(logoDetails);

            String checkedLogo = jsonUtility.getJsonKeyValue("checkedLogo", imagesJson);
            String imgCheckBase64 = pdfUtility.getImageAsBase64(checkedLogo);
            String uncheckedLogo = jsonUtility.getJsonKeyValue("uncheckedLogo", imagesJson);
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(uncheckedLogo);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);
            imgUnchecked.setWidth(15);
            imgUnchecked.setHeight(16);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);
            imgChecked.setWidth(15);
            imgChecked.setHeight(16);


            Table table = new Table(1);
            table.setWidthPercent(100);
            Cell headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(255, 117, 0));
            p = new Paragraph("COVID-19 (Coronavirus) Exposure Questionnaire");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p.setPaddingLeft(10)).setBorder(Border.NO_BORDER);
            table.addCell(headingCell);

            Table applicationDetails = new Table(new float[]{250F, 400F, 250F, 400F});
            applicationDetails.addCell(new Cell(1, 4).add("").setBorder(Border.NO_BORDER));
            applicationDetails.addCell(new Cell().add(new Paragraph("Applicant's Name: ").setBold()).setBorder(Border.NO_BORDER));
            float[] pointColumnWidths = new float[nameOfLifeAssured.length()];
            for (int i = 0; i < nameOfLifeAssured.length(); i++) {
                pointColumnWidths[i] = 10F;
            }
            Table tableCodes = codeTable(nameOfLifeAssured, pointColumnWidths);
            applicationDetails.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            applicationDetails.addCell(new Cell().add(new Paragraph("Application Number: ").setBold()).setBorder(Border.NO_BORDER));
            pointColumnWidths = new float[applicationNumber.length()];
            for (int i = 0; i < applicationNumber.length(); i++) {
                pointColumnWidths[i] = 10F;
            }
            tableCodes = codeTable(applicationNumber, pointColumnWidths);
            applicationDetails.addCell(new Cell().add(tableCodes).setBorder(Border.NO_BORDER));
            applicationDetails.addCell(new Cell(1, 4).add("").setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(applicationDetails).setBorder(Border.NO_BORDER));
            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(255, 117, 0));
            p = new Paragraph("Please answer the following questions with as much detail as possible:");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p.setPaddingLeft(10)).setBorder(Border.NO_BORDER);
            table.addCell(headingCell);

            Table covidQuestions = new Table(new float[]{900F, 200F});
            covidQuestions.addCell(new Cell(1, 2).add("").setBorder(Border.NO_BORDER));
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("1. Are you, or have you been in close contact with anyone who has been quarantined or who has been diagnosed with novel coronavirus " + "(SARS-CoV-2/COVID-19) ? If yes, please provide details.")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject closeContactCovidObj = jsonUtility.getJsonObjectByKey("closeContactCovid", medicalObj);
            String closeContactStatus = jsonUtility.getJsonKeyValue("status", closeContactCovidObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (closeContactStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (closeContactStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (closeContactStatus.equalsIgnoreCase("y")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("details", closeContactCovidObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("2. Have you ever been quarantined due to a possible exposure to novel coronavirus (SARSCoV2/COVID-19)? If yes, please provide dates " + "and locations.")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject quarantinedCovidObj = jsonUtility.getJsonObjectByKey("quarantinedCovid", medicalObj);
            String quarantinedCovidStatus = jsonUtility.getJsonKeyValue("status", quarantinedCovidObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (quarantinedCovidStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (quarantinedCovidStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (quarantinedCovidStatus.equalsIgnoreCase("y")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("details", quarantinedCovidObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("3. Have you been advised to be tested to rule in, or rule out, a diagnosis of novel coronavirus (SARSCoV-2/COVID-19)? Or, are you awaiting " + "the result of a test which has already been submitted for the novel coronavirus (SARS-CoV-2/COVID-19)?")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject ruleInOutCovidObj = jsonUtility.getJsonObjectByKey("ruleInOutCovid", medicalObj);
            String ruleInOutCovidStatus = jsonUtility.getJsonKeyValue("status", ruleInOutCovidObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (ruleInOutCovidStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (ruleInOutCovidStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (ruleInOutCovidStatus.equalsIgnoreCase("y")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("details", ruleInOutCovidObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("4. Have you ever tested positive for the novel coronavirus (SARS-CoV-2/COVID-19)? If yes, provide the date of positive diagnosis")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject testedPositiveCovidObj = jsonUtility.getJsonObjectByKey("testedPositiveCovid", medicalObj);
            String testedPositiveCovidStatus = jsonUtility.getJsonKeyValue("status", testedPositiveCovidObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (testedPositiveCovidStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (testedPositiveCovidStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (testedPositiveCovidStatus.equalsIgnoreCase("y")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("details", testedPositiveCovidObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("5. Have you experienced any of the following symptoms within the last 14 days?")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject symptomsExperiencedObj = jsonUtility.getJsonObjectByKey("symptomsExperienced", medicalObj);
            String symptomsExperiencedStatus = jsonUtility.getJsonKeyValue("status", symptomsExperiencedObj);
            JsonObject symptomsObj = jsonUtility.getJsonObjectByKey("symptoms", symptomsExperiencedObj);
            p = new Paragraph();
            if (symptomsExperiencedStatus.equalsIgnoreCase("y")) {
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("anyFever", symptomsObj))) {
                    p.add("• Any fever ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("cough", symptomsObj))) {
                    p.add("• Cough ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("shortBreath", symptomsObj))) {
                    p.add("• Shortness of breath ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("malaise", symptomsObj))) {
                    p.add("• Malaise (flu-like tiredness) ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("rhinorrhea", symptomsObj))) {
                    p.add("• Rhinorrhea (mucus discharge from the nose) ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("soreThroat", symptomsObj))) {
                    p.add("• Sore throat ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("gastro", symptomsObj))) {
                    p.add("• Gastro-intestinal symptoms such as nausea, vomiting and/or diarrhea");
                }
            } else {
                p = new Paragraph("• Any fever • Cough • Shortness of breath • Malaise (flu-like tiredness) • Rhinorrhea (mucus discharge from the nose) • Sore throat • Gastro-intestinal " + "symptoms such as nausea, vomiting and/or diarrhea");
            }
            p.add(". If yes, to any of these, please indicate which and provide full information.");
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (symptomsExperiencedStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (symptomsExperiencedStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (symptomsExperiencedStatus.equalsIgnoreCase("y")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("symptomsExperiencedDetails", symptomsExperiencedObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("6. Travel Declaration")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("a. Please provide your travel patterns over the past 14 days:")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            JsonObject travelledForeignObj = jsonUtility.getJsonObjectByKey("travelledForeign", travelObj);

            String countryPast = jsonUtility.getJsonKeyValue("country", travelledForeignObj);
            String cityPast = jsonUtility.getJsonKeyValue("cityName", travelledForeignObj);
            String dateArrivedPast = jsonUtility.getJsonKeyValue("dateArrived", travelledForeignObj);
            String dateDepartedPast = jsonUtility.getJsonKeyValue("dateDeparted", travelledForeignObj);

            Table t1 = new Table(new float[]{300F, 300F, 300F, 300F});
            t1.setTextAlignment(TextAlignment.CENTER);
            p = new Paragraph("COUNTRY");
            t1.addCell(p);
            p = new Paragraph("CITY");
            t1.addCell(p);
            p = new Paragraph("DATE ARRIVED");
            t1.addCell(p);
            p = new Paragraph("DATE DEPARTED");
            t1.addCell(p);
            p = new Paragraph(countryPast);
            t1.addCell(p);
            p = new Paragraph(cityPast);
            t1.addCell(p);
            p = new Paragraph(dateArrivedPast);
            t1.addCell(p);
            p = new Paragraph(dateDepartedPast);
            t1.addCell(p);
            covidQuestions.addCell(new Cell(1, 2).add(t1).setBorder(Border.NO_BORDER));
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("b. Please detail your intended future travel plans for the next 30 days:")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            JsonObject futureTravelPlansObj = jsonUtility.getJsonObjectByKey("futureTravelPlans", travelObj);

            String countryFuture = jsonUtility.getJsonKeyValue("country", futureTravelPlansObj);
            String cityFuture = jsonUtility.getJsonKeyValue("cityName", futureTravelPlansObj);
            String dateArrivedFuture = jsonUtility.getJsonKeyValue("dateArrived", futureTravelPlansObj);
            String intendedDuration = jsonUtility.getJsonKeyValue("intendedDuration", futureTravelPlansObj);

            t1 = new Table(new float[]{300F, 300F, 300F, 300F});
            t1.setTextAlignment(TextAlignment.CENTER);
            p = new Paragraph("COUNTRY");
            t1.addCell(p);
            p = new Paragraph("CITY");
            t1.addCell(p);
            p = new Paragraph("DATE ARRIVED");
            t1.addCell(p);
            p = new Paragraph("INTENDED DURATION");
            t1.addCell(p);
            p = new Paragraph(countryFuture);
            t1.addCell(p);
            p = new Paragraph(cityFuture);
            t1.addCell(p);
            p = new Paragraph(dateArrivedFuture);
            t1.addCell(p);
            p = new Paragraph(intendedDuration);
            t1.addCell(p);
            covidQuestions.addCell(new Cell(1, 2).add(t1).setBorder(Border.NO_BORDER));

            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("7. Are you currently in good health?")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject currentHealthObj = jsonUtility.getJsonObjectByKey("currentHealth", travelObj);
            String currentHealthStatus = jsonUtility.getJsonKeyValue("status", currentHealthObj);

            p = new Paragraph();
            p.add("  Yes  ");
            if (currentHealthStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (currentHealthStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (currentHealthStatus.equalsIgnoreCase("n")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("details", currentHealthObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("8. Does your occupation fall within any of the below mentioned? (If yes please provide details)")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject occupationObj = jsonUtility.getJsonObjectByKey("occupation", travelObj);
            JsonObject occupationsObject = jsonUtility.getJsonObjectByKey("occupations", occupationObj);
            String occupationStatus = jsonUtility.getJsonKeyValue("status", occupationObj);
            p = new Paragraph();
            if (occupationStatus.equalsIgnoreCase("y")) {
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("DoctorMedicalProfessional", occupationsObject))) {
                    p.add("• Doctor/ medical professional ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("nursingPersonnel", occupationsObject))) {
                    p.add("• Nursing personnel ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("pharmacist", occupationsObject))) {
                    p.add("• Pharmacist ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("Transport", occupationsObject))) {
                    p.add("• Transport work force personnel ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("policeMilitary", occupationsObject))) {
                    p.add("• Police/Military staff ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("pilots", occupationsObject))) {
                    p.add("• Pilots/ Cabin Crew personnel ");
                }
                if (Boolean.TRUE.equals(jsonUtility.getJsonKeyValueForBoolean("otherOcuupation", occupationsObject))) {
                    p.add("• Any other occupation which has higher exposure to a large population. ");
                }
            } else {
                p = new Paragraph("• Doctor/ medical professional • Nursing personnel • Pharmacist • Transport work force personnel • Police/Military staff • Pilots/ Cabin Crew personnel " + "• Any other occupation which has higher exposure to a large population. ");
            }
            p.add(" If yes, please specify.");
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph();
            p.add("  Yes  ");
            if (occupationStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (occupationStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (occupationStatus.equalsIgnoreCase("y")) {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("occupationsDetails", occupationObj)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            covidQuestions.addCell(new Cell(1, 2).add(new Paragraph("9. Have you been vaccinated?")).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject haveBeenVaccinateObj = jsonUtility.getJsonObjectByKey("haveBeenVaccinate", medicalObj);
            String haveBeenVaccinateStatus = jsonUtility.getJsonKeyValue("status", haveBeenVaccinateObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (haveBeenVaccinateStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (haveBeenVaccinateStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            covidQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(covidQuestions).setBorder(Border.NO_BORDER));

            String dateOf1stDose = jsonUtility.getJsonKeyValue("dateofFirstDose", haveBeenVaccinateObj);
            String dateOf2ndDose = jsonUtility.getJsonKeyValue("dateofSecondDose", haveBeenVaccinateObj);
            String vaccineName = jsonUtility.getJsonKeyValue("vaccine", haveBeenVaccinateObj);

            Table vaccinationDetails = new Table(new float[]{500F, 500F});
            vaccinationDetails.addCell(new Cell().add("Vaccination details"));
            vaccinationDetails.addCell(new Cell().add("Name of vaccine :  " + vaccineName));
            vaccinationDetails.addCell(new Cell().add("a. Date of 1st dose"));
            vaccinationDetails.addCell(new Cell().add(dateOf1stDose));
            vaccinationDetails.addCell(new Cell().add("b. Date of 2nd dose"));
            vaccinationDetails.addCell(new Cell().add(dateOf2ndDose));
            vaccinationDetails.addCell(new Cell().add("c. Any Adverse reaction except fever, body aches"));
            JsonObject feverAchesObj = jsonUtility.getJsonObjectByKey("feverAches", medicalObj);
            String feverAchesStatus = jsonUtility.getJsonKeyValue("status", feverAchesObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (feverAchesStatus.equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (feverAchesStatus.equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            vaccinationDetails.addCell(new Cell().add(p));
            vaccinationDetails.addCell(new Cell(1, 2).add("If response to question no. 9(c) is yes, please provide details:"));
            if (feverAchesStatus.equalsIgnoreCase("y")) {
                vaccinationDetails.addCell(new Cell(1, 2).add(jsonUtility.getJsonKeyValue("details", feverAchesObj)).setUnderline());
            } else {
                vaccinationDetails.addCell(new Cell(1, 2).add("").setUnderline());
            }
            table.addCell(new Cell().add(vaccinationDetails).setBorder(Border.NO_BORDER));

//            p = new Paragraph(jsonUtility.getJsonKeyValue("details", feverAchesObj));
//            p.setUnderline();
//            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            headingCell = new Cell();
            headingCell.setBackgroundColor(new DeviceRgb(255, 117, 0));
            p = new Paragraph("Declaration");
            p.setBold();
            p.setFontKerning(FontKerning.YES);
            p.setFontColor(Color.WHITE);
            headingCell.add(p.setPaddingLeft(10)).setBorder(Border.NO_BORDER);
            table.addCell(headingCell);

            String applicantSignature = "    ";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date d = new Date();

            p = new Paragraph();
            p.add(imgChecked);
            p.add(jsonUtility.getJsonKeyValue("declarationContent1", contentJson));
            p.add("\n");
            p.add("Place: ");
            p.add(new Text(placeName).setUnderline().setBold());
            p.add("     Date: ");
            p.add(new Text(sdf.format(d)).setUnderline().setBold());
            p.add("\n\n");
            p.add(new Text(applicantSignature).setUnderline());
            p.add("\n\n");
            p.add("Applicant Signature");
            table.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            document.add(table);
            p = new Paragraph();
            p.add("OTP Verified    ");
            if (isOmniDoc) {

                p.add(imgChecked);
//                if (countryCode.equalsIgnoreCase("91")) {
//                    p.add(new Text("   Validated through the OTP sent to " + "registered mobile no."));
//                    p.add(new Text(onlineUtility.maskMobileNumber(primaryMobileNo)).setBold());
//                } else {
//                    p.add(new Text("   Validated through the OTP sent to " + "registered email id."));
//                }
//                document.add(p.setTextAlignment(TextAlignment.CENTER));
//                document.add(new Paragraph("\n"));
//                document.add(new Paragraph("***This is OTP Verified***").setTextAlignment(TextAlignment.CENTER));
//                document.add(new Paragraph("\n"));
            }else {
                p.add(imgUnchecked);

            }
            document.add(p);
            Table grandFooterTable = pdfUtility.createFooter();
            PageSize ps = pdfDoc.getDefaultPageSize();

            grandFooterTable.setFixedPosition(document.getLeftMargin(), document.getBottomMargin(), ps.getWidth() - document.getLeftMargin() - document.getRightMargin());
            document.add(grandFooterTable);
            document.close();
            logger.info("Covid PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception e) {
            logger.info("Error in generateCovidPDF: {}", e);
        }
        return bytesPdf;
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

}
