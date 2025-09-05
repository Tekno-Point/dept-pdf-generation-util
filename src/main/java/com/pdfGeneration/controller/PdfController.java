package com.pdfGeneration.controller;

import com.pdfGeneration.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/pdf")
public class PdfController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FatcaPDFService downloadPDFService;
    private final ExistingPolicyPDFService existingPolicyPDFService;
    private final EMandatePDFService eMandatePDFService;
    private final GapPdfGenerationService gapPdfGenerationService;
    private final GPenPDFService gPenPDFService;
    private final DownloadApplicationFormService downloadApplicationFormService;
    private final MoneyBalancePDFService moneyBalancePDFService;
    private final ETermPdfGenerationService eTermPdfGenerationService;
    private final LifePlanPdfGenerationService lifePlanPdfGenerationService;
    private final GppPdfGenerationService gppPdfGenerationService;
    private final GiftCityPdfService giftCityPdfService;
    private final SppPDFService sppPDFService;
    private final TulipPlusPDFService tulipPlusPDFService;
    private final GoldFormPDFService goldFormPDFService;
    private final DownloadMergedPdfService downloadMergedPdfService;

    public PdfController(FatcaPDFService downloadPDFService, ExistingPolicyPDFService existingPolicyPDFService, EMandatePDFService eMandatePDFService, GapPdfGenerationService gapPdfGenerationService, GPenPDFService gPenPDFService, DownloadApplicationFormService downloadApplicationFormService, MoneyBalancePDFService moneyBalancePDFService, ETermPdfGenerationService eTermPdfGenerationService, LifePlanPdfGenerationService lifePlanPdfGenerationService, GppPdfGenerationService gppPdfGenerationService, GiftCityPdfService giftCityPdfService, SppPDFService sppPDFService, TulipPlusPDFService tulipPlusPDFService, GoldFormPDFService goldFormPDFService, DownloadMergedPdfService downloadMergedPdfService) {
        this.downloadPDFService = downloadPDFService;
        this.existingPolicyPDFService = existingPolicyPDFService;
        this.eMandatePDFService = eMandatePDFService;
        this.gapPdfGenerationService = gapPdfGenerationService;
        this.gPenPDFService = gPenPDFService;
        this.downloadApplicationFormService = downloadApplicationFormService;
        this.moneyBalancePDFService = moneyBalancePDFService;
        this.eTermPdfGenerationService = eTermPdfGenerationService;
        this.lifePlanPdfGenerationService = lifePlanPdfGenerationService;
        this.gppPdfGenerationService = gppPdfGenerationService;
        this.giftCityPdfService = giftCityPdfService;
        this.sppPDFService = sppPDFService;
        this.tulipPlusPDFService = tulipPlusPDFService;
        this.goldFormPDFService = goldFormPDFService;
        this.downloadMergedPdfService = downloadMergedPdfService;
    }


    @PostMapping(path = "/downloadPDF/{docType}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public byte[] downloadPDF(@RequestBody String pdfObjReq, @PathVariable("docType") String docType) {

        return switch (docType) {
            case "fatca" -> downloadPDFService.downloadFatcaForm(pdfObjReq);
            case "eMandate" -> eMandatePDFService.downloadEMandateForm(pdfObjReq);
            case "applicationForm" -> downloadApplicationFormService.downloadApplicationForm(pdfObjReq);
            case "existingPolicy" -> existingPolicyPDFService.downloadExistingPolicy(pdfObjReq);
            case "gppPlusForm" -> gppPdfGenerationService.generateGppPdf(pdfObjReq);
            case "gPenForm" -> gPenPDFService.downloadGPenForm(pdfObjReq);
            case "gapForm" -> gapPdfGenerationService.generateGapPdf(pdfObjReq);
            case "goldForm" -> goldFormPDFService.goldForm(pdfObjReq);
            case "eTermForm" -> eTermPdfGenerationService.generateETermPdf(pdfObjReq);
            case "moneyBalanceForm" -> moneyBalancePDFService.moneyBalanceForm(pdfObjReq);
            case "lifePlanForm" -> lifePlanPdfGenerationService.generateLifePlanPdf(pdfObjReq);
            case "tulipPlusForm" -> tulipPlusPDFService.generateTulipPlusPdf(pdfObjReq);
            case "giftCityForm" -> giftCityPdfService.generateGiftCityPdf(pdfObjReq);
            case "sppPlusForm" -> sppPDFService.generateSppPlusPdf(pdfObjReq);
            default -> throw new IllegalStateException("Unexpected value: " + docType);
        };
    }

    @PostMapping(path = "/downloadMergedPDF",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public byte[] downloadMedicalLifeStylePDF(@RequestBody String medicalLifeStylePdfReq){
        try{
            return downloadMergedPdfService.downloadMedicalLifestylePdf(medicalLifeStylePdfReq);
        }catch (Exception e){
            logger.error("Exception occurs in medical and lifestyle merged pdf:{}", e.getMessage());
            return null;

        }
    }
}
