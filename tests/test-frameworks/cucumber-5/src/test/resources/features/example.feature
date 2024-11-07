Feature: Simple addition
  Scenario: Add two numbers
    Given I have the number 5
    And I have another number 10
    When I add the numbers
    Then the result should be 15