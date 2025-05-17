package data.DataStructure;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Warehouse {
    public int id;
    public String name;
    public String location;
    public int capacity;
    public String security_level;
    public boolean is_operational;

    public List<Product> products;
    public String manager;

    @Override
    public String toString() {
        return String.format("%s (#%d) @ %s | Manager: %s | products: %s",
                name, id, location, manager,  products);
    }
}