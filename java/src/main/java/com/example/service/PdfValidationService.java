package com.example.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class PdfValidationService {

	// Base phrase URL we expect to find in the PDF
	private static final String UFSC_AUTHENTICATE_URL = "https://cagr.ufsc.br/autenticidade";

	// Code pattern like 310516-45000004814119
	private static final Pattern CODE_PATTERN = Pattern.compile("([0-9]{6}-[0-9]{14})");


	public record ValidationResult(boolean valid, String message) {}

	public static ValidationResult validate(MultipartFile file) {
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

			// Download authenticity copy and compare
			byte[] originalBytes = file.getBytes();
			byte[] downloaded = downloadPdf(code);
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
		} catch (Exception e) {
			return new ValidationResult(false, "Failed to download authenticity PDF: " + e.getMessage());
		}
	}

	public static byte[] downloadPdf(String code) throws Exception {
        Map<String,String> params = new HashMap<>();
        params.put("verificaAutForm", "verificaAutForm");
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://cagr.sistemas.ufsc.br/autenticidade/"))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        // fazer um metodo especializado
        String cookie_to_post = getCookies(response);

        // Encontra os ids dos campos para fazer o POST
        String response_body = response.body();
        // Value = CODIGO DO DOCUMENTO
        String first  = "verificaAutForm:" + getCode(response_body, "<td class=\" col2\"><input type=\"text\" name=\"verificaAutForm:");
        params.put(first, code);
        
        // Value = verificar
        String second  = "verificaAutForm:" + getCode(response_body, "<br /><input type=\"submit\" name=\"verificaAutForm:");
        params.put(second, "Verificar");

        // Esse se trata do value de "javax.faces.ViewState"
        String third  = getCode(response_body, "</div><input type=\"hidden\" name=\"javax.faces.ViewState\" id=\"javax.faces.ViewState\" value=\"");
        params.put("javax.faces.ViewState", third);
        

        HttpRequest request2 = HttpRequest.newBuilder()
            .uri(URI.create("https://cagr.sistemas.ufsc.br/autenticidade/"))
            .header("content-type", "application/x-www-form-urlencoded")
            .header("Cookie", cookie_to_post)
            .header("accept", "*/*")
            .header("accept-encoding", "gzip, deflate, br, zstd")
            // .timeout(Duration.ofMillis(1000))
            .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(params)))
            .build();
        
        HttpResponse<byte[]> r2 = client.send(request2, BodyHandlers.ofByteArray());
 		  return r2.body();				
    }

    private static String getCookies(HttpResponse<String> response) {
        List<String> cookies = response.headers().allValues("set-cookie");
        String cookie_to_post = "";
        for(String cookie: cookies) {
            for(int i = 0; i < cookie.length(); i++) {
                cookie_to_post += cookie.charAt(i);
                if (cookie.charAt(i) == ';') {
                    break;
                }
            }
        }

        return cookie_to_post;
    }

    private static String getCode(String body, String subString) {
        String ret = "";

        for (int i = body.indexOf(subString, 0) + subString.length(); i < body.length();i++) {
            if (body.charAt(i) == '"') {
                break;
            }
            ret+=body.charAt(i);
        }

        return ret;
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
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
