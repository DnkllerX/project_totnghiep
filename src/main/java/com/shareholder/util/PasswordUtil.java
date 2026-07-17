package com.shareholder.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Bam va kiem tra mat khau bang BCrypt. Khong bao gio luu/so sanh plaintext.
 */
public class PasswordUtil {

    private static final int WORK_FACTOR = 12; // cang cao cang an toan nhung cang cham, 12 la muc can bang hop ly

    private PasswordUtil() {}

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // hash bi sai dinh dang -> coi nhu khong khop, khong nem loi ra ngoai
            return false;
        }
    }
}
