package data.DataStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    public int id;
    public String name;
    public String email;
    public String phone;
    public String license_number;
    public String address;

    public Customer(){}

    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phone + "', license='" + license_number + "', address='" + address;
    }

    public boolean addNewGood(Product good) {
        return false;
    }
}
