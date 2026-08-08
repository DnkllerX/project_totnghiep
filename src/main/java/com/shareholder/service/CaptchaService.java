package com.shareholder.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xac minh Google reCAPTCHA v2 ("I'm not a robot" checkbox) o phia server. Config doc tu
 * src/main/resources/recaptcha.properties. Neu recaptcha.enabled=false (chua cau hinh xong,
 * vd moi truong dev), verify() luon tra ve true de khong chan luong dang nhap/dang ky khi dev.
 */
public class CaptchaService {

    private static final Logger LOGGER = Logger.getLogger(CaptchaService.class.getName());
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final Pattern SUCCESS_PATTERN = Pattern.compile("\"success\"\\s*:\\s*(true|false)");

    private final Properties config = new Properties();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public CaptchaService() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("recaptcha.properties")) {
            if (in != null) config.load(in);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Khong doc duoc recaptcha.properties, coi nhu tat CAPTCHA", e);
        }
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(config.getProperty("recaptcha.enabled", "false"));
    }

    public String getSiteKey() {
        return config.getProperty("recaptcha.site.key", "");
    }

    /**
     * Goi API cua Google de xac minh g-recaptcha-response nguoi dung gui len.
     * @return true neu xac minh thanh cong (hoac CAPTCHA dang tat), false neu that bai/loi
     */
    public boolean verify(String gRecaptchaResponse, String remoteIp) {
        if (!isEnabled()) return true; // CAPTCHA dang tat (dev/chua cau hinh) - khong chan nguoi dung
        if (gRecaptchaResponse == null || gRecaptchaResponse.isBlank()) return false;

        String secretKey = config.getProperty("recaptcha.secret.key", "");
        if (secretKey.isBlank() || secretKey.startsWith("CHANGE_ME")) {
            LOGGER.warning("recaptcha.secret.key chua duoc cau hinh - tu choi xac minh CAPTCHA");
            return false;
        }

        String form = "secret=" + encode(secretKey) +
                "&response=" + encode(gRecaptchaResponse) +
                (remoteIp != null && !remoteIp.isBlank() ? "&remoteip=" + encode(remoteIp) : "");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VERIFY_URL))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Matcher matcher = SUCCESS_PATTERN.matcher(response.body());
            return matcher.find() && Boolean.parseBoolean(matcher.group(1));
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Goi Google reCAPTCHA siteverify that bai", e);
            // Loi mang/timeout -> tu choi (fail-closed) thay vi mac dinh cho qua
            return false;
        }
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
