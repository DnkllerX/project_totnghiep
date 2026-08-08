package com.shareholder.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shareholder.service.ChatbotService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Endpoint noi bo cho AI Chatbox (goc phai man hinh), dung chung cho ca khach (guest) va
 * nguoi dung da dang nhap (SHAREHOLDER/ADMIN/IT) - KHONG nam duoi /app/* nen khong bi
 * AuthFilter chan.
 *
 * Frontend (ai-chatbox.jsp) POST JSON:
 *   { "message": "...", "history": [{"role":"user"|"model","text":"..."}, ...] }
 * Tra ve JSON:
 *   200 { "reply": "..." }
 *   4xx/5xx { "error": "..." }
 *
 * API key Gemini CHI song o server (ChatbotService/gemini.properties), khong bao gio
 * xuong trinh duyet.
 */
@WebServlet("/chatbot")
public class ChatbotServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ChatbotServlet.class.getName());
    private static final int MAX_MESSAGE_LENGTH = 2000;
    // Servlet API khong dinh nghia san hang so cho HTTP 429.
    private static final int SC_TOO_MANY_REQUESTS = 429;
    private static final String SESSION_COUNT_ATTR = "chatbot_msg_count";

    private final ChatbotService chatbotService = new ChatbotService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        if (!chatbotService.isEnabled()) {
            writeError(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Tro ly AI hien chua duoc cau hinh. Vui long lien he quan tri vien de bat tinh nang nay.");
            return;
        }

        JsonObject payload;
        try {
            payload = JsonParser.parseString(readBody(req)).getAsJsonObject();
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Du lieu gui len khong hop le.");
            return;
        }

        String message = payload.has("message") && !payload.get("message").isJsonNull()
                ? payload.get("message").getAsString().trim() : "";

        if (message.isEmpty()) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Vui long nhap noi dung can hoi.");
            return;
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "Noi dung qua dai (toi da " + MAX_MESSAGE_LENGTH + " ky tu).");
            return;
        }

        HttpSession session = req.getSession(true);

        // Chong spam/lam dung chi phi API: gioi han so tin nhan trong 1 phien lam viec.
        Integer count = (Integer) session.getAttribute(SESSION_COUNT_ATTR);
        int newCount = (count == null ? 0 : count) + 1;
        if (newCount > chatbotService.getMaxMessagesPerSession()) {
            writeError(resp, SC_TOO_MANY_REQUESTS,
                    "Ban da dat gioi han so cau hoi cho phien lam viec nay. Vui long tai lai trang de tiep tuc.");
            return;
        }
        session.setAttribute(SESSION_COUNT_ATTR, newCount);

        List<ChatbotService.ChatTurn> history = new ArrayList<>();
        if (payload.has("history") && payload.get("history").isJsonArray()) {
            JsonArray arr = payload.getAsJsonArray("history");
            for (int i = 0; i < arr.size(); i++) {
                try {
                    JsonObject turn = arr.get(i).getAsJsonObject();
                    String role = turn.has("role") ? turn.get("role").getAsString() : "user";
                    String text = turn.has("text") ? turn.get("text").getAsString() : "";
                    if (!text.isBlank()) {
                        history.add(new ChatbotService.ChatTurn(role, text));
                    }
                } catch (Exception ignored) {
                    // Bo qua phan tu history bi loi dinh dang, khong lam hong ca request
                }
            }
        }

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        String currentPage = payload.has("page") && !payload.get("page").isJsonNull()
                ? payload.get("page").getAsString() : null;

        StringBuilder contextNote = new StringBuilder();
        if (role != null) {
            contextNote.append("Nguoi dung da dang nhap voi vai tro ").append(role);
            if (username != null) contextNote.append(" (ten dang nhap: ").append(username).append(")");
            contextNote.append(".");
        } else {
            contextNote.append("Nguoi dung CHUA dang nhap (khach).");
        }
        if (currentPage != null && !currentPage.isBlank()) {
            contextNote.append(" Dang xem trang: ").append(currentPage).append(".");
        }

        try {
            String reply = chatbotService.chat(history, message, contextNote.toString());
            JsonObject out = new JsonObject();
            out.addProperty("reply", reply);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(out.toString());
        } catch (ChatbotService.ChatbotException e) {
            LOGGER.log(Level.WARNING, "Chatbot loi: " + e.getMessage());
            writeError(resp, HttpServletResponse.SC_BAD_GATEWAY, e.getMessage());
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new java.io.InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
            char[] buf = new char[1024];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
        }
        return sb.toString();
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        JsonObject out = new JsonObject();
        out.addProperty("error", message);
        resp.getWriter().write(out.toString());
    }
}
