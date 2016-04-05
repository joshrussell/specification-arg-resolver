package net.kaczmarzyk.spring.data.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Customer> customers = new ArrayList<Customer>();

    public Event(Customer customer, String itemName) {
        this.name = itemName;
        this.customers.add(customer);
        customer.getEvents().add(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
