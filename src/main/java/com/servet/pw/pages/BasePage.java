package com.servet.pw.pages;

import com.microsoft.playwright.Page;

/**
 * Selenium projesindeki BasePage'de click/type/visible gibi bekleme yardımcıları vardı.
 * Burada yok: Playwright her aksiyondan (click, fill, selectOption...) önce elementin
 * görünür, etkin ve sabit olmasını KENDİSİ bekler (auto-wait).
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /** "$29.99" veya "Item total: $39.98" → 29.99 / 39.98 */
    protected static double parsePrice(String text) {
        return Double.parseDouble(text.substring(text.indexOf('$') + 1).trim());
    }
}
