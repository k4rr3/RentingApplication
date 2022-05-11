package files;

import domain.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ProductFileTest {

    static final String PRODUCT_TEST = "product_test.dat";

    ProductFile products;

    Product product1 = new Product(1L, "Product1", 123, 1);
    Product product2 = new Product(2L, "Product2", 456, 2);
    Product product3 = new Product(3L, "Product3", 789, 3);

    @BeforeEach
    void createFile() throws IOException {
        products = new ProductFile(PRODUCT_TEST);
        products.write(product2);
        products.write(product1);
        products.write(product3);
    }

    @AfterEach
    void tearDown() throws IOException {
        products.close();
        new File(PRODUCT_TEST).delete();
    }

    @Test
    void read() throws IOException {
        Product read3 = products.read(3L);
        Product read1 = products.read(1L);
        Product read2 = products.read(2L);
        assertTrue(product1.isEqualTo(read1));
        assertTrue(product2.isEqualTo(read2));
        assertTrue(product3.isEqualTo(read3));
    }

    @Test
    void nextId() throws IOException {
        assertEquals(4, products.nextId());
    }

    @Test
    void isValid() throws IOException {
        assertFalse(products.isValid(0L));
        assertTrue(products.isValid(1L));
        assertTrue(products.isValid(3L));
        assertFalse(products.isValid(4L));
    }

    @Test
    void reset() throws IOException {
        products.reset();
        assertEquals(1L, products.nextId());
    }
}