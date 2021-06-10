package stepDefinitions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import pageObjects.AccountRegistrationPage;
import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;
import utilities.DataReader;

public class steps {

    WebDriver driver;
    HomePage hp;
    LoginPage lp;
    AccountRegistrationPage regpage;
    public List<HashMap<String, String>> datamap; //Data driven
    Logger logger;//for logging
    ResourceBundle rb; // for reading properties file
    String br;

    @Before
    public void setup()
    {
        logger= LogManager.getLogger(this.getClass()); //for logging

        rb=ResourceBundle.getBundle("config");
        br=rb.getString("browser");
    }

    /*@After
    public void tearDown(Scenario scenario) {
        if(scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png",scenario.getName());
        }
    }*/

    @Given("User Launch browser")
    public void user_launch_browser() {
        if(br.equals("chrome"))
        {
            WebDriverManager.chromedriver().setup();
            driver=new ChromeDriver();
        }
        else if (br.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }
        else if (br.equals("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Given("opens URL {string}")
    public void opens_url(String url) {
        driver.get(url);
        driver.manage().window().maximize();
    }

    @When("User navigate to MyAccount menu")
    public void user_navigate_to_my_account() {
        hp=new HomePage(driver);
        hp.clickMyAccount();
    }

    @When("click on Login")
    public void click_on_login() {
        hp.clickLogin();
        logger.info("clicked on login link");
    }

    @When("User enters Email as {string} and Password as {string}")
    public void user_enters_email_as_and_password_as(String email, String pwd) {
        lp=new LoginPage(driver);
        lp.setEmail(email);
        logger.info("Provided Email ");
        lp.setPassword(pwd);
        logger.info("Provided Password ");
    }

    @When("Click on Login button")
    public void click_on_login_button() {
        lp.clickLogin();
        logger.info("Clicked on Login button");
    }


    @Then("User navigates to MyAccount Page")
    public void user_navigates_to_my_account_page() {
        boolean targetpage=lp.isMyAccountPageExists();
        driver.close();
        if(targetpage)
        {
            logger.info("Login Success ");
            Assert.assertTrue(true);
        }
        else
        {
            logger.info("Login Failed ");
            Assert.assertTrue(false);
        }

    }

    //Data Driven test method
    @Then("check User navigates to MyAccount Page by passing Email and Password with excel row {string}")
    public void check_user_navigates_to_my_account_page_by_passing_email_and_password_with_excel_data(String rows)
    {

        datamap=DataReader.data(System.getProperty("user.dir")+"\\testData\\Opencart_LoginData.xlsx", "Sheet1");

        int index=Integer.parseInt(rows)-1;
        String email= datamap.get(index).get("username");
        String pwd= datamap.get(index).get("password");
        String exp_res= datamap.get(index).get("res");

        lp=new LoginPage(driver);
        lp.setEmail(email);
        lp.setPassword(pwd);

        lp.clickLogin();
        try
        {
            boolean targetpage=lp.isMyAccountPageExists();

            if(exp_res.equals("Valid"))
            {
                if(targetpage==true)
                {
                    MyAccountPage myaccpage=new MyAccountPage(driver);
                    myaccpage.clickLogout();
                    Assert.assertTrue(true);
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }

            if(exp_res.equals("Invalid"))
            {
                if(targetpage==true)
                {
                    MyAccountPage myaccpage=new MyAccountPage(driver);
                    myaccpage.clickLogout();
                    Assert.assertTrue(false);
                }
                else
                {
                    Assert.assertTrue(true);
                }
            }


        }
        catch(Exception e)
        {

            Assert.assertTrue(false);
        }
        driver.close();
    }

    //Account Registration

    @When("click on Register")
    public void click_on_register() {
        hp.clickRegister();
    }

    @Then("user navigates to Register Account page")
    public void user_navigates_to_register_account_page() {
        regpage=new AccountRegistrationPage(driver);

        if(regpage.isRegisterAccountPageDiplayed())
        {
            logger.info("Register Account page displayed ");
            Assert.assertTrue(true);
        }

        else {
            logger.info("Register Account page Not displayed ");
            Assert.assertTrue(false);
        }

    }
    @When("user provide valid details")
    public void user_provide_valid_details() {
        regpage.setFirstName("John");
        logger.info("Provided First Name ");

        regpage.setLastName("Canedy");
        logger.info("Provided Last Name ");

        regpage.setEmail(RandomStringUtils.randomAlphabetic(5)+"@gmail.com");// Random Email
        logger.info("Provided Email ");

        regpage.setTelephone("65656565");
        logger.info("Provided Telephone ");

        regpage.setPassword("abcxyz");
        logger.info("Provided Password ");

        regpage.setConfirmPassword("abcxyz");
        logger.info("Provided Confrmed Password ");

        regpage.setPrivacyPolicy();
    }
    @When("Click on continue")
    public void click_on_continue() {
        regpage.clickContinue();
        logger.info("Clicked on Continue ");

    }
    @Then("User should see {string} message")
    public void user_should_see_message(String expmsg) {
        String confmsg=regpage.getConfirmationMsg();
        driver.close();
        if(confmsg.equals(expmsg)) {
            logger.info("Account Registration Success ");
            Assert.assertTrue(true);
        }
         else {
            logger.error("Account Registration Failed ");
            Assert.assertTrue(false);
        }
        }

}
