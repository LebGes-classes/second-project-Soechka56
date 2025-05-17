package data.ReadData;

import data.DataStructure.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;


public class xlsIO {

    public List<Product> allProducts;
    private List<Store> allStores;
    private List<Warehouse> allWarehouses;
    private List<Employee> allEmployees;
    private List<Customer> allCustomers;
    private List<Order> allOrders;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    //для хрвнения данных динамически
    xlsIO(){}

    // Конструктор: загружаем всё при создании объекта
    public xlsIO(HSSFWorkbook excelFile) {
        this.allProducts = readProducts(excelFile);
        this.allStores = readStores(excelFile, allProducts);
        this.allWarehouses = readWarehouses(excelFile);
        this.allEmployees = readEmployees(excelFile);
        this.allCustomers = readCustomers(excelFile);
        this.allOrders = readOrders(excelFile);
    }


    public static List<List<String>> readTable(HSSFWorkbook excelFile, String sheetName) {
        List<List<String>> tableData = new ArrayList<>();

        try {
            HSSFSheet sheet = excelFile.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Лист '" + sheetName + "' не найден");
            }

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                HSSFRow row = sheet.getRow(rowIndex);
                List<String> rowData = new ArrayList<>();

                if (row != null) {
                    for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                        rowData.add(getCellValue(row.getCell(colIndex)));
                    }
                }

