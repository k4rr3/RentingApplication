
package domain;

import utils.PackUtils;

import java.util.Arrays;

public class Client {

    private static final int MAX_PRODUCTS = 3;
    public static final int NAME_LIMIT = 10;
    public static final int SIZE = 8 + 2 * NAME_LIMIT + 4 + 8 * MAX_PRODUCTS + 4 * MAX_PRODUCTS; // TODO: Pas 2

    private final long id;
    private final String name;
    private int balance;

    // This part of the representation is for them to decide:

    private int numProducts;
    private final long[] products;
    private final int[] stock;


    public Client(long id, String name, int balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        numProducts = 0;
        products = new long[MAX_PRODUCTS];
        stock = new int[MAX_PRODUCTS];
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void addBalance(int amount) {

        balance += amount;
    }

    public void subBalance(int amount) {

        balance -= amount;
    }

    public boolean canAddProduct(long idProduct) {
        updateNumProducts();
        return hasProduct(idProduct) || numProducts < MAX_PRODUCTS;
    }

    public boolean hasProduct(long idProduct) {
        for (int i = 0; i < products.length; i++) {
            if (idProduct == products[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean addProduct(long idProduct) {
        /*if (canAddProduct(idProduct)) {//compruebo aqui una vez el has product
            if (!hasProduct(idProduct)) {//y aqui otra vez, hay que arreglarlo0
                products[numProducts] = idProduct;
                numProducts++;
            }
            stock[getStockAndProductPosition(idProduct)] += 1;

            return true;
    }
        return false;*/
        updateNumProducts();
        if (hasProduct(idProduct)) {
            stock[getStockAndProductPosition(idProduct)] += 1;
            return true;
        } else if (numProducts < MAX_PRODUCTS) {
            products[numProducts] = idProduct;
            stock[numProducts] += 1;
            numProducts++;
            return true;
        } else {
            return false;
        }
        /*boolean isAdd = false;
        for (int i = 0; i < MAX_PRODUCTS && !isAdd; i++) {
            if (products[i] == idProduct) {
                stock[i] += 1;
                isAdd = true;
            } else if (products[i] == 0) {
                products[i] = idProduct;
                stock[i] += 1;
                isAdd = true;
            }
        }
        return isAdd;*/
    }

    public boolean removeProduct(long idProduct) {

        if (hasProduct(idProduct)) {
            stock[getStockAndProductPosition(idProduct)] -= 1;
            return true;
        }
        return false;
    }

    private int getStockAndProductPosition(long idProduct) {
        for (int i = 0; i < products.length; i++) {
            if (idProduct == products[i]) {
                return i;
            }
        }
        return -1;
    }

    public int getProductStock(long idProduct) {
        if (hasProduct(idProduct)) {
            return stock[getStockAndProductPosition(idProduct)];
        } else return 0;
    }

    public long[] getProductIds() {
        updateNumProducts();
        if (numProducts == 0) {
            return new long[]{};
        }
        long[] productIds = new long[numProducts];
        for (int i = 0; i < numProducts; i++) {
            if (products[i] != 0) {
                productIds[i] = products[i];
            }
        }
        return productIds;
    }

    private void updateNumProducts() {
        this.numProducts = 0;
        for (int i = 0; i < products.length; i++) {
            if (products[i] != 0) {
                numProducts++;
            }
        }
    }

    public byte[] toBytes() {
        int offset = 0;
        byte[] record = new byte[SIZE];
        PackUtils.packLong(this.id, record, offset);
        offset += 8;
        PackUtils.packLimitedString(this.name, NAME_LIMIT, record, offset);
        offset += 2 * NAME_LIMIT;
        PackUtils.packInt(balance, record, offset);
        offset += 4;
        for (int i = 0; i < products.length; i++) {
            PackUtils.packLong(products[i], record, offset);
            offset += 8;
            PackUtils.packInt(stock[i], record, offset);
            offset += 4;
        }
        return record;
    }

    public static Client fromBytes(byte[] record) {
        int offset = 0;
        long id = PackUtils.unpackLong(record, offset);
        offset += 8;
        String name = PackUtils.unpackLimitedString(NAME_LIMIT, record, offset);
        offset += 2 * NAME_LIMIT;
        int balance = PackUtils.unpackInt(record, offset);
        offset += 4;
        Client client = new Client(id, name, balance);
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            client.products[i] = PackUtils.unpackLong(record, offset);
            offset += 8;
            client.stock[i] = PackUtils.unpackInt(record, offset);
            offset += 4;
        }

        return client;
    }

    public boolean isEqualTo(Client other) {
        if (id != other.id
                || !name.equals(other.name)
                || balance != other.balance) {
            return false;
        }
        long[] myProductIds = getProductIds();
        long[] theirProductIds = other.getProductIds();
        Arrays.sort(myProductIds);
        Arrays.sort(theirProductIds);
        int[] myStocks = getStocks(myProductIds);
        int[] theirStocks = getStocks(theirProductIds);
        return Arrays.equals(myProductIds, theirProductIds)
                && Arrays.equals(myStocks, theirStocks);
    }

    @Override
    public String toString() {
        long[] productIds = getProductIds();
        Arrays.sort(productIds);
        int[] stocks = getStocks(productIds);
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", productIds=" + Arrays.toString(productIds) +
                ", stocks=" + Arrays.toString(stocks) +
                '}';
    }

    private int findProductIndex(long idProduct) {
        for (int i = 0; i < products.length; i++) {
            if (products[i] == idProduct) {
                return i;
            }
        }
        return -1;
    }

    private void removeProductEntry(int pos) {
        numProducts -= 1;
        if (numProducts != 0) {
            products[pos] = products[numProducts];
            stock[pos] = stock[numProducts];
        }
    }

    private int[] getStocks(long[] productIds) {
        int[] stocks = new int[productIds.length];
        for (int i = 0; i < stocks.length; i++) {
            stocks[i] = this.getProductStock(productIds[i]);
        }
        return stocks;
    }
}