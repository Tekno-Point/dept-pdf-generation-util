package com.pdfGeneration.service;

import com.itextpdf.layout.element.Table;

public interface GapPdfGenerationService {

    byte [] generateGapPdf(String gapPdfGenReqObj);

    Table codeTable(String codeData, float[] pointColumnWidths);
}
