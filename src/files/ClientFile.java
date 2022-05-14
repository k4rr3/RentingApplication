package files;

import domain.Client;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ClientFile {
    private final RandomAccessFile clients;

    /***
     * Constructor de ClientFile que inicializa el fichero para realizar operaciones de lectura/escritura
     * @param fileName
     * @throws IOException
     */
    public ClientFile(String fileName) throws IOException {
        this.clients = new RandomAccessFile(fileName, "rw");
    }

    /***
     * Método para escribir los datos de un cliente en el fichero que hemos inicializado
     * @param client
     * @throws IOException
     */
    public void write(Client client) throws IOException {
        byte[] record = new byte[Client.SIZE];
        record = client.toBytes();
        clients.seek(Client.SIZE * (client.getId() - 1L));
        clients.write(record);
    }

    /***
     * Método para lectura de datos de un cliente en función de su id en el fichero que hemos inicializado
     * @param id
     * @return
     * @throws IOException
     */
    public Client read(long id) throws IOException {
        byte[] record = new byte[Client.SIZE];
        clients.seek(Client.SIZE * (id - 1L));
        clients.read(record);
        return Client.fromBytes(record);
    }

    /***
     * Calcula cuál és el próximo id del cliente que se quiere añadir
     * @return
     * @throws IOException
     */
    public long nextId() throws IOException {
        return (clients.length() / Client.SIZE) + 1;
    }

    /***
     * Comprueba que el id que se pide sea válido(que se encuentre dentro de los valores de id que tiene el fichero)
     * @param id
     * @return
     * @throws IOException
     */
    public boolean isValid(long id) throws IOException {
        return id >= 1L && id < nextId();
    }

    /***
     * Establece la longitud del fichero a 0 para borrar todos los datos
     * @throws IOException
     */
    public void reset() throws IOException {
        clients.setLength(0);
    }

    /***
     * Cierra el fichero que contiene los datos de los clientes
     * @throws IOException
     */
    public void close() throws IOException {
        clients.close();
    }
}
