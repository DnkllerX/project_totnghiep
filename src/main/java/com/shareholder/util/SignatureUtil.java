package com.shareholder.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

/**
 * Xu ly chu ky TAY ve tu canvas frontend (gui len dang base64 PNG), KHONG phai chu ky so/dien tu.
 *
 * Quy trinh:
 *   canvas.toDataURL() (frontend) -> "data:image/png;base64,iVBORw0KG..." (gui len server)
 *   -> server bo phan header "data:...;base64,", decode base64
 *   -> kiem tra magic bytes de chac chan la file PNG/JPEG that (khong tin Content-Type client gui)
 *   -> luu thanh file voi ten UUID (KHONG dung ten/duong dan tu client -> tranh path traversal)
 *   -> tra ve duong dan tuong doi de luu vao SHARE_ISSUE_DETAILS.signature_url
 */
public class SignatureUtil {

    private static final long MAX_SIZE_BYTES = 2L * 1024 * 1024; // 2MB, chu ky tay khong can lon hon
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    private SignatureUtil() {}

    public static class InvalidSignatureException extends Exception {
        public InvalidSignatureException(String message) { super(message); }
    }

    /**
     * @param dataUrlOrBase64 chuoi base64 (co the kem prefix "data:image/png;base64,")
     * @param storageRootDir  thu muc goc luu chu ky tren server (ngoai webapp de tranh truy cap truc tiep qua URL)
     * @param issueId         dung de phan thu muc con
     * @param shareholderId   dung de phan thu muc con
     * @return duong dan tuong doi (vd: "signatures/issue_5/3f2a1b.png") de luu vao DB
     */
    public static String saveHandwrittenSignature(String dataUrlOrBase64, String storageRootDir,
                                                    int issueId, int shareholderId)
            throws InvalidSignatureException, IOException {

        if (dataUrlOrBase64 == null || dataUrlOrBase64.isBlank()) {
            throw new InvalidSignatureException("Du lieu chu ky rong");
        }

        String base64Payload = dataUrlOrBase64;
        int commaIdx = dataUrlOrBase64.indexOf(",");
        if (dataUrlOrBase64.startsWith("data:") && commaIdx > -1) {
            base64Payload = dataUrlOrBase64.substring(commaIdx + 1);
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64Payload);
        } catch (IllegalArgumentException e) {
            throw new InvalidSignatureException("Du lieu base64 khong hop le");
        }

        if (imageBytes.length == 0 || imageBytes.length > MAX_SIZE_BYTES) {
            throw new InvalidSignatureException("Kich thuoc anh chu ky khong hop le (toi da 2MB)");
        }

        String extension = detectImageExtension(imageBytes);
        if (extension == null) {
            throw new InvalidSignatureException("File khong phai anh PNG/JPEG hop le");
        }

        // Ten thu muc con dung so nguyen (issueId, shareholderId) -> khong the path-traversal
        Path targetDir = Paths.get(storageRootDir, "issue_" + issueId);
        Files.createDirectories(targetDir);

        String fileName = UUID.randomUUID() + "." + extension;
        Path targetFile = targetDir.resolve(fileName);

        try (OutputStream out = Files.newOutputStream(targetFile, StandardOpenOption.CREATE_NEW)) {
            out.write(imageBytes);
        }

        return "signatures/issue_" + issueId + "/" + fileName;
    }

    /** Kiem tra magic bytes thuc te, khong tin Content-Type/ten file client gui len. */
    private static String detectImageExtension(byte[] bytes) {
        if (startsWith(bytes, PNG_MAGIC)) return "png";
        if (startsWith(bytes, JPEG_MAGIC)) return "jpg";
        return null;
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }
}
