
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
        //movements = readLine("Enter movements file name: ");
        //logger = readLine("Enter your log file name: ");
        movements = "in.txt";
        logger = "out.txt";
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
        productsDB.reset();
        clientsDB.reset();
    }

    private void processMovements() throws IOException {
        String line = movementsFile.readLine();
        while (line != null) {
            processMovement(line);
            line = movementsFile.readLine();
        }
    }

    private void processMovement(String line) throws IOException {
        StringTokenizer st = new StringTokenizer(line, ",");
        if (st.hasMoreTokens()) {//this is to avoid possible errors such as an unexpected empty line at the end of the movements file
            String movement = st.nextToken();
            if (movement.equals("ALTA_PRODUCTO")) {
                registerProduct(st);
            } else if (movement.equals("ALTA_CLIENTE")) {
                registerClient(st);
            } else if (movement.equals("INFO_PRODUCTO")) {
                getProductInfo(st);
            } else if (movement.equals("INFO_CLIENTE")) {
                getClientInfo(st);
            } else if (movement.equals("ALQUILAR")) {
                rentProduct(st);
            } else if (movement.equals("DEVOLVER")) {
                returnProduct(st);
            } else {
                logFile.unknownOperation(movement);
            }
        }
    }

    public void registerProduct(StringTokenizer st) throws IOException {
        long id = productsDB.nextId();
        String description = st.nextToken();
        int price = Integer.parseInt(st.nextToken());
        int stock = Integer.parseInt(st.nextToken());
        if (positivePriceAndStock(description, price, stock)) {
            Product newProduct = new Product(id, description, price, stock);
            productsDB.write(newProduct);
            logFile.okNewProduct(newProduct);
        }


    }

    private boolean positivePriceAndStock(String description, int price, int stock) throws IOException {
        boolean validProduct = true;
        if (price <= 0) {
            logFile.errorPriceCannotBeNegativeOrZero(description, price);
            validProduct = false;
        }
        if (stock <= 0) {
            logFile.errorStockCannotBeNegativeOrZero(description, stock);
            validProduct = false;
        }
        return validProduct;
    }

    public void registerClient(StringTokenizer st) throws IOException {
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

    public void getProductInfo(StringTokenizer st) throws IOException {
        long idProduct = Long.parseLong(st.nextToken());
        if (validProductId(idProduct)) {
            Product productInfo = productsDB.read(idProduct);
            logFile.infoProduct(productInfo);
        }
    }

    private boolean validProductId(long idProduct) throws IOException {
        boolean validProduct = true;
        if (!productsDB.isValid(idProduct)) {
            logFile.errorInvalidProductId(idProduct);
            validProduct = false;
        }
        return validProduct;
    }

    public void getClientInfo(StringTokenizer st) throws IOException {
        long idClient = Long.parseLong(st.nextToken());
        if (validClientId(idClient)) {
            Client clientInfo = clientsDB.read(idClient);
            long[] productIds = clientInfo.getProductIds();
            Product[] clientProducts = new Product[productIds.length];

            for (int i = 0; i < clientProducts.length; i++) {
                clientProducts[i] = productsDB.read(productIds[i]);
            }

            logFile.infoClient(clientInfo, clientProducts);
        }

    }


    private boolean validClientId(long idClient) throws IOException {
        boolean validClient = true;
        if (!clientsDB.isValid(idClient)) {
            logFile.errorInvalidClientId(idClient);
            validClient = false;
        }
        return validClient;
    }

    public void rentProduct(StringTokenizer st) throws IOException {
        long idClient = Long.parseLong(st.nextToken());
        long idProduct = Long.parseLong(st.nextToken());

        if (validClientAndProductIds(idClient, idProduct)) {
            Client client = clientsDB.read(idClient);
            Product product = productsDB.read(idProduct);

            if (isValidRent(client, product)) {
                client.addProduct(idProduct);
                client.subBalance(product.getPrice());
                product.decrementStock();
                clientsDB.write(client);
                productsDB.write(product);
                logFile.okRent(client, product);
            }
        }
    }

    private boolean isValidRent(Client client, Product product) throws IOException {
        boolean validRent = true;
        if (product.getStock() <= 0) {
            logFile.errorCannotRentProductWithNoStock(product);
            validRent = false;
        }
        if (client.getBalance() < product.getPrice()) {
            logFile.errorClientHasNotEnoughFundsToRentProduct(client, product);
            validRent = false;
        }
        if (!client.canAddProduct(product.getId())) {
            logFile.errorClientCannotAddProduct(client, product);
            validRent = false;
        }
        return validRent;
    }


    public void returnProduct(StringTokenizer st) throws IOException {
        long idClient = Long.parseLong(st.nextToken());
        long idProduct = Long.parseLong(st.nextToken());
        if (validClientAndProductIds(idClient, idProduct)) {
            Client client = clientsDB.read(idClient);
            Product product = productsDB.read(idProduct);
            if (!(client.hasProduct(product.getId()))) {
                logFile.errorClientHasNotProduct(client, product.getId());
            } else {
                client.removeProduct(idProduct);
                product.incrementStock();
                clientsDB.write(client);
                productsDB.write(product);
                logFile.okReturn(client, product);
            }
        }
    }

    private boolean validClientAndProductIds(long idClient, long idProduct) throws IOException {
        boolean isValid = true;
        if (!clientsDB.isValid(idClient)) {
            logFile.errorInvalidClientId(idClient);
            isValid = false;
        }
        if (!productsDB.isValid(idProduct)) {
            logFile.errorInvalidProductId(idProduct);
            isValid = false;
        }
        return isValid;
    }
}