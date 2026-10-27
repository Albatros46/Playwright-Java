# SauceDemo · Playwright Java + TestNG

![Java](https://img.shields.io/badge/Java-21-orange) ![Playwright](https://img.shields.io/badge/Playwright-1.64-2EAD33) ![TestNG](https://img.shields.io/badge/TestNG-7.12-red) ![Browsers](https://img.shields.io/badge/Chromium%20%7C%20Firefox%20%7C%20WebKit-tested-blue) ![Tests](https://img.shields.io/badge/tests-14%20passing-brightgreen)

The same 14 UI scenarios as my [Selenium project](https://github.com/Albatros46/saucedemo-selenium-pom), rewritten in **Playwright Java** — so the two tools can be compared on identical tests, on the same machine.
Part of my 52-week QA Automation roadmap — **Week 10**.

## Selenium vs Playwright — measured

Same 14 tests, same machine, measured back to back, headless:

| | Selenium 4.51 | Playwright 1.64 |
|---|---|---|
| Sequential run | 77.3 s · 76.5 s | 29.3 s · 29.8 s · 30.3 s · 33.3 s |
| Parallel, 4 threads | 60.9 s · 60.4 s | 26.0 s · 22.3 s |
| Browsers | Chrome, Edge, Firefox | Chromium, Firefox, **WebKit** (Safari engine, also on Windows) |
| Page-object code (non-blank, non-comment lines) | 207 | 172 |
| Waiting | explicit waits in `BasePage` | **auto-wait** before every action |
| Assertions | `assertEquals` (checks once) | `assertThat(locator).hasText(...)` (retries until true) |
| On failure | screenshot | screenshot **+ trace** (timeline, DOM snapshots, network) |

Why Playwright is faster here: Selenium starts a **new browser per test**; Playwright starts one browser per thread and gives every test a fresh, isolated **BrowserContext** (own cookies and storage, like an incognito window), which opens in milliseconds.

## Architecture

```
src/main/java/com/servet/pw/
├── config/Config.java            config.properties, overridable with -D
├── browser/BrowserManager.java   one Playwright + Browser per thread (Playwright is not thread-safe)
└── pages/                        LoginPage · InventoryPage · CartPage · Checkout pages
src/test/java/com/servet/pw/
├── base/BaseTest.java            new BrowserContext per test, trace on, screenshot + trace on failure
└── tests/                        LoginTest · InventoryTest · CheckoutTest
src/test/resources/suites/        testng · parallel · cross-browser
```

```java
// Locators by test id: saucedemo uses data-test="…", configured once
playwright.selectors().setTestIdAttribute("data-test");
private final Locator username = page.getByTestId("username");

// Find the product card by name, then its button — no XPath
items.filter(new Locator.FilterOptions().setHas(page.getByText(productName, exact)))
     .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to cart"))
     .click();
```

## A flaky test, explained with a trace

`slowUserCanStillLogin` (the `performance_glitch_user`, ~5 s delay) failed once in 4 sequential runs:

```
Locator expected to have text "Products" — element(s) not found
```

The page snapshot in the error already showed "Products". The saved trace showed why:

```
 0.73 s  +0.10 s  click   login-button
 0.83 s  +5.15 s  expect  title         → failed
```

`assertThat` uses its **own default timeout of 5 s**, not the 10 s context timeout used by actions. A ~5 s delay against a 5 s limit = sometimes green, sometimes red. Fix in `BaseTest`:

```java
PlaywrightAssertions.setDefaultAssertionTimeout(Config.timeoutMs());   // one timeout for everything
```

Interesting detail: in Selenium the same delay always happened *inside* the click. In this trace Playwright's click returned after 0.1 s and the delay came afterwards — so the wait moved from the action to the assertion.

## Run

| Command | What runs |
|---|---|
| `mvn test` | all 14 tests, Chromium (window visible) |
| `mvn test -Dsuite=parallel` | all 14 tests in 4 threads |
| `mvn test -Dsuite=cross-browser` | smoke tests in Chromium, Firefox and WebKit at the same time |
| `mvn test -Dbrowser=webkit -Dheadless=true` | any browser, headless |

Playwright downloads its browsers on the first run (to `%LOCALAPPDATA%\ms-playwright`).

**Open a trace** (saved to `target/traces/` when a test fails):

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="show-trace target/traces/<file>.zip"
```

## What I learned
- _TODO: 2–3 sentences in your own words_
