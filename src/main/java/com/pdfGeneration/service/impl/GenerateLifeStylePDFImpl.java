package com.pdfGeneration.service.impl;

import com.google.gson.JsonObject;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.pdfGeneration.constants.PdfConstant;
import com.pdfGeneration.service.GenerateLifeStylePDF;
import com.pdfGeneration.utility.JsonUtility;
import com.pdfGeneration.utility.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GenerateLifeStylePDFImpl implements GenerateLifeStylePDF {

    private final Logger logger = LoggerFactory.getLogger(GenerateLifeStylePDFImpl.class);

    @Autowired
    private PDFUtility pdfUtility;

    @Autowired
    private JsonUtility jsonUtility;

//   @Autowired
//    private OnlineUtility onlineUtility;

    @Override
    public byte[] generateDivingPDF(String applicationNumber, String nameOfLifeAssured, JsonObject divingObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "diving.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(12);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph("\n"));

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
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


            Table divingDetails = new Table(new float[]{800F, 200F});
            divingDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. Please specify type of diving:");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            JsonObject typeOfDiving = jsonUtility.getJsonObjectByKey("typeOfDiving", divingObj);
            if (jsonUtility.getJsonKeyValue("scubaDiving", typeOfDiving).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Scuba diving    ");
            if (jsonUtility.getJsonKeyValue("snorkelDiving", typeOfDiving).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Snorkel diving    ");
            if (jsonUtility.getJsonKeyValue("commercialDiving", typeOfDiving).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Commercial diving    ");
            p.add("\n");
            if (jsonUtility.getJsonKeyValue("otherTypeOfDiving", typeOfDiving).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Other - please provide details");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            divingDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("2. How long have you been diving?\n");
            p.add(new Text(jsonUtility.getJsonKeyValue("howLongYouBeenDiving", divingObj)).setUnderline());
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("3. Which qualifications and certifications do you hold? E.g. CMAS, NAUI, PADI, BSAC");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("divingQualificationAndCertification", divingObj)).setUnderline()).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject areYouMemberOfClub = jsonUtility.getJsonObjectByKey("areYouMemberOfClub", divingObj);
            p = new Paragraph("4. Are you a member of a club?\n");
            if (jsonUtility.getJsonKeyValue("status", areYouMemberOfClub).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", areYouMemberOfClub).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - please state the name of the club      ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", areYouMemberOfClub).equalsIgnoreCase("y")) {
                divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("areYouMemberOfClubDetails", areYouMemberOfClub)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                divingDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("5. When did you perform your last dive?        ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(new Text(jsonUtility.getJsonKeyValue("lastDivePerform", divingObj)).setUnderline());
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("6. How deep (in meters) do you usually dive?        ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(new Text(jsonUtility.getJsonKeyValue("howDeepYouDive", divingObj)).setUnderline());
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));


            p = new Paragraph("7. What is the maximum depth you dive to?        ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add(new Text(jsonUtility.getJsonKeyValue("maximumDepthYouDive", divingObj)).setUnderline());
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));


            p = new Paragraph("8. Where do you dive? (e.g. caves, potholes, sinkholes, wrecks, ocean, deep sea, fresh water)        ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("whereDoYouDive", divingObj)).setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject doYouDiveAlone = jsonUtility.getJsonObjectByKey("doYouDiveAlone", divingObj);
            p = new Paragraph("9. Do you dive alone? \n");
            if (jsonUtility.getJsonKeyValue("status", doYouDiveAlone).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", doYouDiveAlone).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - please state the name of the club      ");
            p.add("\n");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", doYouDiveAlone).equalsIgnoreCase("y")) {
                divingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("doYouDiveAloneDetails", doYouDiveAlone)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                divingDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            p = new Paragraph("10. How of ten do you dive?");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("howOftenYouDive", divingObj)).setUnderline()).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject doYouParticipateIn = jsonUtility.getJsonObjectByKey("doYouParticipateIn", divingObj);
            p = new Paragraph("11. Do you participate in any competitions, record attempts or experimental diving?\n");
            if (jsonUtility.getJsonKeyValue("status", doYouParticipateIn).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", doYouParticipateIn).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - please state the name of the club      ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", doYouParticipateIn).equalsIgnoreCase("y")) {
                divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("doYouParticipateInDetails", doYouParticipateIn)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                divingDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


            JsonObject doYouSufferedMedicalComplication = jsonUtility.getJsonObjectByKey("doYouSufferedMedicalComplication", divingObj);
            p = new Paragraph("12. Have you ever suffered any medical complications as a result of diving, such as decompression sickness?\n");
            if (jsonUtility.getJsonKeyValue("status", doYouSufferedMedicalComplication).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", doYouSufferedMedicalComplication).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - please provide details and dates      ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", doYouSufferedMedicalComplication).equalsIgnoreCase("y")) {
                divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("doYouSufferedMedicalComplicationDetails", doYouSufferedMedicalComplication)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                divingDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject doYouHyperventilate = jsonUtility.getJsonObjectByKey("doYouHyperventilate", divingObj);
            p = new Paragraph("13. Do you ever hyperventilate when diving?\n");
            if (jsonUtility.getJsonKeyValue("status", doYouHyperventilate).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", doYouHyperventilate).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - have you suffered a blackout?      ");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("14. What type of equipment do you use (e.g. aqua lung)?");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("typeOfEquipmentYouUse", divingObj)).setUnderline()).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            p = new Paragraph("When this equipment was last checked?");
            divingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            divingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("equipmentLastChecked", divingObj)).setUnderline()).setBorder(Border.NO_BORDER));

            document.add(divingDetails);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("For Commercial Divers add:").setBold());
            document.add(new Paragraph("\n"));

            JsonObject commercialDivingDetails = jsonUtility.getJsonObjectByKey("commercialDivingDetails", divingObj);
            Table commericalDrivers = new Table(new float[]{800F, 200F});
            commericalDrivers.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. Please state name of employer");
            commericalDrivers.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("nameOfEmployer", commercialDivingDetails)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("2. Give a brief description of your occupational duties, giving details of activities such as exploration, salvage, harbor installations, inshore clearance,\n" + "offshore oil rigs, fish farming, hell fishing.");
            commericalDrivers.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("briefDescriptionOfDuties", commercialDivingDetails)).setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject doYouEverDiveWithExplosives = jsonUtility.getJsonObjectByKey("doYouEverDiveWithExplosives", commercialDivingDetails);
            p = new Paragraph("3. Do you ever dive with explosives? \n");
            if (jsonUtility.getJsonKeyValue("status", doYouEverDiveWithExplosives).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", doYouEverDiveWithExplosives).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes       ");
            p.add("\n");
            commericalDrivers.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            JsonObject doYouExpectChangeInDuties = jsonUtility.getJsonObjectByKey("doYouExpectChangeInDuties", commercialDivingDetails);
            p = new Paragraph("4. Do you expect your duties to change in the future?\n");
            if (jsonUtility.getJsonKeyValue("status", doYouExpectChangeInDuties).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", doYouExpectChangeInDuties).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - please provide details      ");
            commericalDrivers.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", doYouExpectChangeInDuties).equalsIgnoreCase("y")) {
                commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("doYouExpectChangeInDutiesDetails", doYouExpectChangeInDuties)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject haveYouEverUnableToDive = jsonUtility.getJsonObjectByKey("haveYouEverUnableToDive", commercialDivingDetails);
            p = new Paragraph("5. Have you ever been unable to dive?\n");
            if (jsonUtility.getJsonKeyValue("status", haveYouEverUnableToDive).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      \n");
            if (jsonUtility.getJsonKeyValue("status", haveYouEverUnableToDive).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Yes - please provide details      ");
            commericalDrivers.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", haveYouEverUnableToDive).equalsIgnoreCase("y")) {
                commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("haveYouEverUnableToDiveDetails", haveYouEverUnableToDive)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("6. Date of last dive medical\n");
            commericalDrivers.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            commericalDrivers.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("dateOfLastDiveMedical", commercialDivingDetails)).setUnderline()).setBorder(Border.NO_BORDER));

            document.add(commericalDrivers);

            p = new Paragraph();
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            pointColumnWidths = new float[]{250F, 180F, 280F};
            table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of the Life to be assured/Proposer").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
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

            logger.info("Diving PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateDivingPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateDrivingPDF(String applicationNumber, String nameOfLifeAssured, JsonObject drivingObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "driving.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(12);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph("\n"));

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            Paragraph p = new Paragraph();
            p.add("Application No:   ");
            p.add(new Text(applicationNumber).setUnderline());
            document.add(p);

            p = new Paragraph();
            p.add("Full Name of life to be assured:      ");
            p.add(new Text(nameOfLifeAssured).setUnderline());
            document.add(p);
            document.add(new Paragraph(""));

            Table drivingDetails = new Table(new float[]{800F, 210F});
            drivingDetails.setBorder(Border.NO_BORDER);

            p = new Paragraph("1. Please mention which of the below vehicle which you Drive");
            drivingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell().add("Truck/ Lorry").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveTruck", drivingObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveTruck", drivingObj)).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell().add("Bus").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveBus", drivingObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveBus", drivingObj)).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell().add("Tempo").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveTempo", drivingObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveTempo", drivingObj)).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell().add("Car").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveCar", drivingObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouDriveCar", drivingObj)).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            JsonObject doYouDriveOtherVehicle = jsonUtility.getJsonObjectByKey("doYouDriveOtherVehicle", drivingObj);
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell().add("Others (Please mention Details)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", doYouDriveOtherVehicle).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", doYouDriveOtherVehicle).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", doYouDriveOtherVehicle).equalsIgnoreCase("y")) {
                drivingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("doYouDriveOtherVehicleDetails", doYouDriveOtherVehicle)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                drivingDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("2. Please mention the approx no of hours spent in driving per day");
            drivingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("hoursDrivingPerDay", drivingObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("3. Please mention the approx distance travelled per day");
            drivingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("distanceTravelledPerDay", drivingObj)).setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject doYouCarryHazardsGood = jsonUtility.getJsonObjectByKey("doYouCarryHazardsGood", drivingObj);
            p = new Paragraph("4. Do you carry any hazardous good\n" + "(If yes please mention details of goods carried)");
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", doYouCarryHazardsGood).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", doYouCarryHazardsGood).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", doYouCarryHazardsGood).equalsIgnoreCase("y")) {
                drivingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("doYouCarryHazardsGoodDetails", doYouCarryHazardsGood)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                drivingDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("5. Do you consume alcohol, tobacco or any narcotic drugs");
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouConsumeDrugs", drivingObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("doYouConsumeDrugs", drivingObj)).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            //   drivingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            //   drivingDetails.addCell(new Cell(1, 2).add(new Paragraph("ewingfoiwergnoiweengwoeignweoirgnwoegnweoignwoeignowe").setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject hasYourHealthEffected = jsonUtility.getJsonObjectByKey("hasYourHealthEffected", drivingObj);
            p = new Paragraph("6. Has your health ever been effected ever by the nature of the work you do\n (If yes please state the health problem faced by you)");
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", hasYourHealthEffected).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No      ");
            if (jsonUtility.getJsonKeyValue("status", hasYourHealthEffected).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            drivingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", hasYourHealthEffected).equalsIgnoreCase("y")) {
                drivingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("hasYourHealthEffectedDetails", hasYourHealthEffected)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                drivingDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("7. What is your usual route of travel; please give full details (All places traveled).");
            drivingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            drivingDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("detailsOfRouteTravel", drivingObj)).setUnderline()).setBorder(Border.NO_BORDER));

            document.add(drivingDetails);

            p = new Paragraph();
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            pointColumnWidths = new float[]{250F, 180F, 280F};
            Table table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of the Life to be assured/Proposer").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            document.add(table);

            document.add(new Paragraph(""));
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
            logger.info("Driving PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateDrivingPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateAviationPDF(String applicationNumber, String nameOfLifeAssured, JsonObject aviationObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "aviation.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph("\n"));

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
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

            p = new Paragraph();
            p.add(new Text("Applies to: "));
            document.add(p);

            p = new Paragraph();
            p.add("* Pilots, crew or passengers in respect of aviation other than as a fare-paying passenger on scheduled flights and recognized routes.");
            p.add("\n");
            p.add("* Flights by airplane, helicopter, balloon and airship.");
            document.add(p);

            JsonObject flownAsPilot = jsonUtility.getJsonObjectByKey("flownAsPilot", aviationObj);
            Table aviationDetails = new Table(new float[]{800F, 200F});
            aviationDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. Have you ever flown as a pilot?");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", flownAsPilot).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", flownAsPilot).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("a. What type of license do you hold")).setBorder(Border.NO_BORDER));
            String typeOfLicense = jsonUtility.getJsonKeyValue("typeOfLicenseDetails", flownAsPilot);
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(typeOfLicense).setUnderline()).setBorder(Border.NO_BORDER));

            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("b. Which types of aircraft are you authorized to fly?")).setBorder(Border.NO_BORDER));
            String typesOfAircraft = jsonUtility.getJsonKeyValue("typeOfAircraftDetails", flownAsPilot);
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(typesOfAircraft).setUnderline()).setBorder(Border.NO_BORDER));

            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("c. When did you learn to fly?")).setBorder(Border.NO_BORDER));
            String learnToFly = jsonUtility.getJsonKeyValue("dateToFly", jsonUtility.getJsonObjectByKey("aviationQuestionsFormData", aviationObj));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(learnToFly).setUnderline()).setBorder(Border.NO_BORDER));

            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("d. How many hours flying as a pilot have you completed:")).setBorder(Border.NO_BORDER));
            Table hoursDetails = new Table(new float[]{300F, 300F});
            hoursDetails.addCell(new Cell().add("Till date:").setBorder(Border.NO_BORDER));
            hoursDetails.addCell(new Cell().add("In the last 12 months?").setBorder(Border.NO_BORDER));
            String hoursTilLDate = jsonUtility.getJsonKeyValue("hrsOFFlyingTillDate", flownAsPilot);
            String last12Months = jsonUtility.getJsonKeyValue("hrsOFFlyingMonths", flownAsPilot);
            hoursDetails.addCell(new Cell().add(new Paragraph(hoursTilLDate).setUnderline()).setBorder(Border.NO_BORDER));
            hoursDetails.addCell(new Cell().add(new Paragraph(last12Months).setUnderline()).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(hoursDetails).setBorder(Border.NO_BORDER));

            JsonObject flyingAccident = jsonUtility.getJsonObjectByKey("flyingAccident", aviationObj);
            p = new Paragraph("e. Have you been involved in any flying accidents?\n" + "If YES, please provide details.");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", flyingAccident).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", flyingAccident).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", flyingAccident).equalsIgnoreCase("y")) {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("flyingACC", flyingAccident)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject licenseRevokedorGrounded = jsonUtility.getJsonObjectByKey("licenseRevokedorGrounded", aviationObj);
            p = new Paragraph("f. Have you ever had your license revoked or been grounded?`\n" + "If YES, please provide details.");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", licenseRevokedorGrounded).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", licenseRevokedorGrounded).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", licenseRevokedorGrounded).equalsIgnoreCase("y")) {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("licenseRevokedorGroundedDetails", licenseRevokedorGrounded)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("2. Please provide details of the nature of your intended flying,including:");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("a. The type of aircraft (make,model name and number).");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("typeOfAircraft", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("b. Number of hours as a pilot.");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("hrsOfPilot", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("c. Number of hours as a passenger.");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("hrsAsPassenger", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("d. Purpose e.g. pleasure, business, air taxi, as instructor.");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("flyingPurpose", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("e. Who owns the aircraft and does the owner hold an Air Operator's Certificate?");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("whoOwnsAircraft", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("f. Who maintains the aircraft?");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("whoMaintainsAircraft", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("g. Where do you intend to fly? ie starting points and destinations");
            aviationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("destinationToFly", aviationObj)).setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject licensedAirfield = jsonUtility.getJsonObjectByKey("licensedAirfield", aviationObj);
            p = new Paragraph("h. Will flights be between licensed airfields?\n" + "If No, please provide details.");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", licensedAirfield).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", licensedAirfield).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", licensedAirfield).equalsIgnoreCase("n")) {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("licensedAirfieldDetails", licensedAirfield)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject airCompetitions = jsonUtility.getJsonObjectByKey("airCompetitions", aviationObj);
            p = new Paragraph("i. Do you intend to participate in air competitions of any kind, formula air racing, exhibitions, aerobatics or stunt flying\n" + "If YES, please provide details.");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", airCompetitions).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", airCompetitions).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", airCompetitions).equalsIgnoreCase("y")) {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("airCompetitionsDetails", airCompetitions)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject lowLevelFlying = jsonUtility.getJsonObjectByKey("lowLevelFlying", aviationObj);
            p = new Paragraph("J. Do you intend to undertake any low-level or specialised flying or manoeuvring? eg crop spraying, inspection?\n" + "If YES, please provide details");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", lowLevelFlying).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", lowLevelFlying).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", lowLevelFlying).equalsIgnoreCase("y")) {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("lowLevelFlyingDetails", lowLevelFlying)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject testPilot = jsonUtility.getJsonObjectByKey("testPilot", aviationObj);
            p = new Paragraph("k. Do you intend to fly as a test pilot?\n" + "If YES,please state:\n" + "The name of your employer");
            aviationDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", testPilot).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", testPilot).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            aviationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", testPilot).equalsIgnoreCase("y")) {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("testPilotName", testPilot)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph("Whether the aircraft are prototypes, new, reconditioned, etc")).setBorder(Border.NO_BORDER));
            aviationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("testPilotAircraftType", testPilot)).setUnderline()).setBorder(Border.NO_BORDER));

            document.add(aviationDetails);

            p = new Paragraph();
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            pointColumnWidths = new float[]{250F, 180F, 280F};
            table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(50).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of the Life to be assured/Proposer").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
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

            logger.info("Aviation PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateAviationPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateFishingPDF(String applicationNumber, String nameOfLifeAssured, JsonObject fishingObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "fishing.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(12);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            document.add(new Paragraph("\n"));

            float[] pointColumnWidths = new float[]{120F, 280F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            Paragraph p = new Paragraph();
            p.add("Application No:   ");
            p.add(new Text(applicationNumber).setUnderline());
            document.add(p);

            p = new Paragraph();
            p.add("Full Name of life to be assured:      ");
            p.add(new Text(nameOfLifeAssured).setUnderline());
            document.add(p);

            JsonObject fishingObject = jsonUtility.getJsonObjectByKey("occupationSea", fishingObj);
            Table fishingDetails = new Table(new float[]{750F, 450F});
            fishingDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. Does your Occupation Involve going to Sea  ");
            //p.add(new Text(occupationDetails).setUnderline());
            p.add("\n");
            p.add("(Or is it likely to do so in Future, If Yes Please complete the rest of Questionnaire)");
            fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("     Yes     ");
            if (jsonUtility.getJsonKeyValue("status", fishingObject).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     No     ");
            if (jsonUtility.getJsonKeyValue("status", fishingObject).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));

            if (jsonUtility.getJsonKeyValue("status", fishingObject).equalsIgnoreCase("y")) {
                p = new Paragraph("2. What is the tonnage of vessel in which you work  \n");
                p.add(new Text(jsonUtility.getJsonKeyValue("fishingTonnage", fishingObject)).setUnderline());
                p.add(" tones/kgs");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

                p = new Paragraph("3. What is the length of vessel on which you work [tick (v) wherever applicable]");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("< 24m (80 ft)    ");
                if (jsonUtility.getBooleanKeyValue("length24m", fishingObject)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     24 - 40m ( 80 - 130ft)    ");
                if (jsonUtility.getBooleanKeyValue("length24_40m", fishingObject)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     > 40m (130ft)    ");
                if (jsonUtility.getBooleanKeyValue("length40m", fishingObject)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


                p = new Paragraph("4. Which of the following 2 groups your duties fall into?\n");
                fishingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("Group A - Skipper/Officer, Mate (Second Hand), Engineer Fireman, Greaser, Mechanic, Stoker, Cook, Galley hand Radio officer.         ");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                if (jsonUtility.getBooleanKeyValue("FishingDutyGroupA", fishingObject)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
                p = new Paragraph("Group B - Deckhand/Fisherman, Spare hand, Bosun (Third hand) Trainee fisherman/deckhand");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                if (jsonUtility.getBooleanKeyValue("FishingDutyGroupB", fishingObject)) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));

                JsonObject accidentOnDuty = jsonUtility.getJsonObjectByKey("accidentOnDuty", fishingObj);
                p = new Paragraph("5. Have you had any accidents or Illness associated with your duties \n(If Yes please give the details of the same) ");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("     Yes     ");
                if (jsonUtility.getJsonKeyValue("status", accidentOnDuty).equalsIgnoreCase("y")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                p.add("     No     ");
                if (jsonUtility.getJsonKeyValue("status", accidentOnDuty).equalsIgnoreCase("n")) {
                    p.add(imgChecked);
                } else {
                    p.add(imgUnchecked);
                }
                fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
                if (jsonUtility.getJsonKeyValue("status", accidentOnDuty).equalsIgnoreCase("y")) {
                    fishingDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("accidentOnDutyDetails", accidentOnDuty)).setUnderline()).setBorder(Border.NO_BORDER));
                } else {
                    fishingDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
                }
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            } else {
                p = new Paragraph("2. What is the tonnage of vessel in which you work  ");
                p.add(new Text("").setUnderline());
                p.add(" tones/kgs");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

                p = new Paragraph("3. What is the length of vessel on which you work [tick (v) wherever applicable]");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("< 24m (80 ft)    ");
                p.add(imgUnchecked);
                p.add("     24 - 40m ( 80 - 130ft)    ");
                p.add(imgUnchecked);
                p.add("     > 40m (130ft)    ");
                p.add(imgUnchecked);
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                fishingDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


                p = new Paragraph("4. Which of the following 2 groups your duties fall into?\n");
                fishingDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("Group A - Skipper/Officer, Mate (Second Hand), Engineer Fireman, Greaser, Mechanic, Stoker, Cook, Galley hand Radio officer.         ");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add(imgUnchecked);
                fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
                p = new Paragraph("Group B - Deckhand/Fisherman, Spare hand, Bosun (Third hand) Trainee ﬁsherman/deckhand");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add(imgUnchecked);
                fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));

                p = new Paragraph("5. Have you had any accidents or Illness associated with your duties \n(If Yes please give the details of the same) ");
                fishingDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
                p = new Paragraph();
                p.add("     Yes     ");
                p.add(imgUnchecked);
                p.add("     No     ");
                p.add(imgUnchecked);
                fishingDetails.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            }

            document.add(fishingDetails);

            document.add(new Paragraph("\n"));
            p = new Paragraph();
            p.add(new Text("I here by agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            document.add(new AreaBreak());

            pointColumnWidths = new float[]{250F, 180F, 280F};
            Table table = new Table(pointColumnWidths);
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(25).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(15).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add("Signature of the Life to be assured/Proposer").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().setMinHeight(10).setBorder(Border.NO_BORDER));
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

            logger.info("Fishing PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateFishingPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateOccupationPDF(String applicationNumber, String nameOfLifeAssured, JsonObject occupationalObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "occupation.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

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

            Table occupationDetails = new Table(new float[]{800F, 200F});
            occupationDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. Name of the Employer");
            occupationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("employerName", occupationalObj)).setUnderline()).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


            p = new Paragraph("2. Industry in which working and experience in this industry.");
            occupationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("industryExperience", occupationalObj)).setUnderline()).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("3. Please mention the Exact daily duties performed in workplace");
            occupationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("dailyDuties", occupationalObj)).setUnderline()).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject operateMachine = jsonUtility.getJsonObjectByKey("operateMachine", occupationalObj);
            p = new Paragraph("4. Does your job require you to operate any machinery, Boiler, Furnace, Crane? (If yes please provide details of the same.)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", operateMachine).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", operateMachine).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", operateMachine).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("operateMachineDetails", operateMachine)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject drivingRequired = jsonUtility.getJsonObjectByKey("drivingRequired", occupationalObj);
            p = new Paragraph("5. Does your job requires you to drive vehicles\n" + "(If yes please provide the details of the vehicles and the purpose for which it is used)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", drivingRequired).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", drivingRequired).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", drivingRequired).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("drivingRequiredDetails", drivingRequired)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject stayOutOfOffice = jsonUtility.getJsonObjectByKey("stayOutOfOffice", occupationalObj);
            p = new Paragraph("6. Does your job requires you to stay out of office\n" + "(If yes please provide the purpose of outdoor visit)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", stayOutOfOffice).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", stayOutOfOffice).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", stayOutOfOffice).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph("\n" + jsonUtility.getJsonKeyValue("stayOutOfOfficeDetails", stayOutOfOffice)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject workInHeights = jsonUtility.getJsonObjectByKey("workInHeights", occupationalObj);
            p = new Paragraph("7. Does your job requires you to work in Heights / Underground\n" + "(If yes please provide details and the Height / Depth at which the work is performed)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", workInHeights).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", workInHeights).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workInHeights).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("workInHeightsDetails", workInHeights)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject handleEquipment = jsonUtility.getJsonObjectByKey("handleEquipment", occupationalObj);
            p = new Paragraph("8. Does your job handling electrical equipment or high voltages\n" + "(If Yes state the maximum voltage generated and the nature of work and whether operated Directly or by remote control)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", handleEquipment).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", handleEquipment).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", handleEquipment).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("handleEquipmentDetails", handleEquipment)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("9. Please provide the percentage of manual intervention required to perform your work.");
            occupationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("manualInterventionPercentage", occupationalObj)).setUnderline()).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject healthEffects = jsonUtility.getJsonObjectByKey("healthEffects", occupationalObj);
            p = new Paragraph("10. Has the type of work you do ever effected your health. (If yes please give full details)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", healthEffects).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", healthEffects).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", healthEffects).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("healthEffectsDetails", healthEffects)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject liftingHeavyGoods = jsonUtility.getJsonObjectByKey("liftingHeavyGoods", occupationalObj);
            p = new Paragraph("11. Does your duty involve lifting or moving heavy Goods\n" + "If yes please give full details");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", liftingHeavyGoods).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", liftingHeavyGoods).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", liftingHeavyGoods).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("liftingHeavyGoodsDetails", liftingHeavyGoods)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject exposedToFumes = jsonUtility.getJsonObjectByKey("exposedToFumes", occupationalObj);
            p = new Paragraph("12. Do you handle or remain exposed to fumes, gases, acids, dyes,\n" + "(If yes please give full details)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", exposedToFumes).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", exposedToFumes).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", exposedToFumes).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("exposedToFumesDetails", exposedToFumes)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject carryExplosives = jsonUtility.getJsonObjectByKey("carryExplosives", occupationalObj);
            p = new Paragraph("13. Do you carry any explosives or supervise the work of per sons carrying explosives?\n" + "(If yes please give full details)");
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", carryExplosives).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", carryExplosives).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            occupationDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", carryExplosives).equalsIgnoreCase("y")) {
                occupationDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("carryExplosivesDetails", carryExplosives)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                occupationDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            occupationDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("14. Please state any other facts regarding your occupation, which you consider important");
            occupationDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            occupationDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("importantFacts", occupationalObj)).setUnderline()).setBorder(Border.NO_BORDER));

            document.add(occupationDetails);

            p = new Paragraph();
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            document.add(new Paragraph("\n\n\n"));
            pointColumnWidths = new float[]{60F, 70F};
            Table dateTable = new Table(pointColumnWidths);
            p = new Paragraph();
            p.add("Date:   ");
            dateTable.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER));

            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String[] currentDateStr = sdf.format(currentDate).split("/");

            pointColumnWidths = new float[]{20F, 20F, 20F};
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

            logger.info("Occupation PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateOccupationPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateMiningPDF(String applicationNumber, String nameOfLifeAssured, JsonObject miningObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "mining.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

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
                table.addCell(cellLeft).setBorder(Border.NO_BORDER);
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

            Table miningQuestions = new Table(new float[]{800F, 200F});
            miningQuestions.addCell(new Cell().add("1. Are you employed in Mining Industry?: ").setBorder(Border.NO_BORDER));
            JsonObject miningEmp = jsonUtility.getJsonObjectByKey("miningEmp", miningObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", miningEmp).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", miningEmp).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("(If yes please give the type of mine & the exact nature of your work)").setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            if (jsonUtility.getJsonKeyValue("status", miningEmp).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("miningEmpDetails", miningEmp)).setUnderline();
                miningQuestions.addCell(new Cell(1, 2).add(p.setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                miningQuestions.addCell(new Cell(1, 2).add("").setBorder(Border.NO_BORDER));
            }
//            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            miningQuestions.addCell(new Cell().add("2. What is your exact occupation? (If you are involved in more than one occupation, please state all your occupations.)").setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            p = new Paragraph("     " + jsonUtility.getJsonKeyValue("exactOccupation", miningObj));
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            miningQuestions.addCell(new Cell().add("3. Give a description of nature of work performed in your occupation.").setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            p = new Paragraph("      " +jsonUtility.getJsonKeyValue("workedOccupation", miningObj));
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject miningManual = jsonUtility.getJsonObjectByKey("miningManual", miningObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", miningManual).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", miningManual).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }

            miningQuestions.addCell(new Cell().add("4. Do you participate in any manual aspect of mining\n" + "Which of the following types of mining are you involved in?").setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (jsonUtility.getBooleanKeyValue("Coal", miningManual)) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  Coal  \n");
            if (jsonUtility.getBooleanKeyValue("Potashrocksaltgypsumtin", miningManual)) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  Potash, rock-salt, gypsum, tin   \n");
            if (jsonUtility.getBooleanKeyValue("Clayandstoneworking", miningManual)) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  Clay and stone working  \n");
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            miningQuestions.addCell(new Cell().add("5. Are you involved in open cast mining?").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("openCastMining", miningObj)).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", jsonUtility.getJsonObjectByKey("openCastMining", miningObj)).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            p = new Paragraph("6. What percentages of your duties are of a manual or physical nature?       ");
            p.add(new Text(jsonUtility.getJsonKeyValue("openCastMiningPercent", miningObj)).setUnderline());
            p.add(" %");
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject liftingMovingGoods = miningObj.get("liftingMovingGoods").getAsJsonObject();
            miningQuestions.addCell(new Cell().add("7. Does your duty involve:\n" + "(A) Lifting or moving heavy goods. If yes, please provide full details.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", liftingMovingGoods).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", liftingMovingGoods).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));

            if (jsonUtility.getJsonKeyValue("status", liftingMovingGoods).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("liftingMovingGoodsDetails", liftingMovingGoods));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject workingUndergroundOrHeight = jsonUtility.getJsonObjectByKey("workingUndergroundOrHeight", miningObj);
            miningQuestions.addCell(new Cell().add("(B) Working underground or at heights: (If yes please state the maximum height and\n" + "depth involved and equipment used to get to the height or depth)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", workingUndergroundOrHeight).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", workingUndergroundOrHeight).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workingUndergroundOrHeight).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("workingUndergroundOrHeightDetails", workingUndergroundOrHeight));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject workInvoleHighVoltage = jsonUtility.getJsonObjectByKey("workInvoleHighVoltage", miningObj);
            miningQuestions.addCell(new Cell().add("(C) High Voltages: (If yes please give details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", workInvoleHighVoltage).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", workInvoleHighVoltage).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workInvoleHighVoltage).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("workInvoleHighVoltageDetails", workInvoleHighVoltage));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject workingAroundFurnace = jsonUtility.getJsonObjectByKey("workingAroundFurnace", miningObj);
            miningQuestions.addCell(new Cell().add("(D) Working around Furnace: (If yes please give details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", workingAroundFurnace).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", workingAroundFurnace).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workingAroundFurnace).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("workingAroundFurnaceDetails", workingAroundFurnace));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


            JsonObject handleHeatedMotenMetal = jsonUtility.getJsonObjectByKey("handleHeatedMotenMetal", miningObj);
            miningQuestions.addCell(new Cell().add("(E) Do you handle heated or molten metal's or work around molten metals? (If yes please give details).").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", handleHeatedMotenMetal).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", handleHeatedMotenMetal).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", handleHeatedMotenMetal).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("handleHeatedMotenMetalDetails", handleHeatedMotenMetal));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject handleElectricEquip = jsonUtility.getJsonObjectByKey("handleElectricEquip", miningObj);
            miningQuestions.addCell(new Cell().add("(E) Do you handle electrical equipments?\n" + "(If so, state the nature of equipments, Voltage generated & nature of your work)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", handleElectricEquip).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", handleElectricEquip).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workingAroundFurnace).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("handleElectricEquipDetails", handleElectricEquip));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject exposeToChemicals = jsonUtility.getJsonObjectByKey("exposeToChemicals", miningObj);
            miningQuestions.addCell(new Cell().add("(F) Do you handle or remain exposed to fumes, gases, acids, dyes, or any other chemicals?\n" + "(If yes, please state which gas, acid, chemicals, dyes or nature of work)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", exposeToChemicals).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", exposeToChemicals).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", exposeToChemicals).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("exposeToChemicalsDetails", exposeToChemicals));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject handleCarryExplosives = jsonUtility.getJsonObjectByKey("handleCarryExplosives", miningObj);
            miningQuestions.addCell(new Cell().add("(G) Do you handle or carry explosives or super vise the work of persons carrying explosives?\n" + "(If yes, please give full details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", handleCarryExplosives).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", handleCarryExplosives).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", handleCarryExplosives).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("handleCarryExplosivesDetails", handleCarryExplosives));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject workingAffectedHealth = jsonUtility.getJsonObjectByKey("workingAffectedHealth", miningObj);
            miningQuestions.addCell(new Cell().add("8. Has the type of work you do ever effected your health? (If yes, please give full details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", workingAffectedHealth).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", workingAffectedHealth).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workingAffectedHealth).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("workingAffectedHealthDetails", workingAffectedHealth));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject respiratoryTreatment = jsonUtility.getJsonObjectByKey("respiratoryTreatment", miningObj);
            miningQuestions.addCell(new Cell().add("9. Have you ever had treatment for any respiratory complaint? If yes, give details.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", respiratoryTreatment).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", respiratoryTreatment).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", respiratoryTreatment).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("respiratoryTreatmentDetails", respiratoryTreatment));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject accidentOnDuty = jsonUtility.getJsonObjectByKey("accidentOnDuty", miningObj);
            miningQuestions.addCell(new Cell().add("10. Have you ever had an accident while performing the above duties? (If yes, please give full details)").setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", accidentOnDuty).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", accidentOnDuty).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", accidentOnDuty).equalsIgnoreCase("y")) {
                p = new Paragraph(jsonUtility.getJsonKeyValue("accidentOnDutyDetails", accidentOnDuty));
            } else {
                p = new Paragraph("");
            }
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


            miningQuestions.addCell(new Cell().add("11. What safety measures are available while you are at work?").setBorder(Border.NO_BORDER));
            p = new Paragraph();
//            p.add("  Yes  ");
//            p.add(imgChecked);
//            p.add("  No   ");
//            p.add(imgUnchecked);
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph(jsonUtility.getJsonKeyValue("safetyMeasures", miningObj));
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


            miningQuestions.addCell(new Cell().add("12. Please state any other facts regarding your occupation, which you consider important.").setBorder(Border.NO_BORDER));
            p = new Paragraph();
//            p.add("  Yes  ");
//            p.add(imgChecked);
//            p.add("  No   ");
//            p.add(imgUnchecked);
            miningQuestions.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph(jsonUtility.getJsonKeyValue("occupationDetails", miningObj));
            miningQuestions.addCell(new Cell().add(p.setUnderline()).setBorder(Border.NO_BORDER));
            miningQuestions.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            document.add(miningQuestions);

            p = new Paragraph();
            p.add(new Text("\n"));
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
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

            logger.info("Mining PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateMiningPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateOilRefineryPDF(String applicationNumber, String nameOfLifeAssured, JsonObject oilRefineryObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();

            String logoFilename = "oil.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

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

            Table oilRepositoryDetails = new Table(new float[]{800F, 200F});
            oilRepositoryDetails.setBorder(Border.NO_BORDER);
            p = new Paragraph("1. What is your exact occupation? (If you are involved in more than one occupation, please state all your occupations.)");
            oilRepositoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("exactOccupation", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("2. Give a description of nature of work performed in your occupation.");
            oilRepositoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("natureOfWork", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("3. Are you based offshore or do you expect to be based offshore in the future?");
            oilRepositoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("basedOffShore", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject rigsByHelicopter = jsonUtility.getJsonObjectByKey("rigsByHelicopter", oilRefineryObj);
            p = new Paragraph("4. Do you ever travel to and from rigs by helicopter?");
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", rigsByHelicopter).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No  ");
            if (jsonUtility.getJsonKeyValue("status", rigsByHelicopter).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("rigsByHelicopterDetails", rigsByHelicopter)).setUnderline()).setBorder(Border.NO_BORDER));

            p = new Paragraph("5. What percentages of your duties are of a manual or physical nature?");
            oilRepositoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("dutyPercentage", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject liftingOfHeavyWeights = jsonUtility.getJsonObjectByKey("liftingOfHeavyWeights", oilRefineryObj);
            p = new Paragraph("6. Does your duty involve:\n");
            p.add("(a) Lifting or moving heavy goods.");
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", liftingOfHeavyWeights).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", liftingOfHeavyWeights).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("liftingOfHeavyWeightsDetails", liftingOfHeavyWeights)).setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject workingUndergroundOrHeights = jsonUtility.getJsonObjectByKey("workingUndergroundOrHeights", oilRefineryObj);
            p = new Paragraph("(b) Working underground or at heights:\n" + "(If yes please state the maximum height a nd depth involved and equipment used to get to the height or depth)");
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", workingUndergroundOrHeights).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", workingUndergroundOrHeights).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", workingUndergroundOrHeights).equalsIgnoreCase("y")) {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("workingUndergroundOrHeightsDetails", workingUndergroundOrHeights)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject involveHighVoltage = jsonUtility.getJsonObjectByKey("involveHighVoltage", oilRefineryObj);
            p = new Paragraph("(c) High Voltages: (If yes please give details)");
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", involveHighVoltage).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", involveHighVoltage).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", involveHighVoltage).equalsIgnoreCase("y")) {
                oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("involveHighVoltageDetails", involveHighVoltage)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                oilRepositoryDetails.addCell(new Cell().add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            JsonObject involveHighTemperature = jsonUtility.getJsonObjectByKey("involveHighTemperature", oilRefineryObj);
            p = new Paragraph("(d) Working around Furnace, High Temperature: (If yes please give details)");
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", involveHighTemperature).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", involveHighTemperature).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", involveHighTemperature).equalsIgnoreCase("y")) {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("involveHighTemperatureDetails", involveHighTemperature)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject handleElectricEquipments = jsonUtility.getJsonObjectByKey("handleElectricEquipments", oilRefineryObj);
            p = new Paragraph("(e) Do you handle electrical equipments?\n" + "(If so, state the nature of equipments, Voltage generated & nature of your work)");
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", handleElectricEquipments).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", handleElectricEquipments).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", handleElectricEquipments).equalsIgnoreCase("y")) {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("handleElectricEquipmentsDetails", handleElectricEquipments)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject exposedToFumesGases = jsonUtility.getJsonObjectByKey("exposedToFumesGases", oilRefineryObj);
            p = new Paragraph("(f) Do you handle or remain exposed to fumes, gases, acids, dyes or any other chemicals.\n" + "(If yes, please state which gas, acid, chemicals, dyes or nature of work)");
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", exposedToFumesGases).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", exposedToFumesGases).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", exposedToFumesGases).equalsIgnoreCase("y")) {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("exposedToFumesGasesDetails", exposedToFumesGases)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject effectOnHealth = oilRefineryObj.get("effectOnHealth").getAsJsonObject();
            p = new Paragraph("         (a) Has the type of work you do ever effected your health?\n" + "(If yes, please give full details)");
            oilRepositoryDetails.addCell(new Cell(1, 2).add("").setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", effectOnHealth).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", effectOnHealth).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", effectOnHealth).equalsIgnoreCase("y")) {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("effectOnHealthDetails", effectOnHealth)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                oilRepositoryDetails.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            p = new Paragraph("7. Have you ever had an accident while performing the above duties (If yes, please give full details)");
            oilRepositoryDetails.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
//            p.add("  Yes  ");
//            p.add(imgChecked);
//            p.add("  No   ");
//            p.add(imgUnchecked);
            oilRepositoryDetails.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("accidentWhileDuty", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));


            p = new Paragraph("8. What safety measures are available while you are at work.");
            oilRepositoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("safetyMeasures", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("9. Please state any other facts regarding your occupation, which you consider important.");
            oilRepositoryDetails.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("otherFacts", oilRefineryObj)).setUnderline()).setBorder(Border.NO_BORDER));
            oilRepositoryDetails.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            document.add(oilRepositoryDetails);

            p = new Paragraph();
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
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

            logger.info("Oil Refinery PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateOilRefineryPDF: {}", ex.getMessage());
        }
        return bytesPdf;
    }

    @Override
    public byte[] generateArmedForcesPDF(String applicationNumber, String nameOfLifeAssured, JsonObject armObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;

        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();
            String logoFilename = "armedforces.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
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

            p = new Paragraph("1. Which branch of the armed services are you in? Please state rank and details of typical duties involved.");
            Table armedForces = new Table(new float[]{800F, 200F});
            armedForces.setBorder(Border.NO_BORDER);
            armedForces.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            armedForces.addCell(new Cell().add(new Paragraph(jsonUtility.getJsonKeyValue("branchOfArmedService", armObj)).setUnderline()).setBorder(Border.NO_BORDER));
            armedForces.addCell(new Cell().add("").setBorder(Border.NO_BORDER));

            p = new Paragraph("2. Do you fly any type of aircraft as part of your duties?\nPlease state whether pilot or crew member, aircraft type, number of hours flown per annum and details of proposed destinations.");
            armedForces.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            JsonObject typeOfAirCraft = jsonUtility.getJsonObjectByKey("typeOfAirCraft", armObj);
            if (jsonUtility.getJsonKeyValue("status", typeOfAirCraft).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", typeOfAirCraft).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            armedForces.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", typeOfAirCraft).equalsIgnoreCase("y")) {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("typeOfAirCraftDetails", typeOfAirCraft)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject handleExplosives = jsonUtility.getJsonObjectByKey("handleExplosives", armObj);
            p = new Paragraph("3. Do you handle any explosives or engage in mine or bomb disposal?\nPlease provide further details.");
            armedForces.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", handleExplosives).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", handleExplosives).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            armedForces.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", handleExplosives).equalsIgnoreCase("y")) {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("handleExplosivesDetails", handleExplosives)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject diveonDuty = jsonUtility.getJsonObjectByKey("diveonDuty", armObj);
            p = new Paragraph("4. Do you ever dive as part of your duties?\nPlease state number of dives per annum, maximum depths and locations.");
            armedForces.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", diveonDuty).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", diveonDuty).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            armedForces.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", diveonDuty).equalsIgnoreCase("y")) {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("diveonDutyDetails", diveonDuty)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject paratroopingActivities = jsonUtility.getJsonObjectByKey("paratroopingActivities", armObj);
            p = new Paragraph("5. Do you take part in paratrooping, parachuting or commando activities?\nPlease state duties involved, number of jumps per annum and locations.");
            armedForces.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", paratroopingActivities).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", paratroopingActivities).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            armedForces.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", paratroopingActivities).equalsIgnoreCase("y")) {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("paratroopingActivitiesDetails", paratroopingActivities)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            JsonObject troubledArea = jsonUtility.getJsonObjectByKey("troubledArea", armObj);
            p = new Paragraph("6. Is there any immediate possibility of you being posted to any troubled areas?\nPlease state location(s), likely length of posting (s), and details of duties involved.");
            armedForces.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));

            p = new Paragraph();
            p.add("  Yes  ");
            if (jsonUtility.getJsonKeyValue("status", troubledArea).equalsIgnoreCase("y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (jsonUtility.getJsonKeyValue("status", troubledArea).equalsIgnoreCase("n")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            armedForces.addCell(new Cell().add(p).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            if (jsonUtility.getJsonKeyValue("status", troubledArea).equalsIgnoreCase("y")) {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("troubledAreaDetails", troubledArea)).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                armedForces.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            document.add(armedForces);

            p = new Paragraph();
            p.add(new Text("\n"));
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            document.add(new AreaBreak());

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

            logger.info("ArmedForces PDF generated Successfully!");

            bytesPdf = baos.toByteArray();

        } catch (IOException e) {
            logger.info("Error in generateArmedForcesPDF: {}", e.getMessage());
        }
        return bytesPdf;

    }

    @Override
    public byte[] generateMarinePDF(String applicationNumber, String nameOfLifeAssured, JsonObject marineObj, boolean isOmniDoc, String placeName, String primaryMobileNo, String countryCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytesPdf = null;
        try (PdfWriter writer = new PdfWriter(baos)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.addNewPage();
            String logoFilename = "marine.png";
            String logoBase64 =  pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + logoFilename);
            Image img = pdfUtility.getPDFLogo(logoBase64);

            String checkedLogo = "checked_2_20x21.png";
            String imgCheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + checkedLogo);
            String uncheckedLogo = "unchecked_20x21.png";
            String imgUncheckBase64 = pdfUtility.getImageAsBase64(PdfConstant.commonPdfImgUrl + uncheckedLogo);

            Image imgChecked = pdfUtility.getCheckedImage(imgCheckBase64);

            Image imgUnchecked = pdfUtility.getUncheckedImage(imgUncheckBase64);

            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
            Document document = new Document(pdfDoc, PageSize.A4).setFont(font).setFontSize(9);
            document.setMargins(20f, 20f, 20f, 20f);
            document.add(img);

            float[] pointColumnWidths = new float[]{120F, 220F};
            Table applicationTable = new Table(pointColumnWidths);
            applicationTable.setFontSize(9F);
            int appTextLength = applicationNumber.length();
            Paragraph p = new Paragraph();
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


            Table marineQuestions = new Table(new float[]{900F, 200F});
            marineQuestions.addCell(new Cell(1, 2).add("1. What is your exact occupation? (If you are involved in more than one occupation, please state all your occupations.) ").setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("exactOccupation", marineObj)).setUnderline()).setBorder(Border.NO_BORDER));

            marineQuestions.addCell(new Cell(1, 2).add("2. Give a description of nature of work performed in your occupation. ").setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("marineNatureOfWork", marineObj)).setUnderline()).setBorder(Border.NO_BORDER));

            JsonObject involvementInSea = jsonUtility.getJsonObjectByKey("involvementInSea", marineObj);
            String statusInvolvement = jsonUtility.getJsonKeyValue("status", involvementInSea);

            marineQuestions.addCell(new Cell().add("3. Does your occupation involves going to sea or is it likely to do so in future? ").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (statusInvolvement.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!statusInvolvement.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell(1, 2).add("4. Which of the following types of vessel do you work on? ").setBorder(Border.NO_BORDER));
            Table vesselTypes = new Table(new float[]{600F, 450F, 200F});
            JsonObject vesselObj = jsonUtility.getJsonObjectByKey("vessel", marineObj);
            boolean oceanLiner = jsonUtility.getBooleanKeyValue("oceanliner", vesselObj);
            boolean bargedredgerlighterlightshiptugorweathership = jsonUtility.getBooleanKeyValue("Bargedredgerlighterlightshiptugorweathership", vesselObj);
            boolean passengerVesselFerry = jsonUtility.getBooleanKeyValue("passengervesselferry", vesselObj);
            boolean cargoVessel = jsonUtility.getBooleanKeyValue("cargovessel", vesselObj);
            boolean cableandpipelayingvesselfactoryshipoilrigbargeorsupplyship = jsonUtility.getBooleanKeyValue("Cableandpipelayingvesselfactoryshipoilrigbargeorsupplyship", vesselObj);
            boolean otherVessel = jsonUtility.getBooleanKeyValue("other", vesselObj);
            p = new Paragraph();
            if (oceanLiner) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Ocean liner");
            vesselTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (bargedredgerlighterlightshiptugorweathership) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Barge, dredger, lighter, lightship, tug or weather ship");
            vesselTypes.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (passengerVesselFerry) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Passenger vessel/ferry");
            vesselTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (cargoVessel) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Cargo vessel");
            vesselTypes.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (cableandpipelayingvesselfactoryshipoilrigbargeorsupplyship) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Cable and pipe-laying vessel, factory ship, oil rig barge or supply ship");
            vesselTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (otherVessel) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Other (please specify): ");
            vesselTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            if (otherVessel) {
                String otherVesselDetails = jsonUtility.getJsonKeyValue("otherDetails", vesselObj);
                p = new Paragraph(otherVesselDetails).setUnderline();
                vesselTypes.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            } else {
                p = new Paragraph("").setUnderline();
                vesselTypes.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            }
            marineQuestions.addCell(new Cell(1, 2).add(vesselTypes).setBorder(Border.NO_BORDER));
            p = new Paragraph("5. What percentage of your duties is of a manual or physical nature?  ");
            p.add(new Text(jsonUtility.getJsonKeyValue("dutyPercentage", marineObj)).setUnderline());
            p.add(" %");
            marineQuestions.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));

            marineQuestions.addCell(new Cell(1, 2).add("6. Does your duty involve:").setBorder(Border.NO_BORDER));
            p = new Paragraph("(a) Lifting or moving heavy goods. If yes, please provide full details.");
            JsonObject liftingHeavyGoods = jsonUtility.getJsonObjectByKey("liftingHeavyGoods", marineObj);
            String liftingStatus = jsonUtility.getJsonKeyValue("status", liftingHeavyGoods);
            if (liftingStatus.equalsIgnoreCase("Y")) {
                p.add("   ");
                p.add(new Text(jsonUtility.getJsonKeyValue("liftingHeavyGoodsDetails", liftingHeavyGoods)).setUnderline());
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            p.add("  Yes  ");
            if (liftingStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!liftingStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            marineQuestions.addCell(new Cell().add("(b) Operation of cranes. If yes, please state the type of cranes you operate.").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject craneOperationsObj = jsonUtility.getJsonObjectByKey("craneOperations", marineObj);
            String craneOperationsStatus = jsonUtility.getJsonKeyValue("status", craneOperationsObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (craneOperationsStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!craneOperationsStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            boolean jibCrane = jsonUtility.getBooleanKeyValue("jibcrane", craneOperationsObj);
            boolean mobileCrane = jsonUtility.getBooleanKeyValue("mobilecrane", craneOperationsObj);
            boolean overHeadCrane = jsonUtility.getBooleanKeyValue("overheadcrane", craneOperationsObj);
            boolean derrickCrane = jsonUtility.getBooleanKeyValue("derrickcrane", craneOperationsObj);
            boolean gantryCrane = jsonUtility.getBooleanKeyValue("gantrycrane", craneOperationsObj);
            boolean portainerCrane = jsonUtility.getBooleanKeyValue("portainercrane", craneOperationsObj);
            boolean towerCraneDriver = jsonUtility.getBooleanKeyValue("towercranedriver", craneOperationsObj);
            boolean otherCrane = jsonUtility.getBooleanKeyValue("other", craneOperationsObj);
            Table craneTypes = new Table(new float[]{250F, 350F, 200F, 300F});
            p = new Paragraph();
            if (jibCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Jib crane");
            craneTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (mobileCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Mobile crane");
            craneTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (overHeadCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Overhead crane");
            craneTypes.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (derrickCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Derrick crane");
            craneTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (gantryCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Gantry crane");
            craneTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (portainerCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Portainer crane");
            craneTypes.addCell(new Cell(1, 2).add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (towerCraneDriver) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Tower crane driver");
            craneTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (otherCrane) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("     Any other (Please specify):");
            craneTypes.addCell(new Cell().add(p).setBorder(Border.NO_BORDER));
            p = new Paragraph();
            if (otherCrane) {
                p.add(new Text(jsonUtility.getJsonKeyValue("craneOperationsOtherDetails", craneOperationsObj)).setUnderline());
            } else {
                p.add(new Text("").setUnderline());
            }
            craneTypes.addCell(new Cell(1, 2).add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell(1, 2).add(craneTypes).setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell().add("(C) Working at depths or at heights: (If yes please state the maximum height and depth involved and " + "equipment used to get to the height or depth)").setBorder(Border.NO_BORDER));
            JsonObject heightDepts = jsonUtility.getJsonObjectByKey("heightDepts", marineObj);
            String heightStatus = jsonUtility.getJsonKeyValue("status", heightDepts);
            p = new Paragraph();
            p.add("  Yes  ");
            if (heightStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!heightStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            if (heightStatus.equalsIgnoreCase("Y")) {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("heightDeptsDetails", heightDepts)).setVerticalAlignment(VerticalAlignment.MIDDLE).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            marineQuestions.addCell(new Cell().add("(D) High Voltages: (If yes please give details)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject highVoltageObj = jsonUtility.getJsonObjectByKey("highVoltage", marineObj);
            String highVoltageStatus = jsonUtility.getJsonKeyValue("status", highVoltageObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (highVoltageStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!highVoltageStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            if (highVoltageStatus.equalsIgnoreCase("Y")) {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("highVoltageDetails", highVoltageObj)).setVerticalAlignment(VerticalAlignment.MIDDLE).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            marineQuestions.addCell(new Cell().add("(E) Do you handle electrical equipments?\n(If so, state the nature of Equipments, Voltage generated & nature of your work)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject electricEquipmentObj = jsonUtility.getJsonObjectByKey("electricEquipment", marineObj);
            String electricEquipmentStatus = jsonUtility.getJsonKeyValue("status", electricEquipmentObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (electricEquipmentStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!electricEquipmentStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            if (electricEquipmentStatus.equalsIgnoreCase("Y")) {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("electricEquipmentDetails", electricEquipmentObj)).setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            } else {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }

            marineQuestions.addCell(new Cell().add("7. Has the type of work you do ever effected your health? (If yes, please give full details)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject healthAffectObj = jsonUtility.getJsonObjectByKey("healthAffect", marineObj);
            String healthAffectStatus = jsonUtility.getJsonKeyValue("status", healthAffectObj);
            p = new Paragraph();
            p.add("  Yes  ");
            if (healthAffectStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!healthAffectStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            if (healthAffectStatus.equalsIgnoreCase("Y")) {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("healthAffectDetails", healthAffectObj)).setUnderline()).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            } else {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            marineQuestions.addCell(new Cell().add("8. Have you ever had an accident while performing the above duties? (If yes, please give full details)").setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            JsonObject accidentOnDuty = jsonUtility.getJsonObjectByKey("accidentOnDuty", marineObj);
            String accidentDutyStatus = jsonUtility.getJsonKeyValue("status", accidentOnDuty);
            p = new Paragraph();
            p.add("  Yes  ");
            if (accidentDutyStatus.equalsIgnoreCase("Y")) {
                p.add(imgChecked);
            } else {
                p.add(imgUnchecked);
            }
            p.add("  No   ");
            if (!accidentDutyStatus.equalsIgnoreCase("N")) {
                p.add(imgUnchecked);
            } else {
                p.add(imgChecked);
            }
            marineQuestions.addCell(new Cell().add(p).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
            if (accidentDutyStatus.equalsIgnoreCase("Y")) {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("accidentOnDutyDetails", accidentOnDuty)).setVerticalAlignment(VerticalAlignment.MIDDLE).setUnderline()).setBorder(Border.NO_BORDER));
            } else {
                marineQuestions.addCell(new Cell(1, 2).add(new Paragraph("").setUnderline()).setBorder(Border.NO_BORDER));
            }
            marineQuestions.addCell(new Cell(1, 2).add("9. What safety measures are available while you are at work?").setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("safetyMeasures", marineObj)).setUnderline()).setBorder(Border.NO_BORDER));

            marineQuestions.addCell(new Cell(1, 2).add("10. Please state any other facts regarding your occupation, which you consider important.").setBorder(Border.NO_BORDER));
            marineQuestions.addCell(new Cell(1, 2).add(new Paragraph(jsonUtility.getJsonKeyValue("otherFacts", marineObj)).setUnderline()).setBorder(Border.NO_BORDER));
            document.add(marineQuestions);
            p = new Paragraph();
            p.add(new Text("\n"));
            p.add(new Text("I hereby agree that the forgoing questions and answers shall form part of the proposal for insurance made by me to the Company."));
            document.add(p);

            document.add(new Paragraph("\n\n"));
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

            logger.info("Marine PDF generated Successfully!");
            bytesPdf = baos.toByteArray();
        } catch (Exception ex) {
            logger.info("Error in generateMarinePDF: {}", ex.getMessage());
        }
        return bytesPdf;

    }

}
