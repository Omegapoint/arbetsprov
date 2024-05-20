package se.omegapoint.pdfuploader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PdfFileRepository extends JpaRepository<PdfFile, Long> {
    @Query("SELECT p FROM PdfFile p WHERE p.fileName = ?1 AND p.checksum = ?2")
    Optional<PdfFile> findByFileNameAndChecksum(String fileName, String checksum);
    @Query("SELECT p FROM PdfFile p WHERE p.fileName = ?1")
    Optional<PdfFile> findByFileName(String fileName);
}