package worktest.lovisa.fileservice.api.responses;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<MessageResponse> handleStorageException(FileNotFoundException e) {
        MessageResponse errorResponse = new MessageResponse(
                HttpStatus.NOT_FOUND.value(),
                "File could not be found."
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<MessageResponse> handleUnsupportedMediaType(TypeMismatchException e) {
        MessageResponse errorResponse = new MessageResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Unsupported media type, only PDF files are currently allowed."
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageResponse> handleFileTooLargeException(MaxUploadSizeExceededException e) {
        MessageResponse errorResponse = new MessageResponse(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "File is too large, maximum size is " + maxFileSize
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleFileDuplicateException(FileAlreadyExistsException e) {
        MessageResponse errorResponse = new MessageResponse(HttpStatus.CONFLICT.value(), "File already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<MessageResponse> handleMissingRequestPart(MissingServletRequestPartException e) {
        MessageResponse errorResponse = new MessageResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
