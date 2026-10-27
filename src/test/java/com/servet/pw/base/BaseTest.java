package com.servet.pw.base;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.servet.pw.browser.BrowserManager;
import com.servet.pw.config.Config;
import com.servet.pw.pages.LoginPage;

/**
 * Her test kendi BrowserContext'inde çalışır (ayrı çerez/oturum = gizli pencere gibi).
 * Trace her testte kaydedilir; test başarısız olursa diske yazılır, geçerse atılır.
 */
public abstract class BaseTest {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    static {
        // assertThat(...) varsayılan olarak 5 sn bekler; aksiyonlar ise context timeout'unu (timeoutMs) kullanır.
        // performance_glitch_user'ın ~5 sn'lik gecikmesi bu 5 sn sınırına takılıp testi flaky yapıyordu
        // (trace: expect +5.15 s → fail). Tek bir zaman aşımı ayarı olsun diye ikisini eşitliyoruz.
        PlaywrightAssertions.setDefaultAssertionTimeout(Config.timeoutMs());
    }

    /** Tarayıcı önceliği: testng.xml parametresi → -Dbrowser → config.properties */
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void openPage(@Optional String browser) {
        String chosen = (browser == null || browser.isBlank()) ? Config.browser() : browser;
        Browser instance = BrowserManager.browser(chosen, Config.headless());
        BrowserContext context = instance.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
        context.setDefaultTimeout(Config.timeoutMs());
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    @AfterMethod(alwaysRun = true)
    public void closePage(ITestResult result) {
        BrowserContext context = CONTEXT.get();
        if (context == null) {
            return;
        }
        try {
            if (result.isSuccess()) {
                context.tracing().stop();
            } else {
                String name = result.getTestClass().getRealClass().getSimpleName() + "_"
                        + result.getMethod().getMethodName() + "_" + LocalDateTime.now().format(TIMESTAMP);
                Path screenshot = Path.of("target", "screenshots", name + ".png");
                Path trace = Path.of("target", "traces", name + ".zip");
                PAGE.get().screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));
                context.tracing().stop(new Tracing.StopOptions().setPath(trace));
                System.out.printf("[%s] FAIL %s%n  screenshot: %s%n  trace: %s%n",
                        Thread.currentThread().getName(), name, screenshot.toAbsolutePath(), trace.toAbsolutePath());
            }
        } finally {
            context.close();
            CONTEXT.remove();
            PAGE.remove();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void closeBrowsers() {
        BrowserManager.closeAll();
    }

    protected Page page() {
        return PAGE.get();
    }

    protected LoginPage openLoginPage() {
        return new LoginPage(page()).open();
    }
}
