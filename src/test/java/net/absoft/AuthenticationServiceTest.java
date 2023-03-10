package net.absoft;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class AuthenticationServiceTest extends BaseTest {

private AuthenticationService authenticationService;
private String message;


    public AuthenticationServiceTest(String message) {
        this.message = message;

    }

    @BeforeClass(groups = "positive")
public void setUp(){
  authenticationService= new AuthenticationService();
  System.out.println("setup");
}
@Test(
        groups = "positive"
)
public void testSample() throws InterruptedException {
        Thread.sleep(2000);
    System.out.println("testSample: " + new Date());
}

  @Test (description = "Test Successful Authentication",
          groups = "positive"
  )
  @Parameters({"email-address", "password"})
  public void testSuccessfulAuthentication (@Optional("user1@test.com") String email,
                                            @Optional("password1") String password)
          throws InterruptedException
  {
      Response response = authenticationService
            .authenticate(email, password);
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), 200, "Response code should be 200");
    sa.assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
    sa.assertAll();
      Thread.sleep(2000);
    System.out.println("testSuccessfulAuthentication: "+ message + new Date());


  }
  @Test (description = "Test Successful Authentication with Underscore",
          groups = "positive"
  )
  public void testSuccessfulAuthenticationWithUnderscore() {
    Response response = authenticationService
            .authenticate("user_1@test.com", "password1");
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), 200, "Response code should be 200");
    sa.assertTrue(validateToken(response.getMessage()),
            "Token should be the 32 digits string. Got: " + response.getMessage());
    sa.assertAll();
    System.out.println("testSuccessfulAuthenticationwithUnderscore");
  }
  @Test (description = "Test Successful Authentication with Different Case in Password",
          groups = "positive"
  )
  public void testSuccessfulAuthenticationWithDifferentCaseinPassword() {
    Response response = authenticationService
            .authenticate("user_2@test.com", "PaSsword1");
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), 200, "Response code should be 200");
    sa.assertTrue(validateToken(response.getMessage()),
            "Token should be the 32 digits string. Got: " + response.getMessage());
    sa.assertAll();
    System.out.println("Test Successful Authentication with Different Case in Password");


  }

    @DataProvider(name = "invalidlogins", parallel = true)

    public Object[][] invalidlogins(){
        return new Object[][]{
                new Object[]{"user1@test.com", "wrong_password",
                        new Response(401, "Invalid email or password")},
                new Object[]{"", "password1",
                        new Response(400, "Email should not be empty string")}
        };
    }
    @Test(
            groups = "negative"


    )
    public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
        Response actualResponse = authenticationService.authenticate
                ("user1@test.com", "wrong_password");
        assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 401");
        assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(),
                "Response message should be \"Invalid email or password\"");
        System.out.println("testAuthenticationWithWrongPassword");
    fail("FAILING TEST");
    }

    @Test(
            priority = 3,
            groups = "negative",
            dataProvider = "invalidlogins"
    )
    public void testAuthenticationWithEmptyEmail() {

        Response response = authenticationService.authenticate("","password1");
        SoftAssert sa = new SoftAssert();
        sa.assertEquals(response.getCode(), 400, "Email should not be empty string");
        sa.assertEquals(response.getMessage(), "Empty email",
                "Response message should be \"Empty email\"");

        System.out.println("testAuthenticationWithEmptyEmail");
    }
    @Test(
            groups = "negative"
    )
    public void testAuthenticationWithInvalidEmail() {
        Response response = authenticationService.authenticate("user1","password1");
        SoftAssert sa = new SoftAssert();
        sa.assertEquals(response.getCode(), 400, "Response code should be 200");
        sa.assertEquals(response.getMessage(), "Invalid email",
                "Response message should be \"Invalid email\"");
        sa.assertAll();
        System.out.println("testAuthenticationWithInvalidEmail");
    }

    @Test(
            groups = "negative",
            priority = 2,
            dependsOnMethods = {"testAuthenticationWithInvalidEmail"}

    )
    public void testAuthenticationWithEmptyPassword() {
        Response response = authenticationService.authenticate("user1@test", "");
        SoftAssert sa = new SoftAssert();
        sa.assertEquals(response.getCode(), 400, "Response code should be 400");
        sa.assertEquals(response.getMessage(), "Password should not be empty string",
                "Response message should be \"Password should not be empty string\"");
        sa.assertAll();
        System.out.println("testAuthenticationWithEmptyPassword");
    }

    private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
