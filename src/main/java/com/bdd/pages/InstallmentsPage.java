package com.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object for Installment Plans dashboard.
 * Demonstrates clean POM patterns and usage of BasePage table actions.
 */
public class InstallmentsPage extends BasePage {

    @FindBy(id = "installmentsTable")
    private WebElement installmentsTable;

    private final By tableLocator = By.id("installmentsTable");

    public InstallmentsPage(WebDriver driver) {
        super(driver);
    }

    public InstallmentsPage() {
        super();
    }

    public boolean isTableDisplayed() {
        return isDisplayed(installmentsTable);
    }

    public int getPlansCount() {
        return getTableRowCount(tableLocator);
    }

    public boolean hasPlanWithId(String planId) {
        return findRowIndexContainingText(tableLocator, planId) != -1;
    }

    public String getStatusForPlan(String planId) {
        int rowIndex = findRowIndexContainingText(tableLocator, planId);
        if (rowIndex != -1) {
            // Column 6 corresponds to Status (Plan ID=1, Customer=2, Total=3, Installments=4, Monthly=5, Status=6)
            return getTableCellText(tableLocator, rowIndex, 6);
        }
        return null;
    }

    public void clickPayForPlan(String planId) {
        WebElement payButton = driver.findElement(By.id("pay-" + planId));
        click(payButton);
    }
}
