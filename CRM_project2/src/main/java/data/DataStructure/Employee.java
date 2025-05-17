package data.DataStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    public int id;
    public String name;
    public String position;
    public String department;
    public double salary;
    public String hire_date;
    public boolean is_active;

    @Override
    public String toString() {
        return String.format("Employee[%d: %s, %s, $%.2f]", id, name, position, salary);
    }
}