package eu.firen.checkoutsimulator.checkout;

import eu.firen.checkoutsimulator.domain.Item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Adam on 07.05.2016.
 */
public class CheckoutTransaction {
    private Map<String, Item> testItems;
    private Map<String, Position> positions;

    public CheckoutTransaction() {
        this.positions = new LinkedHashMap<>();
        this.testItems = new LinkedHashMap<>();
    }

    public CheckoutTransaction(List<Item> items) {
        this();
        this.testItems = items.stream()
                                .collect(Collectors.toMap(
                                        Item::getSku,
                                        Function.identity()
                                ));
    }

    public long calculateTotalPrice() {
        return positions.values().stream()
                .mapToLong(e -> {
                    if(e.getItem().getSpecialPrice().isPresent()) {
                        long bundleSize = e.getItem().getSpecialPrice().get().getBundleSize();
                        long bundlePrice = e.getItem().getSpecialPrice().get().getBundlePrice();
                        long itemPrice = (e.quantity / bundleSize) * bundlePrice
                                + (e.quantity % bundleSize) * e.getItem().getUnitPrice();
                        return itemPrice;
                    } else {
                        return e.quantity * e.getItem().getUnitPrice();
                    }
                }).sum();
    }

    public boolean addItem(String sku) {
        if(testItems.containsKey(sku)) {
            if(positions.containsKey(sku)) {
                positions.get(sku).incrementQuantity();
            } else {
                positions.put(sku, new Position(sku, testItems.get(sku), 1));
            }
            return true;
        } else {
            return false;
        }
    }

    public Position getPosition(String sku) {
        return positions.get(sku);
    }

    public class Position {
        private String sku;
        private Item item;
        private int quantity;

        private Position(String sku, Item item, int quantity) {
            this.sku = sku;
            this.item = item;
            this.quantity = quantity;
        }

        public String getSku() {
            return sku;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            this.quantity++;
        }

        public Item getItem() {
            return item;
        }
    }
}
