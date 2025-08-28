package com.example;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Fazer GET para encontrar quais são os codigos do formulario e os cookies
// Fazer POST com os codigos do formulario no body e os cookies no header da requisição


public class Main {
    public static void main(String[] args) {
        String code = "AAAAAA-45000004833890";
        try {
            // antes mesmo de fazer essa tratativa podemos fazer uma verificacao se o codigo faz sentido (26 letras, somente numeros e 7o caractere eh um traco)
            getFileByCode(code);
        } catch(HttpTimeoutException error) {
            System.out.println("Codigo invalido");
        } catch(Exception error) {
            System.out.println("Erro desconhecido");
            error.printStackTrace();
        }
    }

    public static void getFileByCode(String document_code) throws Exception {
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
        params.put(first, document_code);
        
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
            .timeout(Duration.ofMillis(700))
            .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(params)))
            .build();
        
        HttpResponse<byte[]> r2 = client.send(request2, BodyHandlers.ofByteArray());

        savePDF(r2.body());
    }

    private static void savePDF(byte[] response) {
        try (FileOutputStream fos = new FileOutputStream("DDD.pdf")) {
            fos.write(response);
            // fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        } catch( Exception e) {
            System.out.println(e.getMessage());
        }
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
}