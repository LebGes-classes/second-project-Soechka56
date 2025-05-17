package data.ReadData;

import UserInterface.UI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import data.DataStructure.*;
import data.DataTransferLogic.WarehouseLogic;
import data.ReadData.xlsIO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class JsonIO {
    private static final ObjectMapper eachNoteInNewRow = new ObjectMapper();

    static {
        eachNoteInNewRow.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static String serialize(Object object) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            eachNoteInNewRow.writeValue(writer, object);
            return writer.toString();
        }
    }


    public static <T> List<T> deserializeList(String json, TypeReference<List<T>> typeRef) throws IOException {
        return eachNoteInNewRow.readValue(json, typeRef);
    }

    public static <T> void saveListToJsonFile(List<T> list, String filePath) throws IOException {
        String json = serialize(list);
        Files.write(Paths.get(filePath), json.getBytes());
    }

    public static void saveAllDataToJson(List<Product> products, List<Warehouse> warehouses, List<Customer> customers, List<Store> stores, List<Employee> employees, List<Order> orders) throws IOException {
        saveListToJsonFile(products, "src/main/asserts/products.json");
        saveListToJsonFile(warehouses, "src/main/asserts/warehouses.json");
        saveListToJsonFile(customers, "src/main/asserts/customers.json");
        saveListToJsonFile(stores, "src/main/asserts/stores.json");
        saveListToJsonFile(employees, "src/main/asserts/employees.json");
        saveListToJsonFile(orders, "src/main/asserts/orders.json");
    }


    public static void readAllDataFromExcel() throws IOException {

        try (FileInputStream fis = new FileInputStream("src/main/asserts/goods.xls");
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {


        xlsIO reader = new xlsIO();

        List<Product> products = reader.readProducts(workbook);
        reader.allProducts = products;
        List<Warehouse> warehouses = reader.readWarehouses(workbook);
        List<Customer> customers = reader.readCustomers(workbook);

        List<Store> stores = reader.readStores(workbook, products);
        List<Employee> employees = reader.readEmployees(workbook);
        List<Order> orders = reader.readOrders(workbook);

        saveAllDataToJson(products, warehouses, customers, stores, employees, orders);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла Excel");
            e.printStackTrace();
        }

    }
}