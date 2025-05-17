package data.DataStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    public int id;
    public int customer_id;
    public int store_id;
    public int product_id;
    public int quantity;
    public String order_date;
    public String status;

    @Override
    public String toString() {
        return String.format("Order #%d (Customer: %d, Product ID: %d, Qty: %d, Status: %s)",
                id, customer_id, product_id, quantity, status);
    }
}
