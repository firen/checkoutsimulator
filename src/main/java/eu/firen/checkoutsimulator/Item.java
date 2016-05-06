package eu.firen.checkoutsimulator;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by Adam on 06.05.2016.
 */
public class Item {
    private String sku;
    private long unitPrice;
    private Optional<SpecialPrice> specialPrice;

    public Item(String sku, long unitPrice, Optional<SpecialPrice> specialPrice) {
        this.sku = sku;
        this.unitPrice = unitPrice;
        this.specialPrice = specialPrice;
    }

    public Item(String sku, long unitPrice) {
        this(sku, unitPrice, Optional.empty());
    }

    public String getSku() {
        return this.sku;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public Optional<SpecialPrice> getSpecialPrice() {
        return specialPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return unitPrice == item.unitPrice &&
                Objects.equals(sku, item.sku) &&
                Objects.equals(specialPrice, item.specialPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, unitPrice, specialPrice);
    }

    public static class SpecialPrice {
        private long bundleSize;
        private long bundlePrice;

        public SpecialPrice(long bundleSize, long bundlePrice) {
            this.bundleSize = bundleSize;
            this.bundlePrice = bundlePrice;
        }

        public long getBundleSize() {
            return bundleSize;
        }

        public long getBundlePrice() {
            return bundlePrice;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpecialPrice that = (SpecialPrice) o;
            return bundleSize == that.bundleSize &&
                    bundlePrice == that.bundlePrice;
        }

        @Override
        public int hashCode() {
            return Objects.hash(bundleSize, bundlePrice);
        }
    }
}
