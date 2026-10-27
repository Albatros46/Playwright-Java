package com.servet.pw.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.servet.pw.base.TestUsers.PASSWORD;
import static com.servet.pw.base.TestUsers.STANDARD;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.servet.pw.base.BaseTest;
import com.servet.pw.pages.CartPage;
import com.servet.pw.pages.CheckoutInfoPage;
import com.servet.pw.pages.CheckoutOverviewPage;

public class CheckoutTest extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @Test(groups = { "smoke", "regression" }, description = "Uçtan uca: giriş → sepet → ödeme → onay")
    public void completePurchase() {
        CartPage cart = openLoginPage()
                .loginAs(STANDARD, PASSWORD)
                .addToCart(BACKPACK)
                .addToCart(BIKE_LIGHT)
                .openCart();
        assertThat(cart.itemNames()).hasText(new String[] { BACKPACK, BIKE_LIGHT });

        CheckoutOverviewPage overview = cart.checkout().continueWith("Servet", "Akcadag", "10115");

        double sumOfPrices = overview.itemPrices().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(overview.itemTotal(), sumOfPrices, 0.001, "Ara toplam");
        assertEquals(overview.total(), overview.itemTotal() + overview.tax(), 0.001, "Toplam");

        assertThat(overview.finish().header()).hasText("Thank you for your order!");
    }

    @Test(groups = "regression", description = "Ad boş bırakılırsa ödeme devam etmez")
    public void checkoutRequiresFirstName() {
        CheckoutInfoPage info = openLoginPage()
                .loginAs(STANDARD, PASSWORD)
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .continueExpectingError("", "Akcadag", "10115");

        assertThat(info.error()).hasText("Error: First Name is required");
    }
}
