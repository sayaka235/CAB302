package org.example.ai_integration;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class Encryption {

    // PTA auth token
    public static String getPtaToken(String ptaBaseUrl, String username, String password) throws Exception {
        var client = HttpClient.newHttpClient();
        var body = "username=" + url(username) + "&password=" + url(password);
        var req = HttpRequest.newBuilder()
                .uri(URI.create(ptaBaseUrl + "/installer/api/getauthtoken"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) throw new RuntimeException("Auth failed: " + res.statusCode() + " " + res.body());
        return res.body().trim();
    }

    //Fetch RSA public key
    public static String fetchServerEncryptionKeyB64(String ptaBaseUrl, String token) throws Exception {
        var client = HttpClient.newHttpClient();
        var req = HttpRequest.newBuilder()
                .uri(URI.create(ptaBaseUrl + "/installer/api/encryptionkey"))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) throw new RuntimeException("Key fetch failed: " + res.statusCode() + " " + res.body());
        return res.body().trim(); // Expected to be raw Base64, not JSON
    }

    //Encrypt API key
    public static String encryptForPTA(String serverKeyBase64, byte[] plaintext) throws Exception {
        byte[] der = Base64.getDecoder().decode(serverKeyBase64);
        var keySpec = new X509EncodedKeySpec(der);
        var kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(keySpec);

        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        } catch (Exception e) {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        }
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        byte[] ct = cipher.doFinal(plaintext);

        String b64 = Base64.getEncoder().encodeToString(ct);
        return "{encrypted}" + b64;
    }

    private static String url(String s) { return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8); }

    // Example usage
    public static void main(String[] args) throws Exception {
        String ptaBaseUrl = "https://pta.mycompany.com";
        String username = "installerUser";
        String password = "installerPass";
        String apiKey   = "MY_SUPER_SECRET_API_KEY";

        String token = getPtaToken(ptaBaseUrl, username, password);                                // (1) :contentReference[oaicite:3]{index=3}
        String serverKeyB64 = fetchServerEncryptionKeyB64(ptaBaseUrl, token);                      // (2) :contentReference[oaicite:4]{index=4}
        String encrypted = encryptForPTA(serverKeyB64, apiKey.getBytes(StandardCharsets.UTF_8));   // (3) :contentReference[oaicite:5]{index=5}

        System.out.println(encrypted);
    }
}
