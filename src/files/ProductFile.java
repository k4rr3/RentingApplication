package files;

import domain.Product;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ProductFile {
    private final RandomAccessFile products;

    public ProductFile(String fileName) throws IOException {
        this.products = new RandomAccessFile(fileName, "rw");
    }

    public void write(Product product) throws IOException {
        byte[] record = product.toBytes();
        long pos = (product.getId() - 1) * Product.SIZE;
        products.seek(pos);
        products.write(record);
    }

    public Product read(long id) throws IOException {
        byte[] record = new byte[Product.SIZE];
        this.products.seek(Product.SIZE * (id - 1L));
        this.products.read(record);
        return Product.fromBytes(record);
    }

    public long nextId() throws IOException {
        return products.length() / Product.SIZE + 1L;
    }

    public boolean isValid(long id) throws IOException {
        return id >= 1L && id < nextId();
    }

    public void reset() throws IOException {
        products.setLength(0);
    }

    public void close() throws IOException {
        products.close();
    }
}
