package vinscape.pages.admin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/financial-reports/manage (admin/financial-reports.jsp)
 */
public class AdminFinancialReportsPage extends AppShellPage {

    public AdminFinancialReportsPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/financial-reports/manage";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public Locator reportYearInput() {
        return page.locator("[name='reportYear']");
    }

    public Locator reportQuarterSelect() {
        return page.locator("[name='reportQuarter']");
    }

    public Locator revenueInput() {
        return page.locator("[name='revenue']");
    }

    public Locator profitBeforeTaxInput() {
        return page.locator("[name='profitBeforeTax']");
    }

    public Locator profitAfterTaxInput() {
        return page.locator("[name='profitAfterTax']");
    }

    public Locator submitButton() {
        return page.locator("#frSubmitBtn");
    }

    public Locator chartCanvas() {
        return page.locator("#financialChartAdmin");
    }

    // --- Tinh nang SUA bao cao (them sau nay) ---
    public Locator formCard() {
        return page.locator("#frFormCard");
    }

    public Locator formTitle() {
        return page.locator("#frFormTitle span").first();
    }

    public Locator reportIdHidden() {
        return page.locator("#frReportId");
    }

    public Locator cancelEditLink() {
        return page.locator("#frCancelEdit");
    }

    public Locator editButtons() {
        return page.locator(".btn-edit-row");
    }

    public Locator editButtonForRow(int rowIndex) {
        return editButtons().nth(rowIndex);
    }

    public int reportRowCount() {
        return page.locator("table tbody tr").count();
    }

    public void clickEditOnFirstRow() {
        editButtons().first().click();
    }

    // --- Bieu do tron "Co cau doanh thu theo quy" ---
    public Locator pieCanvas() {
        return page.locator("#financialPieAdmin");
    }

    public Locator pieYearTabs() {
        return page.locator(".fr-year-tab");
    }

    public Locator pieLegendItems() {
        return page.locator(".fr-pie-legend-item");
    }
}
