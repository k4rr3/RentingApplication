package domain;

import utils.PackUtils;

import java.io.BufferedReader;

public class Product {

    public static final int DESCRIPTION_LIMIT = 20;
    public static final int SIZE = 8 + 2 * DESCRIPTION_LIMIT + 4 + 4; // TODO: Pas 1

    private final long id;
    private final String description;
    private final int price;
    private int stock;

    public Product(long id, String description, int price, int stock) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {

        return description;
    }

    public int getPrice() {

        return price;
    }

    public int getStock() {
        return stock;
    }

    public void incrementStock() {
        stock += 1;
    }

    public void decrementStock() {

        stock -= 1;
    }

    public byte[] toBytes() {
        int offset = 0;
        byte[] record = new byte[SIZE];
        PackUtils.packLong(id, record, offset);
        offset += 8;
        PackUtils.packLimitedString(description, DESCRIPTION_LIMIT, record, offset);
        offset += 2 * DESCRIPTION_LIMIT;
        PackUtils.packInt(price, record, offset);
        offset += 4;
        PackUtils.packInt(stock, record, offset);
        return record;
    }

    public static Product fromBytes(byte[] record) {
        int offset = 0;
        long id = PackUtils.unpackLong(record, offset);
        offset += 8;
        String description = PackUtils.unpackLimitedString(DESCRIPTION_LIMIT, record, offset);
        offset += 2 * DESCRIPTION_LIMIT;
        int price = PackUtils.unpackInt(record, offset);
        offset += 4;
        int stock = PackUtils.unpackInt(record, offset);
        return new Product(id, description, price, stock);
    }

    public boolean isEqualTo(Product other) {
        return id == other.id
                && description.equals(other.description)
                && price == other.price
                && stock == other.stock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
