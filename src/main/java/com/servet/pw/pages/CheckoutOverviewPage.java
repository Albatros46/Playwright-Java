package com.servet.pw.pages;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Ödeme adım 2: özet, ara toplam, vergi, toplam. */
public class CheckoutOverviewPage extends BasePage {

    private final Locator itemPrices = page.getByTestId("inventory-item-price");
    private final Locator subtotal = page.getByTestId("subtotal-label");
    private final Locator tax = page.getByTestId("tax-label");
    private final Locator total = page.getByTestId("total-label");
    private final Locator finish = page.getByTestId("finish");

    public CheckoutOverviewPage(Page page) {
        super(page);
    }

    public List<Double> itemPrices() {
        itemPrices.first().waitFor();
        return itemPrices.allTextContents().stream().map(BasePage::parsePrice).toList();
    }

    public double itemTotal() {
        return parsePrice(subtotal.textContent());
    }

    public double tax() {
        return parsePrice(tax.textContent());
    }

    public double total() {
        return parsePrice(total.textContent());
    }

    public CheckoutCompletePage finish() {
        finish.click();
        return new CheckoutCompletePage(page);
    }
}
