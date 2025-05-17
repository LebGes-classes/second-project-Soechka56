import data.DataTransferLogic.WarehouseLogic;
import UserInterface.UI;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("src/main/asserts/goods.xls");
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            // Загружаем данные из Excel
            WarehouseLogic.initFromExcel(workbook);

            // Запускаем консольный UI
            UI ui = new UI();
            ui.start();

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла Excel");
            e.printStackTrace();
        }
    }
}