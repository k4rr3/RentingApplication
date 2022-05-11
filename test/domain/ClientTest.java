package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void constructor_and_getters() {
        Client client = new Client(1L, "Name", 25);
        assertEquals(1L, client.getId());
        assertEquals("Name", client.getName());
        assertEquals(25, client.getBalance());
    }

    @Test
    void add_first_product() {
        Client client = new Client(1L, "Name", 25);
        assertTrue(client.canAddProduct(1L));
        assertTrue(client.addProduct(1L));
        assertEquals(1L, client.getId());
        assertEquals("Name", client.getName());
        assertEquals(25, client.getBalance());
        assertEquals(1, client.getProductStock(1L));
        assertEquals(0, client.getProductStock(2L));
    }

    @Test
    void add_twice_the_same_product() {
        Client client = new Client(1L, "Name", 25);
        assertTrue(client.canAddProduct(1L));
        assertTrue(client.addProduct(1L));
        assertTrue(client.canAddProduct(1L));
        assertTrue(client.addProduct(1L));
        assertEquals(1L, client.getId());
        assertEquals("Name", client.getName());
        assertEquals(25, client.getBalance());
        assertEquals(2, client.getProductStock(1L));
        assertEquals(0, client.getProductStock(2L));
    }

    @Test
    void add_two_products() {
        Client client = new Client(1L, "Name", 25);
        assertTrue(client.canAddProduct(1L));
        assertTrue(client.addProduct(1L));
        assertTrue(client.canAddProduct(4L));
        assertTrue(client.addProduct(4L));
        assertEquals(1L, client.getId());
        assertEquals("Name", client.getName());
        assertEquals(25, client.getBalance());
        assertEquals(1, client.getProductStock(4L));
        assertEquals(1, client.getProductStock(1L));
    }

    @Test
    void add_four_products() {
        Client client = new Client(1L, "Name", 25);
        assertTrue(client.canAddProduct(1L));
        assertTrue(client.addProduct(1L));
        assertTrue(client.canAddProduct(2L));
        assertTrue(client.addProduct(2L));
        assertTrue(client.canAddProduct(4L));
        assertTrue(client.addProduct(4L));
        assertFalse(client.canAddProduct(5L));
        assertFalse(client.addProduct(5L));
        assertEquals(1L, client.getId());
        assertEquals("Name", client.getName());
        assertEquals(25, client.getBalance());
        assertEquals(1, client.getProductStock(1L));
        assertEquals(1, client.getProductStock(2L));
        assertEquals(1, client.getProductStock(4L));
    }

    @Test
    void remove_one_product() {
        Client client = new Client(1L, "Name", 25);
        client.addProduct(1L);
        client.addProduct(1L);
        client.addProduct(2L);
        assertTrue(client.removeProduct(1L));
        assertEquals(1, client.getProductStock(1L));
        assertEquals(1, client.getProductStock(2L));
    }

    @Test
    void remove_last_one_product() {
        Client client = new Client(1L, "Name", 25);
        assertTrue(client.canAddProduct(1L));
        client.addProduct(1L);
        assertTrue(client.canAddProduct(2L));
        client.addProduct(2L);
        assertTrue(client.removeProduct(1L));
        assertEquals(0, client.getProductStock(1L));
        assertEquals(1, client.getProductStock(2L));
    }

    @Test
    void remove_product_that_not_exists() {
        Client client = new Client(1L, "Name", 25);
        assertTrue(client.canAddProduct(1L));
        client.addProduct(1L);
        assertTrue(client.canAddProduct(2L));
        client.addProduct(2L);
        assertFalse(client.removeProduct(3L));
        assertEquals(1, client.getProductStock(1L));
        assertEquals(1, client.getProductStock(2L));
    }

    @Test
    void has_product_product() {
        Client client = new Client(1L, "Name", 25);
        client.addProduct(1L);
        assertFalse(client.hasProduct(2L));
        assertTrue(client.hasProduct(1L));
    }

    @Test
    void add_balance() {
        Client client = new Client(1L, "Name", 25);
        client.addBalance(20);
        assertEquals(45, client.getBalance());
    }

    @Test
    void sub_balance() {
        Client client = new Client(1L, "Name", 25);
        client.subBalance(20);
        assertEquals(5, client.getBalance());
    }

    @Test
    void get_product_ids() {
        Client client = new Client(1L, "Name", 25);
        client.addProduct(1L);
        long[] ids1 = client.getProductIds();
        assertArrayEquals(new long[]{1L}, ids1);
        long[] ids2 = client.getProductIds();
        assertArrayEquals(ids1, ids2);
        assertNotSame(ids1, ids2);
    }

    @Test
    void round_trip() {
        Client client = new Client(1L, "Name", 25);
        client.addProduct(1L);
        client.addProduct(2L);
        client.addProduct(2L);
        client.addProduct(3L);
        byte[] record = client.toBytes();
        Client recovered = Client.fromBytes(record);
        assertTrue(client.isEqualTo(recovered));
    }

    @Test
    void is_equal_to_no_products() {
        Client client = new Client(1L, "Name", 25);
        Client equal = new Client(1L, "Name", 25);
        Client different1 = new Client(2L, "Name", 25);
        Client different2 = new Client(1L, "Other", 25);
        Client different3 = new Client(1L, "Name", 50);
        assertTrue(equal.isEqualTo(client));
        assertTrue(client.isEqualTo(equal));
        assertFalse(different1.isEqualTo(client));
        assertFalse(client.isEqualTo(different1));
        assertFalse(different2.isEqualTo(client));
        assertFalse(client.isEqualTo(different2));
        assertFalse(different3.isEqualTo(client));
        assertFalse(client.isEqualTo(different3));
    }

    @Test
    void is_equal_to_with_products() {
        Client client = new Client(1L, "Name", 25);
        client.addProduct(1L);
        client.addProduct(2L);
        client.addProduct(1L);

        Client equal = new Client(1L, "Name", 25);
        equal.addProduct(2L);
        equal.addProduct(1L);
        equal.addProduct(1L);

        Client different1 = new Client(1L, "Name", 25);
        different1.addProduct(3L);
        different1.addProduct(1L);
        different1.addProduct(1L);

        Client different2 = new Client(1L, "Name", 25);
        different1.addProduct(1L);
        different1.addProduct(2L);
        different1.addProduct(2L);

        assertTrue(equal.isEqualTo(client));
        assertTrue(client.isEqualTo(equal));
        assertFalse(different1.isEqualTo(client));
        assertFalse(client.isEqualTo(different1));
        assertFalse(different2.isEqualTo(client));
        assertFalse(client.isEqualTo(different2));
    }

    @Test
    void to_string() {
        Client client = new Client(1L, "Name", 25);
        client.addProduct(2L);
        client.addProduct(3L);
        client.addProduct(2L);
        client.addProduct(1L);
        String expected = "Client{id=1, name='Name', balance=25, productIds=[1, 2, 3], stocks=[1, 2, 1]}";
        assertEquals(expected, client.toString());
    }
}