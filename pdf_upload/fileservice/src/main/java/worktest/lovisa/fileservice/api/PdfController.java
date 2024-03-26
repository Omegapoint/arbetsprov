package worktest.lovisa.fileservice.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import worktest.lovisa.fileservice.api.responses.FilesListResponse;
import worktest.lovisa.fileservice.api.responses.MessageResponse;
import worktest.lovisa.fileservice.domain.PdfService;

import java.util.List;

@RestController
@RequestMapping("/v1/api/files")
public class PdfController {

    @Autowired
    PdfService pdfService;

    @PostMapping()
    @ResponseBody
     public ResponseEntity<MessageResponse> uploadPdf(@RequestPart MultipartFile file ) {
         pdfService.storePdf(file);
         return ResponseEntity.ok().body(new MessageResponse(HttpStatus.OK.value(), "File uploaded successfully"));
     }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<FilesListResponse> showFiles() {
        List<String> files = pdfService.listAvailableFiles();
        return ResponseEntity.ok().body(new FilesListResponse(files));
    }

    @GetMapping("/serve")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestParam String name) {
        Resource file = pdfService.loadAsResource(name);
        if (file == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}