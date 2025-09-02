package org.example.ai_integration;

import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class FileUtil {

    public static String readFileContent(File file) throws IOException {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".txt")) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        }
        else if (name.endsWith(".pdf")) {
            try (PDDocument doc = PDDocument.load(file)) {
                if (doc.isEncrypted()) {
                    System.err.println("DEBUG: PDF is encrypted.");
                    return "Error: PDF is encrypted and cannot be read.";
                }
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(doc);
            } catch (IOException e) {
                e.printStackTrace(); // full error in console
                return "Error reading PDF: " + e.getMessage();
            }
        }
        else if (name.endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                XWPFDocument doc = new XWPFDocument(fis);
                StringBuilder text = new StringBuilder();
                for (XWPFParagraph para : doc.getParagraphs()) {
                    text.append(para.getText()).append("\n");
                }
                return text.toString();
            }
        }
        return "Unsupported file type: " + name;
    }
}