package eu.firen.checkoutsimulator.checkout;

import eu.firen.checkoutsimulator.domain.Item;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Adam on 07.05.2016.
 */
public class CheckoutTransaction {
    private Map<String, Item> items;
    private Map<String, Position> positions;

    public CheckoutTransaction() {
        this.positions = new LinkedHashMap<>();
        this.items = new LinkedHashMap<>();
    }

    public CheckoutTransaction(List<Item> items) {
        this();
        this.items = items.stream()
                                .collect(Collectors.toMap(
                                        Item::getSku,
                                        Function.identity()
                                ));
    }

    public CheckoutTransaction(Map<String, Item> items) {
        this();
        this.items = items;
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
        if(items.containsKey(sku)) {
            if(positions.containsKey(sku)) {
                positions.get(sku).incrementQuantity();
            } else {
                positions.put(sku, new Position(sku, items.get(sku), 1));
            }
            return true;
        } else {
            return false;
        }
    }

    public Position getPosition(String sku) {
        return positions.get(sku);
    }

    public List<Position> getPositions() {
        return this.positions.values().stream().collect(Collectors.toList());
    }

    public static class Position {
        private String sku;
        private Item item;
        private int quantity;

        Position(String sku, Item item, int quantity) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return quantity == position.quantity &&
                    Objects.equals(sku, position.sku) &&
                    Objects.equals(item, position.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sku, item, quantity);
        }
    }
}
