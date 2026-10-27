package com.servet.pw.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.servet.pw.base.TestUsers.LOCKED_OUT;
import static com.servet.pw.base.TestUsers.PASSWORD;
import static com.servet.pw.base.TestUsers.PERFORMANCE_GLITCH;
import static com.servet.pw.base.TestUsers.STANDARD;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.servet.pw.base.BaseTest;
import com.servet.pw.pages.LoginPage;

/**
 * Selenium projesindeki LoginTest'in birebir karşılığı.
 * assertThat(locator).hasText(...) → metin gelene kadar tekrar dener (web-first assertion).
 */
public class LoginTest extends BaseTest {

    @Test(groups = { "smoke", "regression" }, description = "Geçerli kullanıcı ürün sayfasına ulaşır")
    public void validLoginOpensProductsPage() {
        assertThat(openLoginPage().loginAs(STANDARD, PASSWORD).title()).hasText("Products");
    }

    @Test(groups = "regression", description = "Yavaş kullanıcı da giriş yapabilir")
    public void slowUserCanStillLogin() {
        assertThat(openLoginPage().loginAs(PERFORMANCE_GLITCH, PASSWORD).title()).hasText("Products");
    }

    @Test(groups = "regression", description = "Kilitli kullanıcı giriş yapamaz")
    public void lockedOutUserSeesError() {
        LoginPage login = openLoginPage().loginExpectingError(LOCKED_OUT, PASSWORD);

        assertThat(login.error()).hasText("Epic sadface: Sorry, this user has been locked out.");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][] {
                { STANDARD, "wrong_password", "Username and password do not match any user in this service" },
                { "unknown_user", PASSWORD, "Username and password do not match any user in this service" },
                { "", PASSWORD, "Username is required" },
                { STANDARD, "", "Password is required" }
        };
    }

    @Test(dataProvider = "invalidLogins", groups = "regression", description = "Hatalı girişte doğru mesaj")
    public void invalidLoginShowsError(String username, String password, String expectedMessage) {
        LoginPage login = openLoginPage().loginExpectingError(username, password);

        assertThat(login.error()).containsText(expectedMessage);
    }

    @Test(groups = "regression", description = "Çıkış yapınca giriş sayfasına dönülür")
    public void logoutReturnsToLoginPage() {
        LoginPage login = openLoginPage().loginAs(STANDARD, PASSWORD).logout();

        assertThat(login.loginButton()).isVisible();
    }
}
