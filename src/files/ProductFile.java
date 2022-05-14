package files;

import domain.Product;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ProductFile {
    private final RandomAccessFile products;

    /***
     * Constructor de ProductFile que inicializa el fichero para realizar operaciones de lectura/escritura
     * @param fileName
     * @throws IOException
     */
    public ProductFile(String fileName) throws IOException {
        this.products = new RandomAccessFile(fileName, "rw");
    }

    /***
     * Escribe los datos de un producto en el fichero que guardará los datos de todos los productos
     * @param product
     * @throws IOException
     */
    public void write(Product product) throws IOException {
        byte[] record = product.toBytes();
        long pos = (product.getId() - 1) * Product.SIZE;
        products.seek(pos);
        products.write(record);
    }

    /***
     * Lee los datos del producto con id que se desea del fichero que contiene los datos de todos los productos
     * @param id
     * @return
     * @throws IOException
     */
    public Product read(long id) throws IOException {
        byte[] record = new byte[Product.SIZE];
        this.products.seek(Product.SIZE * (id - 1L));
        this.products.read(record);
        return Product.fromBytes(record);
    }

    /***
     * Calcula el identificador del siguiente producto que se añadirá
     * @return
     * @throws IOException
     */
    public long nextId() throws IOException {
        return products.length() / Product.SIZE + 1L;
    }

    /***
     * Comprueba que el identificador del producto que se desea sea válido
     * @param id
     * @return
     * @throws IOException
     */
    public boolean isValid(long id) throws IOException {
        return id >= 1L && id < nextId();
    }

    /***
     * Resetea la longitud del fichero de los datos de los productos
     * @throws IOException
     */
    public void reset() throws IOException {
        products.setLength(0);
    }

    /***
     * Cierra el fichero que contiene los datos de los productos
     * @throws IOException
     */
    public void close() throws IOException {
        products.close();
    }
}
