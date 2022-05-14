
package domain;

import utils.PackUtils;

import java.util.Arrays;

public class Client {

    private static final int MAX_PRODUCTS = 3;
    public static final int NAME_LIMIT = 10;
    /***
     * Tamaño del array de bytes en función de los parámetros que deseamos empaquetar
     */
    public static final int SIZE = 8 + 2 * NAME_LIMIT + 4 + 8 * MAX_PRODUCTS + 4 * MAX_PRODUCTS; // TODO: Pas 2

    private final long id;
    private final String name;
    private int balance;

    // This part of the representation is for them to decide:

    private int numProducts;
    private final long[] products;
    private final int[] stock;

    /***
     *Constructor de Client donde inicializamos las variables de instancia
     * @param id
     * @param name
     * @param balance
     */
    public Client(long id, String name, int balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        numProducts = 0;
        products = new long[MAX_PRODUCTS];
        stock = new int[MAX_PRODUCTS];
    }

    /***
     *Retorna el ID del cliente
     * @return
     */
    public long getId() {
        return id;
    }

    /***
     *Retorna  el nombre del cliente
     * @return
     */
    public String getName() {
        return name;
    }

    /***
     *Retorna el saldo actual del clente
     * @return
     */
    public int getBalance() {
        return balance;
    }

    /***
     *Incrementa el saldo del cliente
     * @param amount
     */
    public void addBalance(int amount) {

        balance += amount;
    }

    /***
     *Decrementa el saldo del cliente
     * @param amount
     */
    public void subBalance(int amount) {

        balance -= amount;
    }

    /***
     *Retorna si el cliente puede añadir un producto con un identificador en concreto
     * @param idProduct
     * @return
     */
    public boolean canAddProduct(long idProduct) {
        //updateNumProducts();
        return hasProduct(idProduct) || numProducts < MAX_PRODUCTS;
    }

    /***
     *Retorna si el cliente posee un producto con un identificador en concreto
     * @param idProduct
     * @return
     */
    public boolean hasProduct(long idProduct) {
        for (int i = 0; i < products.length; i++) {
            if (idProduct == products[i]) {
                return true;
            }
        }
        return false;
    }

    /***
     *Añade el producto al cliente en caso de que se pueda
     * @param idProduct
     * @return
     */
    public boolean addProduct(long idProduct) {
        updateNumProducts();
        if (hasProduct(idProduct)) {
            stock[getStockAndProductPosition(idProduct)] += 1;
            return true;
        } else if (numProducts < MAX_PRODUCTS) {
            products[numProducts] = idProduct;
            stock[numProducts] += 1;
            numProducts += 1;
            return true;
        } else {
            return false;
        }
    }

    /***
     *Elimina una unidad del producto del cliente, y en caso de que ya no tenga unidades también se elimina el producto
     * @param idProduct
     * @return
     */
    public boolean removeProduct(long idProduct) {
        if (hasProduct(idProduct)) {
            int productPos = getStockAndProductPosition(idProduct);
            stock[productPos] -= 1;
            if (stock[productPos] == 0) {
                products[productPos] = 0;
                numProducts -= 1;
            }
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

    /***
     *Retorna la cantidad de unidades de un producto que posee el cliente
     * @param idProduct
     * @return
     */
    public int getProductStock(long idProduct) {
        if (hasProduct(idProduct)) {
            return stock[getStockAndProductPosition(idProduct)];
        } else return 0;
    }

    /***
     *Retorna los identificadores de los productos que posee el cliente
     * @return
     */
    public long[] getProductIds() {
        updateNumProducts();
        if (numProducts == 0) {
            return new long[]{};
        }
        int numberOfFoundProducts = 0;
        long[] productIds = new long[numProducts];
        for (int i = 0; i < products.length; i++) {
            if (products[i] != 0) {
                productIds[numberOfFoundProducts] = products[i];
                numberOfFoundProducts++;
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

    /***
     *Empaqueta en un array de bytes las instancias de la clase
     * @return
     */
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

    /***
     * Desempaqueta el array de bytes y crea un nuevo cliente con los datos desempaquetados
     * @param record
     * @return
     */
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