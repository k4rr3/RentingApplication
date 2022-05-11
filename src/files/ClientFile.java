package files;

import domain.Client;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ClientFile {
    private final RandomAccessFile clients;

    public ClientFile(String fileName) throws IOException {
        this.clients = new RandomAccessFile(fileName, "rw");
    }

    public void write(Client client) throws IOException {
        byte[] record = new byte[Client.SIZE];
        record = client.toBytes();
        clients.seek(Client.SIZE * (client.getId() - 1L));
        clients.write(record);
    }

    public Client read(long id) throws IOException {
        byte[] record = new byte[Client.SIZE];
        clients.seek(Client.SIZE * (id - 1L));
        clients.read(record);
        return Client.fromBytes(record);
    }

    public long nextId() throws IOException {
        return (clients.length() / Client.SIZE) + 1;
    }

    public boolean isValid(long id) throws IOException {
        return id >= 1 && id < nextId();
    }

    public void reset() throws IOException {
        clients.setLength(0);
    }

    public void close() throws IOException {
        clients.close();
    }
}
