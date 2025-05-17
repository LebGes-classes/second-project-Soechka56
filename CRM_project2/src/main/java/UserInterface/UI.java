package UserInterface;

import java.io.IOException;
import java.util.Scanner;

import data.DataTransferLogic.WarehouseLogic;
import data.DataStructure.*;

public class UI implements InterfaceUI {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void start() throws IOException {
        while (true) {
            WarehouseLogic.init();
            showMenu();
            String input = scanner.nextLine();
            handleUserInput(input);
        }
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== Меню ===");
        System.out.println("1. Переместить товар между складами");
        System.out.println("2. Сменить ответственного сотрудника");
        System.out.println("3. Продажа товара");
        System.out.println("4. Возврат товара на склад");
        System.out.println("5. Закупить новый товар");
        System.out.println("6. Добавить клиента");
        System.out.println("7. Показать клиента по ID");
        System.out.println("8. Удалить клиента");
        System.out.println("9. Все клиенты");
        System.out.println("10. Инфо о складе");
        System.out.println("11. Общая выручка");
        System.out.println("0. Выход");
        System.out.print("Выберите опцию: ");
    }

    @Override
    public void handleUserInput(String input) {
        try {
            switch (input) {
                case "1" -> {
                    WarehouseLogic.printAllProducts();

                    System.out.print("ID товара: ");
                    int pid = Integer.parseInt(scanner.nextLine());
                    Product selectedProduct = WarehouseLogic.findProductById(pid);

                    if (selectedProduct == null) {
                        System.out.println("Товар с таким ID не найден.");
                        return;
                    }

                    WarehouseLogic.printProductAvailability(pid);

                    System.out.print("\nОткуда (ID склада): ");
                    int from = Integer.parseInt(scanner.nextLine());
                    System.out.print("Куда (ID склада): ");
                    int to = Integer.parseInt(scanner.nextLine());

                    WarehouseLogic.peremestitProduct(pid, from, to);
                }
                case "2" -> {
                    System.out.println("Все склады:");
                    for (Warehouse w : WarehouseLogic.allWarehouses) {
                        System.out.println("ID: " + w.id + ", Локация: " + w.location);
                    }
                    System.out.print("ID склада: ");
                    int wid = Integer.parseInt(scanner.nextLine());

                    System.out.println("Все сотрудники:");
                    for (Employee e : WarehouseLogic.allEmployees) {
                        System.out.println("ID: " + e.id + ", Имя: " + e.name);
                    }
                    System.out.print("ID сотрудника: ");
                    int eid = Integer.parseInt(scanner.nextLine());

                    WarehouseLogic.smenaResponsibleNaSklade(wid, eid);
                }
                case "3" -> {
                    System.out.print("ID магазина: ");
                    int sid = Integer.parseInt(scanner.nextLine());
                    System.out.print("ID товара: ");
                    int pid = Integer.parseInt(scanner.nextLine());
                    System.out.print("Количество: ");
                    int count = Integer.parseInt(scanner.nextLine());
                    WarehouseLogic.prodazhaProduct(sid, pid, count);
                }
                case "4" -> {
                    System.out.println("Все склады:");
                    for (Warehouse w : WarehouseLogic.allWarehouses) {
                        System.out.println("ID: " + w.id + ", Локация: " + w.location);
                    }

                    System.out.print("ID склада: ");
                    int wid = Integer.parseInt(scanner.nextLine());
                    System.out.print("ID товара: ");
                    int pid = Integer.parseInt(scanner.nextLine());
                    System.out.print("Количество: ");
                    int count = Integer.parseInt(scanner.nextLine());
                    WarehouseLogic.vozvratProductNaSklad(wid, pid, count);
                }
                case "5" -> {
                    System.out.println("Все склады:");
                    for (Warehouse w : WarehouseLogic.allWarehouses) {
                        System.out.println("ID: " + w.id + ", Локация: " + w.location);
                    }

                    System.out.print("ID склада: ");
                    int wid = Integer.parseInt(scanner.nextLine());
                    System.out.print("ID товара: ");
                    int pid = Integer.parseInt(scanner.nextLine());
                    System.out.print("Название: ");
                    String name = scanner.nextLine();
                    System.out.print("Цена: ");
                    int price = Integer.parseInt(scanner.nextLine());
                    System.out.print("Количество: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    Product newProduct = new Product(pid, name, price, quantity);
                    WarehouseLogic.zakupkaNovogoProductNaSklad(wid, newProduct, quantity);
                }
                case "6" -> {
                    System.out.print("ID клиента: ");
                    int cid = Integer.parseInt(scanner.nextLine());
                    System.out.print("Имя клиента: ");
                    String name = scanner.nextLine();
                    System.out.print("Email клиента: ");
                    String email = scanner.nextLine();
                    Customer c = new Customer(cid, name, email);
                    WarehouseLogic.dobavitCustomer(c);
                }
                case "7" -> {
                    System.out.print("ID клиента: ");
                    int cid = Integer.parseInt(scanner.nextLine());
                    WarehouseLogic.infoOKliente(cid);
                }
                case "8" -> {
                    System.out.print("ID клиента: ");
                    int cid = Integer.parseInt(scanner.nextLine());
                    WarehouseLogic.udalitCustomer(cid);
                }
                case "9" -> WarehouseLogic.spisokKlientov();
                case "10" -> {
                    System.out.println("Все склады:");
                    for (Warehouse w : WarehouseLogic.allWarehouses) {
                        System.out.println("ID: " + w.id + ", Локация: " + w.location);
                    }
                    System.out.print("ID склада: ");
                    int wid = Integer.parseInt(scanner.nextLine());
                    WarehouseLogic.infoOSklade(wid);
                }
                case "11" -> WarehouseLogic.schetObshheyViruchki();
                case "0" -> {
                    System.out.println("Выход из системы");
                    System.exit(0);
                }
                default -> System.out.println("Неизвестная команда");
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
    }
}