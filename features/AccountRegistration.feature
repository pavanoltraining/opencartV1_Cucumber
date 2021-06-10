Feature: Account Registration
  @regression
  Scenario: Successful Account Registration with Valid user details
    Given User Launch browser
    And opens URL "https://demo.opencart.com/"
    When User navigate to MyAccount menu
    And click on Register
    Then user navigates to Register Account page
    When user provide valid details
    And Click on continue
    Then User should see "Your Account Has Been Created!" message
