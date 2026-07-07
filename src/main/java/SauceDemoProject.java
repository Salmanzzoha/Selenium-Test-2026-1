import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import java.util.List;

public class SauceDemoProject {

    static WebDriver driver;

    static WebDriverWait wait;

    public static void main(String[] args) throws Exception {

        // ── SETUP ──────────────────────────────────────────

        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();

        // ── RUN ALL STEPS ──────────────────────────────────

        try {

            step1_Login();

            step2_VerifyProductPage();

            step3_AddTwoItemsToCart();

            step4_GoToCart();

            step5_Checkout();

            step6_VerifyOrderComplete();

        } catch (Exception e) {

            System.out.println("❌ Test FAILED: " + e.getMessage());

            e.printStackTrace();

        } finally {

            Thread.sleep(3000); // pause to see final result

            driver.quit();

        }

    }

    // ─────────────────────────────────────────────────────

    // STEP 1: Login

    // ─────────────────────────────────────────────────────

    static void step1_Login() {

        System.out.println("\n🔐 STEP 1: Logging in...");

        driver.get("https://www.saucedemo.com");

        // Type username

        WebElement username = wait.until(

                ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));

        username.clear();

        username.sendKeys("standard_user");

        // Type password

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        // Click Login button

        driver.findElement(By.id("login-button")).click();

        System.out.println("✅ Login clicked.");

    }

    // ─────────────────────────────────────────────────────

    // STEP 2: Verify we landed on the Products page

    // ─────────────────────────────────────────────────────

    static void step2_VerifyProductPage() {

        System.out.println("\n🛍️  STEP 2: Verifying Products page...");

        WebElement pageTitle = wait.until(

                ExpectedConditions.visibilityOfElementLocated(

                        By.className("title")));

        String titleText = pageTitle.getText();

        if (titleText.equals("Products")) {

            System.out.println("✅ Products page loaded. Title: " + titleText);

        } else {

            throw new RuntimeException("Wrong page! Found: " + titleText);

        }

        // Print all product names

        List<WebElement> products = driver.findElements(

                By.className("inventory_item_name"));

        System.out.println("📦 " + products.size() + " products found:");

        for (WebElement p : products) {

            System.out.println("   - " + p.getText());

        }

    }

    // ─────────────────────────────────────────────────────

    // STEP 3: Add 2 items to the cart

    // ─────────────────────────────────────────────────────

    static void step3_AddTwoItemsToCart() throws Exception {

        System.out.println("\n🛒 STEP 3: Adding 2 items to cart...");

        // Get all "Add to cart" buttons

        List<WebElement> addButtons = driver.findElements(

                By.cssSelector("button[data-test^='add-to-cart']"));

        // Click first 2 items

        addButtons.get(0).click();

        Thread.sleep(500);

        addButtons.get(1).click();

        Thread.sleep(500);

        // Check cart badge shows "2"

        String cartCount = driver.findElement(

                By.className("shopping_cart_badge")).getText();

        System.out.println("✅ Cart badge shows: " + cartCount + " items");

        if (!cartCount.equals("2")) {

            throw new RuntimeException("Expected 2 items in cart, got: " + cartCount);

        }

    }

    // ─────────────────────────────────────────────────────

    // STEP 4: Open the cart

    // ─────────────────────────────────────────────────────

    static void step4_GoToCart() {

        System.out.println("\n🧺 STEP 4: Opening cart...");

        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.urlContains("cart"));

        // Print cart items

        List<WebElement> cartItems = driver.findElements(

                By.className("inventory_item_name"));

        System.out.println("✅ Cart contains " + cartItems.size() + " items:");

        for (WebElement item : cartItems) {

            System.out.println("   → " + item.getText());

        }

        // Click Checkout

        driver.findElement(By.id("checkout")).click();

    }

    // ─────────────────────────────────────────────────────

    // STEP 5: Fill checkout form

    // ─────────────────────────────────────────────────────

    static void step5_Checkout() {

        System.out.println("\n📝 STEP 5: Filling checkout form...");

        wait.until(ExpectedConditions.visibilityOfElementLocated(

                By.id("first-name")));

        driver.findElement(By.id("first-name")).sendKeys("Adri");

        driver.findElement(By.id("last-name")).sendKeys("AIUB");

        driver.findElement(By.id("postal-code")).sendKeys("1234");

        System.out.println("✅ Form filled. Clicking Continue...");

        driver.findElement(By.id("continue")).click();

        // Verify overview page loaded

        wait.until(ExpectedConditions.visibilityOfElementLocated(

                By.className("summary_info")));

        // Print price summary

        String total = driver.findElement(

                By.className("summary_total_label")).getText();

        System.out.println("💰 Order total: " + total);

        // Click Finish

        driver.findElement(By.id("finish")).click();

    }

    // ─────────────────────────────────────────────────────

    // STEP 6: Verify order complete

    // ─────────────────────────────────────────────────────

    static void step6_VerifyOrderComplete() {

        System.out.println("\n🎉 STEP 6: Verifying order confirmation...");

        WebElement header = wait.until(

                ExpectedConditions.visibilityOfElementLocated(

                        By.className("complete-header")));

        String confirmText = header.getText();

        System.out.println("✅ Confirmation message: " + confirmText);

        if (confirmText.contains("Thank you")) {

            System.out.println("\n══════════════════════════════════");

            System.out.println("  ✅  ALL STEPS PASSED SUCCESSFULLY");

            System.out.println("══════════════════════════════════");

        } else {

            throw new RuntimeException("Order not confirmed! Got: " + confirmText);

        }

    }

}