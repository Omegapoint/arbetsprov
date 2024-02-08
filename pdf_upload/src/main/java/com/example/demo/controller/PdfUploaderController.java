package com.example.demo.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.service.PdfUploaderService;

@RestController
@RequestMapping("/api")
public class PdfUploaderController {

    private final PdfUploaderService pdfUploaderService;

    public PdfUploaderController(PdfUploaderService pdfUploaderService) {
        this.pdfUploaderService = pdfUploaderService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            return pdfUploaderService.validateAndSavePdfFile(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload the PDF file.");
        }
    }

    @GetMapping("/download/{fileName}+{fileChecksum}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String fileName, @PathVariable String fileChecksum) {
        try {

            byte[] pdfBytes = pdfUploaderService.getPdfByNameAndChecksum(fileName, fileChecksum);

            if (pdfBytes == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

        }


    }