                tableData.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения файла Excel", e);
        }

        return tableData;
    }

    public static String getCellValue(List<List<String>> tableData, int rowIndex, int colIndex) {
        if (rowIndex < 0 || rowIndex >= tableData.size()) {
            throw new IndexOutOfBoundsException("Неверный индекс строки: " + rowIndex);
        }

        List<String> row = tableData.get(rowIndex);
        if (colIndex < 0 || colIndex >= row.size()) {
            throw new IndexOutOfBoundsException("Неверный индекс столбца: " + colIndex);
        }

        return row.get(colIndex);
    }


    private static String getCellValue(HSSFCell cell) {
        if (cell == null) return "";

        //для преобразования excel форматов, взят из example
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? dateFormat.format(cell.getDateCellValue())
                    : String.valueOf((cell.getNumericCellValue() == (int)cell.getNumericCellValue())
                    ? (int)cell.getNumericCellValue()
                    : cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    public static List<Product> findProducts(List<Integer> ids, List<Product> allProducts) {
        List<Product> result = new ArrayList<>();

        for (Integer id : ids) {
            for (Product product : allProducts) {
                if (product.id == id) {
                    result.add(product);
                    break;
                }
            }
        }
        return result;
    }

    //**cчитываем бухгалтерию//

    //*покупатели
    public static List<Customer> readCustomers(HSSFWorkbook excelFile) {
        List<Customer> customers = new ArrayList<>();
        List<List<String>> tableData = readTable(excelFile, "customers");

        // Предполагаем, что первая строка - заголовки
        for (int i = 1; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            if (row.size() < 6) continue; // Пропускаем неполные строки

            Customer customer = new Customer();
            customer.id = (int)Double.parseDouble(row.get(0));
            customer.name = row.get(1);
            customer.email = row.get(2);
            customer.phone = row.get(3);
            customer.license_number = row.get(4);
            customer.address = row.get(5);

            customers.add(customer);
        }

        return customers;
    }

    //*товары
    public static List<Product> readProducts(HSSFWorkbook excelFile) {
        List<Product> products = new ArrayList<>(); // список товаров
        List<List<String>> tableData = readTable(excelFile, "products"); // читаем лист

        for (int i = 1; i < tableData.size(); i++) { // пропускаем заголовки
            List<String> row = tableData.get(i);
            if (row.size() < 7) continue; // контроль пропуска данных

            Product product = new Product();
            product.id = (int)Double.parseDouble(row.get(0));
            product.name = row.get(1);
            product.price = (int)Double.parseDouble(row.get(2));
            product.stock = (int)Double.parseDouble(row.get(3));
            product.type = row.get(4);
            product.caliber = row.get(5);
            product.manufacturer = row.get(6);
            product.quantity = (int) Double.parseDouble(row.get(7));


            products.add(product); // добавили в список
        }

        return products; // вернули всё
    }

    //*магазины
    public static List<Store> readStores(HSSFWorkbook excelFile, List<Product> allProducts) {
        List<Store> stores = new ArrayList<>();
        List<List<String>> tableData = readTable(excelFile, "stores");

        // Предполагаем, что первая строка - заголовки
        for (int i = 1; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            if (row.size() < 6) continue; // Пропускаем неполные строки

            Store store = new Store();
            store.id = (int)Double.parseDouble(row.get(0));
            store.name = row.get(1);
            store.location = row.get(2);
            store.manager = row.get(3);
            store.phone = row.get(4);
            store.income = (int)Double.parseDouble(row.get(5));

            List<Integer> products = Arrays.stream(row.get(6).split(","))
                    .map(Integer::parseInt)
                    .toList();

            store.products = findProducts(products, allProducts);
            stores.add(store);
        }

        return stores;
    }


    //*заказы
    public static List<Order> readOrders(HSSFWorkbook excelFile) {
        List<Order> orders = new ArrayList<>();
        List<List<String>> tableData = readTable(excelFile, "orders");

        // Предполагаем, что первая строка - заголовки
        for (int i = 1; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            if (row.size() < 7) continue; // Пропускаем неполные строки

            Order order = new Order();
            order.id = (int)Double.parseDouble(row.get(0));
            order.customer_id = (int)Double.parseDouble(row.get(1));
            order.store_id = (int)Double.parseDouble(row.get(2));
            order.product_id = (int)Double.parseDouble(row.get(3));
            order.quantity = (int)Double.parseDouble(row.get(4));
            order.order_date = row.get(5);
            order.status = row.get(6);

            orders.add(order);
        }

        return orders;
    }

    // просто загружаем всё подряд
    public void loadEverything(HSSFWorkbook excelFile) {
        List<Customer> customers = readCustomers(excelFile);
        List<Product> products = readProducts(excelFile);
        List<Store> stores = readStores(excelFile, allProducts);
        List<Order> orders = readOrders(excelFile);
        List<Warehouse> warehouses = readWarehouses(excelFile);

        // тупо выводим сколько чего
        System.out.println(customers.get(0));
        System.out.println(products.get(0));
        System.out.println(stores.get(0));
        System.out.println(orders.get(0));
        System.out.println(warehouses.get(0));

    }

    //*сотрудники
    public static List<Employee> readEmployees(HSSFWorkbook excelFile) {
        List<Employee> employees = new ArrayList<>();
        List<List<String>> tableData = readTable(excelFile, "employees");

        // Предполагаем, что первая строка - заголовки
        for (int i = 1; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            if (row.size() < 7) continue; // Пропускаем неполные строки

            Employee employee = new Employee();
            employee.id = (int)Double.parseDouble(row.get(0));
            employee.name = row.get(1);
            employee.position = row.get(2);
            employee.department = row.get(3);
            employee.salary = Double.parseDouble(row.get(4));
            employee.hire_date = row.get(5);
            employee.is_active = Boolean.parseBoolean(row.get(6));

            employees.add(employee);
        }

        return employees;
    }

    public List<Product> getAllProducts() {
        return this.allProducts;
    }

    //*склады
    public List<Warehouse> readWarehouses(HSSFWorkbook excelFile) {
        List<Warehouse> warehouses = new ArrayList<>();
        List<List<String>> tableData = readTable(excelFile, "warehouse");

        // Предполагаем, что первая строка - заголовки
        for (int i = 1; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            if (row.size() < 6) continue; // Пропускаем неполные строки

            Warehouse warehouse = new Warehouse();
            warehouse.id = (int)Double.parseDouble(row.get(0));
            warehouse.name = row.get(1);
            warehouse.location = row.get(2);
            warehouse.capacity = (int)Double.parseDouble(row.get(3));
            warehouse.security_level = row.get(4);
            warehouse.manager = row.get(5);

            List<Integer> products = Arrays.stream(row.get(6).split(","))
                    .map(Integer::parseInt)
                    .toList();

            warehouse.products = findProducts(products, getAllProducts());


            warehouses.add(warehouse);
        }

        return warehouses;
    }

    }
