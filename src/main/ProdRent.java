package main;

import acm.program.CommandLineProgram;
import domain.Client;
import domain.Product;
import files.ClientFile;
import files.LogFile;
import files.ProductFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ProdRent extends CommandLineProgram {

    private static final String PRODUCTS = "productsDB.dat";
    private static final String CLIENTS = "clientsDB.dat";
    private String movements;
    private String logger;

    private BufferedReader movementsFile;
    private LogFile logFile;
    private ProductFile productsDB;
    private ClientFile clientsDB;

    public static void main(String[] args) {
        new ProdRent().start(args);
    }

    public void run() {
        try {
            askFileNames();
            openFiles();
            resetFiles();
            processMovements();
        } catch (IOException ex) {
            println("ERROR");
            ex.printStackTrace();
        } finally {
            try {
                closeFiles();
            } catch (IOException ex) {
                println("ERROR Closing");
                ex.printStackTrace();
            }
        }
    }

    private void askFileNames() {
        movements = readLine("Enter movements file name: ");
        logger = readLine("Enter your log file name: ");
    }

    private void openFiles() throws IOException {
        movementsFile = new BufferedReader(new FileReader(movements));
        logFile = new LogFile(logger);
        productsDB = new ProductFile(PRODUCTS);
        clientsDB = new ClientFile(CLIENTS);
    }

    private void closeFiles() throws IOException {
        movementsFile.close();
        logFile.close();
        productsDB.close();
        clientsDB.close();
    }

    private void resetFiles() throws IOException {
        movementsFile.reset();
        productsDB.reset();
        clientsDB.reset();
    }

    private void processMovements() throws IOException {
        String line = movementsFile.readLine();
        while (line != null) {
            detectMovement(line);
            line = movementsFile.readLine();
        }
    }

    private void detectMovement(String line) throws IOException {
        StringTokenizer st = new StringTokenizer(line, ",");
        String movement = st.nextToken();
        if (movement.equals("ALTA_PRODUCTO")) {
            registerProduct(st);
        } else if (movement.equals("ALTA_CLIENTE")) {
            registerClient(st);
        } else if (movement.equals("INFO_PRODUCT")) {
            getProductInfo(st);
        } else if (movement.equals("INFO_CLIENT")) {
            getClientInfo(st);
        } else if (movement.equals("ALQUILAR")) {
            rentProduct(st);
        } else if (movement.equals("DEVOLVER")) {
            returnProduct(st);
        } else {
            logFile.unknownOperation(movement);
        }

    }

    private void registerProduct(StringTokenizer st) throws IOException {
        long id = productsDB.nextId();
        String description = st.nextToken();
        int price = Integer.parseInt(st.nextToken());
        int stock = Integer.parseInt(st.nextToken());
        if (price <= 0) {
            logFile.errorPriceCannotBeNegativeOrZero(description, price);
        } else if (stock <= 0) {
            logFile.errorStockCannotBeNegativeOrZero(description, price);
        } else {
            Product newProduct = new Product(id, description, price, stock);
            productsDB.write(newProduct);
            logFile.okNewProduct(newProduct);
        }


    }

    private void registerClient(StringTokenizer st) throws IOException {
        long id = clientsDB.nextId();
        String name = st.nextToken();
        int balance = Integer.parseInt(st.nextToken());
        if (balance <= 0) {
            logFile.errorBalanceCannotBeNegativeOrZero(name, balance);
        } else {
            Client newCLient = new Client(id, name, balance);
            clientsDB.write(newCLient);
            logFile.okNewClient(newCLient);
        }

    }

    private void getProductInfo(StringTokenizer st) throws IOException {
        long idProduct = Long.parseLong(st.nextToken());
        if (invalidProductId(idProduct)) {
            logFile.errorInvalidProductId(idProduct);
        } else {
            Product productInfo = productsDB.read(idProduct);
            logFile.infoProduct(productInfo);
        }
    }

    private boolean invalidProductId(long idProduct) throws IOException {
        return !(idProduct >= 1L && idProduct < productsDB.nextId());
    }

    private void getClientInfo(StringTokenizer st) throws IOException {
        long idClient = Long.parseLong(st.nextToken());
        if (invalidClientId(idClient)) {
            logFile.errorInvalidClientId(idClient);
        } else {
            Client clientInfo = clientsDB.read(idClient);
            long[] productIds = clientInfo.getProductIds();
            Product[] clientProducts = new Product[productIds.length];
            for (int i = 0; i < clientProducts.length; i++) {
                clientProducts[i] = productsDB.read(productIds[i]);
            }
            logFile.infoClient(clientInfo, clientProducts);
        }

    }

    private boolean invalidClientId(long idClient) throws IOException {

        return !(idClient >= 1L && idClient < clientsDB.nextId());
    }

    private void rentProduct(StringTokenizer st) throws IOException {
        long idClient = Long.parseLong(st.nextToken());
        long idProduct = Long.parseLong(st.nextToken());
        if (invalidClientId(idClient)) {
            logFile.errorInvalidClientId(idClient);
        } else if (invalidProductId(idProduct)) {
            logFile.errorInvalidProductId(idProduct);
        } else {
            Client client = clientsDB.read(idClient);
            Product product = productsDB.read(idProduct);
            if (product.getStock() <= 0) {
                logFile.errorCannotRentProductWithNoStock(product);
            } else if (client.getBalance() < product.getPrice()) {
                logFile.errorClientHasNotEnoughFundsToRentProduct(client, product);
            } else if (!client.canAddProduct(idProduct)) {
                logFile.errorClientCannotAddProduct(client, product);
            } else {
                client.addProduct(idProduct);
                client.subBalance(product.getPrice());
                product.decrementStock();
                clientsDB.write(client);
                productsDB.write(product);
                logFile.okRent(client, product);
            }
        }
    }

    private void returnProduct(StringTokenizer st) throws IOException {
        long idClient = Long.parseLong(st.nextToken());
        long idProduct = Long.parseLong(st.nextToken());
        if (invalidClientId(idClient)) {
            logFile.errorInvalidClientId(idClient);
        } else if (invalidProductId(idProduct)) {
            logFile.errorInvalidProductId(idProduct);
        } else {
            Client client = clientsDB.read(idClient);
            Product product = productsDB.read(idProduct);
            if (!client.hasProduct(idProduct)) {
                logFile.errorClientHasNotProduct(client, idProduct);
            } else {
                client.removeProduct(idProduct);
                product.incrementStock();
                clientsDB.write(client);
                productsDB.write(product);
                logFile.okReturn(client, product);
            }
        }
    }


}
