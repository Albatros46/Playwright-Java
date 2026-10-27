package com.servet.pw.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.servet.pw.config.Config;

public class LoginPage extends BasePage {

    // Locator'lar "tembeldir": tanımlanınca aranmaz, her kullanımda yeniden bulunur → stale element yok.
    private final Locator username = page.getByTestId("username");
    private final Locator password = page.getByTestId("password");
    private final Locator loginButton = page.getByTestId("login-button");
    private final Locator error = page.getByTestId("error");

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage open() {
        page.navigate(Config.baseUrl());
        return this;
    }

    public InventoryPage loginAs(String user, String pass) {
        submit(user, pass);
        return new InventoryPage(page);
    }

    public LoginPage loginExpectingError(String user, String pass) {
        submit(user, pass);
        return this;
    }

    public Locator error() {
        return error;
    }

    public Locator loginButton() {
        return loginButton;
    }

    private void submit(String user, String pass) {
        username.fill(user);
        password.fill(pass);
        loginButton.click();
    }
}
