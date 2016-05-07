package eu.firen.checkoutsimulator.checkout;

import eu.firen.checkoutsimulator.checkout.CheckoutTransaction.Position;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static eu.firen.checkoutsimulator.PricingRulesLoaderTests.TEST_ITEMS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Adam on 07.05.2016.
 */
public class CheckoutTransactionTests {
    @Test
    public void shouldReturn0ForEmptyTransaction() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction();

        //when
        long totalPrice = checkoutTransaction.calculateTotalPrice();

        //then
        assertThat(totalPrice, is(0L));
    }

    @Test
    public void shouldAddExistingItemToTransaction() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String existingSku = TEST_ITEMS.get(0).getSku();

        //when
        boolean result = checkoutTransaction.addItem(existingSku);

        //then
        assertThat(result, is(true));
    }

    @Test
    public void shouldRefuseToAddNotExistingItemToTransaction() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String notExistingSku = "NOT EXISTING SKU";

        //when
        boolean result = checkoutTransaction.addItem(notExistingSku);

        //then
        assertThat(result, is(false));
    }

    @Test
    public void shouldReturnRecentlyAddedItemWithQuantity1() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String existingSku = TEST_ITEMS.get(0).getSku();

        //when
        boolean result = checkoutTransaction.addItem(existingSku);
        Position position = checkoutTransaction.getPosition(existingSku);

        //then
        assertThat(result, is(true));
        assertThat(position, is(not(nullValue())));
        assertThat(position.getSku(), is(existingSku));
        assertThat(position.getQuantity(), is(1));
    }

    @Test
    public void shouldReturnRecentlyAddedItemWithQuantity2() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String existingSku = TEST_ITEMS.get(0).getSku();

        //when
        checkoutTransaction.addItem(existingSku);
        boolean result = checkoutTransaction.addItem(existingSku);
        Position position = checkoutTransaction.getPosition(existingSku);

        //then
        assertThat(result, is(true));
        assertThat(position, is(not(nullValue())));
        assertThat(position.getSku(), is(existingSku));
        assertThat(position.getQuantity(), is(2));
    }

    @Test
    public void shouldCalculateUnitPriceForAddedItemsOneWithoutSpecialPrice() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String existingSkuA = TEST_ITEMS.get(0).getSku();
        final String existingSkuB = TEST_ITEMS.get(1).getSku();
        final String existingSkuC = TEST_ITEMS.get(2).getSku();

        //when
        boolean resultAddA = checkoutTransaction.addItem(existingSkuA);
        boolean resultAddB = checkoutTransaction.addItem(existingSkuB);
        boolean resultAddC = checkoutTransaction.addItem(existingSkuC);
        long totalPrice = checkoutTransaction.calculateTotalPrice();

        //then
        assertThat(resultAddA, is(true));
        assertThat(resultAddB, is(true));
        assertThat(resultAddC, is(true));
        assertThat(totalPrice, is(100L));
    }

    @Test
    public void shouldCalculateTotalPriceBasedOnUnitPriceAndSpecialPriceForAddedItemsOneWithoutSpecialPrice() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String existingSkuA = TEST_ITEMS.get(0).getSku();
        final String existingSkuB = TEST_ITEMS.get(1).getSku();
        final String existingSkuC = TEST_ITEMS.get(2).getSku();

        //when
        boolean resultAddA = checkoutTransaction.addItem(existingSkuA);
        boolean resultAddB = checkoutTransaction.addItem(existingSkuB);
        boolean resultAddC = checkoutTransaction.addItem(existingSkuC);
        boolean resultAddB2 = checkoutTransaction.addItem(existingSkuB);
        long totalPrice = checkoutTransaction.calculateTotalPrice();

        //then
        assertThat(resultAddA, is(true));
        assertThat(resultAddB, is(true));
        assertThat(resultAddC, is(true));
        assertThat(resultAddB2, is(true));
        assertThat(totalPrice, is(115L));
        assertThat(checkoutTransaction.getPosition(existingSkuA).getPrice(), is(50L));
        assertThat(checkoutTransaction.getPosition(existingSkuB).getPrice(), is(45L));
        assertThat(checkoutTransaction.getPosition(existingSkuC).getPrice(), is(20L));
    }

    @Test
    public void shouldReturnAllAddedItemsInOrder() {
        //given
        CheckoutTransaction checkoutTransaction = new CheckoutTransaction(TEST_ITEMS);
        final String existingSkuA = TEST_ITEMS.get(0).getSku();
        final String existingSkuB = TEST_ITEMS.get(1).getSku();
        final String existingSkuC = TEST_ITEMS.get(2).getSku();

        //when
        boolean resultAddA = checkoutTransaction.addItem(existingSkuA);
        boolean resultAddB = checkoutTransaction.addItem(existingSkuB);
        boolean resultAddC = checkoutTransaction.addItem(existingSkuC);

        //then
        List<Position> expected = Arrays.asList(
            new Position(existingSkuA, TEST_ITEMS.get(0), 1, 50),
            new Position(existingSkuB, TEST_ITEMS.get(1), 1, 30),
            new Position(existingSkuC, TEST_ITEMS.get(2), 1, 20)
        );

        List<Position> result = checkoutTransaction.getPositions();

        assertThat(resultAddA, is(true));
        assertThat(resultAddB, is(true));
        assertThat(resultAddC, is(true));
        assertThat(result.size(), is(3));
        assertThat(result, is(expected));
    }
}
