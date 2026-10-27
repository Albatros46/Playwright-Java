package com.servet.pw.browser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Her thread'e kendi Playwright'ını ve tarayıcısını verir (Playwright nesneleri thread-safe DEĞİLDİR).
 *
 * Selenium projesinden farkı: tarayıcı her test için yeniden AÇILMAZ. Thread başına bir kez açılır;
 * testler arasındaki izolasyonu her test için oluşturulan yeni BrowserContext sağlar
 * (ayrı çerezler, ayrı localStorage — gizli pencere gibi). Context açmak, tarayıcı açmaktan çok daha hızlıdır.
 */
public final class BrowserManager {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, Browser>> BROWSERS = ThreadLocal.withInitial(HashMap::new);
    private static final List<Playwright> ALL = new CopyOnWriteArrayList<>();

    private BrowserManager() {
    }

    public static Browser browser(String name, boolean headless) {
        Playwright playwright = PLAYWRIGHT.get();
        if (playwright == null) {
            playwright = Playwright.create();
            // saucedemo test id'leri "data-test" özelliğinde tutuyor → page.getByTestId("username")
            playwright.selectors().setTestIdAttribute("data-test");
            PLAYWRIGHT.set(playwright);
            ALL.add(playwright);
        }
        Playwright pw = playwright;
        return BROWSERS.get().computeIfAbsent(name.toLowerCase(), n -> launch(pw, n, headless));
    }

    private static Browser launch(Playwright playwright, String name, boolean headless) {
        BrowserType type = switch (name) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> throw new IllegalArgumentException("Desteklenmeyen tarayıcı: " + name + " (chromium | firefox | webkit)");
        };
        return type.launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    /** Tüm thread'lerin Playwright'larını (ve tarayıcılarını) kapatır. Suite sonunda bir kez çağrılır. */
    public static void closeAll() {
        for (Playwright playwright : ALL) {
            playwright.close();
        }
        ALL.clear();
    }
}
