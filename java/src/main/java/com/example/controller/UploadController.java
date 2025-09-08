package com.example.controller;

import com.example.service.PdfValidationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Validated
public class UploadController {

    private final PdfValidationService pdfValidationService;

    public UploadController(PdfValidationService pdfValidationService) {
        this.pdfValidationService = pdfValidationService;
    }

    @GetMapping("/pdf-validator")
    public String index() {
        return "index";
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleUpload(@RequestParam("file") @NotNull MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a PDF file to upload.");
            model.addAttribute("status", "error");
            return "result";
        }

        var result = pdfValidationService.validate(file);
        model.addAttribute("message", result.message());
        model.addAttribute("status", result.valid() ? "success" : "error");
        return "result";
    }
}
