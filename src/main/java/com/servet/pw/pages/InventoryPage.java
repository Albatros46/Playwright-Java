package com.servet.pw.pages;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Ürün listesi sayfası ("Products"). */
public class InventoryPage extends BasePage {

    public enum SortOption {
        NAME_A_TO_Z("az"), NAME_Z_TO_A("za"), PRICE_LOW_TO_HIGH("lohi"), PRICE_HIGH_TO_LOW("hilo");

        private final String value;

        SortOption(String value) {
            this.value = value;
        }
    }

    private final Locator title = page.getByTestId("title");
    private final Locator items = page.getByTestId("inventory-item");
    private final Locator itemNames = page.getByTestId("inventory-item-name");
    private final Locator itemPrices = page.getByTestId("inventory-item-price");
    private final Locator sortDropdown = page.getByTestId("product-sort-container");
    private final Locator cartBadge = page.getByTestId("shopping-cart-badge");
    private final Locator cartLink = page.getByTestId("shopping-cart-link");
    private final Locator menuButton = page.locator("#react-burger-menu-btn");
    private final Locator logoutLink = page.locator("#logout_sidebar_link");

    public InventoryPage(Page page) {
        super(page);
    }

    public Locator title() {
        return title;
    }

    public Locator cartBadge() {
        return cartBadge;
    }

    public List<String> productNames() {
        itemNames.first().waitFor();
        return itemNames.allTextContents();
    }

    public List<Double> productPrices() {
        itemPrices.first().waitFor();
        return itemPrices.allTextContents().stream().map(BasePage::parsePrice).toList();
    }

    public InventoryPage sortBy(SortOption option) {
        sortDropdown.selectOption(option.value);
        return this;
    }

    /** Ürünü ADINA göre bulur: "adı X olan ürün kartındaki Add to cart butonu". XPath gerekmez. */
    public InventoryPage addToCart(String productName) {
        items.filter(new Locator.FilterOptions().setHas(page.getByText(productName, new Page.GetByTextOptions().setExact(true))))
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to cart"))
                .click();
        return this;
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage(page);
    }

    public LoginPage logout() {
        menuButton.click();
        // Menü kayarak açılır. Selenium'da elementToBeClickable beklemesi gerekiyordu; burada auto-wait yeterli.
        logoutLink.click();
        return new LoginPage(page);
    }
}
