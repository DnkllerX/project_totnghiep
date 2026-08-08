package com.shareholder.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gui email qua SMTP (reset mat khau, thong bao duyet tai khoan). Cau hinh doc tu
 * src/main/resources/mail.properties - KHONG bao gio hardcode SMTP credential trong code.
 *
 * Neu mail.enabled=false (mac dinh khi chua cau hinh SMTP that), EmailService CHI log noi dung
 * email ra console (getServletContext log) thay vi goi SMTP that - giup dev test luong nghiep vu
 * (duyet tai khoan, reset mat khau) ma khong can SMTP that trong moi truong dev.
 *
 * QUAN TRONG: goi service nay KHONG duoc lam fail nghiep vu chinh (duyet tai khoan / reset mat khau
 * van phai thanh cong du SMTP loi) - moi noi goi EmailService deu nen bat EmailException va chi log,
 * khong throw nguoc len lam rollback nghiep vu.
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    public static class EmailException extends Exception {
        public EmailException(String message, Throwable cause) { super(message, cause); }
    }

    private final Properties config = new Properties();
    private final boolean enabled;
    private final String smtpPassword;

    public EmailService() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) config.load(in);
        } catch (IOException e) {
            // Khong co file cau hinh -> coi nhu tat tinh nang gui mail, khong crash app
        }
        this.enabled = Boolean.parseBoolean(config.getProperty("mail.enabled", "false"));
        this.smtpPassword = resolveSmtpPassword();
    }

    /**
     * Uu tien lay mat khau SMTP tu bien moi truong MAIL_SMTP_PASSWORD (khuyen nghi cho
     * production - khong nam trong file, khong bi push len Git). Neu khong co, fallback ve
     * mail.smtp.password trong mail.properties (tien loi cho dev/test local).
     */
    private String resolveSmtpPassword() {
        String envPassword = System.getenv("MAIL_SMTP_PASSWORD");
        if (envPassword != null && !envPassword.isBlank()) {
            LOGGER.log(Level.INFO, "SMTP password: dang dung bien moi truong MAIL_SMTP_PASSWORD");
            return envPassword;
        }

        String propPassword = config.getProperty("mail.smtp.password", "");
        if (propPassword != null && !propPassword.isBlank()) {
            LOGGER.log(Level.WARNING,
                    "SMTP password: dang dung mail.properties (fallback). Khuyen nghi chuyen "
                            + "sang bien moi truong MAIL_SMTP_PASSWORD cho moi truong production.");
            return propPassword;
        }

        if (enabled) {
            LOGGER.log(Level.WARNING,
                    "SMTP password: khong tim thay o bien moi truong MAIL_SMTP_PASSWORD lan "
                            + "mail.properties du mail.enabled=true. Gui mail se that bai luc authenticate.");
        }
        return "";
    }

    public void sendAccountApprovedEmail(String toEmail, String toName, String username)
            throws EmailException {
        String subject = "Tai khoan cua ban da duoc duyet";
        String body = "Xin chao " + toName + ",\n\n" +
                "Tai khoan co dong cua ban (username: " + username + ") da duoc quan tri vien phe duyet " +
                "va co the dang nhap ngay bay gio.\n\n" +
                "Neu ban khong thuc hien dang ky nay, vui long lien he bo phan quan tri de duoc ho tro.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    /**
     * Thong bao truc tiep khi tai khoan SHAREHOLDER bi khoa - KHAC voi luong xac nhan qua email
     * danh cho ADMIN/IT (xem sendActionConfirmationEmail): day chi la thong bao MOT CHIEU, hanh
     * dong da co hieu luc ngay, khong can nguoi dung xac nhan gi ca (dung khi IT khoa tai khoan
     * khach hang binh thuong, khong phai tinh huong leo thang dac quyen nhu voi ADMIN/IT).
     */
    public void sendAccountLockedEmail(String toEmail, String toName, String username)
            throws EmailException {
        String subject = "Tai khoan cua ban da bi khoa";
        String body = "Xin chao " + toName + ",\n\n" +
                "Tai khoan cua ban (username: " + username + ") vua bi bo phan IT khoa, khong the " +
                "dang nhap cho den khi duoc mo khoa lai.\n\n" +
                "Neu ban cho rang day la nham lan, vui long lien he bo phan quan tri de duoc ho tro " +
                "kiem tra va mo khoa lai.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    /**
     * Thong bao truc tiep khi ADMIN dieu chinh/gan lai so co phan cua 1 co dong - mot chieu,
     * khong can xac nhan (day khong phai thao tac len tai khoan he thong nhu lock/reset-password,
     * chi la thong bao thay doi so du co phan - nhung van can bao ngay vi anh huong quyen loi tai chinh).
     */
    public void sendShareAdjustmentEmail(String toEmail, String toName, int oldQuantity,
                                          int newQuantity, String reason) throws EmailException {
        String subject = "So co phan cua ban vua duoc dieu chinh";
        String delta = newQuantity > oldQuantity ? "+" + (newQuantity - oldQuantity) : String.valueOf(newQuantity - oldQuantity);
        String body = "Xin chao " + toName + ",\n\n" +
                "So co phan cua ban trong He thong Quan ly Co dong vua duoc dieu chinh boi quan tri vien:\n\n" +
                "  So cu:      " + oldQuantity + "\n" +
                "  So moi:     " + newQuantity + " (" + delta + ")\n" +
                "  Ly do:      " + reason + "\n\n" +
                "Neu ban co thac mac ve thay doi nay, vui long lien he bo phan quan tri de duoc giai thich.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    public void sendPasswordResetEmail(String toEmail, String toName, String username, String tempPassword)
            throws EmailException {
        String subject = "Mat khau tam thoi cho tai khoan cua ban";
        String body = "Xin chao " + toName + ",\n\n" +
                "Quan tri vien da dat lai mat khau cho tai khoan (username: " + username + ") cua ban.\n\n" +
                "Mat khau tam thoi: " + tempPassword + "\n\n" +
                "Vi ly do bao mat, vui long dang nhap va DOI MAT KHAU NGAY sau khi nhan duoc email nay. " +
                "Mat khau tam thoi nay chi nen dung 1 lan.\n\n" +
                "Neu ban khong yeu cau reset mat khau, vui long lien he bo phan quan tri ngay lap tuc.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    public void sendTransferCompletedEmail(String toEmail, String toName, String partyRole,
                                           int quantity, String counterpartName) throws EmailException {
        String subject = "Giao dich chuyen nhuong co phan da hoan tat";
        String roleText = "nguoi chuyen".equals(partyRole)
                ? "ban da chuyen " + quantity + " co phan cho " + counterpartName
                : "ban da nhan " + quantity + " co phan tu " + counterpartName;
        String body = "Xin chao " + toName + ",\n\n" +
                "Yeu cau chuyen nhuong co phan da duoc ADMIN duyet va hoan tat: " + roleText + ".\n\n" +
                "Ban co the dang nhap va xem lai lich su giao dich trong trang \"Chuyen nhuong\" cua he thong.\n\n" +
                "Neu day khong phai giao dich ban thuc hien, vui long lien he bo phan quan tri ngay lap tuc.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    /**
     * Gui LINK tu doi mat khau (khac voi sendPasswordResetEmail() o tren - cai do gui thang mat khau
     * TAM cho admin/IT reset ho; cai nay gui LINK de chinh nguoi dung tu bam doi, dung cho tinh nang
     * "Quen mat khau" tu phuc vu, xac thuc bang JWT (xem JwtUtil.generatePasswordResetToken).
     */
    public void sendPasswordResetLinkEmail(String toEmail, String toName, String resetLink)
            throws EmailException {
        String subject = "Yeu cau dat lai mat khau";
        String body = "Xin chao " + toName + ",\n\n" +
                "He thong nhan duoc yeu cau dat lai mat khau cho tai khoan cua ban. Bam vao link ben duoi " +
                "de dat mat khau moi (link co hieu luc trong 30 phut va CHI dung duoc 1 lan):\n\n" +
                resetLink + "\n\n" +
                "Neu ban khong yeu cau dat lai mat khau, hay bo qua email nay - mat khau cua ban se " +
                "KHONG bi thay doi.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    /**
     * Gui link "xac nhan hanh dong nhay cam" toi CHINH tai khoan bi tac dong (ADMIN/IT khac), khi
     * IT muon dat lai mat khau hoac khoa tai khoan do. Hanh dong CHUA co hieu luc cho den khi
     * chinh chu tai khoan nay tu bam link xac nhan - chan IT tu leo thang chiem quyen ADMIN/IT
     * khac chi bang cach reset mat khau ho ma khong can biet mat khau cu (xem JwtUtil.generateActionConfirmToken).
     */
    public void sendActionConfirmationEmail(String toEmail, String toName, String actorUsername,
                                             String actionLabelVi, String confirmLink) throws EmailException {
        String subject = "Yeu cau xac nhan: " + actionLabelVi + " tai khoan cua ban";
        String body = "Xin chao " + toName + ",\n\n" +
                "Tai khoan IT \"" + actorUsername + "\" vua yeu cau " + actionLabelVi +
                " CHO TAI KHOAN CUA BAN trong He thong Quan ly Co dong.\n\n" +
                "Vi day la thao tac nhay cam (tac dong len tai khoan quan tri), hanh dong nay CHUA " +
                "duoc thuc hien - chi co hieu luc khi CHINH BAN bam xac nhan qua link ben duoi " +
                "(link co hieu luc trong 30 phut va CHI dung duoc 1 lan):\n\n" +
                confirmLink + "\n\n" +
                "Neu DUNG la ban (hoac ban dong y) yeu cau nay, hay bam link de xac nhan.\n\n" +
                "Neu KHONG phai ban yeu cau, TUYET DOI KHONG bam link nay - hay doi mat khau ngay " +
                "lap tuc va bao cap tren/bo phan quan tri de kiem tra tai khoan IT noi tren.\n\n" +
                "Tran trong,\nHe thong Quan ly Co dong";
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String body) throws EmailException {
        if (!enabled) {
            // Che do dev/chua cau hinh SMTP that: chi log ra console de kiem tra noi dung.
            LOGGER.log(Level.INFO, "[mail.enabled=false] To={0} Subject={1}\n{2}",
                    new Object[]{toEmail, subject, body});
            return;
        }

        String host = config.getProperty("mail.smtp.host");
        String port = config.getProperty("mail.smtp.port", "587");
        String username = config.getProperty("mail.smtp.username");
        String password = smtpPassword;
        String fromAddress = config.getProperty("mail.from.address", username);
        String fromName = config.getProperty("mail.from.name", "He thong Quan ly Co dong");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress, fromName, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setText(body, "UTF-8");
            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Gui email that bai toi " + toEmail, e);
        }
    }
}