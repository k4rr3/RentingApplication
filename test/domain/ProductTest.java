package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void constructor_and_getters() {
        Product product = new Product(12L, "ACME Laser Beam", 1234, 4);
        assertEquals(12L, product.getId());
        assertEquals("ACME Laser Beam", product.getDescription());
        assertEquals(1234, product.getPrice());
        assertEquals(4, product.getStock());
    }

    @Test
    void increment_stock() {
        Product product = new Product(12L, "ACME Laser Beam", 1234, 4);
        product.incrementStock();
        assertEquals(5, product.getStock());
    }

    @Test
    void decrement_stock() {
        Product product = new Product(12L, "ACME Laser Beam", 1234, 4);
        product.decrementStock();
        assertEquals(3, product.getStock());
    }

    @Test
    void round_trip() {
        Product product = new Product(12L, "ACME Laser Beam", 1234, 4);
        byte[] record = product.toBytes();
        Product recovered = Product.fromBytes(record);
        assertTrue(product.isEqualTo(recovered));
    }

    @Test
    void round_trip_long_description() {
        Product product = new Product(12L, "A product with a big, big, name, a very long description so long it can be boring", 1234, 4);
        byte[] record = product.toBytes();
        Product recovered = Product.fromBytes(record);
        assertEquals(product.getId(), recovered.getId());
        String expectedDescription = product.getDescription().substring(0, Product.DESCRIPTION_LIMIT);
        assertEquals(expectedDescription, recovered.getDescription());
        assertEquals(product.getPrice(), recovered.getPrice());
        assertEquals(product.getStock(), recovered.getStock());
    }

    @Test
    void to_string() {
        Product product = new Product(12L, "ACME Laser Beam", 1234, 4);
        String expected = "Product{id=12, description='ACME Laser Beam', price=1234, stock=4}";
        assertEquals(expected, product.toString());
    }

    @Test
    void is_equal_to() {
        Product product = new Product(12L, "ACME Laser Beam", 1234, 4);
        Product equal = new Product(12L, "ACME Laser Beam", 1234, 4);
        Product different1 = new Product(10L, "ACME Laser Beam", 1234, 4);
        Product different2 = new Product(12L, "GROMEK Gun", 1234, 4);
        Product different3 = new Product(12L, "ACME Laser Beam", 4321, 4);
        Product different4 = new Product(12L, "ACME Laser Beam", 1234, 10);
        assertTrue(equal.isEqualTo(product));
        assertTrue(product.isEqualTo(equal));
        assertFalse(different1.isEqualTo(product));
        assertFalse(product.isEqualTo(different1));
        assertFalse(different2.isEqualTo(product));
        assertFalse(product.isEqualTo(different2));
        assertFalse(different3.isEqualTo(product));
        assertFalse(product.isEqualTo(different3));
        assertFalse(different4.isEqualTo(product));
        assertFalse(product.isEqualTo(different4));
    }
}