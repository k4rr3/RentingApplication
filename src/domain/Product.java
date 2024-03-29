package domain;

import utils.PackUtils;

public class Product {

    public static final int DESCRIPTION_LIMIT = 20;
    /***
     * Tamaño del array de bytes en función de los parámetros que deseamos empaquetar
     */
    public static final int SIZE = 8 + 2 * DESCRIPTION_LIMIT + 4 + 4; // TODO: Pas 1

    private final long id;
    private final String description;
    private final int price;
    private int stock;

    /***
     * Constructor de la clase Product, donde inicializamos las variables de instancia
     * @param id
     * @param description
     * @param price
     * @param stock
     */
    public Product(long id, String description, int price, int stock) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    /***
     * Getter del id del producto
     * @return
     */
    public long getId() {
        return id;
    }

    /***
     * Getter de la descripción del producto
     * @return
     */
    public String getDescription() {
        return description;
    }

    /***
     * Getter del precio del producto
     * @return
     */
    public int getPrice() {
        return price;
    }

    /***
     * Getter de las unidades que hay en stock que hay del producto
      * @return
     */
    public int getStock() {
        return stock;
    }

    /***
     * Incrementa las unidades en stock de un producto
     */
    public void incrementStock() {
        stock += 1;
    }

    /***
     * Decrementa las unidades de stock que hay de un producto
     */
    public void decrementStock() {
        stock -= 1;
    }

    /***
     * Empaqueta en un array de bytes las instancias de la clase
     * @return
     */
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

    /***
     * Desempaqueta el array de bytes y crea un producto en función de los parámetros desempaquetados
     * @param record
     * @return
     */
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
