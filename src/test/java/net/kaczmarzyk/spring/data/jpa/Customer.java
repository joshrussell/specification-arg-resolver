/**
 * Copyright 2014-2016 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kaczmarzyk.spring.data.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.*;

/**
 * A simple entity for specification testing
 *
 * @author Tomasz Kaczmarzyk
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private Gender gender;

    private String firstName;

    private String lastName;

    private String nickName;

    @Embedded
    private Address address = new Address();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date registrationDate;

    private Integer weight;

    private int weightInt;

    private long weightLong;

    private boolean gold;

    private Boolean goldObj;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Order> orders;

    @OneToMany(mappedBy = "customer2", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Order> orders2;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "Customers_Events",
            joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id")
    )
    private List<Event> events = new ArrayList<Event>();

    public Customer() {
    }

    public Customer(String firstName, String lastName, Gender gender, Date registrationDate, String street) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.registrationDate = registrationDate;
        this.address.setStreet(street);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        this.weightInt = weight;
        this.weightLong = weight;
    }

    public boolean isGold() {
        return gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
        this.goldObj = gold;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public Collection<Order> getOrders() {
        if (orders == null) {
            orders = new HashSet<>();
        }
        return orders;
    }

    public Collection<Order> getOrders2() {
        if (orders2 == null) {
            orders2 = new HashSet<>();
        }
        return orders2;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Customer[" + firstName + " " + lastName + "]";
    }
}
