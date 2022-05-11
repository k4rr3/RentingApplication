package files;

import domain.Client;
import domain.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogFile {
    private final BufferedWriter log;

    public LogFile(String fileName) throws IOException {
        log = new BufferedWriter(new FileWriter(fileName));
    }

    public void close() throws IOException {
        log.close();
    }

    private void writeln(String line) throws IOException {
        log.write(line);
        log.newLine();
    }

    private void error(String message) throws IOException {
        writeln("ERROR: " + message);
    }

    private void ok(String message) throws IOException {
        writeln("OK: " + message);
    }

    private void info(String message) throws IOException {
        writeln("INFO: " + message);
    }

    public void unknownOperation(String op) throws IOException {
        error(String.format("Unknown operation %s", op));
    }

    public void errorPriceCannotBeNegativeOrZero(String description, int price) throws IOException {
        error(String.format("Price %d is negative or zero in new product %s", price, description));
    }

    public void errorStockCannotBeNegativeOrZero(String description, int stock) throws IOException {
        error(String.format("Stock %d is negative or zero in new product %s", stock, description));
    }

    public void okNewProduct(Product product) throws IOException {
        ok(String.format("Successfully created new product %s", product));
    }

    public void errorBalanceCannotBeNegativeOrZero(String name, int balance) throws IOException {
        error(String.format("Balance %d is negative or zero in new client %s", balance, name));
    }

    public void okNewClient(Client client) throws IOException {
        ok(String.format("Successfully created new client %s", client));
    }

    public void errorInvalidProductId(long idProduct) throws IOException {
        error(String.format("There is no product with id %d", idProduct));
    }

    public void infoProduct(Product product) throws IOException {
        info(String.format("The info of the product is %s", product));
    }

    public void errorInvalidClientId(long idClient) throws IOException {
        error(String.format("There is no client with id %d", idClient));
    }

    public void infoClient(Client client, Product[] products) throws IOException {
        info(String.format("The info of the client is %s", client));
        for (int i = 0; i < products.length; i++) {
            info(String.format("\t %d: %s", i + 1, products[i]));
        }
    }

    public void errorCannotRentProductWithNoStock(Product product) throws IOException {
        error(String.format("Cannot rent a product with zero stock %s", product));
    }

    public void errorClientHasNotEnoughFundsToRentProduct(Client client, Product product) throws IOException {
        error(String.format("Client %s has not enough funds for renting product %s", client, product));
    }

    public void errorClientCannotAddProduct(Client client, Product product) throws IOException {
        error(String.format("Client %s cannot rent product %s", client, product));
    }

    public void okRent(Client client, Product product) throws IOException {
        ok(String.format("Client %s has successfully rented product %s", client, product));
    }

    public void errorClientHasNotProduct(Client client, long idProduct) throws IOException {
        error(String.format("Client %s has not product %d", client, idProduct));
    }

    public void okReturn(Client client, Product product) throws IOException {
        ok(String.format("Client %s has successfully returned product %s", client, product));
    }
}
