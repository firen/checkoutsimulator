package eu.firen.checkoutsimulator.loader;

import eu.firen.checkoutsimulator.domain.Item;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Adam on 06.05.2016.
 */
public class PricingRulesLoader {

    public static final String SPECIAL_PRICE_VALIDATION_PATTERN = "([0-9]+) for ([0-9]+)";

    public Map load(String filename) {
        Objects.requireNonNull(filename, "filename not null required");
        if(StringUtils.isEmpty(filename)) {
            throw new IllegalArgumentException("filename is empty string");
        }
        File file = new File(filename);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.lines()
                    .skip(1)
                    .map(this::parseItem)
                    .collect(Collectors.toMap(Item::getSku, Function.identity()));
        } catch (IOException e) {
            throw new PriceRulesFileReadException("Something bad happened while reading file :" + filename + " check stacktrace for more information", e);
        }
    }

    private Item parseItem(String line) {
        List<String> values = Arrays.asList(line.split(","));
        if(values.size() < 2 && values.size() > 3) {
            throw new IllegalArgumentException("csv file should have 2-3 columns, has: " + values.size() + " in line: " + line);
        }
        validateSku(values.get(0), line);
        validateUnitPrice(values.get(1), line);
        if(values.size() == 2) {
            return new Item(values.get(0), Long.parseLong(values.get(1)));
        } else {
            validateSpecialPrice(values.get(2), line);
            return new Item(values.get(0), Long.parseLong(values.get(1)), parseSpecialPrice(values.get(2)));
        }
    }

    private Optional<Item.SpecialPrice> parseSpecialPrice(String specialPrice) {
        if(StringUtils.isEmpty(specialPrice)) {
            return Optional.empty();
        }
        Matcher matcher = Pattern.compile(SPECIAL_PRICE_VALIDATION_PATTERN).matcher(specialPrice);
        matcher.matches();
        return Optional.of(new Item.SpecialPrice(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2))));
    }

    private void validateSpecialPrice(String specialPrice, String line) {
        if(!StringUtils.isEmpty(specialPrice)
                && !Pattern.matches(SPECIAL_PRICE_VALIDATION_PATTERN, specialPrice)) {
            throw new IllegalArgumentException("special price must have format 'quantity for price' or be empty string in line: " + line);
        }
    }

    private void validateUnitPrice(String unitPrice, String line) {
        if(StringUtils.isEmpty(unitPrice)) {
            throw new IllegalArgumentException("unit price cannot be empty string in line: " + line);
        }
        try {
            Long.parseLong(unitPrice);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("unit price must be long number in line: " + line);
        }
    }

    private void validateSku(String sku, String line) {
        if(StringUtils.isEmpty(sku)) {
            throw new IllegalArgumentException("SKU cannot be empty string in line: " + line);
        }
    }
}
