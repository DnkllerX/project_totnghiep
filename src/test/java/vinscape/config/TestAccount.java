package vinscape.config;

/**
 * Tai khoan test co san (du lieu seed cua snapshot01db), dung xuyen suot cac test.
 */
public enum TestAccount {

    ADMIN(ConfigReader.get("admin.login"), ConfigReader.get("admin.password"), "ADMIN"),
    IT(ConfigReader.get("it.login"), ConfigReader.get("it.password"), "IT"),
    SHAREHOLDER(ConfigReader.get("shareholder.login"), ConfigReader.get("shareholder.password"), "SHAREHOLDER");

    private final String login;
    private final String password;
    private final String role;

    TestAccount(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String login() {
        return login;
    }

    public String password() {
        return password;
    }

    public String role() {
        return role;
    }
}
