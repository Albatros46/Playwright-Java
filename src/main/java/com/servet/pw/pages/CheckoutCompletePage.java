package com.servet.pw.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CheckoutCompletePage extends BasePage {

    private final Locator header = page.getByTestId("complete-header");

    public CheckoutCompletePage(Page page) {
        super(page);
    }

    public Locator header() {
        return header;
    }
}
