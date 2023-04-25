package org.example.TextCalculator;

import org.junit.jupiter.api.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TextDeductionTests {

    private TextDeduction calculator = new TextDeduction();

    @Test
    @Order(1)
    public void testSingleFiler() {

        double expectedStandardDeduction = 4750;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Single"));
    }

    @Test
    @Order(2)
    public void testMarriedJointFiler() {

        double expectedStandardDeduction = 9500;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Joint"));
    }

    @Test
    @Order(3)
    public void testMarriedSeparateFiler() {

        double expectedStandardDeduction = 7000;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Separate"));
    }

    @Test
    @Order(4)
    public void test65PlusFilerSingle() {
        double expectedStandardDeduction = 4750;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Single", 66, false,false));
    }

    @Test
    @Order(5)
    public void testBlindFilerSingle() {

        double expectedStandardDeduction = 4750;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Single", 50, true, false));
    }

    @Test
    @Order(6)
    public void test65PlusAndBlindFilerSingle() {
        double expectedStandardDeduction = 4750;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Single", 66, true, false));
    }

    @Test
    @Order(7)
    public void testSpouseNotTakingStandardDeduction() {
        double expectedStandardDeduction = 0;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Separate", 56, false, true));
    }

    @Test
    @Order(10)
    public void test65PlusAndBlindFilerJoint() {
        double expectedStandardDeduction = 11500;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Joint", 66, true, false));
    }

    @Test
    @Order(8)
    public void test65PlusFilerJoint() {
        double expectedStandardDeduction = 10500;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Joint", 66, false, false));
    }

    @Test
    @Order(9)
    public void testBlindFilerJoint() {
        double expectedStandardDeduction = 10500;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Joint", 64, true, false));
    }

    @Test
    @Order(11)
    public void testFilerJoint() {
        double expectedStandardDeduction = 9500;
        Assertions.assertEquals(expectedStandardDeduction, calculator.calculateStandardDeduction("Married Joint", 64, false, false));
    }
}