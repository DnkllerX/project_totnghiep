package com.shareholder.util;

import java.util.regex.Pattern;

/**
 * Validate input co ban o tang Service/Controller truoc khi cham vao DB.
 * Day la lop phong thu bo sung, KHONG thay the cho PreparedStatement (van la lop chinh chong SQLi).
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{4,50}$");
    private static final Pattern CITIZEN_ID_PATTERN = Pattern.compile("^[0-9]{9,12}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{8,20}$");

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidCitizenId(String citizenId) {
        return citizenId != null && CITIZEN_ID_PATTERN.matcher(citizenId).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone == null || phone.isBlank() || PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Mat khau toi thieu 8 ky tu, co it nhat 1 chu va 1 so. KHONG con dung de CHAN tao tai khoan -
     * chi con lai isPasswordNonEmpty() lam dieu kien bat buoc. Giu ham nay lai phong khi can hien thi
     * goi y phia server; thanh do do manh tren giao dien la o client (xem js/password-strength.js).
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }

    /** Dieu kien BAT BUOC duy nhat cho mat khau: khong duoc rong. Do manh chi la goi y, khong chan. */
    public static boolean isPasswordNonEmpty(String password) {
        return password != null && !password.isEmpty();
    }

    public static boolean isPositiveInt(Integer value) {
        return value != null && value > 0;
    }

    public static boolean isNonNegativeInt(Integer value) {
        return value != null && value >= 0;
    }

    /** Cat bot khoang trang thua, tra ve chuoi rong neu null - dung truoc khi luu cac truong text tu do. */
    public static String sanitizeText(String input, int maxLength) {
        if (input == null) return "";
        String trimmed = input.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }
}
