package data.DataTransferLogic;

import com.fasterxml.jackson.core.type.TypeReference;
import data.ReadData.*;
import data.DataStructure.*;
import data.ReadData.JsonIO;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WarehouseLogic {

    public static List<Product> allProducts = new ArrayList<>();
    public static List<Employee> allEmployees = new ArrayList<>();
    public static List<Warehouse> allWarehouses = new ArrayList<>();
    public static List<Store> allStores = new ArrayList<>();
    public static List<Customer> allCustomers = new ArrayList<>();

    //пути к файлам
    private static final String PRODUCTS_JSON = "src/main/asserts/products.json";
    private static final String WAREHOUSES_JSON = "src/main/asserts/warehouses.json";
    private static final String EMPLOYEES_JSON = "src/main/asserts/employees.json";
    private static final String STORES_JSON = "src/main/asserts/stores.json";
    private static final String CUSTOMERS_JSON = "src/main/asserts/customers.json";

    public static void init() throws IOException {
        JsonIO.readAllDataFromExcel();
    }

    //метод для загрузки данных
    private static <T> List<T> loadFromJson(String filePath, TypeReference<List<T>> typeRef) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        return JsonIO.deserializeList(json, typeRef);
    }

    //метод для сохранения данных
    private static <T> void saveToJson(String filePath, List<T> data) throws IOException {
        String json = JsonIO.serialize(data);
        Files.write(Paths.get(filePath), json.getBytes());
    }

    public static void printAllProducts() {
        try {
            allProducts = loadFromJson(PRODUCTS_JSON, new TypeReference<List<Product>>() {});
            System.out.println("Все товары:");
            for (Product p : allProducts) {
                System.out.println("ID: " + p.id + ", Название: " + p.name);
            }
            allProducts = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении products.json: " + e.getMessage());
        }
    }

    public static void printProductAvailability(int productId) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            System.out.println("\nНаличие товара по складам:");
            for (Warehouse w : allWarehouses) {
                int count = 0;
                for (Product p : w.products) {
                    if (p.id == productId) {
                        count = p.quantity;
                        break;
                    }
                }
                System.out.println("Склад ID: " + w.id + " (" + w.location + ") — " + count + " шт.");
            }
            allWarehouses = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении warehouses.json: " + e.getMessage());
        }
    }

    public static void printAllWarehouses() {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            System.out.println("Все склады:");
            for (Warehouse w : allWarehouses) {
                System.out.println("ID: " + w.id + ", Локация: " + w.location);
            }
            allWarehouses = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении warehouses.json: " + e.getMessage());
        }
    }

    public static boolean skladSoderzhitProduct(Warehouse warehouse, int productId) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            for (Product p : warehouse.products) {
                if (p.id == productId) {
                    allWarehouses = null; // чистка памяти
                    return true;
                }
            }
            allWarehouses = null; // чистка памяти
            return false;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении warehouses.json: " + e.getMessage());
            return false;
        }
    }

    public static void peremestitProduct(int productId, int fromId, int toId) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            Warehouse from = findWarehouseById(fromId);
            Warehouse to = findWarehouseById(toId);

            if (from == null || to == null) {
                System.out.println("Ошибка: один из складов не найден");
                allWarehouses = null; // чистка памяти
                return;
            }

            Product fromProduct = null;
            for (Product p : from.products) {
                if (p.id == productId) {
                    fromProduct = p;
                    break;
                }
            }

            if (fromProduct == null) {
                System.out.println("Ошибка: товар с ID " + productId + " не найден на складе-отправителе");
                allWarehouses = null; // чистка памяти
                return;
            }

            System.out.print("Введите количество для перемещения (доступно: " + fromProduct.quantity + "): ");
            Scanner scanner = new Scanner(System.in);
            int count = Integer.parseInt(scanner.nextLine());

            if (count <= 0 || count > fromProduct.quantity) {
                System.out.println("Ошибка: некорректное количество");
                allWarehouses = null; // чистка памяти
                return;
            }

            fromProduct.quantity -= count;
            if (fromProduct.quantity == 0) {
                from.products.remove(fromProduct);
            }

            Product toProduct = null;
            for (Product p : to.products) {
                if (p.id == productId) {
                    toProduct = p;
                    break;
                }
            }

            if (toProduct != null) {
                toProduct.quantity += count;
            } else {
                Product newProduct = new Product(fromProduct.id, fromProduct.name, fromProduct.price, count);
                to.products.add(newProduct);
            }

            // Сохраняем изменения
            saveToJson(WAREHOUSES_JSON, allWarehouses);

            System.out.println("Перемещено " + count + " шт. товара ID " + productId +
                    " со склада " + fromId + " на склад " + toId);
            allWarehouses = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлами: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ввведите корректное число");
        }
    }

    public static void smenaResponsibleNaSklade(int warehouseId, int employeeId) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            allEmployees = loadFromJson(EMPLOYEES_JSON, new TypeReference<List<Employee>>() {});

            Warehouse warehouse = findWarehouseById(warehouseId);
            Employee employee = findEmployeeById(employeeId);

            if (warehouse != null && employee != null) {
                warehouse.manager = employee.name;
                // Сохраняем изменения
                saveToJson(WAREHOUSES_JSON, allWarehouses);
                System.out.println("Ответственный на складе " + warehouseId + ": " + employee.name);
            } else {
                System.out.println("Ошибка: Склад или сотрудник не найдены");
            }

            allWarehouses = null; // чистка памяти
            allEmployees = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении JSON файлов: " + e.getMessage());
        }
    }

    public static void prodazhaProduct(int storeId, int productId, int count) {
        try {
            allStores = loadFromJson(STORES_JSON, new TypeReference<List<Store>>() {});
            allProducts = loadFromJson(PRODUCTS_JSON, new TypeReference<List<Product>>() {});

            Store store = findStoreById(storeId);
            Product product = findProductById(productId);

            if (store != null && product != null && product.quantity >= count) {
                product.quantity -= count;
                store.income += product.price * count;

                // Сохраняем изменения
                saveToJson(STORES_JSON, allStores);
                saveToJson(PRODUCTS_JSON, allProducts);

                System.out.println("Продано " + count + " шт. товара " + productId + ". Выручка: " + store.income);
            } else {
                System.out.println("Ошибка: Недостаточно товара или объект не найден");
            }

            allStores = null; // чистка памяти
            allProducts = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении джейсон файлов: " + e.getMessage());
        }
    }

    public static void vozvratProductNaSklad(int warehouseId, int productId, int count) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            allProducts = loadFromJson(PRODUCTS_JSON, new TypeReference<List<Product>>() {});

            Warehouse warehouse = findWarehouseById(warehouseId);
            Product product = findProductById(productId);

            if (warehouse != null && product != null) {
                warehouse.products.add(product);
                product.quantity += count;

                // Сохраняем изменения
                saveToJson(WAREHOUSES_JSON, allWarehouses);
                saveToJson(PRODUCTS_JSON, allProducts);

                System.out.println("Возвращено " + count + " шт. товара " + productId + " на склад " + warehouseId);
            } else {
                System.out.println("Ошибка: Склад или товар не найдены");
            }

            allWarehouses = null; // чистка памяти
            allProducts = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении JSON файлов: " + e.getMessage());
        }
    }

    public static void zakupkaNovogoProductNaSklad(int warehouseId, Product newProduct, int quantity) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            allProducts = loadFromJson(PRODUCTS_JSON, new TypeReference<List<Product>>() {});

            Warehouse warehouse = findWarehouseById(warehouseId);

            if (warehouse != null && newProduct != null) {
                newProduct.quantity = quantity;
                warehouse.products.add(newProduct);
                allProducts.add(newProduct);

                // Сохраняем изменения
                saveToJson(WAREHOUSES_JSON, allWarehouses);
                saveToJson(PRODUCTS_JSON, allProducts);

                System.out.println("Закуплено " + quantity + " шт. товара " + newProduct.id + " на склад " + warehouseId);
            } else {
                System.out.println("Ошибка: Склад или товар не найдены");
            }

            allWarehouses = null; // чистка памяти
            allProducts = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении warehouses.json: " + e.getMessage());
        }
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    public static void dobavitCustomer(Customer customer) {
        try {
            allCustomers = loadFromJson(CUSTOMERS_JSON, new TypeReference<List<Customer>>() {});

            if (customer == null) {
                System.out.println("Ошибка: клиент пустой");
                allCustomers = null; // чистка памяти
                return;
            }

            if (customer.name == null || customer.name.trim().isEmpty()) {
                System.out.println("Ошибка: имя клиента не может быть пустым");
                allCustomers = null; // чистка памяти
                return;
            }

            if (!isValidEmail(customer.email)) {
                System.out.println("Ошибка: некорректный email: " + customer.email);
                allCustomers = null; // чистка памяти
                return;
            }

            if (findCustomerById(customer.id) != null) {
                System.out.println("Ошибка: клиент с таким ID уже существует");
                allCustomers = null; // чистка памяти
                return;
            }

            allCustomers.add(customer);
            saveToJson(CUSTOMERS_JSON, allCustomers);
            System.out.println("Добавлен клиент: " + customer.name);
            allCustomers = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при работе с customers.json: " + e.getMessage());
        }
    }

    public static Customer findCustomerById(int id) {
        try {
            allCustomers = loadFromJson(CUSTOMERS_JSON, new TypeReference<List<Customer>>() {});
            for (Customer c : allCustomers) {
                if (c.id == id) {
                    Customer result = c;
                    allCustomers = null; // чистка памяти
                    return result;
                }
            }
            allCustomers = null; // чистка памяти
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении customers.json: " + e.getMessage());
            return null;
        }
    }

    public static void infoOKliente(int customerId) {
        try {
            allCustomers = loadFromJson(CUSTOMERS_JSON, new TypeReference<List<Customer>>() {});
            Customer customer = findCustomerById(customerId);

            if (customer != null) {
                System.out.println("Клиент ID: " + customer.id);
                System.out.println("Имя: " + customer.name);
                System.out.println("email: " + customer.email);
            } else {
                System.out.println("Клиент не найден");
            }

            allCustomers = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении customers.json: " + e.getMessage());
        }
    }

    public static void udalitCustomer(int customerId) {
        try {
            allCustomers = loadFromJson(CUSTOMERS_JSON, new TypeReference<List<Customer>>() {});
            Customer customer = findCustomerById(customerId);

            if (customer != null) {
                allCustomers.remove(customer);
                saveToJson(CUSTOMERS_JSON, allCustomers);
                System.out.println("Клиент удалён: " + customer.name);
            } else {
                System.out.println("Ошибка: клиент не найден");
            }

            allCustomers = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении customers.json: " + e.getMessage());
        }
    }

    public static void spisokKlientov() {
        try {
            allCustomers = loadFromJson(CUSTOMERS_JSON, new TypeReference<List<Customer>>() {});
            System.out.println("=== Клиенты ===");
            for (Customer c : allCustomers) {
                System.out.println("ID: " + c.id + ", Имя: " + c.name + ", email: " + c.email);
            }
            allCustomers = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении customers.json: " + e.getMessage());
        }
    }

    // Методы поиска
    public static Product findProductById(int id) {
        try {
            allProducts = loadFromJson(PRODUCTS_JSON, new TypeReference<List<Product>>() {});
            for (Product p : allProducts) {
                if (p.id == id) {
                    Product result = p;
                    allProducts = null; // чистка памяти
                    return result;
                }
            }
            allProducts = null; // чистка памяти
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении products.json: " + e.getMessage());
            return null;
        }
    }

    public static Warehouse findWarehouseById(int id) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            for (Warehouse w : allWarehouses) {
                if (w.id == id) {
                    Warehouse result = w;
                    allWarehouses = null; // чистка памяти
                    return result;
                }
            }
            allWarehouses = null; // чистка памяти
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении warehouses.json: " + e.getMessage());
            return null;
        }
    }

    public static Employee findEmployeeById(int id) {
        try {
            allEmployees = loadFromJson(EMPLOYEES_JSON, new TypeReference<List<Employee>>() {});
            for (Employee e : allEmployees) {
                if (e.id == id) {
                    Employee result = e;
                    allEmployees = null; // чистка памяти
                    return result;
                }
            }
            allEmployees = null; // чистка памяти
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении employees.json: " + e.getMessage());
            return null;
        }
    }

    public static Store findStoreById(int id) {
        try {
            allStores = loadFromJson(STORES_JSON, new TypeReference<List<Store>>() {});
            for (Store s : allStores) {
                if (s.id == id) {
                    Store result = s;
                    allStores = null; // чистка памяти
                    return result;
                }
            }
            allStores = null; // чистка памяти
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении stores.json: " + e.getMessage());
            return null;
        }
    }

    public static void infoOSklade(int warehouseId) {
        try {
            allWarehouses = loadFromJson(WAREHOUSES_JSON, new TypeReference<List<Warehouse>>() {});
            Warehouse warehouse = findWarehouseById(warehouseId);

            if (warehouse != null) {
                System.out.println("Склад ID: " + warehouse.id);
                System.out.println("Адрес: " + warehouse.location);
                System.out.println("Ответственный: " + warehouse.manager);
            } else {
                System.out.println("Склад не найден");
            }

            allWarehouses = null; // чистка памяти
        } catch (IOException e) {
            System.out.println("Ошибка при чтении warehouses.json: " + e.getMessage());
        }
    }

    public static int schetObshheyViruchki() {
        try {
            allStores = loadFromJson(STORES_JSON, new TypeReference<List<Store>>() {});
            int total = 0;
            for (Store store : allStores) {
                total += store.income;
            }
            System.out.println("Общая выручка: " + total);
            allStores = null; // чистка памяти
            return total;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении stores.json: " + e.getMessage());
            return 0;
        }
    }

    public static void initFromExcel(HSSFWorkbook workbook) {
        xlsIO read = new xlsIO(workbook);
        allProducts = xlsIO.readProducts(workbook);
        allStores = xlsIO.readStores(workbook, allProducts);
        allWarehouses = read.readWarehouses(workbook);
        allEmployees = xlsIO.readEmployees(workbook);
        allCustomers = xlsIO.readCustomers(workbook);
    }

    //методы для сохранения и загрузки данных из джееееейсона
    public static void saveAllProductsToJson(String filePath) throws IOException {
        String json = JsonIO.serialize(allProducts);
        Files.write(Paths.get(filePath), json.getBytes());
        allProducts = null; // чистка памяти
    }

    public static void loadAllProductsFromJson(String filePath) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        allProducts = JsonIO.deserializeList(json, new TypeReference<List<Product>>() {});
    }

}