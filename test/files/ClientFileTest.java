package files;

import domain.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClientFileTest {

    static final String CLIENT_TEST = "client_test.dat";

    ClientFile clients;

    Client client1 = new Client(1L, "Client1", 123);
    Client client2 = new Client(2L, "Client2", 456);
    Client client3 = new Client(3L, "Client3", 789);

    @BeforeEach
    void createFile() throws IOException {
        clients = new ClientFile(CLIENT_TEST);
        clients.write(client2);
        clients.write(client1);
        clients.write(client3);
    }

    @AfterEach
    void tearDown() throws IOException {
        clients.close();
        new File(CLIENT_TEST).delete();
    }

    @Test
    void read() throws IOException {
        Client read3 = clients.read(3L);
        Client read1 = clients.read(1L);
        Client read2 = clients.read(2L);
        assertTrue(client1.isEqualTo(read1));
        assertTrue(client2.isEqualTo(read2));
        assertTrue(client3.isEqualTo(read3));
    }

    @Test
    void nextId() throws IOException {
        assertEquals(4, clients.nextId());
    }

    @Test
    void isValid() throws IOException {
        assertFalse(clients.isValid(0L));
        assertTrue(clients.isValid(1L));
        assertTrue(clients.isValid(3L));
        assertFalse(clients.isValid(4L));
    }

    @Test
    void reset() throws IOException {
        clients.reset();
        assertEquals(1L, clients.nextId());
    }
}