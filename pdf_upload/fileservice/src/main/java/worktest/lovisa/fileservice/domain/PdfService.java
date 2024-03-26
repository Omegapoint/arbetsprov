package worktest.lovisa.fileservice.domain;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import worktest.lovisa.fileservice.domain.exceptions.StorageException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

@Service
public class PdfService {

    @Value("${storage.upload-directory}")
    String uploadDirectory;

    Path uploadPath;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        this.uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public void storePdf(MultipartFile file) {
        checkFile(file);
        try {
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String filename = Objects.requireNonNull(file.getOriginalFilename());
            Files.copy(file.getInputStream(), uploadPath.resolve(filename));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getName(), e);
        }
    }

    private void checkFile(MultipartFile file){
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            throw new TypeMismatchException(file, MultipartFile.class);
        }
    }

    public File retrievePdf(String pdfName) {
        return uploadPath.resolve(Path.of(pdfName)).toFile();
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = uploadPath.resolve(filename);
            return new UrlResource(file.toUri());
        }
        catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + filename, e);
        }
    }

    public List<String> listAvailableFiles() {
        try {
            return Files.walk(uploadPath)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .map(File::getName)
                    .toList();
        }
        catch (IOException e) {
            return List.of();
        }
    }
}
