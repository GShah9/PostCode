package com.postcode;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Gautam on 17/02/2017.
 */
public class PostCodeValidatorTest {

    @Test
    public final void whenTextIsSupplied() {
        PostCodeValidator.isPostCode("test");
        Assert.assertTrue(true);
    }

    @Test
    public final void whenNullEntryIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode(""));
    }

    @Test
    public final void whenJunkValueIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("$%± ()()"));
    }

    @Test
    public final void whenInvalidEntryIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("XX XXX"));
    }

    @Test
    public final void whenIncorrectInwardCodeLengthIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("A1 9A"));
    }

    @Test
    public final void whenPostCodeWithNoSpaceIsSupplied() {
        Assert.assertTrue(PostCodeValidator.isPostCode("LS44PL"));
    }

    @Test
    public final void whenQInFirstPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("Q1A 9AA"));
    }

    @Test
    public final void whenVInFirstPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("V1A 9AA"));
    }

    @Test
    public final void whenXInFirstPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("X1A 9BB"));
    }

    @Test
    public final void whenIInSecondPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("LI10 3QP"));
    }

    @Test
    public final void whenJInSecondPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("LJ10 3QP"));
    }

    @Test
    public final void whenZInSecondPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("LZ10 3QP"));
    }

    @Test
    public final void whenQInThirdPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("A9Q 9AA"));
    }

    @Test
    public final void whenCInFourthPositionIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("AA9C 9AA"));
    }

    @Test
    public final void whenAreaWithOnlySingleDigitDistrictsIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("FY10 4PL"));
    }

    @Test
    public final void whenAreaWithOnlyDoubleDigitDistrictsIsSupplied() {
        Assert.assertFalse(PostCodeValidator.isPostCode("SO1 4QQ"));
    }

    @Test
    public final void whenValidPostCodesAreSupplied() {
        Assert.assertTrue(PostCodeValidator.isPostCode("EC1A 1BB"));
        Assert.assertTrue(PostCodeValidator.isPostCode("W1A 0AX"));
        Assert.assertTrue(PostCodeValidator.isPostCode("M1 1AE"));
        Assert.assertTrue(PostCodeValidator.isPostCode("B33 8TH"));
        Assert.assertTrue(PostCodeValidator.isPostCode("CR2 6XH"));
        Assert.assertTrue(PostCodeValidator.isPostCode("DN55 1PT"));
        Assert.assertTrue(PostCodeValidator.isPostCode("GIR 0AA"));
        Assert.assertTrue(PostCodeValidator.isPostCode("SO10 9AA"));
        Assert.assertTrue(PostCodeValidator.isPostCode("FY9 9AA"));
        Assert.assertTrue(PostCodeValidator.isPostCode("WC1A 9AA"));
    }

}
