package com.example.demo.component;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.storage.PdfStorage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PdfValidator {

    private static final byte[] PDF_SIGNATURE = {0x25, 0x50, 0x44, 0x46, 0x2D};
    private static final long MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024;
    private final PdfStorage pdfStorage;

    public PdfValidator(PdfStorage pdfStorage) {
        this.pdfStorage = pdfStorage;
    }

    public String calculateChecksum(byte[] fileContent) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(fileContent);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean isPdfDuplicate(MultipartFile file) throws IOException {
        return pdfStorage.getPdfByNameAndChecksum(
                file.getOriginalFilename(),
                calculateChecksum(file.getBytes())) != null;
    }

    public boolean isPdfFile(byte[] data) {
        if (data.length >= 5) {
            for (int i = 0; i < PDF_SIGNATURE.length; i++) {
                if (data[i] != PDF_SIGNATURE[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isFileSizeExceedLimit(MultipartFile file) {
        long fileSize = file.getSize();
        return fileSize > MAX_FILE_SIZE_BYTES;
    }

}
