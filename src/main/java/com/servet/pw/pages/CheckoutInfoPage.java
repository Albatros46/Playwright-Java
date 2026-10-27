package com.servet.pw.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Ödeme adım 1: ad, soyad, posta kodu. */
public class CheckoutInfoPage extends BasePage {

    private final Locator firstName = page.getByTestId("firstName");
    private final Locator lastName = page.getByTestId("lastName");
    private final Locator postalCode = page.getByTestId("postalCode");
    private final Locator continueButton = page.getByTestId("continue");
    private final Locator error = page.getByTestId("error");

    public CheckoutInfoPage(Page page) {
        super(page);
    }

    public CheckoutOverviewPage continueWith(String first, String last, String zip) {
        fill(first, last, zip);
        return new CheckoutOverviewPage(page);
    }

    public CheckoutInfoPage continueExpectingError(String first, String last, String zip) {
        fill(first, last, zip);
        return this;
    }

    public Locator error() {
        return error;
    }

    private void fill(String first, String last, String zip) {
        firstName.fill(first);
        lastName.fill(last);
        postalCode.fill(zip);
        continueButton.click();
    }
}
