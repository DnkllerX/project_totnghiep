package vinscape.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Doc cau hinh tu file config.properties (src/test/resources).
 * Uu tien: System property (-Dkey=value khi chay mvn) > gia tri trong file.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("Khong tim thay config.properties trong classpath");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Loi doc config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : Boolean.parseBoolean(value);
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String baseUrl() {
        String url = get("base.url", "http://localhost:8080/shareholder-system");
        // Bo dau "/" cuoi neu co, de cac page object tu ghep duong dan cho nhat quan
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
