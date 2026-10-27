package com.servet.pw.base;

/**
 * saucedemo.com'un herkese açık demo kullanıcıları (giriş sayfasında yazıyor).
 * Gerçek bir projede şifreler koda yazılmaz; ortam değişkeninden veya bir secret store'dan okunur.
 */
public final class TestUsers {

    public static final String STANDARD = "standard_user";
    public static final String LOCKED_OUT = "locked_out_user";
    public static final String PERFORMANCE_GLITCH = "performance_glitch_user";
    public static final String PASSWORD = "secret_sauce";

    private TestUsers() {
    }
}
