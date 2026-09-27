package com.devhire.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeTextExtractorService {

    public String extractText(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".pdf")) {
            return extractPdf(file);
        }

        if (lowerName.endsWith(".docx")) {
            return extractDocx(file);
        }

        throw new IllegalArgumentException(
                "Only PDF and DOCX files are supported"
        );
    }

    private String extractPdf(MultipartFile file) throws IOException {

        try (PDDocument document =
                     Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(document);
        }
    }

    private String extractDocx(MultipartFile file) throws IOException {

        try (
                XWPFDocument document =
                        new XWPFDocument(file.getInputStream());

                XWPFWordExtractor extractor =
                        new XWPFWordExtractor(document)
        ) {

            return extractor.getText();
        }
    }
}