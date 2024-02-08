package com.example.demo;

import com.example.demo.component.PdfValidator;
import com.example.demo.service.PdfUploaderService;
import com.example.demo.storage.PdfStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfValidatorTest {


    private PdfStorage pdfStorage;
    private PdfValidator pdfValidator;

    private static final byte[] PDF_SIGNATURE = {0x25, 0x50, 0x44, 0x46, 0x2D};

    @BeforeEach
    public void setup() {
        pdfStorage = new PdfStorage();
        pdfValidator = new PdfValidator(pdfStorage);
    }


    @Test
    public void CalculateCheckSum_Correct_ReturnsValidChecksum() {
        byte[] fileContent = new byte[1024 * 1024];
        System.arraycopy(PDF_SIGNATURE, 0, fileContent, 0, PDF_SIGNATURE.length);
        assertEquals("70f3e7ebabc226cd39072ae22ef72be8", pdfValidator.calculateChecksum(fileContent));
    }

    @Test
    public void IsPdfDuplicate_ReturnsTrue() throws IOException {
        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", new byte[1]);
        pdfStorage.saveFile(file.getOriginalFilename(), file.getBytes(), pdfValidator.calculateChecksum(file.getBytes()));

        assertTrue(pdfValidator.isPdfDuplicate(file));
    }

    @Test
    public void IsPdfFile_ReturnsTrue() throws IOException {
        byte[] fileContent = new byte[1024 * 1024];
        System.arraycopy(PDF_SIGNATURE, 0, fileContent, 0, PDF_SIGNATURE.length);
        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", fileContent);
        pdfStorage.saveFile(file.getOriginalFilename(), file.getBytes(), pdfValidator.calculateChecksum(file.getBytes()));

        assertTrue(pdfValidator.isPdfFile(file.getBytes()));
    }

}




