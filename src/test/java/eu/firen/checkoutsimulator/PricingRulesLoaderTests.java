package eu.firen.checkoutsimulator;

import eu.firen.checkoutsimulator.domain.Item;
import eu.firen.checkoutsimulator.loader.PriceRulesFileReadException;
import eu.firen.checkoutsimulator.loader.PricingRulesLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Adam on 06.05.2016.
 */
public class PricingRulesLoaderTests {
    public static final List<Item> TEST_ITEMS = Arrays.asList(
            new Item("A",50,Optional.of(new Item.SpecialPrice(3,130))),
            new Item("B",30,Optional.of(new Item.SpecialPrice(2,45))),
            new Item("C",20, Optional.empty()),
            new Item("D",15, Optional.empty())
    );

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowNullPointerExceptionWhenNull() {
        //given
        PricingRulesLoader pricingRulesLoader = new PricingRulesLoader();
        thrown.expect(NullPointerException.class);

        //when
        Map<String, Item> result = pricingRulesLoader.load(null);

        //then
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmptyString() {
        //given
        PricingRulesLoader pricingRulesLoader = new PricingRulesLoader();
        thrown.expect(IllegalArgumentException.class);

        //when
        Map<String, Item> result = pricingRulesLoader.load("");

        //then
    }

    @Test
    public void shouldThrowExceptionWhenFileNotExists() {
        //given
        PricingRulesLoader pricingRulesLoader = new PricingRulesLoader();
        thrown.expect(PriceRulesFileReadException.class);

        //when
        Map<String, Item> result = pricingRulesLoader.load("notexisting.file");

        //then
    }

    @Test
    public void shouldLoadCorrectlyFormattedPriceRules() {
        //given
        PricingRulesLoader pricingRulesLoader = new PricingRulesLoader();
        String correctRulesPath = this.getClass().getResource("/correctRules.csv").getFile();

        //when
        Map result = pricingRulesLoader.load(correctRulesPath);

        //then
        Map<String, Item> expected = TEST_ITEMS.stream()
                .collect(
                        Collectors.toMap(
                                Item::getSku,
                                Function.identity()
                        )
                );
        assertThat(result, equalTo(expected));
    }
}
