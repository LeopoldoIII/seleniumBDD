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

    @FindBy(id = "newPlanId")
    private WebElement newPlanIdField;

    @FindBy(id = "newCustomer")
    private WebElement newCustomerField;

    @FindBy(id = "newTotalAmount")
    private WebElement newTotalAmountField;

    @FindBy(id = "newInstallments")
    private WebElement newInstallmentsField;

    @FindBy(id = "createPlanButton")
    private WebElement createPlanButton;

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
        try {
            return wait.until(d -> findRowIndexContainingText(tableLocator, planId) != -1);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPlanNotPresent(String planId) {
        try {
            return wait.until(d -> findRowIndexContainingText(tableLocator, planId) == -1);
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatusForPlan(String planId) {
        int rowIndex = findRowIndexContainingText(tableLocator, planId);
        if (rowIndex != -1) {
            // Column 6 corresponds to Status (Plan ID=1, Customer=2, Total=3, Installments=4, Monthly=5, Status=6)
            return getTableCellText(tableLocator, rowIndex, 6);
        }
        return null;
    }

    public boolean waitForStatus(String planId, String expectedStatus) {
        try {
            return wait.until(d -> expectedStatus.equalsIgnoreCase(getStatusForPlan(planId)));
        } catch (Exception e) {
            return false;
        }
    }

    public void clickPayForPlan(String planId) {
        WebElement payButton = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.id("pay-" + planId)));
        click(payButton);
        waitForStatus(planId, "PAID");
    }

    public void clickDeleteForPlan(String planId) {
        WebElement deleteButton = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.id("delete-" + planId)));
        click(deleteButton);
        isPlanNotPresent(planId);
    }

    public void createNewPlan(String id, String customer, String totalAmount, String installments) {
        type(newPlanIdField, id);
        type(newCustomerField, customer);
        type(newTotalAmountField, totalAmount);
        type(newInstallmentsField, installments);
        click(createPlanButton);
        wait.until(d -> hasPlanWithId(id));
    }
}
