package com.servet.pw.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage extends BasePage {

    private final Locator itemNames = page.getByTestId("inventory-item-name");
    private final Locator checkout = page.getByTestId("checkout");

    public CartPage(Page page) {
        super(page);
    }

    public Locator itemNames() {
        return itemNames;
    }

    public CheckoutInfoPage checkout() {
        checkout.click();
        return new CheckoutInfoPage(page);
    }
}
