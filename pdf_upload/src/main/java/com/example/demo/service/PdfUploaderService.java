package com.example.demo.service;


import com.example.demo.component.PdfValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.storage.PdfStorage;

import java.io.IOException;

@Service
public class PdfUploaderService {
    private final PdfStorage pdfStorage;
    private final PdfValidator pdfValidator;

    public PdfUploaderService(PdfValidator pdfValidator, PdfStorage pdfStorage) {
        this.pdfValidator = pdfValidator;
        this.pdfStorage = pdfStorage;
    }


    public ResponseEntity<String> validateAndSavePdfFile(MultipartFile file) throws IOException {
        String errorMessage = "";
        try {
            if (file != null) {
                if (!pdfValidator.isPdfFile(file.getBytes())) {
                    errorMessage += "Error: The file is not a PDF; ";
                }
                if (pdfValidator.isFileSizeExceedLimit(file)) {
                    errorMessage += "The file is too large, maximum filesize is 2MB; ";
                }
                if (pdfValidator.isPdfDuplicate(file)) {
                    errorMessage += "The file already exists, duplicates are not allowed; ";
                }
                if (!errorMessage.isEmpty()) {
                   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
                pdfStorage.saveFile(file.getOriginalFilename(), file.getBytes(), pdfValidator.calculateChecksum(file.getBytes()));
                return ResponseEntity.ok("Upload successful!");
            }
            return null;
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    public byte[] getPdfByNameAndChecksum(String fileName, String fileChecksum) {
        return pdfStorage.getPdfByNameAndChecksum(fileName, fileChecksum);
    }
}
