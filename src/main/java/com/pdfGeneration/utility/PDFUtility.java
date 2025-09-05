package com.pdfGeneration.utility;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Base64;

@Service
public class PDFUtility {

    private final Logger logger = LoggerFactory.getLogger(PDFUtility.class);

    public Image getPDFLogo(String logoBase64){
        Image img = null;
        try {
            byte[] logoBytes = Base64.getDecoder().decode(logoBase64);
            ImageData data = ImageDataFactory.create(logoBytes);
            img = new Image(data);
        } catch (Exception e) {
            logger.info("PDF Logo Exception: {}", e.getMessage());
        }
        return img;
    }

    public Image getCheckedImage(String checkImgBase64){
        Image imgChecked = null;
        try{
            byte[] checkedBytes = Base64.getDecoder().decode(checkImgBase64);
            ImageData data = ImageDataFactory.create(checkedBytes);
            imgChecked = new Image(data);
            imgChecked.setWidth(15);
            imgChecked.setHeight(16);
        }catch (Exception e){
            logger.info("PDF Checked Image Exception: {}", e.getMessage());
        }
        return imgChecked;
    }

    public Image getUncheckedImage(String uncheckImgBase64){
        Image imgChecked = null;
        try{
            byte[] uncheckedBytes = Base64.getDecoder().decode(uncheckImgBase64);
            ImageData data = ImageDataFactory.create(uncheckedBytes);
            imgChecked = new Image(data);
            imgChecked.setWidth(15);
            imgChecked.setHeight(16);
        }catch (Exception e){
            logger.info("PDF unchecked Image Exception: {}", e.getMessage());
        }
        return imgChecked;
    }

    public Table createFooter() {
        Table grandFooterTable = new Table(2);
        grandFooterTable.setFontSize(8F);
        grandFooterTable.setTextAlignment(TextAlignment.CENTER);
        Paragraph companyText = new Paragraph(
                new Text("IndiaFirst Life Insurance Company Ltd.").setFontColor(Color.BLUE));
        companyText.add(new Text("\n12th and 13th Floor, North [C] Wing, Tower 4, Nesco IT Park, Nesco Center,\n" +
                "Western Express Highway, Goregaon (East), Mumbai – 400063,\n" +
                "IRDA Reg. No. 143. CIN: U66010MH2008PLC183679."));
        grandFooterTable.addCell(companyText).setTextAlignment(TextAlignment.LEFT);
        Paragraph companyContact = new Paragraph(new Text("Tel: ").setBold().setFontColor(Color.ORANGE));
        companyContact.add(new Text("+91 22 6165 8700"));
        companyContact.add(new Text("  Fax: ").setBold().setFontColor(Color.ORANGE));
        companyContact.add(new Text("+91 22 6857 0600"));
        companyContact.add(new Text("  Toll Free: ").setBold().setFontColor(Color.ORANGE));
        companyContact.add(new Text("1800-209-8700"));

        companyContact.add(
                new Text("\n----------------------------------------------------------------------------------------"));
        companyContact.add(new Text("\nE-mail: ").setBold().setFontColor(Color.ORANGE));
        companyContact.add(new Text("customer.\u001Afirst@indiafirstlife.com"));
        companyContact.add(new Text("  Website: ").setBold().setFontColor(Color.ORANGE));
        companyContact.add(new Text("www.india\u001Afirstlife.com"));

        grandFooterTable.addCell(companyContact);
        grandFooterTable.setWidth(UnitValue.createPercentValue(100));
        return grandFooterTable;
    }

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

    public String feetAndInchesToCms(double feet, double inches) {
        double totalInches = (feet * 12) + inches;
        double centimeters = totalInches * 2.54;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(centimeters);
    }

    public Table createDataTable(String actualData,String emptyDataColumn) {
        // Default value for empty panNumber
        String panNumberData = actualData.isEmpty() ? emptyDataColumn : actualData;

        // Initialize column widths
        float[] pointColumnWidths = new float[panNumberData.length()];
        for (int i = 0; i < panNumberData.length(); i++) {
            pointColumnWidths[i] = 20F;
        }

        // Create and return the table
        return !actualData.isEmpty()
                ? codeTable(actualData, pointColumnWidths)
                : codeTable(panNumberData, pointColumnWidths).setFontColor(Color.WHITE);
    }

    public String maskMobileNumber(String mobileNumber) {
        if (mobileNumber.length() >= 7) {
            return mobileNumber.substring(0, 2) + "******" + mobileNumber.substring(8);
        } else {
            return mobileNumber;
        }
    }


    public String getImageAsBase64(String imageUrl) {
        try {
            disableSSLVerification();
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream()) {
                byte[] bytes = in.readAllBytes();
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void disableSSLVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    public String removeTrailingZeros(String formattedNumber) {
        formattedNumber = !formattedNumber.contains(".") ? formattedNumber
                : formattedNumber.replaceAll("0*$", "").replaceAll("\\.$", "");
        return formattedNumber;
    }

    public static final String[] units = { "", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
            "Eighteen", "Nineteen" };

    public static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty",
            "Seventy", "Eighty", "Ninety"
    };

    public String convert(final long n) {
        if (n < 0) {
            return "Minus " + convert(-n);
        }
        if (n < 20) {
            return units[(int) n];
        }
        if (n < 100) {
            return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " : "") + units[(int) (n % 10)];
        }
        if (n < 1000) {
            return units[(int) (n / 100)] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }
        if (n < 100000) {
            return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
        }
        if (n < 10000000) {
            return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
        }
        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
    }




}
