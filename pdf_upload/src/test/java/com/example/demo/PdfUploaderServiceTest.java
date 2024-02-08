package com.example.demo;

import com.example.demo.component.PdfValidator;
import com.example.demo.service.PdfUploaderService;
import com.example.demo.storage.PdfStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdfUploaderServiceTest {


    private PdfUploaderService pdfUploaderService;
    private static final byte[] PDF_SIGNATURE = {0x25, 0x50, 0x44, 0x46, 0x2D};


    @BeforeEach
    public void setup() {
        PdfStorage pdfStorage = new PdfStorage();
        PdfValidator pdfValidator = new PdfValidator(pdfStorage);
        pdfUploaderService = new PdfUploaderService(pdfValidator, pdfStorage);
    }

    @Test
    public void IsPdfFile_ValidPdfSignature_ReturnsOK() throws IOException {

        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", PDF_SIGNATURE);
        ResponseEntity<String> response = pdfUploaderService.validateAndSavePdfFile(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Upload successful!", response.getBody());
    }

    @Test
    public void IsPdfFile_NotValidPdfSignature_ReturnsBadRequest() throws IOException {
        byte[] NOT_PDF_SIGNATURE = {0x26, 0x51, 0x45, 0x47, 0x3D};

        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", NOT_PDF_SIGNATURE);
        ResponseEntity<String> response = pdfUploaderService.validateAndSavePdfFile(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: The file is not a PDF; ", response.getBody());
    }

    @Test
    public void IsFileSizeExceedLimit_ExceedingLimit_ReturnsBadRequest() throws IOException {
        byte[] tooLargeFileContent = new byte[3024 * 3024];

        System.arraycopy(PDF_SIGNATURE, 0, tooLargeFileContent, 0, PDF_SIGNATURE.length);

        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", tooLargeFileContent);
        ResponseEntity<String> response = pdfUploaderService.validateAndSavePdfFile(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The file is too large, maximum filesize is 2MB; ", response.getBody());
    }

    @Test
    public void IsFileSizeExceedLimit_NotExceedingLimit_ReturnsOK() throws IOException {
        byte[] fileContent = new byte[1024 * 1024];

        System.arraycopy(PDF_SIGNATURE, 0, fileContent, 0, PDF_SIGNATURE.length);

        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", fileContent);
        ResponseEntity<String> response = pdfUploaderService.validateAndSavePdfFile(file);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Upload successful!", response.getBody());
    }


    @Test
    public void isPdfDuplicate_Yes_ReturnsBadRequest() throws IOException {
        byte[] fileContent = new byte[1024 * 1024];

        System.arraycopy(PDF_SIGNATURE, 0, fileContent, 0, PDF_SIGNATURE.length);

        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", fileContent);
        MultipartFile fileDuplicate = new MockMultipartFile("pdfMock", "filename", "application/pdf", fileContent);
        pdfUploaderService.validateAndSavePdfFile(file);
        ResponseEntity<String> response2 = pdfUploaderService.validateAndSavePdfFile(fileDuplicate);

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals("The file already exists, duplicates are not allowed; ", response2.getBody());
    }

    @Test
    public void isPdfDuplicate_No_ReturnsOK() throws IOException {
        byte[] fileContent = new byte[1024 * 1024];

        System.arraycopy(PDF_SIGNATURE, 0, fileContent, 0, PDF_SIGNATURE.length);

        MultipartFile file = new MockMultipartFile("pdfMock", "filename", "application/pdf", fileContent);
        MultipartFile file2 = new MockMultipartFile("pdfMock", "filename2", "application/pdf", fileContent);
        ResponseEntity<String> response = pdfUploaderService.validateAndSavePdfFile(file);
        ResponseEntity<String> response2 = pdfUploaderService.validateAndSavePdfFile(file2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Upload successful!", response.getBody());
        assertEquals("Upload successful!", response2.getBody());
    }







}
