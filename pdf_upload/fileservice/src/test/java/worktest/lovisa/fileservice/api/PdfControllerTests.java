package worktest.lovisa.fileservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import worktest.lovisa.fileservice.BaseTest;
import worktest.lovisa.fileservice.api.responses.FilesListResponse;
import worktest.lovisa.fileservice.api.responses.MessageResponse;

import java.util.List;

public class PdfControllerTests extends BaseTest {

    @Autowired
    PdfController pdfController;

    @Test
    public void shouldUploadPdfFile() {
        // given
        final MultipartFile pdfFile = getFile(pdfExt, 0);
        // when
        ResponseEntity<MessageResponse> response = pdfController.uploadPdf(pdfFile);
        // then
        MessageResponse expected = new MessageResponse(HttpStatus.OK.value(), "File uploaded successfully");
        assert response.getBody().equals(expected);
    }

    @Test
    public void shouldUploadAndRetrievePdfFile() {
        // given
        final MultipartFile pdfFile = getFile(pdfExt, 0);
        // when
        pdfController.uploadPdf(pdfFile);
        ResponseEntity<Resource> uploaded = pdfController.serveFile("file.pdf");
        // then
        assert uploaded.getBody().getFilename().equals("file.pdf");
    }

    @Test
    public void shouldRetrieveAvailableFiles() {
        // given
        final MultipartFile pdfFile = getFile(pdfExt, 0);
        // when
        pdfController.uploadPdf(pdfFile);
        ResponseEntity<FilesListResponse> response = pdfController.showFiles();
        // then
        List<String> expected = List.of("file.pdf");
        assert response.getBody().files().equals(expected);
    }
}
