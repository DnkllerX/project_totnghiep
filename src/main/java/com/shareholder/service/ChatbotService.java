package com.shareholder.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Goi Google Gemini API (generateContent) de tra loi cho AI Chatbox.
 * Config doc tu src/main/resources/gemini.properties (xem gemini.properties.example).
 *
 * API key CHI nam o server (properties file, khong nhung vao JSP/JS) - trinh duyet chi
 * goi ve servlet noi bo (/chatbot), khong bao gio goi thang len Google.
 */
public class ChatbotService {

    private static final Logger LOGGER = Logger.getLogger(ChatbotService.class.getName());

    /** Mot luot hoi/dap don gian de truyen lich su hoi thoai. */
    public record ChatTurn(String role, String text) {
        public ChatTurn {
            role = "model".equalsIgnoreCase(role) ? "model" : "user";
        }
    }

    public static class ChatbotException extends Exception {
        public ChatbotException(String message) { super(message); }
        public ChatbotException(String message, Throwable cause) { super(message, cause); }
    }

    private final Properties config = new Properties();
    private final String apiKey;
    private HttpClient httpClient;

    public ChatbotService() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("gemini.properties")) {
            if (in != null) config.load(in);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Khong doc duoc gemini.properties, tro ly AI se tat", e);
        }
        this.apiKey = resolveApiKey();
    }

    /**
     * Uu tien lay API key tu bien moi truong GEMINI_API_KEY (khuyen nghi cho production -
     * khong nam trong file, khong bi push len Git). Neu khong co, fallback ve
     * gemini.api.key trong gemini.properties (tien loi cho dev/test local).
     *
     * Khong throw exception khi thieu key: chi log canh bao va de isEnabled() tra ve
     * false, de servlet tra 503 "chua cau hinh" thay vi lam sap ca ung dung.
     */
    private String resolveApiKey() {
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            LOGGER.log(Level.INFO, "Gemini API key: dang dung bien moi truong GEMINI_API_KEY");
            return envKey.trim();
        }

        String propKey = config.getProperty("gemini.api.key", "");
        if (propKey != null && !propKey.isBlank() && !propKey.startsWith("CHANGE_ME")) {
            LOGGER.log(Level.WARNING,
                    "Gemini API key: dang dung gemini.properties (fallback). Khuyen nghi chuyen "
                            + "sang bien moi truong GEMINI_API_KEY cho moi truong production.");
            return propKey.trim();
        }

        LOGGER.log(Level.WARNING,
                "Gemini API key: khong tim thay o bien moi truong GEMINI_API_KEY lan gemini.properties. "
                        + "Tro ly AI se bi tat (isEnabled() = false).");
        return "";
    }

    private HttpClient client() {
        if (httpClient == null) {
            httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(getTimeoutSeconds()))
                    .build();
        }
        return httpClient;
    }

    public boolean isEnabled() {
        boolean enabled = Boolean.parseBoolean(config.getProperty("gemini.enabled", "false"));
        return enabled && !apiKey.isBlank();
    }

    public int getMaxMessagesPerSession() {
        return parseIntSafe(config.getProperty("gemini.max.messages.per.session", "60"), 60);
    }

    private int getTimeoutSeconds() {
        return parseIntSafe(config.getProperty("gemini.timeout.seconds", "20"), 20);
    }

    private int getMaxHistory() {
        return parseIntSafe(config.getProperty("gemini.max.history", "10"), 10);
    }

    private int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Goi Gemini de sinh cau tra loi.
     *
     * @param history      lich su hoi thoai truoc do (co the rong), theo thu tu cu -> moi
     * @param userMessage  tin nhan moi nhat cua nguoi dung
     * @param contextNote  ngu canh bo sung (vd trang dang xem, vai tro dang nhap) noi vao system prompt
     * @return noi dung tra loi cua AI (da rut gon do dai neu can)
     */
    public String chat(List<ChatTurn> history, String userMessage, String contextNote) throws ChatbotException {
        if (!isEnabled()) {
            throw new ChatbotException("Tro ly AI hien chua duoc cau hinh API key. Vui long lien he quan tri vien.");
        }
        if (userMessage == null || userMessage.isBlank()) {
            throw new ChatbotException("Tin nhan trong.");
        }

        String model = config.getProperty("gemini.model", "gemini-3.1-flash-lite");
        String baseUrl = config.getProperty("gemini.api.base.url",
                "https://generativelanguage.googleapis.com/v1beta/models");
        String systemPrompt = config.getProperty("gemini.system.prompt", "");
        if (contextNote != null && !contextNote.isBlank()) {
            systemPrompt = systemPrompt + "\n\nNgu canh hien tai: " + contextNote;
        }

        JsonObject body = buildRequestBody(history, userMessage, systemPrompt);

        String url = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(getTimeoutSeconds()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOGGER.log(Level.WARNING, "Gemini API tra ve loi HTTP {0}: {1}",
                        new Object[]{response.statusCode(), truncate(response.body(), 500)});
                throw new ChatbotException("Tro ly AI dang gap su co (ma loi " + response.statusCode()
                        + "). Vui long thu lai sau.");
            }

            return parseReply(response.body());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Goi Gemini API that bai", e);
            throw new ChatbotException("Khong the ket noi toi dich vu AI luc nay. Vui long thu lai sau.", e);
        }
    }

    private JsonObject buildRequestBody(List<ChatTurn> history, String userMessage, String systemPrompt) {
        JsonObject body = new JsonObject();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            JsonObject systemInstruction = new JsonObject();
            JsonArray sysParts = new JsonArray();
            JsonObject sysPart = new JsonObject();
            sysPart.addProperty("text", systemPrompt);
            sysParts.add(sysPart);
            systemInstruction.add("parts", sysParts);
            body.add("systemInstruction", systemInstruction);
        }

        JsonArray contents = new JsonArray();
        if (history != null) {
            int maxHistory = getMaxHistory();
            int start = Math.max(0, history.size() - maxHistory);
            for (int i = start; i < history.size(); i++) {
                ChatTurn turn = history.get(i);
                if (turn.text() == null || turn.text().isBlank()) continue;
                contents.add(toContent(turn.role(), turn.text()));
            }
        }
        contents.add(toContent("user", userMessage));
        body.add("contents", contents);

        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature",
                Double.parseDouble(config.getProperty("gemini.temperature", "0.4")));
        generationConfig.addProperty("maxOutputTokens",
                parseIntSafe(config.getProperty("gemini.max.output.tokens", "1024"), 1024));
        body.add("generationConfig", generationConfig);

        return body;
    }

    private JsonObject toContent(String role, String text) {
        JsonObject content = new JsonObject();
        content.addProperty("role", role);
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", text);
        parts.add(part);
        content.add("parts", parts);
        return content;
    }

    private String parseReply(String responseBody) throws ChatbotException {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray candidates = root.getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new ChatbotException("AI khong tra ve noi dung. Vui long dien dat lai cau hoi.");
            }
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            if (content == null) {
                throw new ChatbotException("AI khong tra ve noi dung. Vui long dien dat lai cau hoi.");
            }
            JsonArray parts = content.getAsJsonArray("parts");
            StringBuilder sb = new StringBuilder();
            if (parts != null) {
                for (int i = 0; i < parts.size(); i++) {
                    JsonObject part = parts.get(i).getAsJsonObject();
                    if (part.has("text")) {
                        sb.append(part.get("text").getAsString());
                    }
                }
            }
            String reply = sb.toString().trim();
            if (reply.isEmpty()) {
                throw new ChatbotException("AI khong tra ve noi dung. Vui long dien dat lai cau hoi.");
            }
            return reply;
        } catch (ChatbotException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Khong parse duoc phan hoi Gemini: " + truncate(responseBody, 500), e);
            throw new ChatbotException("Khong doc duoc phan hoi tu AI. Vui long thu lai sau.", e);
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}