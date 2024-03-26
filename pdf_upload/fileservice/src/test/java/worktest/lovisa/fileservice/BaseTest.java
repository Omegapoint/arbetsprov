package worktest.lovisa.fileservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import worktest.lovisa.fileservice.domain.PdfService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@SpringBootTest
public class BaseTest {


    @Value("${storage.upload-directory}")
    String uploadDirectory;

    @Autowired
    PdfService pdfService;

    public String pdfExt = "pdf";
    public String txtExt = "txt";

    public MultipartFile getFile(String ext, Integer sizeMb) {
        MediaType mediaType = ext.equals(pdfExt) ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN;
        byte[] content = sizeMb == 0 ? "Hello, World!".getBytes() : new byte[sizeMb * 1024 * 1024];

        return new MockMultipartFile(
                "file." + ext,
                "file." + ext,
                mediaType.toString(),
                content
        );
    }

    @BeforeEach
    void initialize(){
        pdfService.initialize();
    }

    @AfterEach
    void purgeTestFiles() throws IOException {
        Path path = Path.of(uploadDirectory);
        // delete directory
        if (Files.exists(path))
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
    }
}
