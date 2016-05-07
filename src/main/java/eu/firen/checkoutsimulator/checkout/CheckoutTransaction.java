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
                .mapToLong(this::calculatePositionPrice)
                .sum();
    }

    private long calculatePositionPrice(Position position) {
        if(position.getItem().getSpecialPrice().isPresent()) {
            long bundleSize = position.getItem().getSpecialPrice().get().getBundleSize();
            long bundlePrice = position.getItem().getSpecialPrice().get().getBundlePrice();
            long itemPrice = (position.quantity / bundleSize) * bundlePrice
                    + (position.quantity % bundleSize) * position.getItem().getUnitPrice();
            return itemPrice;
        } else {
            return position.quantity * position.getItem().getUnitPrice();
        }
    }

    public boolean addItem(String sku) {
        if(items.containsKey(sku)) {
            if(positions.containsKey(sku)) {
                Position position = positions.get(sku);
                position.incrementQuantity();
                position.setPrice(calculatePositionPrice(position));
            } else {
                Position position = new Position(sku, items.get(sku), 1, 0);
                position.setPrice(calculatePositionPrice(position));
                positions.put(sku, position);
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
        private long price;

        Position(String sku, Item item, int quantity, long price) {
            this.sku = sku;
            this.item = item;
            this.quantity = quantity;
            this.price = price;
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

        public long getPrice() {
            return price;
        }

        private void setPrice(long price) {
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return quantity == position.quantity &&
                    price == position.price &&
                    Objects.equals(sku, position.sku) &&
                    Objects.equals(item, position.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sku, item, quantity, price);
        }
    }
}
