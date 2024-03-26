package worktest.lovisa.fileservice.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import worktest.lovisa.fileservice.BaseTest;
import worktest.lovisa.fileservice.domain.exceptions.StorageException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PdfServiceTests extends BaseTest {

    @Autowired
    PdfService pdfService;

    @Test
    public void shouldUploadPdfMultipartFile(){
        // given
        final MultipartFile file = getFile(pdfExt, 0);
        // when
        pdfService.storePdf(file);
        // then
        // no error
    }

    @Test
    public void storesMultipartFileOnLocalDisk() {
        // given
        final MultipartFile file = getFile(pdfExt, 0);
        // when
        pdfService.storePdf(file);
        // then
        assert pdfService.retrievePdf("file.pdf").getName().equals("file.pdf");
    }

    @Test
    public void checksIfFileIsPdf() {
        // given
        final MultipartFile file = getFile(txtExt, 0);
        // when
        // then
        assertThrows(TypeMismatchException.class, () -> pdfService.storePdf(file));
    }

    @Test
    public void shouldNotUploadDuplicateFile() {
        // given
        final MultipartFile file = getFile(pdfExt, 0);
        // when
        pdfService.storePdf(file);
        // then
        assertThrows(StorageException.class, () -> pdfService.storePdf(file));
    }

    @Test
    public void shouldRetrieveNamesOfUploadedFiles() {
        // given
        final MultipartFile file = getFile(pdfExt, 0);
        pdfService.storePdf(file);
        // when
        List<String> expected = List.of("file.pdf");
        List<String> result = pdfService.listAvailableFiles();
        // then
        assert result.equals(expected);


    }
}
