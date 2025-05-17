package data.DataStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {

    public Integer id;
    public String name;
    public Integer price;
    public Integer stock;
    public String type;
    public String caliber;
    public String manufacturer;
    public Integer quantity;

    public Product(){};

    public Product(int id, String name, int price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%s #%d (%s) | Price: $%d | Stock: %d | Type: %s | Caliber: %s",
                name, id, manufacturer, price, stock, type, caliber);
    }
}

