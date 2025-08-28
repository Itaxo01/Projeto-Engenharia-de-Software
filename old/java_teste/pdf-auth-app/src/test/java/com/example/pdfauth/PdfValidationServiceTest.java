package com.example.pdfauth;

import com.example.pdfauth.service.PdfValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

public class PdfValidationServiceTest {

    @Test
    void rejectsNonPdf() {
        var service = new PdfValidationService();
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        var result = service.validate(file);
        assertFalse(result.valid());
    }
}
