package org.example.ai_integration.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Utility class for reading the content of files in different formats.
 * <p>
 * Supports reading plain text, PDF, and Word (.docx) files.
 */
public class FileUtil {
    /**
     * Reads the content of the provided file as a {@code String}.
     * <p>
     * Supports multiple file formats:
     * <ul>
     *     <li><b>.txt</b> → read as UTF-8 plain text</li>
     *     <li><b>.pdf</b> → parsed using PDFBox (if not encrypted)</li>
     *     <li><b>.docx</b> → parsed using Apache POI</li>
     * </ul>
     * Returns an error message if the file is unsupported or cannot be read.
     *
     * @param file the file to be read
     * @return the text content of the file, or an error message if unsupported or unreadable
     * @throws IOException if an error occurs while reading text or Word files
     */
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