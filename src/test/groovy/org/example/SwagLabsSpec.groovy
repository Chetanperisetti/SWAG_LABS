package org.example

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.Duration

@Stepwise
class SwagLabsSpec extends Specification {

    @Shared WebDriver driver
    @Shared WebDriverWait wait

    static final String BASE_URL = "https://www.saucedemo.com"
    static final String USERNAME = "standard_user"
    static final String PASSWORD = "secret_sauce"

    // Runs ONCE before all tests
    def setupSpec() {
        // Point directly to the local msedgedriver.exe — no internet needed
        System.setProperty("webdriver.edge.driver", "drivers/msedgedriver.exe")

        EdgeOptions options = new EdgeOptions()
        // Remove the line below if you want to SEE the browser window
        options.addArguments("--headless=new")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")
        options.addArguments("--window-size=1280,800")
        options.addArguments("--remote-allow-origins=*")

        driver = new EdgeDriver(options)
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10))
    }

    // Runs ONCE after all tests — always close the browser
    def cleanupSpec() {
        driver?.quit()
    }

    // =========================================================================
    // TEST 1 — Login with valid credentials
    // =========================================================================
    def "Test 1: User can log in with valid credentials"() {

        given: "The user is on the Swag Labs login page"
        driver.get(BASE_URL)

        when: "The user enters credentials and clicks Login"
        driver.findElement(By.id("user-name")).sendKeys(USERNAME)
        driver.findElement(By.id("password")).sendKeys(PASSWORD)
        driver.findElement(By.id("login-button")).click()

        then: "The Products page heading is displayed"
        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("title"))
        )
        heading.text == "Products"
    }

    // =========================================================================
    // TEST 2 — Add a product to the cart
    // =========================================================================
    def "Test 2: User can add a product to the shopping cart"() {

        given: "The user is on the Products page (after login in Test 1)"

        when: "The user clicks Add to cart on the first product"
        driver.findElement(By.cssSelector(".inventory_item button")).click()

        then: "The cart badge shows 1 item"
        WebElement cartBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge"))
        )
        cartBadge.text == "1"
    }

    // =========================================================================
    // TEST 3 — Product details page
    // =========================================================================
    def "Test 3: Clicking a product name opens its detail page"() {

        given: "The user goes back to the Products page"
        driver.findElement(By.id("react-burger-menu-btn")).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inventory_sidebar_link"))).click()
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")))

        when: "The user clicks the first product name"
        WebElement firstProduct = driver.findElement(By.className("inventory_item_name"))
        String productName = firstProduct.text
        firstProduct.click()

        then: "The detail page shows the correct product name"
        WebElement detailName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_name"))
        )
        detailName.text == productName
    }

    // =========================================================================
    // TEST 4 — Logout
    // =========================================================================
    def "Test 4: User can log out and returns to the login page"() {

        given: "The user is on the inventory page"
        driver.get(BASE_URL + "/inventory.html")

        when: "The user opens the menu and clicks Logout"
        driver.findElement(By.id("react-burger-menu-btn")).click()
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link"))
        ).click()

        then: "The login button is visible again"
        WebElement loginButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("login-button"))
        )
        loginButton.isDisplayed()
    }
}