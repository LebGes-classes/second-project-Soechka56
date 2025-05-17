package data.DataStructure;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Store {
    public Integer id;
    public String name;
    public String location;
    public String manager;
    public String phone;
    public int income;

    public List<Product> products;

    @Override
    public String toString() {
        return String.format("Store #%d: %s | Location: %s | Products: %s | Income: $%,d",
                id, name, location, products != null ? products : 0, income);

    }
}
