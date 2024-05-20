package se.omegapoint.pdfuploader.repository;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@Entity
@Table(name = "pfd_file")
public class PdfFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fileName;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private String checksum;
}
