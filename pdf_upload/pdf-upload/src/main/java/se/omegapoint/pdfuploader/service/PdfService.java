package se.omegapoint.pdfuploader.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.omegapoint.pdfuploader.repository.PdfFile;
import se.omegapoint.pdfuploader.repository.PdfFileRepository;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Slf4j
@Service
public class PdfService {

    private final PdfFileRepository pdfFileRepository;

    @Autowired
    public PdfService(PdfFileRepository pdfFileRepository) {
        this.pdfFileRepository = pdfFileRepository;
    }

    public void storePdf(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        validatePdf(file);
        String checksum = getChecksum(file.getBytes());
        String fileName = file.getOriginalFilename();

        Optional<PdfFile> existingFile = pdfFileRepository.findByFileNameAndChecksum(fileName, checksum);
        if (existingFile.isPresent()) {
            throw new IllegalArgumentException("File already uploaded.");
        }

        pdfFileRepository.save(PdfFile.builder()
                .fileName(fileName)
                .fileName(checksum)
                .data(file.getBytes())
                .build());
    }

    public byte[] getPdf(String fileName) {
        Optional<PdfFile> pdfFile = pdfFileRepository.findByFileName(fileName);
        if (pdfFile.isEmpty()) {
            log.warn("File with name {} was not found in the system.", fileName);
            throw new IllegalArgumentException("File not found.");
        }
        return pdfFile.get().getData();
    }

    private void validatePdf(MultipartFile file) throws IOException {
        if (file == null || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".pdf")) {
            log.error("The file provided is not a pdf {}.", file);
            throw new IllegalArgumentException("File is not a PDF.");
        }
        if (file.getSize() > 2 * 1024 * 1024) { // 2MB
            log.error("The file exceeds the maximum size of 2MB{}.", file);
            throw new IllegalArgumentException("File size exceeds 2MB.");
        }
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            // Load the PDF to ensure it's a valid PDF file
        } catch (IOException e) {
            log.error("An error has occurred. The file is corrupted or is not  a valid pdf file{}.", file);
            throw new IllegalArgumentException("Invalid PDF file.");
        }
    }

    private String getChecksum(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
