package UserInterface;
import data.DataStructure.Customer;

import java.io.IOException;

public interface InterfaceUI {

    void start() throws IOException;
    void showMenu();
    void handleUserInput(String input);

}
