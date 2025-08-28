package com.example.pdfauth.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfValidationService {

    // Base phrase URL we expect to find in the PDF
    private static final String UFSC_AUTHENTICATE_URL = "https://cagr.ufsc.br/autenticidade";

    // Code pattern like 310516-45000004814119
    private static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{6}-[0-9]{14})");


    public record ValidationResult(boolean valid, String message) {}

    public ValidationResult validate(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            return new ValidationResult(false, "The uploaded file is not a PDF.");
        }
        try (var is = file.getInputStream(); var doc = PDDocument.load(is)) {
            if (doc.getNumberOfPages() <= 0) {
                return new ValidationResult(false, "Invalid PDF: no pages found.");
            }
            if (doc.isEncrypted()) {
                return new ValidationResult(false, "PDF is encrypted. Please upload an unencrypted PDF.");
            }

            // Extract text and validate presence of authenticity URL + code
            String text = new PDFTextStripper().getText(doc);
            String code = getVerificationCode(text);
            if (code == null || !containsAuthenticateUrl(text)) {
                return new ValidationResult(false, "Verification string not found in PDF.");
            }

            // Download authenticity copy (via JSF flow, then fallbacks) and compare
            byte[] originalBytes = file.getBytes();
            byte[] downloaded = downloadAuthenticPdf(code);
            if (downloaded == null || downloaded.length == 0) {
                return new ValidationResult(false, "Could not download authenticity PDF for code " + code + ".");
            }

            boolean equal = Arrays.equals(sha256(originalBytes), sha256(downloaded));
            if (!equal) {
                return new ValidationResult(false, "PDF content differs from authenticity copy (code: " + code + ").");
            }

            return new ValidationResult(true, "Valid PDF and matches authenticity copy. URL: " + UFSC_AUTHENTICATE_URL + ", code: " + code);
        } catch (IOException e) {
            return new ValidationResult(false, "Failed to read PDF: " + e.getMessage());
        }
    }

	 private static byte[] downloadAuthenticPdf(String codigo) {

		Path downloadDir = Paths.get("downloads").toAbsolutePath();
		try {
			Files.createDirectories(downloadDir);
		} catch (IOException e) {
			return null;
		}

		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", downloadDir.toString());
		prefs.put("download.prompt_for_download", false);
		prefs.put("download.directory_upgrade", true);
		prefs.put("safebrowsing.enabled", true);

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new", "--window-size=1280,1000");
		options.setExperimentalOption("prefs", prefs);
		
		WebDriver driver = new ChromeDriver();
		try {
            driver.get(UFSC_AUTHENTICATE_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // The input can have dynamic ids; target by nearby label text "Código"
            // Try a few robust locators:
            WebElement input = null;
            try {
                input = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//label[contains(.,'Código') or contains(.,'Codigo')]/following::input[1]")));
            } catch (TimeoutException e) {
                // fallback: first text input on the page
                input = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[type='text'], input:not([type])")));
            }

            input.clear();
            input.sendKeys(codigo);

            // Click the button that says "Verificar" (or contains that text)
            WebElement btn = null;
            try {
                btn = driver.findElement(By.xpath("//button[contains(.,'Verificar') or contains(.,'verificar')]"));
            } catch (NoSuchElementException e) {
                // fallback: first submit button
                btn = driver.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
            }
            btn.click();

            // The site typically triggers a file download on success.
            // We’ll wait a bit and then check that a new file appeared in the download directory.
            Path before = latestFile(downloadDir);
            boolean gotNewFile = waitForNewFile(downloadDir, before, Duration.ofSeconds(20));

            if (gotNewFile) {
                Path f = latestFile(downloadDir);
                System.out.println("Downloaded: " + f);
					 try {
						 byte[] data = Files.readAllBytes(f);
						 Files.delete(f); // clean up
						 return data;
					 } catch (IOException e) {
						 System.err.println("Failed to read downloaded file: " + e.getMessage());
						 return null;
					 }
            } else {
                // If nothing downloaded, page may have shown an error. Dump visible text to help debug.
                System.err.println("No file downloaded. Page text:\n" + driver.findElement(By.tagName("body")).getText());
					 return null;
            }
        } finally {
            driver.quit();
        }
    }

      private static Path latestFile(Path dir) {
        try (var s = Files.list(dir)) {
            return s.filter(Files::isRegularFile)
                    .max((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b));
                        } catch (Exception e) { return 0; }
                    }).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

     private static boolean waitForNewFile(Path dir, Path previousLatest, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            Path now = latestFile(dir);
            if (now != null && (previousLatest == null || !now.equals(previousLatest))) return true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private static boolean containsAuthenticateUrl(String text) {
        // Be tolerant to http/https and presence of www
        return Pattern.compile("https?://(?:www\\.)?cagr\\.ufsc\\.br/autenticidade", Pattern.CASE_INSENSITIVE)
                .matcher(text)
                .find();
    }

    private static String getVerificationCode(String text) {
        Matcher matcher = CODE_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    // Try the JSF form flow seen in the saved HTML, then known GET endpoints

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception e) {
            return data; // fallback; equality will be raw bytes compare if used
        }
    }
}
